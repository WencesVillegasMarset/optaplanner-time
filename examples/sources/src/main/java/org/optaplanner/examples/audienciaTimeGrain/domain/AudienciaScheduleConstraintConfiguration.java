package org.optaplanner.examples.audienciaTimeGrain.domain;

import org.apache.commons.math3.fitting.HarmonicCurveFitter;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.audienciaTimeGrain.helper.ScoreAdapter;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.audienciaTimeGrain.solver")

public class AudienciaScheduleConstraintConfiguration {

    /* Declarations */

    //HARD - Previene el conflicto de uso de una Room mas de una vez al mismo tiempo
    public static final String ROOM_CONFLICT = "Room conflict";

    //SOFT - Inserta un TimeGrain preventivo entre cada Audiencia
    public static final String ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS = "One TimeGrain break between two consecutive meetings";

    //HARD - Las audiencias deben empezar y terminar el mismo día
    public static final String START_AND_END_ON_SAME_DAY = "Start and end on same day";

    //HARD - Asegura que el TimeGrain en el que termina la Audiencia exista, es decir, que no pase el horario estipulado
    public static final String DONT_GO_IN_OVERTIME = "Don't go in overtime";

    //SOFT - Planificar las Audiencias para tiempos mas cercanos si es posible
    public static final String DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE = "Do all meetings as soon as possible";

    //HARD - Que el Juez no este en más de una Audiencia al mismo tiempo
    public static final String DO_NOT_CONFLICT_JUEZ = "Do not conflict Juez";

    //HARD - Que no se utilice una Room en un tiempo que se determinó que no está disponible
    public static final String DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME = "Do not use room in prohibited time";

    //HARD - Que el Fiscal no este en más de una Audiencia al mismo tiempo
    public static final String DO_NOT_CONFLICT_FISCAL = "Do not conflict Fiscal";

    //HARD - Que el Defensor no este en más de una Audiencia al mismo tiempo
    public static final String DO_NOT_CONFLICT_DEFENSOR = "Do not conflict Defensor";

    //HARD - Que se respeten los breaks durante un mismo dia
//    public static final String DO_NOT_USE_BREAKS = "Avoid Mid-break";

    //SOFT - Que se asignen similares cantidades de audiencias por Room
//    public static final String DISTRIBUTE_WORKLOAD_FAIRLY = "Distribute workload fairly";

    //HARD - Que se resteten las ubicaciones
//    public static final String RESPECT_LOCATIONS = "Respect Locations";

    //HARD - Que se respeten los tiempos minimos de realizacion
    public static final String RESPECT_MINIMUM_STARTING_TIME = "Respect Minimum Starting Time";

    //HARD - Que se respeten los tiempos maximos de realizacion
    public static final String RESPECT_MAXIMUM_STARTING_TIME = "Respect Maximum Starting Time";

    //SOFT - One TimeGrain between two consecutive audiencias with the same Juez
    public static final String ONE_TIMEGRAIN_JUEZ = "One TimeGrain Juez";

    //SOFT - One TimeGrain between two consecutive audiencias with the same Defensor
    public static final String ONE_TIMEGRAIN_DEFENSOR = "One TimeGrain Defensor";

    //SOFT - One TimeGrain between two consecutive audiencias with the same Fiscal
    public static final String ONE_TIMEGRAIN_FISCAL = "One TimeGrain Fiscal";

    //HARD - Que un Juez no tenga audiencias en distintas ubicaciones el mismo dia
//    public static final String DONT_CONFLICT_JUEZ_LOCATION = "Dont conflict Juez with locations";

    //HARD - Que un Defensor no tenga audiencias en distintas ubicaciones el mismo dia
//    public static final String DONT_CONFLICT_DEFENSOR_LOCATION = "Dont conflict Defensor with locations";

    //HARD - Que un Fiscal no tenga audiencias en distintas ubicaciones el mismo dia
//    public static final String DONT_CONFLICT_FISCAL_LOCATION = "Dont conflict Fiscal with locations";

    //HARD - No permite que una audiencia comience después del horario permitido de comienzo
    public static final String DONT_START_AFTER_MAXIMUM_STARTING_MINUTE = "Don't start after maximum starting time of the day";

    //HARD - No permite que se asigne una audiencia en un TimeGrain que el Juez no este disponible
    public static final String DONT_CONFLICT_JUEZ_AND_TIMEGRAIN = "Do not conflict Juez with TimeGrain";

    //HARD - Que el Querellante no este en más de una Audiencia al mismo tiempo
    public static final String DO_NOT_CONFLICT_QUERELLANTE = "Do not conflict Querellante";

    //HARD - Que el Asesor no este en más de una Audiencia al mismo tiempo
    public static final String DO_NOT_CONFLICT_ASESOR = "Do not conflict Asesor";

    //SOFT - One TimeGrain between two consecutive audiencias with the same Querellante
    public static final String ONE_TIMEGRAIN_QUERELLANTE = "One TimeGrain Querellante";

    //SOFT - One TimeGrain between two consecutive audiencias with the same Asesor
    public static final String ONE_TIMEGRAIN_ASESOR = "One TimeGrain Asesor";

    //HARD - Appeals need to be appointed in afternoon time
    public static final String APPEALS_IN_AFTERNOON = "Appeals in the afternoon";

    //SOFT - Las audiencias que tienen detenidos deben ser priorizadas temporalmente
    public static final String PRIORITIZE_DETAINEES = "Prioritize Detainees";

    //SOFT - Problematic audiencias need to be the last audiencia in the room
    public static final String PROBLEMATIC_HEARINGS_FOR_LAST_ROOM = "Problematic Hearings for last - Room";

    //SOFT - Problematic audiencias need to be the last audiencia in the room
    public static final String PROBLEMATIC_HEARINGS_FOR_LAST_JUEZ = "Problematic Hearings for last - Juez";

    //SOFT - Problematic audiencias need to be the last audiencia in the room
    public static final String PROBLEMATIC_HEARINGS_FOR_LAST_FISCAL = "Problematic Hearings for last - Fiscal";

    //SOFT - Problematic audiencias need to be the last audiencia in the room
    public static final String PROBLEMATIC_HEARINGS_FOR_LAST_ASESOR = "Problematic Hearings for last - Asesor";

    //SOFT - Defensor that is working in other location needs time to commute
    public static final String TIME_FOR_EXTERNAL_DEFENSOR = "Time for External Defensor";

    //HARD - Audiencias de Boulogne Sur Mer
    public static final String HEARINGS_IN_BOULOGNE = "Rooms in Boulogne";

    //HARD - Audiencias de Alma Fuerte
    public static final String HEARINGS_IN_ALMA_FUERTE = "Rooms in Alma Fuerte";

    //HARD - Audiencias que no son de Boulogne o Alma Fuerte no se calendarizan en sus salas
    public static final String HEARTINGS_NOT_EJEC = "Rooms not in Boulogne or Alma Fuerte";

    //SOFT - Jueces can't work for more than 6 hours a day (72 timegrains)
    public static final String MAXIMUM_WORK_TIME_JUEZ = "Maximum work time Juez";

    /* SCHEDULING */
    @ConstraintWeight(ROOM_CONFLICT)
    private BendableScore roomConflict = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(DONT_GO_IN_OVERTIME)
    private BendableScore contGoInOvertime = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(START_AND_END_ON_SAME_DAY)
    private BendableScore startAndEndOnSameDay = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(DO_NOT_CONFLICT_JUEZ)
    private BendableScore dontConflictJuez = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(DO_NOT_CONFLICT_FISCAL)
    private BendableScore dontConflictFiscal = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(DO_NOT_CONFLICT_DEFENSOR)
    private BendableScore dontConflictDefensor = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(DO_NOT_CONFLICT_QUERELLANTE)
    private BendableScore dontConflictQuerellante = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(DO_NOT_CONFLICT_ASESOR)
    private BendableScore dontConflictAsesor = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(DO_NOT_USE_ROOM_IN_PRHOHIBITED_TIME)
    private BendableScore dontConflictRoomTime = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS)
    private BendableScore oneTimeGrainBreakBetweenTwoConsecutiveMeetings = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(ONE_TIMEGRAIN_JUEZ)
    private BendableScore oneTimeGrainJuez = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(ONE_TIMEGRAIN_DEFENSOR)
    private BendableScore oneTimeGrainDefensor = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(ONE_TIMEGRAIN_FISCAL)
    private BendableScore oneTimeGrainFiscal = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(ONE_TIMEGRAIN_QUERELLANTE)
    private BendableScore oneTimeGrainQuerellante = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(ONE_TIMEGRAIN_ASESOR)
    private BendableScore oneTimeGrainAsesor = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(TIME_FOR_EXTERNAL_DEFENSOR)
    private BendableScore timeExternalDefensor = BendableScore.ofHard(2, 3, 0, 1);
    @ConstraintWeight(DONT_START_AFTER_MAXIMUM_STARTING_MINUTE)
    private BendableScore dontStartAfterMaximumStartingMinute = BendableScore.ofHard(2, 3, 0, 1);


    /* LEGAL */
    @ConstraintWeight(RESPECT_MINIMUM_STARTING_TIME)
    private BendableScore respectMinimumStartingTime = BendableScore.ofHard(2, 3, 1, 1);
    @ConstraintWeight(RESPECT_MAXIMUM_STARTING_TIME)
    private BendableScore respectMaximumStartingTime = BendableScore.ofHard(2, 3, 1, 1);
    @ConstraintWeight(DONT_CONFLICT_JUEZ_AND_TIMEGRAIN)
    private BendableScore dontConflictJuezAndTimeGrain = BendableScore.ofHard(2, 3, 1, 1);
    @ConstraintWeight(APPEALS_IN_AFTERNOON)
    private BendableScore appealsInAfternoon = BendableScore.ofHard(2, 3, 1, 1);
    @ConstraintWeight(HEARINGS_IN_BOULOGNE)
    private BendableScore hearingsInBoulogne = BendableScore.ofHard(2, 3, 1, 1);
    @ConstraintWeight(HEARINGS_IN_ALMA_FUERTE)
    private BendableScore hearingsInAlmaFuerte = BendableScore.ofHard(2, 3, 1, 1);
    @ConstraintWeight(HEARTINGS_NOT_EJEC)
    private BendableScore hearingsNotEjec = BendableScore.ofHard(2, 3, 1, 1);


    /* OGAP */
    @ConstraintWeight(PRIORITIZE_DETAINEES)
    private BendableScore prioritizeDetainees = BendableScore.ofSoft(2, 3, 0, 2);
    @ConstraintWeight(PROBLEMATIC_HEARINGS_FOR_LAST_ROOM)
    private BendableScore problematicHearingsForLastRoom = BendableScore.ofSoft(2, 3, 0, 2);
    @ConstraintWeight(PROBLEMATIC_HEARINGS_FOR_LAST_JUEZ)
    private BendableScore problematicHearingsForLastJuez = BendableScore.ofSoft(2, 3, 0, 2);
    @ConstraintWeight(PROBLEMATIC_HEARINGS_FOR_LAST_FISCAL)
    private BendableScore problematicHearingsForLastFiscal = BendableScore.ofSoft(2, 3, 0, 2);
    @ConstraintWeight(PROBLEMATIC_HEARINGS_FOR_LAST_ASESOR)
    private BendableScore problematicHearingsForLastAsesor = BendableScore.ofSoft(2, 3, 0, 2);
    @ConstraintWeight(MAXIMUM_WORK_TIME_JUEZ)
    private BendableScore maximumWorkTimeJuez = BendableScore.ofSoft(2, 3, 0, 1);

    /* MAGISTRADOS */


    /* SOFT */
    @ConstraintWeight(DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE)
    private BendableScore doAllMeetingsAsSoonAsPossible = BendableScore.ofSoft(2, 3, 2, 1);



    /* Constructor */

    public AudienciaScheduleConstraintConfiguration() {
    }

    /* Setters y Getters */

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getRoomConflict() {
        return roomConflict;
    }

    public void setRoomConflict(BendableScore roomConflict) {
        this.roomConflict = roomConflict;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getContGoInOvertime() {
        return contGoInOvertime;
    }

    public void setContGoInOvertime(BendableScore contGoInOvertime) {
        this.contGoInOvertime = contGoInOvertime;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getStartAndEndOnSameDay() {
        return startAndEndOnSameDay;
    }

    public void setStartAndEndOnSameDay(BendableScore startAndEndOnSameDay) {
        this.startAndEndOnSameDay = startAndEndOnSameDay;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getDontConflictJuez() {
        return dontConflictJuez;
    }

    public void setDontConflictJuez(BendableScore dontConflictJuez) {
        this.dontConflictJuez = dontConflictJuez;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getDontConflictFiscal() {
        return dontConflictFiscal;
    }

    public void setDontConflictFiscal(BendableScore dontConflictFiscal) {
        this.dontConflictFiscal = dontConflictFiscal;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getDontConflictDefensor() {
        return dontConflictDefensor;
    }

    public void setDontConflictDefensor(BendableScore dontConflictDefensor) {
        this.dontConflictDefensor = dontConflictDefensor;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getDontConflictQuerellante() {
        return dontConflictQuerellante;
    }

    public void setDontConflictQuerellante(BendableScore dontConflictQuerellante) {
        this.dontConflictQuerellante = dontConflictQuerellante;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getDontConflictAsesor() {
        return dontConflictAsesor;
    }

    public void setDontConflictAsesor(BendableScore dontConflictAsesor) {
        this.dontConflictAsesor = dontConflictAsesor;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getDontConflictRoomTime() {
        return dontConflictRoomTime;
    }

    public void setDontConflictRoomTime(BendableScore dontConflictRoomTime) {
        this.dontConflictRoomTime = dontConflictRoomTime;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getOneTimeGrainBreakBetweenTwoConsecutiveMeetings() {
        return oneTimeGrainBreakBetweenTwoConsecutiveMeetings;
    }

    public void setOneTimeGrainBreakBetweenTwoConsecutiveMeetings(BendableScore oneTimeGrainBreakBetweenTwoConsecutiveMeetings) {
        this.oneTimeGrainBreakBetweenTwoConsecutiveMeetings = oneTimeGrainBreakBetweenTwoConsecutiveMeetings;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getOneTimeGrainJuez() {
        return oneTimeGrainJuez;
    }

    public void setOneTimeGrainJuez(BendableScore oneTimeGrainJuez) {
        this.oneTimeGrainJuez = oneTimeGrainJuez;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getOneTimeGrainDefensor() {
        return oneTimeGrainDefensor;
    }

    public void setOneTimeGrainDefensor(BendableScore oneTimeGrainDefensor) {
        this.oneTimeGrainDefensor = oneTimeGrainDefensor;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getOneTimeGrainFiscal() {
        return oneTimeGrainFiscal;
    }

    public void setOneTimeGrainFiscal(BendableScore oneTimeGrainFiscal) {
        this.oneTimeGrainFiscal = oneTimeGrainFiscal;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getOneTimeGrainQuerellante() {
        return oneTimeGrainQuerellante;
    }

    public void setOneTimeGrainQuerellante(BendableScore oneTimeGrainQuerellante) {
        this.oneTimeGrainQuerellante = oneTimeGrainQuerellante;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getOneTimeGrainAsesor() {
        return oneTimeGrainAsesor;
    }

    public void setOneTimeGrainAsesor(BendableScore oneTimeGrainAsesor) {
        this.oneTimeGrainAsesor = oneTimeGrainAsesor;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getTimeExternalDefensor() {
        return timeExternalDefensor;
    }

    public void setTimeExternalDefensor(BendableScore timeExternalDefensor) {
        this.timeExternalDefensor = timeExternalDefensor;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getDontStartAfterMaximumStartingMinute() {
        return dontStartAfterMaximumStartingMinute;
    }

    public void setDontStartAfterMaximumStartingMinute(BendableScore dontStartAfterMaximumStartingMinute) {
        this.dontStartAfterMaximumStartingMinute = dontStartAfterMaximumStartingMinute;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getRespectMinimumStartingTime() {
        return respectMinimumStartingTime;
    }

    public void setRespectMinimumStartingTime(BendableScore respectMinimumStartingTime) {
        this.respectMinimumStartingTime = respectMinimumStartingTime;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getRespectMaximumStartingTime() {
        return respectMaximumStartingTime;
    }

    public void setRespectMaximumStartingTime(BendableScore respectMaximumStartingTime) {
        this.respectMaximumStartingTime = respectMaximumStartingTime;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getDontConflictJuezAndTimeGrain() {
        return dontConflictJuezAndTimeGrain;
    }

    public void setDontConflictJuezAndTimeGrain(BendableScore dontConflictJuezAndTimeGrain) {
        this.dontConflictJuezAndTimeGrain = dontConflictJuezAndTimeGrain;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getAppealsInAfternoon() {
        return appealsInAfternoon;
    }

    public void setAppealsInAfternoon(BendableScore appealsInAfternoon) {
        this.appealsInAfternoon = appealsInAfternoon;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getHearingsInBoulogne() {
        return hearingsInBoulogne;
    }

    public void setHearingsInBoulogne(BendableScore hearingsInBoulogne) {
        this.hearingsInBoulogne = hearingsInBoulogne;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getHearingsInAlmaFuerte() {
        return hearingsInAlmaFuerte;
    }

    public void setHearingsInAlmaFuerte(BendableScore hearingsInAlmaFuerte) {
        this.hearingsInAlmaFuerte = hearingsInAlmaFuerte;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getHearingsNotEjec() {
        return hearingsNotEjec;
    }

    public void setHearingsNotEjec(BendableScore hearingsNotEjec) {
        this.hearingsNotEjec = hearingsNotEjec;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getPrioritizeDetainees() {
        return prioritizeDetainees;
    }

    public void setPrioritizeDetainees(BendableScore prioritizeDetainees) {
        this.prioritizeDetainees = prioritizeDetainees;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getProblematicHearingsForLastRoom() {
        return problematicHearingsForLastRoom;
    }

    public void setProblematicHearingsForLastRoom(BendableScore problematicHearingsForLastRoom) {
        this.problematicHearingsForLastRoom = problematicHearingsForLastRoom;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getProblematicHearingsForLastJuez() {
        return problematicHearingsForLastJuez;
    }

    public void setProblematicHearingsForLastJuez(BendableScore problematicHearingsForLastJuez) {
        this.problematicHearingsForLastJuez = problematicHearingsForLastJuez;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getProblematicHearingsForLastFiscal() {
        return problematicHearingsForLastFiscal;
    }

    public void setProblematicHearingsForLastFiscal(BendableScore problematicHearingsForLastFiscal) {
        this.problematicHearingsForLastFiscal = problematicHearingsForLastFiscal;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getProblematicHearingsForLastAsesor() {
        return problematicHearingsForLastAsesor;
    }

    public void setProblematicHearingsForLastAsesor(BendableScore problematicHearingsForLastAsesor) {
        this.problematicHearingsForLastAsesor = problematicHearingsForLastAsesor;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getMaximumWorkTimeJuez() {
        return maximumWorkTimeJuez;
    }

    public void setMaximumWorkTimeJuez(BendableScore maximumWorkTimeJuez) {
        this.maximumWorkTimeJuez = maximumWorkTimeJuez;
    }

    @XmlJavaTypeAdapter(value = ScoreAdapter.class)
    public BendableScore getDoAllMeetingsAsSoonAsPossible() {
        return doAllMeetingsAsSoonAsPossible;
    }

    public void setDoAllMeetingsAsSoonAsPossible(BendableScore doAllMeetingsAsSoonAsPossible) {
        this.doAllMeetingsAsSoonAsPossible = doAllMeetingsAsSoonAsPossible;
    }
}
