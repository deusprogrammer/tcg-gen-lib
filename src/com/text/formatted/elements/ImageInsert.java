/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.text.formatted.elements;

import java.awt.image.BufferedImage;

/**
 *
 * @author mmain
 */
public class ImageInsert extends MarkupElement {
    protected String imageString;
    
    public ImageInsert(String imageString) {
        this.imageString = imageString;
    }
    
    public String toString() {
        return "[ICON: " + imageString + "]";
    }

    public void drawTo(BufferedImage bi, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getText() {
        return imageString;
    }
}
