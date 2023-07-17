package com.lifehopehealthapp.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class MyAxisValueFormatter implements IAxisValueFormatter {

    private final DecimalFormat mFormat;
    private final String metrics;

    public MyAxisValueFormatter(String chartMetrics) {
        this.metrics = chartMetrics;
        mFormat = new DecimalFormat("###,###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value) + " " + metrics;
    }
}
