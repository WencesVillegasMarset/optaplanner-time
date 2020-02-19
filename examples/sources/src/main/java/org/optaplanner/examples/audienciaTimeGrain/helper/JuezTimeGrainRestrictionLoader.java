package org.optaplanner.examples.audienciaTimeGrain.helper;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.Day;
import org.optaplanner.examples.audienciaTimeGrain.domain.Juez;
import org.optaplanner.examples.audienciaTimeGrain.domain.TimeGrain;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JuezTimeGrainRestrictionLoader {

    private AudienciaSchedule audienciaSchedule;

    public AudienciaSchedule loadRestrictions(AudienciaSchedule audienciaSchedule){
        this.audienciaSchedule = audienciaSchedule;
        juezTimeGrainLicence();
        juezTimeGrainSpecialRestrictions();
        juezTimeGrainAfternoon();
        return this.audienciaSchedule;
    }

    private void juezTimeGrainLicence(){
        File file = new File("data/JuezTimeGrainLicence.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document document;

        try {
            document = documentBuilder.parse(file);
            NodeList juezNodes = document.getElementsByTagName("Juez");
            for(int i=0; i<juezNodes.getLength(); i++) {
                Node juezNode = juezNodes.item(i);
                if(juezNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element juezElement = (Element) juezNode;

                    String juezId = juezElement.getElementsByTagName("id").item(0).getTextContent();
                    int id = Integer.parseInt(juezId);

                    String dayFrom = juezElement.getElementsByTagName("dayfrom").item(0).getTextContent();
                    String dayTo = juezElement.getElementsByTagName("dayto").item(0).getTextContent();
                    String[] stringDayFrom = dayFrom.split("-");
                    String[] stringDayTo = dayFrom.split("-");
                    LocalDate dateDayFrom = LocalDate.of(Integer.parseInt(stringDayFrom[2]), Integer.parseInt(stringDayFrom[1]), Integer.parseInt(stringDayFrom[0]));
                    LocalDate dateDayTo = LocalDate.of(Integer.parseInt(stringDayTo[2]), Integer.parseInt(stringDayTo[1]), Integer.parseInt(stringDayTo[0]));

                    List<TimeGrain> timeGrainList = audienciaSchedule.getTimeGrainList().stream().filter(t -> (t.getDate().isEqual(dateDayFrom) || t.getDate().isAfter(dateDayFrom)) && (t.getDate().isEqual(dateDayTo) || t.getDate().isBefore(dateDayTo))).collect(Collectors.toList());
                    List<Juez> juezList = audienciaSchedule.getJuezList().stream().filter(j -> j.getIdJuez() == id).collect(Collectors.toList());

                    if(juezList.isEmpty()){
                        throw new Exception("No existe el juez con id " + id);
                    }
                    for(Juez juez : juezList){
                        timeGrainList.forEach(juez::addProhibitedTimeGrains);
                    }

                }
            }
        } catch (NullPointerException | FileNotFoundException e){
            System.out.println("No se encontró el archivo JuezTimeGrainLicence.xml, se continuará con la ejecución del programa");
        } catch (SAXParseException e){
            System.out.println("El archivo JuezTimeGrainLicence esta vacío, se continuará con la ejecución del programa");
        } catch (Exception e) {
            System.out.println("Ocurrió un fallo leyendo el archivo JuezTimeGrainLicence.xml, posiblemente exista un error en el mismo");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void juezTimeGrainSpecialRestrictions(){
        File file = new File("data/JuezTimeGrainSpecial.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document document;

        try {
            document = documentBuilder.parse(file);
            NodeList juezNodes = document.getElementsByTagName("Juez");
            for(int i=0; i<juezNodes.getLength(); i++) {
                Node juezNode = juezNodes.item(i);
                if(juezNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element juezElement = (Element) juezNode;

                    String juezId = juezElement.getElementsByTagName("id").item(0).getTextContent();
                    int id = Integer.parseInt(juezId);

                    NodeList dayNodes = juezElement.getElementsByTagName("day");
                    for(int j = 0; j<dayNodes.getLength(); j++){
                        Node dayNode = dayNodes.item(j);
                        if (dayNode.getNodeType() == Node.ELEMENT_NODE){
                            Element dayElement = (Element) dayNode;
                            String juezStartingTime = dayElement.getElementsByTagName("startingTime").item(0).getTextContent();
                            String juezEndingTime = dayElement.getElementsByTagName("endingTime").item(0).getTextContent();
                            String date = dayElement.getElementsByTagName("date").item(0).getTextContent();
                            String[] splitDate = date.split("-");
                            LocalDate localDate = LocalDate.of(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[0]));


                            String[] stringStartingTime = juezStartingTime.split(":");
                            String[] stringEndingTime = juezEndingTime.split(":");

                            int startingTime = Integer.parseInt(stringStartingTime[0]) * 60 + Integer.parseInt(stringStartingTime[1]);
                            int endingTime = Integer.parseInt(stringEndingTime[0]) * 60 + Integer.parseInt(stringEndingTime[1]);

                            for(Juez juez : audienciaSchedule.getJuezList()){
                                if(juez.getIdJuez() == id){
                                    ArrayList<TimeGrain> timeGrainArrayList = new ArrayList<>();
                                    for(TimeGrain timeGrain : audienciaSchedule.getTimeGrainList()){
                                        if(timeGrain.getDate().isEqual(localDate) && timeGrain.getStartingMinuteOfDay() >= startingTime && timeGrain.getStartingMinuteOfDay() <= endingTime ){
                                            timeGrainArrayList.add(timeGrain);
                                        }
                                    }

                                    ArrayList<TimeGrain> oldArray = juez.getProhibitedTimeGrains();

                                    if(!timeGrainArrayList.isEmpty()){
                                        for(TimeGrain timeGrain : timeGrainArrayList){
                                            if(!oldArray.contains(timeGrain)){
                                                oldArray.add(timeGrain);
                                            }
                                        }
                                        juez.setProhibitedTimeGrains(oldArray);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException | FileNotFoundException e){
            System.out.println("No se encontró el archivo JuezTimeGrainSpecial.xml, se continuará con la ejecución del programa");
        } catch (SAXParseException e){
            System.out.println("El archivo JuezTimeGrainSpecial esta vacío, se continuará con la ejecución del programa");
        } catch (Exception e) {
            System.out.println("Ocurrió un fallo leyendo el archivo JuezTimeGrainSpecial.xml, posiblemente exista un error en el mismo");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void juezTimeGrainAfternoon(){
        File file = new File("data/JuezTimeGrainAfternoon.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document document;

        List<Day> existingDays = new ArrayList<>();

        try {
            document = documentBuilder.parse(file);
            NodeList dayNodes = document.getElementsByTagName("Day");

            for(int i=0; i<dayNodes.getLength(); i++) {
                Node dayNode = dayNodes.item(i);
                if(dayNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element dayElement = (Element) dayNode;

                    String date = dayElement.getElementsByTagName("date").item(0).getTextContent();
                    String[] dateString = date.split("-");
                    LocalDate localDate = LocalDate.of(Integer.parseInt(dateString[2]), Integer.parseInt(dateString[1]), Integer.parseInt(dateString[0]));

                    List<TimeGrain> timeGrainList = audienciaSchedule.getTimeGrainList().stream().filter(t -> t.getDate().isEqual(localDate) && t.getStartingMinuteOfDay() >= 780).collect(Collectors.toList());

                    Optional<Day> day = audienciaSchedule.getDayList().stream().filter(day1 -> day1.toDate().isEqual(localDate)).findFirst();

                    day.ifPresent(existingDays::add);

                    if(!timeGrainList.isEmpty()){
                        Element juecesElement = (Element) dayElement.getElementsByTagName("Jueces").item(0);
                        NodeList juezList = juecesElement.getElementsByTagName("Juez");
                        List<Integer> juezIdList = new ArrayList<>();
                        for(int j=0; j<juezList.getLength(); j++){
                            Node juezNode = juezList.item(j);
                            if(juezNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element juezElement = (Element) juezNode;
                                juezIdList.add(Integer.parseInt(juezElement.getElementsByTagName("id").item(0).getTextContent()));
                            }
                        }

                        audienciaSchedule.getJuezList().stream().filter(j -> !juezIdList.contains(j.getIdJuez())).forEach(juez -> timeGrainList.forEach(juez::addProhibitedTimeGrains));
                    } else{
                        System.out.println("No existe el dia " + localDate.toString() + " especificado en el archivo JuezTimeGrainAfternoon, se ignorará las restricciones de horarios de jueces para este día");
                    }

                }
            }

        } catch (NullPointerException | FileNotFoundException e){
            System.out.println("No se encontró el archivo JuezTimeGrainAfternoon.xml, se continuará con la ejecución del programa");
        } catch (SAXParseException e){
            System.out.println("El archivo JuezTimeGrainAfternoon esta vacío, se continuará con la ejecución del programa");
        } catch (Exception e) {
            System.out.println("Ocurrió un fallo leyendo el archivo JuezTimeGrainAfternoon.xml, posiblemente exista un error en el mismo");
            e.printStackTrace();
            System.exit(1);
        } finally {
            List<Day> dayList = audienciaSchedule.getDayList().stream().filter(d -> !existingDays.contains(d)).collect(Collectors.toList());

            audienciaSchedule.getTimeGrainList().stream().filter(t -> t.getStartingMinuteOfDay() >= 780 && dayList.contains(t.getDay())).forEach(t -> audienciaSchedule.getJuezList().forEach(juez -> juez.addProhibitedTimeGrains(t)));
        }
    }



}
