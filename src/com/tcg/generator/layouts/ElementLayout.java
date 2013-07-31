/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.layouts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author mmain
 */
public class ElementLayout {
    private String name, type;
    private Integer x, y;
    private Integer width, height;
    private Integer margin;
    private CardFont font;
    private Double transparency;
    private BufferedImage layer;
    private Integer columns;
    private Condition condition;
    private LinkedHashMap<String, ElementMapping> mappings;
    
    @JsonCreator
    public ElementLayout(
            @JsonProperty("name")         String name,
            @JsonProperty("type")         String type,
            @JsonProperty("x")            Integer x,
            @JsonProperty("y")            Integer y,
            @JsonProperty("width")        Integer width,
            @JsonProperty("height")       Integer height,
            @JsonProperty("margin")       Integer margin,
            @JsonProperty("columns")      Integer columns,
            @JsonProperty("transparency") Double transparency,
            @JsonProperty("font")         CardFont font,
            @JsonProperty("layer")        String layer,
            @JsonProperty("mappings")     LinkedHashMap<String, ElementMapping> mappings,
            @JsonProperty("condition")    String condition
            ) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.margin = margin;
        this.columns = columns;
        this.font = font;
        this.transparency = transparency;
        this.mappings = mappings;
        try {
            if (layer != null) {
                this.layer = ImageIO.read(new File(layer));
                if (this.height == null) {
                    this.height = this.layer.getHeight();
                }
                if (this.width == null) {
                    this.width = this.layer.getWidth();
                }
                System.out.println("Successfully opened: " + layer);
            }
        } catch (IOException ex) {
            System.out.println("Failed to open:      " + layer);
        }
        
        if (condition != null) {
            this.condition = new Condition(condition);
        }
    }
    
    public String getName() {
        return name;
    }
        
    public String getType() {
        return type;
    }
    
    public Integer getX() {
        return x;
    }
    
    public Integer getY() {
        return y;
    }
        
    public Integer getWidth() {
        return width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public Integer getMargin() {
        return margin;
    }
    
    public Integer getColumns() {
        return columns;
    }
    
    public Double getTransparency() {
        return transparency;
    }
    
    public CardFont getCardFont() {
        return font;
    }
    
    public Condition getCondition() {
        return condition;
    }
    
    public boolean shouldDraw(LinkedHashMap<String, Object> object) {
        if (condition != null) {
            return condition.test(object);
        } else {
            return true;
        }
    }
    
    public boolean shouldDraw(Object object) {
        if (condition != null) {
            return condition.test(object);
        } else {
            return true;
        }
    }
    
    public Font getFont() {
        if (font == null) {
            return null;
        }
        
        int fontWeight = 0;
        
        switch (font.getWeight()) {
            case "bold":
                fontWeight = Font.BOLD;
                break;
            case "normal":
                fontWeight = Font.PLAIN;
                break;
        }
        
        return new Font(font.getFamily(), fontWeight, font.getSize());
    }
    
    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
    
    public BufferedImage getLayer() {
        return layer;
    }
    
    public LinkedHashMap<String, ElementMapping> getMappings() {
        return mappings;
    }
    
    public ElementMapping getMapping(String fieldName) {
        return mappings.get(fieldName);
    }
    
    public String toString() {
        return  "\tname:         " + name + "\n" +
                "\ttype:         " + type + "\n" +
                "\tx:            " + x + "\n" +
                "\ty:            " + y + "\n" +
                "\twidth:        " + width + "\n" +
                "\theight:       " + height +  "\n" +
                "\tmargin:       " + margin + "\n" +
                "\tcolumns:      " + columns + "\n" +
                "\ttransparency: " + transparency + "\n" +
                "\tfont:         " + "\n" + font;
    }    
}