package org.optaplanner.examples.audienciaTimeGrain.persistence;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.time.*;
import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.audienciaTimeGrain.app.Main;
import org.optaplanner.examples.audienciaTimeGrain.domain.*;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;

import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.json.Json;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class ExcelReader extends AbstractXlsxSolutionFileIO<AudienciaSchedule>{

    private LocalDate date;

    public void setDate(LocalDate date){
        this.date = date;
    }

    @Override
    public AudienciaSchedule read(File file) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AudienciaSchedulingXlsxReader(workbook, this.date).read();
    }

    private static class AudienciaSchedulingXlsxReader extends AbstractXlsxReader<AudienciaSchedule>{

        AudienciaSchedulingXlsxReader (XSSFWorkbook workbook, LocalDate fechaCalendarizacion){
            super(workbook, Main.SOLVER_CONFIG);
            fechainicial = fechaCalendarizacion;
        }

        private LocalDate fechainicial;

        public AudienciaSchedule read(){
            solution = new AudienciaSchedule();
//            readConfiguration();
            readXML();
            readDayList();
            readPossibleRooms();
//            readRoomList();
//            readJuezList();
//            readFiscalList();
//            readDefensorList();
//            readTipoList();
            readAudienciaList();
            return solution;
        }

        private void readConfiguration(){
            nextSheet("Configuration");
            nextRow();
            fechainicial = LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER);
            solution.setFechaCorrida(fechainicial);
            nextRow(true);
            readHeaderCell("Constraint");
            readHeaderCell("Weight");
            readHeaderCell("Description");

            AudienciaScheduleConstraintConfiguration constraintConfiguration = new AudienciaScheduleConstraintConfiguration();

            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.ROOM_CONFLICT, hardScore -> constraintConfiguration.setRoomConflict(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.START_AND_END_ON_SAME_DAY, hardScore -> constraintConfiguration.setStartAndEndOnSameDay(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DONT_GO_IN_OVERTIME, hardScore -> constraintConfiguration.setDontGoInOvertime(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_JUEZ, hardScore -> constraintConfiguration.setDontConflictJuez(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME, hardScore -> constraintConfiguration.setDontConflictRoomTime(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_FISCAL, hardScore -> constraintConfiguration.setDontConflictFiscal(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_DEFENSOR, hardScore -> constraintConfiguration.setDontConflictDefensor(HardMediumSoftScore.ofHard(hardScore)), "");
//            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_NOT_USE_BREAKS, hardScore -> constraintConfiguration.setDontUseBreaks(HardMediumSoftScore.ofHard(hardScore)), "");
//            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.RESPECT_LOCATIONS, hardScore -> constraintConfiguration.setRespectLocations(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.RESPECT_MINIMUM_STARTING_TIME, hardScore -> constraintConfiguration.setRespectMinimumStartingTime(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.RESPECT_MAXIMUM_STARTING_TIME, hardScore -> constraintConfiguration.setRespectMaximumStartingTime(HardMediumSoftScore.ofHard(hardScore)), "");
//            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DONT_CONFLICT_JUEZ_LOCATION, hardScore -> constraintConfiguration.setDontConflictJuezLocation(HardMediumSoftScore.ofHard(hardScore)), "");
//            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DONT_CONFLICT_DEFENSOR_LOCATION, hardScore -> constraintConfiguration.setDontConflictDefensorLocation(HardMediumSoftScore.ofHard(hardScore)), "");
//            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DONT_CONFLICT_FISCAL_LOCATION, hardScore -> constraintConfiguration.setDontConflictFiscalLocation(HardMediumSoftScore.ofHard(hardScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DONT_START_AFTER_MAXIMUM_STARTING_MINUTE, hardScore -> constraintConfiguration.setDontStartAfterMaximumStartingMinute(HardMediumSoftScore.ofHard(hardScore)), "");


            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS, softScore -> constraintConfiguration.setOneTimeGrainBreakBetweenTwoConsecutiveMeetings(HardMediumSoftScore.ofSoft(softScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.ONE_TIMEGRAIN_JUEZ, softScore -> constraintConfiguration.setOneTimeGrainJuez(HardMediumSoftScore.ofSoft(softScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.ONE_TIMEGRAIN_DEFENSOR, softScore -> constraintConfiguration.setOneTimeGrainDefensor(HardMediumSoftScore.ofSoft(softScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.ONE_TIMEGRAIN_FISCAL, softScore -> constraintConfiguration.setOneTimeGrainFiscal(HardMediumSoftScore.ofSoft(softScore)), "");
            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE, softScore -> constraintConfiguration.setDoAllMeetingsAsSoonAsPossible(HardMediumSoftScore.ofSoft(softScore)), "");
//            readIntConstraintParameterLine(AudienciaScheduleConstraintConfiguration.DISTRIBUTE_WORKLOAD_FAIRLY, softScore -> constraintConfiguration.setDistributeWorkloadFairly(HardMediumSoftScore.ofSoft(softScore)), "");

            solution.setConstraintConfiguration(constraintConfiguration);
        }

        private void readJuezList(){
            nextSheet("Jueces");
            nextRow(false);
            readHeaderCell("Nombre Completo");
            readHeaderCell("Id");
            List<Juez> juezList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
//            System.out.println("Jueces: ");
            while (nextRow()) {
                Juez juez = new Juez();
                juez.setNombre(nextStringCell().getStringCellValue());
                juez.setIdJuez((int)nextNumericCell().getNumericCellValue());
//                if (!VALID_NAME_PATTERN.matcher(juez.getNombre()).matches()) {
//                    throw new IllegalStateException(
//                            currentPosition() + ": The person name (" + juez.getNombre()
//                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
//                }
                juezList.add(juez);
//                System.out.println(juez.getNombre() + " con id numero " + juez.getIdJuez());
            }
            solution.setJuezList(juezList);
        }

        private void readDefensorList(){
            nextSheet("Defensores");
            nextRow(false);
            readHeaderCell("Nombre Completo");
            readHeaderCell("Id");
            List<Defensor> defensorList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
//            System.out.println("Defensores: ");
            while (nextRow()) {
                Defensor defensor = new Defensor();
                String nombredefensor = nextStringCell().getStringCellValue();
                defensor.setNombreDefensor(nombredefensor);
                String id = Base64.getEncoder().encodeToString(nombredefensor.getBytes());
                defensor.setIdDefensor(id);
                defensor.setIdNegocio((int)nextNumericCell().getNumericCellValue());
//                if (!VALID_NAME_PATTERN.matcher(defensor.getNombreDefensor()).matches()) {
//                    throw new IllegalStateException(
//                            currentPosition() + ": The person name (" + defensor.getNombreDefensor()
//                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
//                }
                defensorList.add(defensor);
//                System.out.println(defensor.getNombreDefensor() + " con id numero " + defensor.getIdDefensor());
            }
            solution.setDefensorList(defensorList);
        }

        private void readFiscalList(){
            nextSheet("Fiscales");
            nextRow(false);
            readHeaderCell("Nombre Completo");
            readHeaderCell("Id");
            List<Fiscal> fiscalList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
//            System.out.println("Fiscales: ");
            while (nextRow()) {
                Fiscal fiscal = new Fiscal();
                fiscal.setNombreFiscal(nextStringCell().getStringCellValue());
                fiscal.setIdFiscal((int)nextNumericCell().getNumericCellValue());
//                if (!VALID_NAME_PATTERN.matcher(fiscal.getNombreFiscal()).matches()) {
//                    throw new IllegalStateException(
//                            currentPosition() + ": The person name (" + fiscal.getNombreFiscal()
//                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
//                }
                fiscalList.add(fiscal);
//                System.out.println(fiscal.getNombreFiscal() + " con id numero " + fiscal.getIdFiscal());
            }
            solution.setFiscalList(fiscalList);
        }

        private void readTipoList(){
            nextSheet("Tipos de Audiencia");
            nextRow(false);
            readHeaderCell("Tipo");
            readHeaderCell("Id");
            readHeaderCell("Tiempo Realizacion Minimo");
            readHeaderCell("Tiempo Realizacion Maximo");
            readHeaderCell("Tiempo de fijacion");
            List<Tipo> tipoList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
//            System.out.println("Tipos: ");
            while (nextRow()){
                Tipo tipo = new Tipo();
                tipo.setNombreTipo(nextStringCell().getStringCellValue());
                tipo.setIdTipo((int)nextNumericCell().getNumericCellValue());
                XSSFCell minimo = nextCell();
                if(minimo.getNumericCellValue() != 0){
                    tipo.setTiempoRealizacionMinimo((int)minimo.getNumericCellValue());
//                    System.out.println((int)minimo.getNumericCellValue());
                }else {
                    tipo.setTiempoRealizacionMinimo(2);
                }
                XSSFCell maximo = nextCell();
                if(maximo.getNumericCellValue() != 0){
                    tipo.setTiempoRealizacionMaximo((int)maximo.getNumericCellValue());
//                    System.out.println((int)maximo.getNumericCellValue());
                }
                tipo.setTiempoFijacion((int)nextNumericCell().getNumericCellValue());
//                System.out.println(tipo.getTiempoRealizacionMinimo() + " " + tipo.getTiempoRealizacionMaximo());
//                if (!VALID_NAME_PATTERN.matcher(tipo.getNombreTipo()).matches()) {
//                    throw new IllegalStateException(
//                            currentPosition() + ": The person name (" + tipo.getNombreTipo()
//                                    + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
//                }
                tipoList.add(tipo);
//                System.out.println(tipo.getNombreTipo() + " con id numero " + tipo.getIdTipo());
            }
            solution.setTipoList(tipoList);
        }

        private void readXML(){
            JAXBContext jaxbContext;

            try {
                jaxbContext = JAXBContext.newInstance(AudienciaSchedule.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                AudienciaSchedule audienciaSchedule = (AudienciaSchedule) jaxbUnmarshaller.unmarshal(new File("data/database.xml"));
                solution.setDefensorList(audienciaSchedule.getDefensorList());
                solution.setFiscalList(audienciaSchedule.getFiscalList());
                solution.setJuezList(audienciaSchedule.getJuezList());
                solution.setRoomList(audienciaSchedule.getRoomList());
                solution.setTipoList(audienciaSchedule.getTipoList());
                solution.setConstraintConfiguration(audienciaSchedule.getConstraintConfiguration());
            } catch (JAXBException e) {
                e.printStackTrace();
            }

        }

        public void readPossibleRooms() {
            File file = new File("data/rooms_mendoza.txt");

            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String st = null;
            List<String> stringList = new ArrayList<>();
            List<Room> roomList = new ArrayList<>();
            while (true) {
                try {
                    if ((st = br.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stringList.add(st);
            }

            for(Room existingRoom : solution.getRoomList()){
                if(stringList.contains(String.valueOf(existingRoom.getIdRoom()))){
                    roomList.add(existingRoom);
                }
            }

            solution.setPossibleRooms(roomList);
        }


        private void readDayList(){
//            nextSheet("Días");
//            nextRow(false);
//            readHeaderCell("Inicio");
//            readHeaderCell("Fin");
//            readHeaderCell("Tiempo Maximo de Inicio");
            solution.setFechaCorrida(fechainicial);
            List<Day> dayList = new ArrayList<>(60);
            List<TimeGrain> timeGrainList = new ArrayList<>();
            int dayId = 0, timeGrainId = 0;

            List<LocalDate> feriados = new ArrayList<LocalDate>();

            try {
                feriados = getFeriados();
            } catch (IOException e) {
                e.printStackTrace();
            }


            LocalDate fechaActual = fechainicial.minusDays(5);


            LocalTime startTime = LocalTime.of(8,0);
            LocalTime endTime = LocalTime.of(21,0);
            LocalTime lastStartingMinute = LocalTime.of(18,0);
            int startMinuteOfDay = startTime.getHour() * 60 + startTime.getMinute();
            int endMinuteOfDay = endTime.getHour() * 60 + endTime.getMinute();
            int maximumStartingMinuteOfDay = lastStartingMinute.getHour() * 60 + lastStartingMinute.getMinute();


            for(int j=0; j<150; j++){
                boolean isFeriado;
                do {
                    isFeriado = false;
                    for (LocalDate localDate : feriados){
                        if(fechaActual.isEqual(localDate)){
                            isFeriado = true;
                        }
                    }
                    if(fechaActual.getDayOfWeek().getValue() == 6 || fechaActual.getDayOfWeek().getValue() == 7){
                        isFeriado = true;
                    }
                    if(isFeriado){
                        fechaActual = fechaActual.plusDays(1);
                    }
                }while (isFeriado);


                LocalDate diaLeido = fechaActual;


                Day day = new Day();
                day.setLastStartingMinute(maximumStartingMinuteOfDay);
                day.setIdDay(dayId);
                day.setDayOfYear(diaLeido.getDayOfYear());
                day.setDate(diaLeido);
                dayList.add(day);
//                System.out.println("Día " + day.getDateString());

                dayId++;
                fechaActual = fechaActual.plusDays(1);


                if(diaLeido.isAfter(solution.getFechaCorrida().minusDays(1)))
                for (int i = 0; (endMinuteOfDay - startMinuteOfDay) > i * TimeGrain.GRAIN_LENGTH_IN_MINUTES; i++) {
                    int timeGrainStartingMinuteOfDay = i * TimeGrain.GRAIN_LENGTH_IN_MINUTES + startMinuteOfDay;
                    TimeGrain timeGrain = new TimeGrain();
                    timeGrain.setIdTimeGrain(timeGrainId);
                    timeGrain.setGrainIndex(timeGrainId);
                    timeGrain.setDay(day);
                    timeGrain.setStartingMinuteOfDay(timeGrainStartingMinuteOfDay);
                    timeGrainList.add(timeGrain);
                    timeGrainId++;
//                    System.out.println(timeGrain.getDateTimeString() + " " + timeGrain.getIdTimeGrain() + " " + timeGrain.getGrainIndex());
                }
            }
            solution.setDayList(dayList);
            solution.setTimeGrainList(timeGrainList);
        }

        private List<LocalDate> getFeriados() throws IOException {

            int anoActual = fechainicial.getYear();
            List<LocalDate> feriadosList = new ArrayList<LocalDate>();

            for (int j = 0; j<2; j++){
                URL url = null;
                try {
                    url = new URL("http://nolaborables.com.ar/api/v2/feriados/" + anoActual);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection con = null;
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                if(con.getResponseCode() == 404){
                    continue;
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null){
                    response.append(inputLine);
                }
                in.close();



//                 System.out.print(response.toString());
                JSONArray jsonArray = new JSONArray(response.toString());

                for (int i = 0; i< jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    System.out.println(jsonObject.toString());
//                    System.out.println(dia + ' ' + mes);
                    LocalDate feriado = LocalDate.of(anoActual, jsonObject.getInt("mes"), jsonObject.getInt("dia"));
//                    System.out.println(feriado.toString());
                    feriadosList.add(feriado);
                }

                anoActual++;
            }

            return feriadosList;
        }

        private void readRoomList(){
            nextSheet("Salas");
            nextRow(false);
            readHeaderCell("Sala");
            readHeaderCell("Id");
//            readHeaderCell("Ubicacion");
            List<Room> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            while (nextRow()){
                Room room = new Room();
                room.setNombreRoom(nextStringCell().getStringCellValue());
                room.setIdRoom((int)nextNumericCell().getNumericCellValue());
//                room.setUbicacion((int)nextNumericCell().getNumericCellValue());
                roomList.add(room);
//                System.out.println(room.getNombreRoom() + " con id numero " + room.getIdRoom());
            }
            solution.setRoomList(roomList);
//            for(Room room :solution.getRoomList()){
//                System.out.println(room.getNombreRoom() + " " + room.getIdRoom());
//            }

        }

        private void readAudienciaList(){
            nextSheet("Audiencias");
            nextRow(false);
            readHeaderCell("Id");
            readHeaderCell("Duración");
            readHeaderCell("Tipo");
            readHeaderCell("Juez");
            readHeaderCell("Defensor");
            readHeaderCell("Nombre Defensor");
            readHeaderCell("Fiscal");
            readHeaderCell("Querellante");
            readHeaderCell("Asesor");
            readHeaderCell("Riesgosa");
            readHeaderCell("Detenido");
            readHeaderCell("A la Tarde");
            readHeaderCell("Boulogne");
            readHeaderCell("Alma Fuerte");
            readHeaderCell("Fecha de Pedido");
            readHeaderCell("Sala");
            readHeaderCell("Fecha Calendarizado");
            readHeaderCell("Hora de Comienzo");


            List<Audiencia> audienciaList = new ArrayList<>();
            List<AudienciaAssignment> audienciaAssignmentList = new ArrayList<>();
            while(nextRow()){
                int id = (int)nextNumericCell().getNumericCellValue();
                if(!audienciaList.isEmpty() && id == audienciaList.get(audienciaList.size()-1).getIdAudiencia()){
                    Audiencia audiencia = audienciaList.get(audienciaList.size()-1);
                    nextCell();
                    nextCell();
                    XSSFCell juezCell = nextCell();
                    if(juezCell.getCellTypeEnum() != CellType.BLANK){
                        int juezRead = (int)juezCell.getNumericCellValue();
                        containsJuez(solution.getJuezList(), juezRead, audiencia);
                    }
                    nextCell();
                    XSSFCell defensorCell = nextCell();
                    if(defensorCell.getCellTypeEnum() != CellType.BLANK){
                        String defensorRead =  Base64.getEncoder().encodeToString(defensorCell.getStringCellValue().getBytes());
                        containsDefensor(solution.getDefensorList(), defensorRead, audiencia);
                    }
                    XSSFCell fiscalCell = nextCell();
                    if(fiscalCell.getCellTypeEnum() != CellType.BLANK){
                        int fiscalRead = (int)fiscalCell.getNumericCellValue();
                        containsFiscal(solution.getFiscalList(), fiscalRead, audiencia);
                    }
                    XSSFCell querellanteCell = nextCell();
                    if(querellanteCell.getCellTypeEnum() != CellType.BLANK){
                        int querellanteRead = (int)querellanteCell.getNumericCellValue();
                        containsQuerellante(solution.getQuerellanteList(), querellanteRead, audiencia);
                    }
                    XSSFCell asesorCell = nextCell();
                    if(asesorCell.getCellTypeEnum() != CellType.BLANK){
                        int asesorRead = (int)asesorCell.getNumericCellValue();
                        containsAsesor(solution.getAsesorList(), asesorRead, audiencia);
                    }

                    continue;
                }
                Audiencia audiencia = new Audiencia();
                AudienciaAssignment audienciaAssignment = new AudienciaAssignment();
                audiencia.setIdAudiencia(id);
                audienciaAssignment.setId(id);

                if(!readAudienciaDuration(audiencia)){
                    continue;
                }
                int tipoRead = (int)nextNumericCell().getNumericCellValue();
                int juezRead = (int)nextNumericCell().getNumericCellValue();
                int defensorRead = (int)nextNumericCell().getNumericCellValue();
                String defensorNombre =  nextStringCell().getStringCellValue();
                defensorNombre = Base64.getEncoder().encodeToString(defensorNombre.getBytes());
                int fiscalRead = (int)nextNumericCell().getNumericCellValue();

                int querellanteRead = (int)nextCell().getNumericCellValue();
                int asesorRead = (int)nextCell().getNumericCellValue();

                XSSFCell riesgosa = nextCell();
                if(riesgosa.getCellTypeEnum() != CellType.BLANK && riesgosa.getNumericCellValue() == 1){
                    audiencia.setRiesgosa(true);
                }
                XSSFCell detenido = nextCell();
                if(detenido.getCellTypeEnum() != CellType.BLANK && detenido.getNumericCellValue() == 1){
                    audiencia.setDetenido(true);
                }
                XSSFCell tarde = nextCell();
                if(tarde.getCellTypeEnum() != CellType.BLANK && tarde.getNumericCellValue() == 1){
                    audiencia.setaLaTarde(true);
                }
                XSSFCell boulogne = nextCell();
                if(boulogne.getCellTypeEnum() != CellType.BLANK && boulogne.getNumericCellValue() == 1){
                    audiencia.setBoulogne(true);
                }
                XSSFCell almaFuerte = nextCell();
                if(almaFuerte.getCellTypeEnum() != CellType.BLANK && almaFuerte.getNumericCellValue() == 1){
                    audiencia.setAlmaFuerte(true);
                }

                XSSFCell fechaPedidoCell = nextCell();
                if(fechaPedidoCell.getCellTypeEnum() != CellType.BLANK){
                    LocalDate fechaPedido = fechaPedidoCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    audiencia.setFechaPedido(fechaPedido);
                }


                containsTipo(solution.getTipoList(), tipoRead, audiencia);
                containsJuez(solution.getJuezList(), juezRead, audiencia);
                containsDefensor(solution.getDefensorList(), defensorNombre, audiencia);
                containsFiscal(solution.getFiscalList(), fiscalRead, audiencia);
                if(querellanteRead != 0){
                    containsQuerellante(solution.getQuerellanteList(), querellanteRead, audiencia);
                }
                if(asesorRead != 0){
                    containsAsesor(solution.getAsesorList(), asesorRead, audiencia);
                }

                XSSFCell sala = nextCell();
                if(sala.getCellTypeEnum() == CellType.NUMERIC){
                    int salaRead = (int)sala.getNumericCellValue();
                    LocalDate fechaCalendarizado = nextCell().getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    String horaMinutosRead = nextStringCell().getStringCellValue();
                    String[] splitString = horaMinutosRead.split(":");
                    int horaRead = Integer.parseInt(splitString[0]);
                    int minutosRead = Integer.parseInt(splitString[1]);
                    int startingMinute = horaRead * 60 + minutosRead;
                    containsSala(solution.getRoomList(), salaRead, audienciaAssignment);

                    int[] salasExternas = {484, 452, 875, 503, 504, 451, 458, 469, 521, 457, 497, 501, 454, 455, 502, 498, 456, 499, 500, };

                    for (int i : salasExternas){
                        if (audienciaAssignment.getRoom().getIdRoom() == i){
                            audiencia.setExterna(true);
                            break;
                        }
                    }

                    Day dayToUse = null;
                    for (Day day : solution.getDayList()){
                        if (day.toDate().isEqual(fechaCalendarizado)){
                            dayToUse = day;
                            break;
                        }
                    }
                    if (dayToUse == null){
                        System.out.println("No existe el dia" + fechaCalendarizado.toString());
                        continue;

                    }

                    TimeGrain timeGrainToUse = null;
                    for (TimeGrain timeGrain : solution.getTimeGrainList()){
                        if (timeGrain.getDay().equals(dayToUse) && timeGrain.getStartingMinuteOfDay() == startingMinute){
                            timeGrainToUse = timeGrain;
                            break;
                        }
                    }
                    if (timeGrainToUse != null){
                        audienciaAssignment.setStartingTimeGrain(timeGrainToUse);
                    } else{
                        System.out.println("No hay timegrain para el horario " + horaMinutosRead);
                        continue;
                    }
                }

                audienciaList.add(audiencia);
                audienciaAssignment.setAudiencia(audiencia);
                if(sala.getCellTypeEnum() == CellType.NUMERIC){
                    audienciaAssignment.setPinned(true);
                }
                audienciaAssignmentList.add(audienciaAssignment);
//                System.out.println(audiencia.getNumTimeGrains() + " " + audiencia.getDefensor().getNombreDefensor() + audiencia.getFiscal().getNombreFiscal() + audiencia.getJuez().getIdJuez());
            }

            solution.setAudienciaList(audienciaList);
            solution.setAudienciaAssignmentList(audienciaAssignmentList);
            }

        private void containsTipo(final List<Tipo> list, final int numero, Audiencia audiencia){
            boolean existe = list.stream().anyMatch(o -> o.getIdTipo() == numero);
            if(existe){
                for (Tipo tipo : solution.getTipoList()) {
                    if (tipo.getIdTipo() == numero){
                        audiencia.setTipo(tipo);
                        break;
                    }
                }
            } else {
                throw new IllegalStateException(
                        currentPosition() + ": The tipo with id (" + numero
                                + ") does not exist.");
            }
        }

        private void containsJuez(final List<Juez> list, final int numero, Audiencia audiencia){
            boolean existe = list.stream().anyMatch(o -> o.getIdJuez() == numero);
            if(existe){
                for (Juez juez : solution.getJuezList()) {
                    if (juez.getIdJuez() == numero){
                        if(!audiencia.getJuezList().contains(juez)){
                            audiencia.addJuez(juez);
                        }
                        break;
                    }
                }
            } else {
                throw new IllegalStateException(
                        currentPosition() + ": The juez with id (" + numero
                                + ") does not exist.");
            }
        }

        private void containsDefensor(final List<Defensor> list, final String nombre, Audiencia audiencia){
            boolean existe = list.stream().anyMatch(o -> o.getIdDefensor().equals(nombre));
            if(existe){
                for (Defensor defensor : solution.getDefensorList()) {
                    if (defensor.getIdDefensor().equals(nombre)){
                        if(!audiencia.getDefensorList().contains(defensor)){
                            audiencia.addDefensor(defensor);
                        }
                        break;
                    }
                }
            } else {
                throw new IllegalStateException(
                        currentPosition() + ": The defensor with id (" + nombre
                                + ") does not exist.");
            }
        }

        private void containsFiscal(final List<Fiscal> list, final int numero, Audiencia audiencia){
            boolean existe = list.stream().anyMatch(o -> o.getIdFiscal() == numero);
            if(existe){
                for (Fiscal fiscal : solution.getFiscalList()) {
                    if (fiscal.getIdFiscal() == numero){
                        if(!audiencia.getFiscalList().contains(fiscal)){
                            audiencia.addFiscal(fiscal);
                        }
                        break;
                    }
                }
            } else {
                throw new IllegalStateException(
                        currentPosition() + ": The fiscal with id (" + numero
                                + ") does not exist.");
            }
        }

        private void containsQuerellante(final List<Querellante> list, final int numero, Audiencia audiencia){
            boolean existe = list.stream().anyMatch(o -> o.getIdQuerellante() == numero);
            if(existe){
                for (Querellante querellante : solution.getQuerellanteList()) {
                    if (querellante.getIdQuerellante() == numero){
                        if(!audiencia.getQuerellanteList().contains(querellante)){
                            audiencia.addQuerellante(querellante);
                        }
                        break;
                    }
                }
            } else {
                throw new IllegalStateException(
                        currentPosition() + ": The querellante with id (" + numero
                                + ") does not exist.");
            }
        }

        private void containsAsesor(final List<Asesor> list, final int numero, Audiencia audiencia){
            boolean existe = list.stream().anyMatch(o -> o.getIdAsesor() == numero);
            if(existe){
                for (Asesor asesor : solution.getAsesorList()) {
                    if (asesor.getIdAsesor() == numero){
                        if(!audiencia.getAsesorList().contains(asesor)){
                            audiencia.addAsesor(asesor);
                        }
                        break;
                    }
                }
            } else {
                throw new IllegalStateException(
                        currentPosition() + ": The asesor with id (" + numero
                                + ") does not exist.");
            }
        }

        private void containsSala(final List<Room> list, final int numero, AudienciaAssignment audienciaAssignment){
            boolean existe = list.stream().anyMatch(o -> o.getIdRoom() == numero);
            if(existe){
                for (Room room : solution.getRoomList()) {
                    if (room.getIdRoom() == numero){
                        audienciaAssignment.setRoom(room);
                        break;
                    }
                }
            } else {
                throw new IllegalStateException(
                        currentPosition() + ": The room with id (" + numero
                                + ") does not exist.");
            }
        }

        private boolean readAudienciaDuration(Audiencia audiencia) {
            String durationDouble = nextCell().getStringCellValue();
//            System.out.println(durationDouble);
            String[] time = durationDouble.split ( ":" );
            int hour = Integer.parseInt ( time[0].trim() );
            int min = Integer.parseInt ( time[1].trim() );
            int totalMinutes = 60 * hour + min;
            if (totalMinutes <= 0 || totalMinutes != Math.floor(totalMinutes)) {
//                throw new IllegalStateException(
//                        currentPosition() + ": The audiencia with id (" + audiencia.getIdAudiencia()
//                                + ")'s has a duration (" + durationDouble + ") that isn't a strictly positive integer number.");
//                totalMinutes = 15;
                return false;
            }
            if (totalMinutes % TimeGrain.GRAIN_LENGTH_IN_MINUTES != 0) {
//                throw new IllegalStateException(
//                        currentPosition() + ": The audiencia with id (" + audiencia.getIdAudiencia()
//                                + ") has a duration (" + durationDouble + ") that isn't a multiple of "
//                                + TimeGrain.GRAIN_LENGTH_IN_MINUTES + ".");
                return false;
            }
            audiencia.setNumTimeGrains(totalMinutes / TimeGrain.GRAIN_LENGTH_IN_MINUTES);
            return true;
        }

    }

    @Override
    public void write(AudienciaSchedule solution, File outputScheduleFile) {
        try (FileOutputStream out = new FileOutputStream(outputScheduleFile)) {
            Workbook workbook = new AudienciaSchedulingXlsxWriter(solution, this.date).write();
            workbook.write(out);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed writing outputScheduleFile (" + outputScheduleFile
                    + ") for schedule (" + solution + ").", e);
        }
    }

    private class AudienciaSchedulingXlsxWriter extends AbstractXlsxWriter<AudienciaSchedule> {

        private LocalDate startingDate;

        AudienciaSchedulingXlsxWriter(AudienciaSchedule solution, LocalDate startingDate) {
            super(solution, Main.SOLVER_CONFIG);
            this.startingDate = startingDate;

        }


        @Override
        public Workbook write() {
            workbook = new XSSFWorkbook();
            creationHelper = workbook.getCreationHelper();
            createStyles();
            cleanAudienciaSchedule();
            writeRoomsView();
            writeJuezView();
            writeDefensorView();
            writeFiscalView();
            writePrintedFormView();
            writeScoreView(justificationList -> justificationList.stream()
                    .filter(o -> o instanceof AudienciaAssignment).map(o -> ((AudienciaAssignment) o).toString())
                    .collect(joining(", ")));
            return workbook;
        }

        private void cleanAudienciaSchedule(){
            LocalDate endingDate = startingDate.plusDays(31);
            List<TimeGrain> validTimeGrains = solution.getTimeGrainList().stream().filter(timeGrain -> timeGrain.getDate().isBefore(endingDate)).collect(toList());
            List<AudienciaAssignment> validAudienciaAssignments = solution.getAudienciaAssignmentList().stream().filter(audienciaAssignment -> audienciaAssignment.getStartingTimeGrain().getDate().isBefore(endingDate)).collect(toList());
            solution.setTimeGrainList(validTimeGrains);
            solution.setAudienciaAssignmentList(validAudienciaAssignments);
        }

        private void writeRoomsView(){
            nextSheet("Rooms view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Room");
            writeTimeGrainHoursHeaders();
            for (Room room : solution.getRoomList()) {
                nextRow();
                currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
                nextCell().setCellValue(room.getNombreRoom());
                List<AudienciaAssignment> roomAudienciaAssignmentList = solution.getAudienciaAssignmentList().stream().filter(audienciaAssignment -> audienciaAssignment.getRoom() == room).collect(toList());
                writeAudienciaAssignmentList(roomAudienciaAssignmentList);
            }
            autoSizeColumns();
            autoSizeColumnOne();
        }

        private void writeTimeGrainDaysHeaders() {
            Day previousTimeGrainDay = null;
            int mergeStart = -1;

            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                Day timeGrainDay = timeGrain.getDay();
                if (timeGrainDay.equals(previousTimeGrainDay)) {
                    nextHeaderCell("");
                } else {
                    if (previousTimeGrainDay != null) {
                        currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                    }
                    nextHeaderCell(DAY_FORMATTER.format(
                            LocalDate.ofYearDay(timeGrainDay.toDate().getYear(), timeGrainDay.getDayOfYear())));
                    previousTimeGrainDay = timeGrainDay;
                    mergeStart = currentColumnNumber;
                }
            }
            if (previousTimeGrainDay != null) {
                currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
            }
        }

        private void writeTimeGrainHoursHeaders() {
            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                LocalTime startTime = LocalTime.ofSecondOfDay(timeGrain.getStartingMinuteOfDay() * 60);
                nextHeaderCell(TIME_FORMATTER.format(startTime));
            }
        }

        private void writeAudienciaAssignmentList(List<AudienciaAssignment> audienciaAssignmentList) {
            String[] filteredConstraintNames = {
                    AudienciaScheduleConstraintConfiguration.ROOM_CONFLICT,
                    AudienciaScheduleConstraintConfiguration.START_AND_END_ON_SAME_DAY,
                    AudienciaScheduleConstraintConfiguration.DONT_GO_IN_OVERTIME,
                    AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_JUEZ,
                    AudienciaScheduleConstraintConfiguration.DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME,
                    AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_FISCAL,
                    AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_DEFENSOR,
//                    AudienciaScheduleConstraintConfiguration.DO_NOT_USE_BREAKS,
//                    AudienciaScheduleConstraintConfiguration.RESPECT_LOCATIONS,
                    AudienciaScheduleConstraintConfiguration.RESPECT_MINIMUM_STARTING_TIME,
                    AudienciaScheduleConstraintConfiguration.RESPECT_MAXIMUM_STARTING_TIME,
//                    AudienciaScheduleConstraintConfiguration.DONT_CONFLICT_JUEZ_LOCATION,
//                    AudienciaScheduleConstraintConfiguration.DONT_CONFLICT_DEFENSOR_LOCATION,
//                    AudienciaScheduleConstraintConfiguration.DONT_CONFLICT_FISCAL_LOCATION,
                    AudienciaScheduleConstraintConfiguration.DONT_START_AFTER_MAXIMUM_STARTING_MINUTE,
                    AudienciaScheduleConstraintConfiguration.DONT_CONFLICT_JUEZ_AND_TIMEGRAIN,
                    AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_QUERELLANTE,
                    AudienciaScheduleConstraintConfiguration.DO_NOT_CONFLICT_ASESOR,
                    AudienciaScheduleConstraintConfiguration.APPEALS_IN_AFTERNOON,
                    AudienciaScheduleConstraintConfiguration.TIME_FOR_EXTERNAL_DEFENSOR,
                    AudienciaScheduleConstraintConfiguration.HEARINGS_IN_BOULOGNE,
                    AudienciaScheduleConstraintConfiguration.HEARINGS_IN_ALMA_FUERTE,
                    AudienciaScheduleConstraintConfiguration.HEARTINGS_NOT_EJEC,

                    AudienciaScheduleConstraintConfiguration.ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS,
                    AudienciaScheduleConstraintConfiguration.ONE_TIMEGRAIN_JUEZ,
                    AudienciaScheduleConstraintConfiguration.ONE_TIMEGRAIN_DEFENSOR,
                    AudienciaScheduleConstraintConfiguration.ONE_TIMEGRAIN_FISCAL,
                    AudienciaScheduleConstraintConfiguration.DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE,
                    AudienciaScheduleConstraintConfiguration.ONE_TIMEGRAIN_QUERELLANTE,
                    AudienciaScheduleConstraintConfiguration.ONE_TIMEGRAIN_ASESOR,
                    AudienciaScheduleConstraintConfiguration.PROBLEMATIC_HEARINGS_FOR_LAST_ROOM,
                    AudienciaScheduleConstraintConfiguration.PROBLEMATIC_HEARINGS_FOR_LAST_JUEZ,
                    AudienciaScheduleConstraintConfiguration.PROBLEMATIC_HEARINGS_FOR_LAST_FISCAL,
                    AudienciaScheduleConstraintConfiguration.PROBLEMATIC_HEARINGS_FOR_LAST_ASESOR,
                    AudienciaScheduleConstraintConfiguration.PRIORITIZE_DETAINEES,
                    AudienciaScheduleConstraintConfiguration.MAXIMUM_WORK_TIME_JUEZ,
//                    AudienciaScheduleConstraintConfiguration.DISTRIBUTE_WORKLOAD_FAIRLY,
            };
            int mergeStart = -1;
            int previousAudienciaRemainingTimeGrains = 0;
            boolean mergingPreviousAudienciaList = false;

            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                List<AudienciaAssignment> timeGrainAudienciaAssignmentList = audienciaAssignmentList.stream()
                        .filter(audienciaAssignment -> audienciaAssignment.getStartingTimeGrain() == timeGrain)
                        .collect(toList());
                if (timeGrainAudienciaAssignmentList.isEmpty() && mergingPreviousAudienciaList && previousAudienciaRemainingTimeGrains > 0) {
                    previousAudienciaRemainingTimeGrains--;
                    nextCell();
                } else {
                    if (mergingPreviousAudienciaList && mergeStart < currentColumnNumber) {
                        currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                    }
                    nextAudienciaAssignmentListCell(timeGrainAudienciaAssignmentList, audienciaAssignment -> audienciaAssignment.getAudiencia().getIdAudiencia() + "\n  ", Arrays.asList(filteredConstraintNames));
                    mergingPreviousAudienciaList = !timeGrainAudienciaAssignmentList.isEmpty();
                    mergeStart = currentColumnNumber;
                    previousAudienciaRemainingTimeGrains = getLongestDurationInGrains(timeGrainAudienciaAssignmentList) - 1;
                }
            }

            if (mergingPreviousAudienciaList && mergeStart < currentColumnNumber) {
                currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
            }
        }

        private int getLongestDurationInGrains(List<AudienciaAssignment> audienciaAssignmentList) {
            int longestDurationInGrains = 1;
            for (AudienciaAssignment audienciaAssignment : audienciaAssignmentList) {
                if (audienciaAssignment.getAudiencia().getNumTimeGrains() > longestDurationInGrains) {
                    longestDurationInGrains = audienciaAssignment.getAudiencia().getNumTimeGrains();
                }
            }
            return longestDurationInGrains;
        }

        void nextAudienciaAssignmentListCell(List<AudienciaAssignment> audienciaAssignmentList, Function<AudienciaAssignment, String> stringFunction, List<String> filteredConstraintNames) {
            if (audienciaAssignmentList == null) {
                audienciaAssignmentList = Collections.emptyList();
            }
            HardMediumSoftScore score = audienciaAssignmentList.stream()
                    .map(indictmentMap::get).filter(Objects::nonNull)
                    .flatMap(indictment -> indictment.getConstraintMatchSet().stream())
                    // Filter out filtered constraints
                    .filter(constraintMatch -> filteredConstraintNames == null
                            || filteredConstraintNames.contains(constraintMatch.getConstraintName()))
                    .map(constraintMatch -> (HardMediumSoftScore) constraintMatch.getScore())
                    // Filter out positive constraints
                    .filter(indictmentScore -> !(indictmentScore.getHardScore() >= 0 && indictmentScore.getSoftScore() >= 0))
                    .reduce(Score::add).orElse(HardMediumSoftScore.ZERO);

            boolean pinned = false;
            boolean externa = false;
            for (AudienciaAssignment audienciaAssignment : audienciaAssignmentList){
                if (audienciaAssignment.isPinned()){
                    pinned = true;
                }
                if (audienciaAssignment.getAudiencia().isExterna()){
                    externa = true;
                }
            }

            XSSFCell cell = getXSSFCellOfScore(score, pinned, externa);

            if (!audienciaAssignmentList.isEmpty()) {
                ClientAnchor anchor = creationHelper.createClientAnchor();
                anchor.setCol1(cell.getColumnIndex());
                anchor.setCol2(cell.getColumnIndex() + 4);
                anchor.setRow1(currentRow.getRowNum());
                anchor.setRow2(currentRow.getRowNum() + 4);
                Comment comment = currentDrawing.createCellComment(anchor);
                String commentString = getAudienciaAssignmentListString(audienciaAssignmentList);
                comment.setString(creationHelper.createRichTextString(commentString));
                cell.setCellComment(comment);
            }
            cell.setCellValue(audienciaAssignmentList.stream().map(stringFunction).collect(joining("\n")));
            currentRow.setHeightInPoints(Math.max(currentRow.getHeightInPoints(), audienciaAssignmentList.size() * currentSheet.getDefaultRowHeightInPoints()));
        }

        private XSSFCell getXSSFCellOfScore(HardMediumSoftScore score, boolean pinned, boolean externa) {
            XSSFCell cell;
            if (externa){
                cell = nextCell(republishedStyle);
            } else if (pinned){
                cell = nextCell(pinnedStyle);
            } else if (!score.isFeasible()) {
                cell = nextCell(hardPenaltyStyle);
            } else if (score.getMediumScore() < 0) {
                cell = nextCell(mediumPenaltyStyle);
            } else if (score.getSoftScore() < 0) {
                cell = nextCell(softPenaltyStyle);
            } else {
                cell = nextCell(wrappedStyle);
            }
            return cell;
        }

        private String getAudienciaAssignmentListString(List<AudienciaAssignment> audienciaAssignmentList) {
            StringBuilder commentString = new StringBuilder(audienciaAssignmentList.size() * 200);
            for (AudienciaAssignment audienciaAssignment : audienciaAssignmentList) {
                commentString.append("Date and Time: ").append(audienciaAssignment.getStartingTimeGrain().getDateTimeString()).append("\n")
                        .append("Duration: ").append(audienciaAssignment.getAudiencia().getNumTimeGrains() * TimeGrain.GRAIN_LENGTH_IN_MINUTES).append(" minutes.\n")
                        .append("Room: ").append(audienciaAssignment.getRoom().getNombreRoom()).append("\n");

                Indictment indictment = indictmentMap.get(audienciaAssignment);
                if (indictment != null) {
                    commentString.append("\n").append(indictment.getScore().toShortString())
                            .append(" total");
                    Set<ConstraintMatch> constraintMatchSet = indictment.getConstraintMatchSet();
                    List<String> constraintNameList = constraintMatchSet.stream()
                            .map(ConstraintMatch::getConstraintName).distinct().collect(toList());
                    for (String constraintName : constraintNameList) {
                        List<ConstraintMatch> filteredConstraintMatchList = constraintMatchSet.stream()
                                .filter(constraintMatch -> constraintMatch.getConstraintName().equals(constraintName))
                                .collect(toList());
                        HardMediumSoftScore sum = filteredConstraintMatchList.stream()
                                .map(constraintMatch -> (HardMediumSoftScore) constraintMatch.getScore())
                                .reduce(HardMediumSoftScore::add)
                                .orElse(HardMediumSoftScore.ZERO);
                        String justificationTalkCodes = filteredConstraintMatchList.stream()
                                .flatMap(constraintMatch -> constraintMatch.getJustificationList().stream())
                                .filter(justification -> justification instanceof AudienciaAssignment && justification != audienciaAssignment)
                                .distinct().map(o -> Long.toString(((AudienciaAssignment) o).getAudiencia().getIdAudiencia()))
                                .collect(joining(", "));
                        commentString.append("\n    ").append(sum.toShortString())
                                .append(" for ").append(filteredConstraintMatchList.size())
                                .append(" ").append(constraintName).append("s")
                                .append("\n        ").append(justificationTalkCodes);
                    }
                }
                commentString.append("\n\n");
            }
            return commentString.toString();
        }

        private void writeJuezView(){
            nextSheet("Juez view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Juez");
            writeTimeGrainHoursHeaders();
            for(Juez juez : solution.getJuezList()){
                boolean tieneAudiencias = false;
                for (Audiencia audiencia : solution.getAudienciaList()){
                    for(Juez juezaudiencia : audiencia.getJuezList()){
                        if (juezaudiencia.equals(juez)){
                            tieneAudiencias = true;
                            break;
                        }
                    }

                    if (tieneAudiencias){
                        break;
                    }
                }
                if(tieneAudiencias){
                    writeJuezAudienciaList(juez);
                }
            }
            autoSizeColumns();
            autoSizeColumnOne();
        }

        private void writeJuezAudienciaList(Juez juez){
            nextRow();
            currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
            nextHeaderCell(juez.getNombre());

            List<Audiencia> juezAudienciaList;

            juezAudienciaList = solution.getAudienciaList().stream().filter(audiencia -> audiencia.getJuezList().contains(juez)).collect(toList());

            List<AudienciaAssignment> juezAudienciaAssignmentList = solution.getAudienciaAssignmentList().stream()
                    .filter(audienciaAssignment -> juezAudienciaList.contains(audienciaAssignment.getAudiencia()))
                    .collect(toList());
            writeAudienciaAssignmentList(juezAudienciaAssignmentList);
        }


        private void writeFiscalView(){
            nextSheet("Fiscal view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Fiscal");
            writeTimeGrainHoursHeaders();
            for(Fiscal fiscal : solution.getFiscalList()){
                boolean tieneAudiencias = false;
                for (Audiencia audiencia : solution.getAudienciaList()){
                    for(Fiscal fiscalAudiencia : audiencia.getFiscalList()){
                        if (fiscalAudiencia.equals(fiscal)){
                            tieneAudiencias = true;
                            break;
                        }
                    }
                    if(tieneAudiencias){
                        break;
                    }
                }
                if(tieneAudiencias){
                    writeFiscalAudienciaList(fiscal);
                }
            }
            autoSizeColumns();
            autoSizeColumnOne();
        }

        private void writeFiscalAudienciaList(Fiscal fiscal){
            nextRow();
            currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
            nextHeaderCell(fiscal.getNombreFiscal());

            List<Audiencia> fiscalAudienciaList;

            fiscalAudienciaList = solution.getAudienciaList().stream().filter(audiencia -> audiencia.getFiscalList().contains(fiscal)).collect(toList());

            List<AudienciaAssignment> fiscalAudienciaAssignmentList = solution.getAudienciaAssignmentList().stream()
                    .filter(audienciaAssignment -> fiscalAudienciaList.contains(audienciaAssignment.getAudiencia()))
                    .collect(toList());
            writeAudienciaAssignmentList(fiscalAudienciaAssignmentList);
        }

        private void writeDefensorView(){
            nextSheet("Defensor view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainDaysHeaders();
            nextRow();
            nextHeaderCell("Defensor");
            writeTimeGrainHoursHeaders();
            for(Defensor defensor : solution.getDefensorList()){
                boolean tieneAudiencias = false;
                for (Audiencia audiencia : solution.getAudienciaList()){
                    for (Defensor defensorAudiencia : audiencia.getDefensorList()){
                        if (defensorAudiencia.equals(defensor)){
                            tieneAudiencias = true;
                            break;
                        }
                    }
                    if (tieneAudiencias){
                        break;
                    }
                }
                if(tieneAudiencias){
                    writeDefensorAudienciaList(defensor);
                }
            }
            autoSizeColumns();
            autoSizeColumnOne();
        }

        private void writeDefensorAudienciaList(Defensor defensor){
            nextRow();
            currentRow.setHeightInPoints(2 * currentSheet.getDefaultRowHeightInPoints());
            nextHeaderCell(defensor.getNombreDefensor());

            List<Audiencia> defensorAudienciaList;

            defensorAudienciaList = solution.getAudienciaList().stream().filter(audiencia -> audiencia.getDefensorList().contains(defensor)).collect(toList());

            List<AudienciaAssignment> defensorAudienciaAssignmentList = solution.getAudienciaAssignmentList().stream()
                    .filter(audienciaAssignment -> defensorAudienciaList.contains(audienciaAssignment.getAudiencia()))
                    .collect(toList());
            writeAudienciaAssignmentList(defensorAudienciaAssignmentList);
        }

        private void writePrintedFormView() {
            nextSheet("Printed form view", 1, 1, true);
            nextRow();
            nextHeaderCell("");
            writeTimeGrainsHoursVertically(30);
            currentColumnNumber = 0;
            for (Room room : solution.getRoomList()) {
                List<AudienciaAssignment> roomAudienciaAssignmentList = solution.getAudienciaAssignmentList().stream()
                        .filter(audienciaAssignment -> audienciaAssignment.getRoom() == room)
                        .collect(toList());
                if (roomAudienciaAssignmentList.isEmpty()) {
                    continue;
                }

                currentColumnNumber++;
                currentRowNumber = -1;
                nextHeaderCellVertically(room.getNombreRoom());
                writeAudienciaAssignmentListVertically(roomAudienciaAssignmentList);
            }
            setSizeColumnsWithHeader(6000);
        }

        private void writeAudienciaAssignmentListVertically(List<AudienciaAssignment> roomAudienciaAssignmentList) {
            int mergeStart = -1;
            int previousAudienciaRemainingTimeGrains = 0;
            boolean mergingPreviousTimeGrain = false;
            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                List<AudienciaAssignment> audienciaAssignmentList = roomAudienciaAssignmentList.stream()
                        .filter(meetingAssignment -> meetingAssignment.getStartingTimeGrain() == timeGrain)
                        .collect(toList());
                if (audienciaAssignmentList.isEmpty() && mergingPreviousTimeGrain && previousAudienciaRemainingTimeGrains > 0) {
                    previousAudienciaRemainingTimeGrains--;
                    nextCellVertically();
                } else {
                    if (mergingPreviousTimeGrain && mergeStart < currentRowNumber) {
                        currentSheet.addMergedRegion(new CellRangeAddress(mergeStart, currentRowNumber, currentColumnNumber, currentColumnNumber));
                    }

                    StringBuilder meetingInfo = new StringBuilder();
                    for (AudienciaAssignment audienciaAssignment : audienciaAssignmentList) {
                        String startTimeString = getTimeString(audienciaAssignment.getStartingTimeGrain().getStartingMinuteOfDay());
                        int lastTimeGrainIndex = audienciaAssignment.getLastTimeGrainIndex() <= solution.getTimeGrainList().size() - 1 ?
                                audienciaAssignment.getLastTimeGrainIndex() : solution.getTimeGrainList().size() - 1;
                        String endTimeString = getTimeString(solution.getTimeGrainList().get(lastTimeGrainIndex).getStartingMinuteOfDay()
                                + TimeGrain.GRAIN_LENGTH_IN_MINUTES);
                        meetingInfo.append(StringUtils.abbreviate(String.valueOf(audienciaAssignment.getAudiencia().getIdAudiencia()), 150)).append("\n  ")
                                .append(startTimeString).append(" - ").append(endTimeString)
                                .append(" (").append(audienciaAssignment.getAudiencia().getNumTimeGrains() * TimeGrain.GRAIN_LENGTH_IN_MINUTES).append(" mins)");
                    }
                    nextCellVertically().setCellValue(meetingInfo.toString());

                    previousAudienciaRemainingTimeGrains = getLongestDurationInGrains(audienciaAssignmentList) - 1;
                    mergingPreviousTimeGrain = previousAudienciaRemainingTimeGrains > 0;
                    mergeStart = currentRowNumber;
                }
            }
            if (mergeStart < currentRowNumber) {
                currentSheet.addMergedRegion(new CellRangeAddress(mergeStart, currentRowNumber, currentColumnNumber, currentColumnNumber));
            }
        }

        private String getTimeString(int minuteOfDay) {
            return TIME_FORMATTER.format(LocalTime.ofSecondOfDay(minuteOfDay * 60));
        }

        private void writeTimeGrainsHoursVertically(int minimumInterval) {
            int mergeStart = -1;
            for (TimeGrain timeGrain : solution.getTimeGrainList()) {
                if (timeGrain.getGrainIndex() % (Math.ceil(minimumInterval * 1.0 / TimeGrain.GRAIN_LENGTH_IN_MINUTES)) == 0) {
                    if (mergeStart > 0) {
                        currentSheet.addMergedRegion(new CellRangeAddress(mergeStart, currentRowNumber, 0, 0));
                    }
                    nextRow();
                    nextCell().setCellValue(timeGrain.getDateTimeString());
                    mergeStart = currentRowNumber;
                } else {
                    nextRow();
                }
            }
            if (mergeStart < currentRowNumber) {
                currentSheet.addMergedRegion(new CellRangeAddress(mergeStart, currentRowNumber, 0, 0));
            }
        }

        private void autoSizeColumns(){
            for (int i = 1; i < headerCellCount; i++) {
                currentSheet.setColumnWidth(i, 2000);
            }
        }

        private void autoSizeColumnOne(){
            currentSheet.setColumnWidth(0, 6000);
        }

    }

}
