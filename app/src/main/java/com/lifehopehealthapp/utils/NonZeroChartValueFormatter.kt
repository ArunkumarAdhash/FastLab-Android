package com.lifehopehealthapp.utils

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler

class NonZeroChartValueFormatter(digits: Int) : DefaultValueFormatter(digits) {
    override fun getFormattedValue(
        value: Float, entry: Entry?, dataSetIndex: Int,
        viewPortHandler: ViewPortHandler?
    ): String {
        return if (value > 0) {
            mFormat.format(value.toDouble())
        } else {
            ""
        }
    }
}