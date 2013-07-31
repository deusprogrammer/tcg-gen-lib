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
public class TextInsert extends MarkupElement {
    protected String text;
    
    TextInsert(String text) {
        this.text = text;
    }

    public void drawTo(BufferedImage bi, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String getText() {
        return text;
    }
    
    public String toString() {
        return "[WORD: " + text + "]";
    }
}
