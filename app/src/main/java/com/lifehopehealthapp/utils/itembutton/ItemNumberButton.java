package com.lifehopehealthapp.utils.itembutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lifehopehealthapp.R;


/**
 * Created by Ashik Vetrivelu on 10/08/16.
 */
public class ItemNumberButton extends RelativeLayout {
    private Context context;
    private AttributeSet attrs;
    private int styleAttr;
    private OnClickListener mListener;
    private int initialNumber;
    private int lastNumber;
    private int currentNumber;
    private int finalNumber;
    private TextView textView;
    private Button addBtn, subtractBtn;
    private View view;
    private OnValueChangeListener mOnValueChangeListener;

    public ItemNumberButton(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ItemNumberButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public ItemNumberButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        this.view = this;
        inflate(context, R.layout.layout_number_button, this);
        final Resources res = getResources();
        final int defaultColor = res.getColor(R.color.white);
        final int defaultTextColor = res.getColor(R.color.purple_500);
        final Drawable defaultDrawable = res.getDrawable(R.drawable.background);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ElegantNumberButton, styleAttr, 0);

        initialNumber = a.getInt(R.styleable.ElegantNumberButton_initialNumber, 0);
        finalNumber = a.getInt(R.styleable.ElegantNumberButton_finalNumber, Integer.MAX_VALUE);
        float textSize = a.getDimension(R.styleable.ElegantNumberButton_textSize, 15);
        int color = a.getColor(R.styleable.ElegantNumberButton_backGroundColor, defaultColor);
        int textColor = a.getColor(R.styleable.ElegantNumberButton_textColor, defaultTextColor);
        Drawable drawable = a.getDrawable(R.styleable.ElegantNumberButton_backgroundDrawable);

        subtractBtn = (Button) findViewById(R.id.subtract_btn);
        addBtn = (Button) findViewById(R.id.add_btn);
        textView = (TextView) findViewById(R.id.number_counter);
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.layout);

        subtractBtn.setTextColor(textColor);
        addBtn.setTextColor(textColor);
        textView.setTextColor(color);
        subtractBtn.setTextSize(textSize);
        addBtn.setTextSize(textSize);
        textView.setTextSize(textSize);
        textView.setBackgroundColor(textColor);

        if (drawable == null) {
            drawable = defaultDrawable;
        }
        assert drawable != null;
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
       /* if (Build.VERSION.SDK_INT > 16)
            mLayout.setBackground(drawable);
        else
            mLayout.setBackgroundDrawable(drawable);*/

        textView.setText(String.valueOf(initialNumber));

        currentNumber = initialNumber;
        lastNumber = initialNumber;

        subtractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                int num = Integer.valueOf(textView.getText().toString());
                setNumber(String.valueOf(num - 1), true);
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                int num = Integer.valueOf(textView.getText().toString());
                setNumber(String.valueOf(num + 1), true);
            }
        });
        a.recycle();
    }

    private void callListener(View view) {
        if (mListener != null) {
            mListener.onClick(view);
        }

        if (mOnValueChangeListener != null) {
            if (lastNumber != currentNumber) {
                mOnValueChangeListener.onValueChange(this, lastNumber, currentNumber);
            }
        }
    }

    public String getNumber() {
        return String.valueOf(currentNumber);
    }

    public void setNumber(String number) {
        lastNumber = currentNumber;
        this.currentNumber = Integer.parseInt(number);
        if (this.currentNumber > finalNumber) {
            this.currentNumber = finalNumber;
        }
        if (this.currentNumber < initialNumber) {
            this.currentNumber = initialNumber;
        }
        textView.setText(String.valueOf(currentNumber));
    }

    public void setNumber(String number, boolean notifyListener) {
        setNumber(number);
        if (notifyListener) {
            callListener(this);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mListener = onClickListener;
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        mOnValueChangeListener = onValueChangeListener;
    }

    public interface OnClickListener {

        void onClick(View view);

    }

    public interface OnValueChangeListener {
        void onValueChange(ItemNumberButton view, int oldValue, int newValue);
    }

    public void setRange(Integer startingNumber, Integer endingNumber) {
        this.initialNumber = startingNumber;
        this.finalNumber = endingNumber;
    }

    public void updateColors(int backgroundColor, int textColor) {
        this.textView.setBackgroundColor(backgroundColor);
        this.addBtn.setBackgroundColor(backgroundColor);
        this.subtractBtn.setBackgroundColor(backgroundColor);

        this.textView.setTextColor(textColor);
        this.addBtn.setTextColor(textColor);
        this.subtractBtn.setTextColor(textColor);
    }

    public void updateTextSize(int unit, float newSize) {
        this.textView.setTextSize(unit, newSize);
        this.addBtn.setTextSize(unit, newSize);
        this.subtractBtn.setTextSize(unit, newSize);
    }
}
