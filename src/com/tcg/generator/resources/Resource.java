/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author mmain
 */
public class Resource {
    protected String resourcePath;
    protected BufferedImage resourceImage;
    protected Integer width, height;
    
    @JsonCreator
    public Resource(@JsonProperty("file") String resourcePath) {
        this.resourcePath = resourcePath;
        try {
            this.resourceImage = ImageIO.read(new File(resourcePath));
            System.out.println("Successfully opened: " + resourcePath);
        } catch (IOException ex) {
            System.out.println("Failed to open:      " + resourcePath);
        }
    }
    
    public BufferedImage getImage() {
        return resourceImage;
    }
    
    public int getWidth() {
        return resourceImage.getWidth();
    }
    
    public int getHeight() {
        return resourceImage.getHeight();
    }
    
    public String toString() {
        return "\t\tresourcePath: " + resourcePath;
    }
}
