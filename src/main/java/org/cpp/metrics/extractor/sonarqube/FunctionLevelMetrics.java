/**
 * Cpp Metrics Extractor
 * Copyright (C) 2021
 * http://github.com/ericlemes/cpp-metrics-extractor
 */

package org.cpp.metrics.extractor.sonarqube;

import javax.annotation.CheckForNull;

import org.sonar.cxx.squidbridge.measures.CalculatedMetricFormula;
import org.sonar.cxx.squidbridge.measures.MetricDef;

public enum FunctionLevelMetrics implements MetricDef {
    FUNCTION_LEVEL_METRICS;

    @Override
    public String getName() {
      return name();
    }
  
    @Override
    public boolean isCalculatedMetric() {
      return false;
    }
  
    @Override
    public boolean aggregateIfThereIsAlreadyAValue() {
      return true;
    }
  
    @Override
    public boolean isThereAggregationFormula() {
      return true;
    }
  
    @Override
    @CheckForNull
    public CalculatedMetricFormula getCalculatedMetricFormula() {
      return null;
    }
}
