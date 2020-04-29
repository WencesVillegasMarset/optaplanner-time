package org.optaplanner.examples.audienciaTimeGrain.app;

import org.json.JSONArray;
import org.json.JSONObject;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;
import org.optaplanner.examples.audienciaTimeGrain.helper.JuezTimeGrainRestrictionLoader;
import org.optaplanner.examples.audienciaTimeGrain.persistence.ExcelReader;
import org.optaplanner.examples.audienciaTimeGrain.persistence.XMLExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OGAPSolver {

    /* DIRECTORIOS */

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/audienciaTimeGrain/solver/audienciaTimeGrainSolverConfig.xml";

    public static final String XML_OUTPUT_DIR_NAME = "data/xml/";

    public static final String EXCEL_OUTPUT_DIR_NAME = "data/excel/";

    public static final String UNSOLVED_DIR = "data/unsolved";

    public static final Logger logger = LoggerFactory.getLogger(OGAPSolver.class);

    public static void main(String[] args){

        createDirectories();

        AudienciaSchedule audienciaSchedule = new AudienciaSchedule();

        /* LECTURA DE XML (SOLICITUDES) */

        File folder = new File(UNSOLVED_DIR);
        File[] fileEntry = folder.listFiles();
        assert fileEntry != null;
        File file = null;

        try{
            file = fileEntry[0];
        } catch (ArrayIndexOutOfBoundsException e){
            logger.error("No existe el archivo que contiene las solicitudes de calendarizacion", e);
            System.exit(1);
        }

        String[] dateString = file.getName().split("-");
        LocalDate date = LocalDate.of(Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]), Integer.parseInt(dateString[2].substring(0, 2)));

        ExcelReader excelReader = new ExcelReader();
        excelReader.setDate(date);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AudienciaSchedule.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            audienciaSchedule = (AudienciaSchedule) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            logger.error("Hubo un error al parsear el archivo con solicitudes", e);
            System.exit(1);
        }

        /* OBTENCIÓN DE DATOS DE AUDIENCIAS Y CONFIGURACION BÁSICA */

        audienciaSchedule.getRoomList().forEach(r -> r.setNombreRoom(String.valueOf(r.getIdRoom())));

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

        /* BORRADO DE AUDIENCIAS QUE NO CORRESPONDEN CON DIAS A CALENDARIZAR */

        List<AudienciaAssignment> audienciaAssignmentsToDelete = new ArrayList<>();

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
                    logger.warn("No existe el dia " + audiencia.getFechaRealizacion().toString() + " para la audiencia con id " + audiencia.getIdAudiencia());
                    audienciaAssignmentsToDelete.add(audienciaAssignment);
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
                    logger.warn("No existe el timegrain para el minuto" + audiencia.getStartingMinuteOfDay() + " del día, para la audiencia con id " + audiencia.getIdAudiencia());
                    audienciaAssignmentsToDelete.add(audienciaAssignment);
                }
            }
        }

        if (!audienciaAssignmentsToDelete.isEmpty()){
            List<AudienciaAssignment> currentAudienciaAssignments = audienciaSchedule.getAudienciaAssignmentList();

            audienciaSchedule.setAudienciaAssignmentList(currentAudienciaAssignments.stream().filter(a -> !audienciaAssignmentsToDelete.contains(a)).collect(Collectors.toList()));
        }

        /* CREACION DE LISTAS PARA EL SOLVER Y HORARIOS RESTRINGIDOS DE JUECES */

        createActorLists(audienciaSchedule);

        JuezTimeGrainRestrictionLoader restrictionLoader = new JuezTimeGrainRestrictionLoader();
        audienciaSchedule = restrictionLoader.loadRestrictions(audienciaSchedule);

        /* CREACIÓN, CONFIGURACIÓN Y EJECUCIÓN DEL SOLVER */

        SolverFactory<AudienciaSchedule> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        Solver<AudienciaSchedule> solver = solverFactory.buildSolver();

        try{
            audienciaSchedule = solver.solve(audienciaSchedule);
        } catch (IllegalStateException e){
            logger.error("Error al intentar correr solver", e);
            System.exit(1);
        }


        /* GUARDADO DE SOLUCIÓN EN EXCEL Y XML */

        String excelFileName = "Result.xlsx";
        excelReader.write(audienciaSchedule, new File(EXCEL_OUTPUT_DIR_NAME + excelFileName));

        if (args.length != 0 && args[0].equals("short")){
            cleanForXml(audienciaSchedule);
        }

        XMLExporter xmlExporter = new XMLExporter(XML_OUTPUT_DIR_NAME);
        try{
            xmlExporter.write(audienciaSchedule);
        } catch (FileNotFoundException | JAXBException e) {
            logger.warn("Hubo un error al exportar el archivo en excel", e);
        }

        file.delete();

    }

    /* VERIFICACIÓN DE LA EXISTENCIA DE DIRECTORIOS NECESARIOS */
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

    /* CREACION DE TIMEGRAINS PARA LA RESOLUCIÓN */
    private static void createDaysAndTimeGrains(AudienciaSchedule audienciaSchedule){

        List<Day> dayList = new ArrayList<>(75);
        List<TimeGrain> timeGrainList = new ArrayList<>();
        int dayId = 0, timeGrainId = 0;

        List<LocalDate> feriados = new ArrayList<>();

        try {
            feriados = getFeriados(audienciaSchedule.getFechaCorrida());
        } catch (IOException e) {
            logger.error("Hubo un error al intentar obtener los feriados", e);
            System.exit(1);
        }

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


        while(true){
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

    /* CONEXIÓN CON API PARA OBTENER FERIADOS Y DIAS NO LABORABLES */
    private static List<LocalDate> getFeriados(LocalDate date) throws IOException {

        int anioActual = date.getYear();
        List<LocalDate> feriadosList = new ArrayList<>();

        for (int j = 0; j<2; j++){
            URL url = null;
            try {
                File urlFile = new File("local/log/config.txt");
                BufferedReader br = new BufferedReader(new FileReader(urlFile));
//                url = new URL("http://nolaborables.com.ar/api/v2/feriados/" + anioActual);
//                url = new URL("http://0.0.0.0:5000/v1/feriados/" + anioActual);
                String readURL = br.readLine();
                url = new URL(readURL + anioActual);
            } catch (IOException e) {
                logger.error("La URL para la obtención de feriados está mal construída o hay un error en el archivo config.txt", e);
                System.exit(1);
            }
            HttpURLConnection con;
            con = (HttpURLConnection) url.openConnection();

            try {
                assert con != null;
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                logger.error("Error en el protocolo de la conexión con la API de feriados", e);
                System.exit(1);
            }
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);

            if(con.getResponseCode() == 404 || con.getResponseCode() == 400){
                logger.warn("La API de feriados devuelve un error " + con.getResponseCode() + " para el año " + anioActual);
                continue;
            }
            BufferedReader in;

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();


            JSONArray jsonArray = new JSONArray(response.toString());

            for (int i = 0; i< jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                LocalDate feriado = LocalDate.of(anioActual, jsonObject.getInt("mes"), jsonObject.getInt("dia"));
                feriadosList.add(feriado);
            }

            anioActual++;
        }

        return feriadosList;
    }

    /* CREACION DE LISTAS DE ACTORES PARA UTILIZACIÓN DEL SOLVER */
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
                    if (defensor.getIdDefensor() == defensor1.getIdDefensor()){
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

    /* LIMPIEZA DE SOLUCIÓN PARA EXPORTAR AL XML */
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
