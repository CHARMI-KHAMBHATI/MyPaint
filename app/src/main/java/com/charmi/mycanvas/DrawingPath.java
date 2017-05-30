package com.charmi.mycanvas;

import android.graphics.Path;

/**
 * Created by ADMIN on 1/27/2017.
 */
public class DrawingPath {
    Path path;
    int strokeWidth,penColor;

    DrawingPath(int strokeWidth, int color) {
        path = new Path();
        this.strokeWidth = strokeWidth;
        this.penColor=color;
    }
}
