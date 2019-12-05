package org.optaplanner.examples.audienciaTimeGrain.helper;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.Juez;
import org.optaplanner.examples.audienciaTimeGrain.domain.TimeGrain;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class JuezTimeGrainRestrictionLoader {

    private AudienciaSchedule audienciaSchedule;

    public AudienciaSchedule loadRestrictions(AudienciaSchedule audienciaSchedule){
        this.audienciaSchedule = audienciaSchedule;
        juezTimeGrainCommonRestrictions();
        juezTimeGrainSpecialRestrictions();
        return this.audienciaSchedule;
    }

    private void juezTimeGrainCommonRestrictions(){
        File file = new File("data/JuezTimeGrainCommon.xml");
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
                            int dayOfWeek = Integer.parseInt(dayElement.getElementsByTagName("dayofweek").item(0).getTextContent());


                            String[] stringStartingTime = juezStartingTime.split(":");
                            String[] stringEndingTime = juezEndingTime.split(":");

                            int startingTime = Integer.parseInt(stringStartingTime[0]) * 60 + Integer.parseInt(stringStartingTime[1]);
                            int endingTime = Integer.parseInt(stringEndingTime[0]) * 60 + Integer.parseInt(stringEndingTime[1]);

//                        System.out.println("Juez Id = " + juezId);
//                        System.out.println("StartingTime = " + juezStartingTime);
//                        System.out.println("EndingTime = " + juezEndingTime);

                            for(Juez juez : audienciaSchedule.getJuezList()){
                                if(juez.getIdJuez() == id){
                                    ArrayList<TimeGrain> timeGrainArrayList = new ArrayList<>();
                                    for(TimeGrain timeGrain : audienciaSchedule.getTimeGrainList()){
                                        if(timeGrain.getDate().getDayOfWeek().getValue() == dayOfWeek && timeGrain.getStartingMinuteOfDay() >= startingTime && timeGrain.getStartingMinuteOfDay() <= endingTime){
                                            timeGrainArrayList.add(timeGrain);
                                        }
                                    }
                                    juez.setProhibitedTimeGrains(timeGrainArrayList);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

//                        System.out.println("Juez Id = " + juezId);
//                        System.out.println("StartingTime = " + juezStartingTime);
//                        System.out.println("EndingTime = " + juezEndingTime);

                            for(Juez juez : audienciaSchedule.getJuezList()){
                                if(juez.getIdJuez() == id){
                                    ArrayList<TimeGrain> timeGrainArrayList = new ArrayList<>();
                                    for(TimeGrain timeGrain : audienciaSchedule.getTimeGrainList()){
                                        if(timeGrain.getDate().isEqual(localDate) && timeGrain.getStartingMinuteOfDay() >= startingTime && timeGrain.getStartingMinuteOfDay() <= endingTime ){
                                            timeGrainArrayList.add(timeGrain);
                                        }
                                    }

                                    ArrayList<TimeGrain> oldArray = juez.getProhibitedTimeGrains();

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
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
