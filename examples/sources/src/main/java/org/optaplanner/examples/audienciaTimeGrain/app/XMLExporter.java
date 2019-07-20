package org.optaplanner.examples.audienciaTimeGrain.app;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaAssignment;
import org.optaplanner.examples.audienciaTimeGrain.domain.AudienciaSchedule;
import org.optaplanner.examples.audienciaTimeGrain.domain.Audiencia;
import org.optaplanner.examples.common.persistence.AbstractXmlSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;


public class XMLExporter extends AbstractXmlSolutionExporter<AudienciaSchedule> {

    public static void main(String[] args) {
        SolutionConverter<AudienciaSchedule> converter = SolutionConverter.createExportConverter(
                Main.DATA_DIR_NAME, AudienciaSchedule.class, new XMLExporter());
        converter.convertAll();
    }

    @Override
    public XmlOutputBuilder<AudienciaSchedule> createXmlOutputBuilder() {
        return new AudienciaScheduelingOutputBuilder();
    }

    public static class AudienciaScheduelingOutputBuilder extends XmlOutputBuilder<AudienciaSchedule> {

        private AudienciaSchedule audienciaSchedule;

        @Override
        public void setSolution(AudienciaSchedule solution) {
            audienciaSchedule = solution;
            try {
                this.writeSolution();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void writeSolution() throws IOException {

            Element solutionElement = new Element("Solution");
            document.setRootElement(solutionElement);

            Element schedulingPeriodIDElement = new Element("SchedulingPeriodID");
            schedulingPeriodIDElement.setText(audienciaSchedule.getAudienciaAssignmentList().get(0).getStartingTimeGrain().getDateTimeString());
            solutionElement.addContent(schedulingPeriodIDElement);

            Element ConstraintsPenaltyElement = new Element("SoftConstraintsPenalty");
            ConstraintsPenaltyElement.setText(audienciaSchedule.getScore().toString());
            solutionElement.addContent(ConstraintsPenaltyElement);

            for (AudienciaAssignment audienciaAssignment : audienciaSchedule.getAudienciaAssignmentList()) {
                Audiencia audiencia = audienciaAssignment.getAudiencia();

                Element assignmentElement = new Element("Assignment");
                solutionElement.addContent(assignmentElement);

                Element dateElement = new Element("Date");
                dateElement.setText(audienciaAssignment.getStartingTimeGrain().getDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
                assignmentElement.addContent(dateElement);

                Element employeeElement = new Element("Juez");
                employeeElement.setText(String.valueOf(audiencia.getJuez().getIdJuez()));
                assignmentElement.addContent(employeeElement);

                Element roomElement = new Element("Room");
                roomElement.setText(audienciaAssignment.getRoom().getNombreRoom());
                assignmentElement.addContent(roomElement);

            }
        }
    }

}