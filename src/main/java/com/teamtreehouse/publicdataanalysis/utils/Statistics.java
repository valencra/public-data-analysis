package com.teamtreehouse.publicdataanalysis.utils;

import com.teamtreehouse.publicdataanalysis.Application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistics {
    public static Map<String, Double> calculateStatistics(List<Double> indicator) {
        // Calculate indicator statistics
        Map<String, Double> indicatorStats = new HashMap<>();
        Double indicatorMin = indicator.stream()
                .collect(Collectors.summarizingDouble(Double::doubleValue))
                .getMin();
        indicatorStats.put("min", indicatorMin);
        Double indicatorMax = indicator.stream()
                .collect(Collectors.summarizingDouble(Double::doubleValue))
                .getMax();
        indicatorStats.put("max", indicatorMax);
        Long indicatorCount = indicator.stream()
                .collect(Collectors.summarizingDouble(Double::doubleValue))
                .getCount();
        indicatorStats.put("count", (double) indicatorCount);
        Double indicatorAvg = indicator.stream()
                .collect(Collectors.summarizingDouble(Double::doubleValue))
                .getAverage();
        indicatorStats.put("avg", indicatorAvg);
        return indicatorStats;
    }

    public static Double calculateCorrelation(
            List<Double> indicatorA, Map<String, Double> indicatorAStats,
            List<Double> indicatorB, Map<String, Double> indicatorBStats) {
        // Calculate correlation between two indicators
        Double indicatorAAvg = indicatorAStats.get("avg");
        Double indicatorACount = indicatorAStats.get("count");
        Double indicatorAStdDev = calculateStdDev(indicatorA, indicatorAAvg, indicatorACount);
        List<Double> stdIndicatorA = standardize(indicatorA, indicatorAAvg, indicatorAStdDev);

        Double indicatorBAvg = indicatorBStats.get("avg");
        Double indicatorBCount = indicatorBStats.get("count");
        Double indicatorBStdDev = calculateStdDev(indicatorB, indicatorBAvg, indicatorBCount);
        List<Double> stdIndicatorB = standardize(indicatorB, indicatorBAvg, indicatorBStdDev);

        Double stdIndicatorDotProd = 0.0;
        Double[] stdIndicatorAArr = stdIndicatorA.toArray(new Double[stdIndicatorA.size()]);
        Double[] stdIndicatorBArr = stdIndicatorB.toArray(new Double[stdIndicatorB.size()]);
        for (int i = 0; i < indicatorACount; i++) {
            stdIndicatorDotProd += stdIndicatorAArr[i] * stdIndicatorBArr[i];
        }
        return stdIndicatorDotProd / (indicatorACount - 1);
    }

    private static Double calculateStdDev(List<Double> indicator, Double indicatorAvg, Double indicatorCount) {
        // Calculate indicator standard deviation
        Double indicatorVar = indicator.stream()
                .map((indicatorVal) -> {
                    return Math.pow(
                            (indicatorVal - indicatorAvg),
                            2.0
                    );
                })
                .reduce((indicatorCurrVal, indicatorNextVal) -> {
                    return indicatorCurrVal + indicatorNextVal;
                })
                .orElseThrow(RuntimeException::new)
                / (indicatorCount - 1);
        return Math.sqrt(indicatorVar);
    }

    private static List<Double> standardize(List<Double> indicator, Double indicatorAvg, Double indicatorStdDev) {
        // Standardize indicator values
        List<Double> stdIndicator = indicator.stream()
                .map((indicatorVal) -> {return (indicatorVal - indicatorAvg) / indicatorStdDev;})
                .collect(Collectors.toList());
        return stdIndicator;
    }
}
