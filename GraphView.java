package com.example.priya.graphgeneration;

/**
 * Created by rahulrao on 8/25/16.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;

/**
 * GraphView creates a scaled line or bar graph with x and y axis labels.
 * @author Arno den Hond
 *
 */
public class GraphView extends View {

    public static boolean BAR = false;
    public static boolean LINE = true;

    private Paint paint;
    private float[] values;
    private float[] values1;
    private float[] values2;
    private String[] horlabels;
    private String[] verlabels;
    private String title;
    private boolean type;

    public GraphView(Context context, float[] values, float[] values1,float[] values2, String title, String[] horlabels, String[] verlabels, boolean type) {
        super(context);
        if (values == null)
            values = new float[0];
        else
            this.values = values;
        if (values1 == null)
            values1 = new float[0];
        else
            this.values1 = values1;
        if (values2 == null)
            values2 = new float[0];
        else
            this.values2 = values2;
        if (title == null)
            title = "";
        else
            this.title = title;
        if (horlabels == null)
            this.horlabels = new String[0];
        else
            this.horlabels = horlabels;
        if (verlabels == null)
            this.verlabels = new String[0];
        else
            this.verlabels = verlabels;
        this.type = type;
        paint = new Paint();
    }

    public void setValues(float[] newValues)
    {
        this.values = newValues;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float border = 20;
        float horstart = border * 2;
        float height = getHeight();
        float width = getWidth() - 1;
        float max = getMax();
        float min = getMin();
        float diff = max - min;
        float graphheight = height - (2 * border);
        float graphwidth = width - (2 * border);


        paint.setTextAlign(Align.LEFT);
        int vers = verlabels.length - 1;
        for (int i = 0; i < verlabels.length; i++) {
            paint.setColor(Color.DKGRAY);
            float y = ((graphheight / vers) * i) + border;
            canvas.drawLine(horstart, y, width, y, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(verlabels[i], 0, y, paint);
        }
        int hors = horlabels.length - 1;
        for (int i = 0; i < horlabels.length; i++) {
            paint.setColor(Color.DKGRAY);
            float x = ((graphwidth / hors) * i) + horstart;
            canvas.drawLine(x, height - border, x, border, paint);
            paint.setTextAlign(Align.CENTER);
            if (i==horlabels.length-1)
                paint.setTextAlign(Align.RIGHT);
            if (i==0)
                paint.setTextAlign(Align.LEFT);
            paint.setColor(Color.WHITE);
            canvas.drawText(horlabels[i], x, height - 4, paint);
        }

        paint.setTextAlign(Align.CENTER);
        canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);

        if (max != min) {
            paint.setColor(Color.WHITE);
            if (type == BAR) {
                float datalength = values.length;
                float colwidth = (width - (2 * border)) / datalength;
                for (int i = 0; i < values.length; i++) {
                    float val = values[i] - min;
                    float rat = val / diff;
                    float h = graphheight * rat;
                    canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint);
                }
            } else {
                float datalength = values.length;
                float colwidth = (width - (2 * border)) / datalength;
                float halfcol = colwidth / 2;
                float lasth = 0;
                for (int i = 0; i < values.length; i++) {
                    float val = values[i] - min;
                    float rat = val / diff;
                    float h = graphheight * rat;
                    if (i > 0)
                        paint.setColor(Color.GREEN);
                    paint.setStrokeWidth(2.0f);
                    canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
                    lasth = h;

                }
                for (int j = 0; j < values1.length; j++) {
                    float val = values1[j] - min;
                    float rat = val / diff;
                    float h = graphheight * rat;
                    if (j > 0)
                        paint.setColor(Color.CYAN);
                    paint.setStrokeWidth(2.0f);
                    canvas.drawLine(((j - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (j * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
                    lasth = h;

                }
                for (int k = 0; k < values2.length; k++) {
                    float val = values2[k] - min;
                    float rat = val / diff;
                    float h = graphheight * rat;
                    if (k > 0)
                        paint.setColor(Color.YELLOW);
                    paint.setStrokeWidth(2.0f);
                    canvas.drawLine(((k - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (k * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
                    lasth = h;

                }
            }
        }
    }

    private float getMax() {
        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values2.length; i++)
                if (values2[i] > largest)
                largest = values2[i];

        //largest = 3000;
        return largest+5;
    }

    private float getMin() {
        float smallest = Integer.MAX_VALUE;
        for (int i = 0; i < values.length; i++)
            if (values[i] < smallest)
                smallest = values[i];

        //smallest = 0;
        return smallest;
    }

}