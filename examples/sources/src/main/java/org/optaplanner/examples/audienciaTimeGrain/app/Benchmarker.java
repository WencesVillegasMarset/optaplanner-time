package org.optaplanner.examples.audienciaTimeGrain.app;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.aggregator.swingui.BenchmarkAggregatorFrame;

public class Benchmarker {
    public static void main(String[] args) {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "org/optaplanner/examples/audienciaTimeGrain/solver/audienciaBenchmarkConfig.xml");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmark.benchmarkAndShowReportInBrowser();
    }
}