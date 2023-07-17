package com.lifehopehealthapp.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class BPMYAxisFormatter implements IAxisValueFormatter {

    private final DecimalFormat mFormat;

    public BPMYAxisFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value) + " BPM";
    }
}


