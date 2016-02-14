package com.lcc.uestc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.lcc.uestc.R;

/**
 * Created by wanli on 2015/8/25.
 */
public class TimeLineView extends View{

    private int markerSize = 24;
    private int lineSize = 1;

    private Drawable beginLine;
    private Drawable endLine;
    private Drawable marker;


    Paint paint;

    private String time = "01-11";

    private int width,height;
    Context context;
    public TimeLineView(Context context) {
        this(context, null);
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        this.context=context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeLineView,0,0);

        markerSize = typedArray.getDimensionPixelSize(R.styleable.TimeLineView_markerSize,markerSize);
        lineSize = typedArray.getDimensionPixelSize(R.styleable.TimeLineView_lineSize, lineSize);

        beginLine = typedArray.getDrawable(R.styleable.TimeLineView_beginLine);
        endLine = typedArray.getDrawable(R.styleable.TimeLineView_endLine);
        marker = typedArray.getDrawable(R.styleable.TimeLineView_marker);

        typedArray.recycle();

        if(beginLine != null){
            beginLine.setCallback(this);
        }
        if(endLine != null){
            endLine.setCallback(this);
        }
        if(marker != null){
            marker.setCallback(this);
        }
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.green_primary_dark));
        paint.setTextSize(45);
        Paint.FontMetrics fm = paint.getFontMetrics();
        fontHeight = fm.descent - fm.ascent;
        fontWidth = paint.measureText(time);
    }

    float fontHeight,fontWidth;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.heightMeasureSpec = heightMeasureSpec;
        setMeasuredDimension((int) (markerSize + fontWidth) + paddingLeft * 2,MeasureSpec.getSize(heightMeasureSpec));
    }

    public void setTextColor(int color)
    {
        paint.setColor(color );
        invalidate();
    }
    private int heightMeasureSpec;
    public void setTime(String time) {
        this.time = time;
        fontWidth = paint.measureText(time);
        setMeasuredDimension((int) (markerSize + fontWidth) + paddingLeft * 2,MeasureSpec.getSize(heightMeasureSpec));
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(beginLine != null)
        {
            beginLine.draw(canvas);
        }
        if(endLine != null)
        {
            endLine.draw(canvas);
        }
        if(marker != null)
        {
            marker.draw(canvas);
        }
        canvas.drawText(time,bounds.centerX()+markerSize/2+5,(getHeight()+fontHeight)/2,paint);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDrawableSize();
    }
    Rect bounds;
    private int paddingLeft = 7;
    private void initDrawableSize() {
        width = getWidth();
        height = getHeight();


        if(marker != null)
        {
            int markerSize = Math.min(this.markerSize,Math.min(width,height));
            int offsetY = (height - markerSize) / 2 + 10;
            marker.setBounds(paddingLeft,offsetY,markerSize + paddingLeft,markerSize + offsetY);
            bounds = marker.getBounds();
        }
        else {
            bounds = new Rect(markerSize, 0, width, height);
        }
        int halfLineSize = lineSize >> 1;

        int lineLeft = bounds.centerX() - halfLineSize;

        if(beginLine != null)
        {
            beginLine.setBounds(lineLeft,0,lineLeft + lineSize,bounds.top);
        }
        if(endLine != null)
        {
            endLine.setBounds(lineLeft,bounds.bottom,lineLeft + lineSize,height);
        }
    }

    public void setLineSize(int size)
    {
        if(lineSize != size)
        {
            lineSize = size;
            initDrawableSize();
            invalidate();
        }
    }

    public void setMarkerSize(int size)
    {
        if(markerSize != size)
        {
            markerSize = size;
            initDrawableSize();
            invalidate();
        }
    }

    public void setBeginLine(Drawable line)
    {
        if (this.beginLine != line)
        {
            this.beginLine = line;
            if (line != null) {
                line.setCallback(this);
            }
            initDrawableSize();
            invalidate();
        }
    }

    public void setEndLine(Drawable line)
    {
        if (this.endLine != line)
        {
            this.endLine = line;
            if (line != null) {
                line.setCallback(this);
            }
            initDrawableSize();
            invalidate();
        }
    }

    public void setMarker(Drawable mark)
    {
        if (this.marker != mark)
        {
            this.marker = mark;
            if (mark != null) {
                mark.setCallback(this);

                initDrawableSize();
                invalidate();
            }

        }
    }

}
