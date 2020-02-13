package org.optaplanner.examples.audienciaTimeGrain.app;

import org.json.JSONArray;
import org.json.JSONObject;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;
import org.optaplanner.examples.audienciaTimeGrain.helper.JuezTimeGrainRestrictionLoader;
import org.optaplanner.examples.audienciaTimeGrain.persistence.ExcelReader;
import org.optaplanner.examples.audienciaTimeGrain.persistence.XMLExporter;
import org.optaplanner.examples.audienciaTimeGrain.persistence.XMLImporter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OGAPSolver {

//    public static final String SOLVER_CONFIG = "data/solver/audienciaTimeGrainSolverConfig.xml";
    public static final String SOLVER_CONFIG = "org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml";


    public static final String XML_OUTPUT_DIR_NAME = "data/xml/";

    public static final String EXCEL_OUTPUT_DIR_NAME = "data/excel/";

    public static final String UNSOLVED_DIR = "data/unsolved";

    public static void main(String[] args){

        createDirectories();

        AudienciaSchedule audienciaSchedule = new AudienciaSchedule();

        File folder = new File(UNSOLVED_DIR);
        File[] fileEntry = folder.listFiles();
        assert fileEntry != null;
        File file = fileEntry[0];
        String[] dateString = file.getName().split("-");
        LocalDate date = LocalDate.of(Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]), Integer.parseInt(dateString[2].substring(0, 2)));

        ExcelReader excelReader = new ExcelReader();
        excelReader.setDate(date);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AudienciaSchedule.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            audienciaSchedule = (AudienciaSchedule) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }



        audienciaSchedule.setPossibleRooms(audienciaSchedule.getRoomList().stream().filter(Room::isUsable).collect(Collectors.toList()));
        if (audienciaSchedule.getConstraintConfiguration() == null){
            audienciaSchedule.setConstraintConfiguration(new AudienciaScheduleConstraintConfiguration());
        }

        audienciaSchedule.setFechaCorrida(date);
        audienciaSchedule.getAudienciaAssignmentList().forEach(a -> a.setFechaCorrida(date));

        for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            if (audienciaAssignment.getAudiencia().getDurationMinutes() % TimeGrain.GRAIN_LENGTH_IN_MINUTES == 0){
                audienciaAssignment.getAudiencia().setNumTimeGrains(audienciaAssignment.getAudiencia().getDurationMinutes() / TimeGrain.GRAIN_LENGTH_IN_MINUTES);
            } else{
                audienciaAssignment.getAudiencia().setNumTimeGrains((audienciaAssignment.getAudiencia().getDurationMinutes() + 5) / TimeGrain.GRAIN_LENGTH_IN_MINUTES);
            }
        }

        createDaysAndTimeGrains(audienciaSchedule);

        List<AudienciaAssignment> audienciaAssignmentsToDelete = new ArrayList<>();
        List<Audiencia> audienciasToDelete = new ArrayList<>();

        for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            if (audienciaAssignment.isPinned()){
                Audiencia audiencia = audienciaAssignment.getAudiencia();
                if (audiencia.getStartingMinuteOfDay() % TimeGrain.GRAIN_LENGTH_IN_MINUTES != 0){
                    audiencia.setStartingMinuteOfDay(audiencia.getStartingMinuteOfDay() - 5);
                }

                Day dayToUse = null;
                for (Day day : audienciaSchedule.getDayList()){
                    if (day.toDate().isEqual(audiencia.getFechaRealizacion())){
                        dayToUse = day;
                        break;
                    }
                }
                if (dayToUse == null){
                    System.out.println("No existe el dia " + audiencia.getFechaRealizacion().toString() + " para la audiencia con id " + audiencia.getIdAudiencia());
                    audienciaAssignmentsToDelete.add(audienciaAssignment);
                    audienciasToDelete.add(audiencia);
                    continue;
                }

                TimeGrain timeGrainToUse = null;
                for (TimeGrain timeGrain : audienciaSchedule.getTimeGrainList()){
                    if (timeGrain.getDay().equals(dayToUse) && timeGrain.getStartingMinuteOfDay() == audiencia.getStartingMinuteOfDay()){
                        timeGrainToUse = timeGrain;
                        audienciaAssignment.setStartingTimeGrain(timeGrainToUse);
                        break;
                    }
                }
                if (timeGrainToUse == null){
                    System.out.println("No existe el timegrain para el minuto" + audiencia.getStartingMinuteOfDay() + " del día, para la audiencia con id " + audiencia.getIdAudiencia());
                    audienciaAssignmentsToDelete.add(audienciaAssignment);
                    audienciasToDelete.add(audiencia);
                }
            }
        }

        if (!audienciaAssignmentsToDelete.isEmpty()){
            List<AudienciaAssignment> currentAudienciaAssignments = audienciaSchedule.getAudienciaAssignmentList();
            List<Audiencia> currentAudiencias = audienciaSchedule.getAudienciaList();

            audienciaSchedule.setAudienciaAssignmentList(currentAudienciaAssignments.stream().filter(a -> !audienciaAssignmentsToDelete.contains(a)).collect(Collectors.toList()));
        }

        createActorLists(audienciaSchedule);

        JuezTimeGrainRestrictionLoader restrictionLoader = new JuezTimeGrainRestrictionLoader();
        audienciaSchedule = restrictionLoader.loadRestrictions(audienciaSchedule);


        SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        Solver<AudienciaSchedule> solver = solverFactory.buildSolver();

        audienciaSchedule = solver.solve(audienciaSchedule);

        String excelFileName = "Result.xlsx";
        excelReader.write(audienciaSchedule, new File(EXCEL_OUTPUT_DIR_NAME + excelFileName));

        if (args.length != 0 && args[0].equals("short")){
            cleanForXml(audienciaSchedule);
        }

        XMLExporter xmlExporter = new XMLExporter(XML_OUTPUT_DIR_NAME);
        try{
            xmlExporter.write(audienciaSchedule);
        } catch (FileNotFoundException | JAXBException e) {
            e.printStackTrace();
        }

//        file.delete();

    }

    private static void createDirectories(){

        File directory = new File("data/");
        if(!directory.exists()){
            directory.mkdir();
        }

        directory = new File("data/unsolved/");
        if(!directory.exists()){
            directory.mkdir();
        }

        directory = new File("data/excel/");
        if(!directory.exists()){
            directory.mkdir();
        }

        directory = new File("data/xml/");
        if(!directory.exists()){
            directory.mkdir();
        }
    }

    private static void createDaysAndTimeGrains(AudienciaSchedule audienciaSchedule){

        List<Day> dayList = new ArrayList<>(75);
        List<TimeGrain> timeGrainList = new ArrayList<>();
        int dayId = 0, timeGrainId = 0;

        List<LocalDate> feriados;

        feriados = getFeriados(audienciaSchedule.getFechaCorrida());

        boolean isDateBehind = audienciaSchedule.getAudienciaAssignmentList().stream().filter(assignment -> assignment.getRoom() == null)
                .min(Comparator.comparingInt(a -> a.getFechaPedido().getYear() + a.getFechaPedido().getDayOfYear())).isPresent();

        LocalDate minimumDate = isDateBehind ? audienciaSchedule.getAudienciaAssignmentList().stream().filter(assignment -> assignment.getRoom() == null)
                .min(Comparator.comparingInt(a -> a.getFechaPedido().getYear() + a.getFechaPedido().getDayOfYear()))
                .get().getFechaPedido() : audienciaSchedule.getFechaCorrida();

        LocalTime startTime = LocalTime.of(8,0);
        LocalTime endTime = LocalTime.of(21,0);
        LocalTime lastStartingMinute = LocalTime.of(18,0);
        int startMinuteOfDay = startTime.getHour() * 60 + startTime.getMinute();
        int endMinuteOfDay = endTime.getHour() * 60 + endTime.getMinute();
        int maximumStartingMinuteOfDay = lastStartingMinute.getHour() * 60 + lastStartingMinute.getMinute();


        for(int j=0; j<75; j++){
            if (minimumDate.isAfter(audienciaSchedule.getFechaCorrida().plusMonths(2))){
                break;
            }
            boolean isFeriado;
            do {
                isFeriado = false;
                for (LocalDate localDate : feriados){
                    if(minimumDate.isEqual(localDate)){
                        isFeriado = true;
                    }
                }
                if(minimumDate.getDayOfWeek().getValue() == 6 || minimumDate.getDayOfWeek().getValue() == 7){
                    isFeriado = true;
                }
                if(isFeriado){
                    minimumDate = minimumDate.plusDays(1);
                }
            }while (isFeriado);


            Day day = new Day();
            day.setLastStartingMinute(maximumStartingMinuteOfDay);
            day.setIdDay(dayId);
            day.setDayOfYear(minimumDate.getDayOfYear());
            day.setDate(minimumDate);
            dayList.add(day);

            dayId++;
            minimumDate = minimumDate.plusDays(1);


            if(minimumDate.isAfter(audienciaSchedule.getFechaCorrida())){
                for (int i = 0; (endMinuteOfDay - startMinuteOfDay) > i * TimeGrain.GRAIN_LENGTH_IN_MINUTES; i++) {
                    int timeGrainStartingMinuteOfDay = i * TimeGrain.GRAIN_LENGTH_IN_MINUTES + startMinuteOfDay;
                    TimeGrain timeGrain = new TimeGrain();
                    timeGrain.setIdTimeGrain(timeGrainId);
                    timeGrain.setGrainIndex(timeGrainId);
                    timeGrain.setDay(day);
                    timeGrain.setStartingMinuteOfDay(timeGrainStartingMinuteOfDay);
                    timeGrainList.add(timeGrain);
                    timeGrainId++;
                }
            }

        }
        audienciaSchedule.setDayList(dayList);
        audienciaSchedule.setTimeGrainList(timeGrainList);
    }

    private static List<LocalDate> getFeriados(LocalDate date) {

        int anoActual = date.getYear();
        List<LocalDate> feriadosList = new ArrayList<>();

        for (int j = 0; j<2; j++){
            URL url = null;
            try {
                url = new URL("http://nolaborables.com.ar/api/v2/feriados/" + anoActual);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            try {
                assert url != null;
                con = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert con != null;
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            try {
                if(con.getResponseCode() == 404){
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String inputLine = null;
            StringBuilder response = new StringBuilder();
            while (true){
                try {
                    assert in != null;
                    if ((inputLine = in.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.append(inputLine);
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONArray jsonArray = new JSONArray(response.toString());

            for (int i = 0; i< jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                LocalDate feriado = LocalDate.of(anoActual, jsonObject.getInt("mes"), jsonObject.getInt("dia"));
                feriadosList.add(feriado);
            }

            anoActual++;
        }

        return feriadosList;
    }

    private static void createActorLists(AudienciaSchedule audienciaSchedule){

        List<Juez> juezList = new ArrayList<>();
        for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            Audiencia audiencia = audienciaAssignment.getAudiencia();
            List<Juez> juecesToAdd = new ArrayList<>();
            for (Juez juez : audiencia.getJuezList()){
                boolean juezExists = false;
                for (Juez juez1 : juezList){
                    if (juez.getIdJuez() == juez1.getIdJuez()){
                        juecesToAdd.add(juez1);
                        juezExists = true;
                        break;
                    }
                }
                if (!juezExists){
                    juezList.add(juez);
                    juecesToAdd.add(juez);
                }
            }
            audiencia.setJuezList(juecesToAdd);
        }
        audienciaSchedule.setJuezList(juezList);


        List<Defensor> defensorList = new ArrayList<>();
        for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            Audiencia audiencia = audienciaAssignment.getAudiencia();
            List<Defensor> defensoresToAdd = new ArrayList<>();
            for (Defensor defensor : audiencia.getDefensorList()){
                boolean defensorExists = false;
                for (Defensor defensor1 : defensorList){
                    if (defensor.getIdDefensor().equals(defensor1.getIdDefensor())){
                        defensoresToAdd.add(defensor1);
                        defensorExists = true;
                        break;
                    }
                }
                if (!defensorExists){
                    defensorList.add(defensor);
                    defensoresToAdd.add(defensor);
                }
            }
            audiencia.setDefensorList(defensoresToAdd);
        }
        audienciaSchedule.setDefensorList(defensorList);


        List<Fiscal> fiscalList = new ArrayList<>();
        for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            Audiencia audiencia = audienciaAssignment.getAudiencia();
            List<Fiscal> fiscalesToAdd = new ArrayList<>();
            for (Fiscal fiscal : audiencia.getFiscalList()){
                boolean fiscalExists = false;
                for (Fiscal fiscal1 : fiscalList){
                    if (fiscal.getIdFiscal() == fiscal1.getIdFiscal()){
                        fiscalesToAdd.add(fiscal1);
                        fiscalExists = true;
                        break;
                    }
                }
                if (!fiscalExists){
                    fiscalList.add(fiscal);
                    fiscalesToAdd.add(fiscal);
                }
            }
            audiencia.setFiscalList(fiscalesToAdd);
        }
        audienciaSchedule.setFiscalList(fiscalList);


        List<Asesor> asesorList = new ArrayList<>();
        for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            Audiencia audiencia = audienciaAssignment.getAudiencia();
            List<Asesor> asesoresToAdd = new ArrayList<>();
            for (Asesor asesor : audiencia.getAsesorList()){
                boolean asesorExists = false;
                for (Asesor asesor1 : asesorList){
                    if (asesor.getIdAsesor() == asesor1.getIdAsesor()){
                        asesoresToAdd.add(asesor1);
                        asesorExists = true;
                        break;
                    }
                }
                if (!asesorExists){
                    asesorList.add(asesor);
                    asesoresToAdd.add(asesor);
                }
            }
            audiencia.setAsesorList(asesoresToAdd);
        }
        audienciaSchedule.setAsesorList(asesorList);


        List<Querellante> querellanteList = new ArrayList<>();
        for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            Audiencia audiencia = audienciaAssignment.getAudiencia();
            List<Querellante> querellantesToAdd = new ArrayList<>();
            for (Querellante querellante : audiencia.getQuerellanteList()){
                boolean querellanteExists = false;
                for (Querellante querellante1 : querellanteList){
                    if (querellante.getIdQuerellante() == querellante1.getIdQuerellante()){
                        querellantesToAdd.add(querellante1);
                        querellanteExists = true;
                        break;
                    }
                }
                if (!querellanteExists){
                    querellanteList.add(querellante);
                    querellantesToAdd.add(querellante);
                }
            }
            audiencia.setQuerellanteList(querellantesToAdd);
        }
        audienciaSchedule.setQuerellanteList(querellanteList);


        List<Tipo> tipoList = new ArrayList<>();
        for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()){
            Audiencia audiencia = audienciaAssignment.getAudiencia();

            boolean tipoExists = false;
            for (Tipo tipo : tipoList){
                if (tipo.getIdTipo() == audiencia.getTipo().getIdTipo()){
                    audiencia.setTipo(tipo);
                    tipoExists = true;
                    break;
                }
            }
            if (!tipoExists){
                tipoList.add(audiencia.getTipo());
            }
        }
        audienciaSchedule.setTipoList(tipoList);


        audienciaSchedule.setAudienciaList(audienciaSchedule.getAudienciaAssignmentList().stream().map(AudienciaAssignment::getAudiencia).collect(Collectors.toList()));

        for(AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList().stream().filter(a -> a.getRoom() != null).collect(Collectors.toList())){
            for(Room room : audienciaSchedule.getRoomList()){
                if(room.getIdRoom() == audienciaAssignment.getRoom().getIdRoom()){
                    audienciaAssignment.setRoom(room);
                    break;
                }
            }
        }

    }

    private static void cleanForXml(AudienciaSchedule audienciaSchedule){
        audienciaSchedule.setAudienciaList(null);
        audienciaSchedule.setTipoList(null);
        audienciaSchedule.setRoomList(null);
        audienciaSchedule.setPossibleRooms(null);
        audienciaSchedule.setJuezList(null);
        audienciaSchedule.setDefensorList(null);
        audienciaSchedule.setFiscalList(null);
        audienciaSchedule.setQuerellanteList(null);
        audienciaSchedule.setAsesorList(null);
        audienciaSchedule.setTimeGrainList(null);
        audienciaSchedule.setDayList(null);

        audienciaSchedule.getAudienciaAssignmentList().forEach(aa -> aa.setAudiencia(null));
        audienciaSchedule.getAudienciaAssignmentList().forEach(aa -> aa.setFechaCorrida(null));
    }
}
