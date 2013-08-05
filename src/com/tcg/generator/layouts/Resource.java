/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.layouts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcg.generator.config.ConfigHolder;
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
        String filePath = ConfigHolder.getConfig("rootDirectory") + resourcePath;
        try {
            this.resourceImage = ImageIO.read(new File(filePath));
            System.out.println("Successfully opened: " + filePath);
        } catch (IOException ex) {
            System.out.println("Failed to open:      " + filePath);
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
