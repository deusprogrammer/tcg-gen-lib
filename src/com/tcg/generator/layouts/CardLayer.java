/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.layouts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Michael
 */
public class CardLayer {
    protected String type;
    protected String file;
    protected Integer r, g, b;
    protected Integer width, height;
    protected BufferedImage image;
    
    @JsonCreator
    public CardLayer(
            @JsonProperty("type")   String type,
            @JsonProperty("file")   String file,
            @JsonProperty("r")      Integer r,
            @JsonProperty("g")      Integer g,
            @JsonProperty("b")      Integer b,
            @JsonProperty("width")  Integer width,
            @JsonProperty("height") Integer height
            ) {
        this.type = type;
        this.file = file;
        this.r = r;
        this.g = g;
        this.b = b;
        
        if (file != null) {
            try {
                this.image = ImageIO.read(new File(file));
            } catch (IOException ex) {
                System.out.println("Unable to open layer!");
            }
        } else if (r != null && g != null && b != null) {
            this.width = width;
            this.height = height;
            this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D graphics = this.image.createGraphics();
            graphics.setColor(new Color(r, g, b));
            graphics.draw(new Rectangle(this.width, this.height));
        }
    }
    
    public Integer getWidth() {
        return image.getWidth();
    }
    
    public Integer getHeight() {
        return image.getHeight();
    }
    
    public BufferedImage getImage() {
        return image;
    }
}