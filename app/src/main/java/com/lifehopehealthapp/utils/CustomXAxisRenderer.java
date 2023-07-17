package com.lifehopehealthapp.utils;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomXAxisRenderer extends XAxisRenderer {
    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        float labelHeight = mXAxis.getTextSize();
        float labelInterval = 5f;
        String[] labels = formattedLabel.split(" ");

        Paint mFirstLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFirstLinePaint.setColor(Color.parseColor("#07080a"));
        mFirstLinePaint.setTextAlign(Paint.Align.LEFT);
        mFirstLinePaint.setTextSize(com.github.mikephil.charting.utils.Utils.convertDpToPixel(10.5f));
        mFirstLinePaint.setTypeface(mXAxis.getTypeface());

        Paint mSecondLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondLinePaint.setColor(Color.parseColor("#07080a"));
        mSecondLinePaint.setTextAlign(Paint.Align.LEFT);
        mSecondLinePaint.setTextSize(com.github.mikephil.charting.utils.Utils.convertDpToPixel(10.5f));
        mSecondLinePaint.setTypeface(mXAxis.getTypeface());


        if (labels.length > 1) {
            com.github.mikephil.charting.utils.Utils.drawXAxisValue(c, labels[0], x, y, mFirstLinePaint, anchor, angleDegrees);
            com.github.mikephil.charting.utils.Utils.drawXAxisValue(c, labels[1], x, y + labelHeight + labelInterval, mSecondLinePaint, anchor, angleDegrees);
        } else {
            com.github.mikephil.charting.utils.Utils.drawXAxisValue(c, formattedLabel, x, y, mFirstLinePaint, anchor, angleDegrees);
        }

    }
}
