/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.layouts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.Color;

/**
 *
 * @author mmain
 */
public class CardFont {
    private String family;
    private String weight;
    private int size;
    private Color color;
    
    @JsonCreator
    public CardFont(
            @JsonProperty("family") String family,
            @JsonProperty("weight") String weight,
            @JsonProperty("size")   int size,
            @JsonProperty("color")  String color
            ) {
        this.family = family;
        this.weight = weight;
        this.size = size;
        
        switch(color) {
            case "black":
                this.color = Color.black;
                break;
            case "white":
                this.color = Color.white;
                break;
        }
    }
    
    public String getFamily() {
        return family;
    }
    
    public String getWeight() {
        return weight;
    }
    
    public int getSize() {
        return size;
    }
    
    public Color getColor() {
        return color;
    }
    
    public String toString() {
        return  "\t\tfamily: " + family + "\n" +
                "\t\tweight: " + weight + "\n" +
                "\t\tsize:   " + size + "\n";
    }
}
