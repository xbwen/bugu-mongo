/*
 * Copyright (c) www.bugull.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bugull.mongo.fs;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class Watermark {
    
    public final static int CENTER = 1;
    public final static int BOTTOM_RIGHT = 2;
    public final static int BOTTOM_LEFT = 3;
    
    private String imagePath;
    private String text;
    
    private String fontName = "Arial";
    private int fontStyle = Font.PLAIN;
    private Color color = Color.GRAY;
    private int fontSize = 30;
    private float alpha = 0.5f;
    
    private int align = CENTER;  //default align is center
    
    //margin
    private int right = 20;
    private int left = 20;
    private int bottom = 20;

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public String getImagePath() {
        return imagePath;
    }

    /**
     * set the absolute path of the image file
     * @param imagePath 
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getBottom() {
        return bottom;
    }

    /**
     * set margin bottom
     * @param bottom 
     */
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRight() {
        return right;
    }

    /**
     * set margin right
     * @param right 
     */
    public void setRight(int right) {
        this.right = right;
    }

    public int getLeft() {
        return left;
    }

    /**
     * set margin left
     * @param left 
     */
    public void setLeft(int left) {
        this.left = left;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }
    
}
