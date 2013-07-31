/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.layouts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.generator.cards.GenericCard;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmain
 */
public class CardLayout {
    protected ArrayList<ElementLayout> elements;
    protected String name;
    protected Integer width, height;
    protected Integer nColumns;
    protected ObjectMapper mapper = new ObjectMapper();
    
    @JsonCreator
    public CardLayout(
            @JsonProperty("name")     String name,
            @JsonProperty("elements") ArrayList<ElementLayout> elements,
            @JsonProperty("width")    Integer width,
            @JsonProperty("height")   Integer height
            ) {
        this.name = name;
        this.elements = elements;
        this.width = width;
        this.height = height;
    }
    
    public String getName() {
        return name;
    }
    
    public ArrayList<ElementLayout> getElements() {
        return elements;
    }
    
    public ElementLayout getElementByMapping(String fieldName) {
        for (ElementLayout element : elements) {
            if (element.getMappings() != null && element.getMappings().get(fieldName) != null) {
                return element;
            }
        }
        return null;
    }
    
    public ArrayList<ElementLayout> getElementsByType(String type) {
        ArrayList<ElementLayout> found = new ArrayList<>();
        for (ElementLayout element : elements) {
            if (element.getName().equals(type)) {
                found.add(element);
            }
        }
        return found;
    }
    
    public ElementLayout getElementByName(String elementName) {
        for (ElementLayout element : elements) {
            if (element.getName().equals(elementName)) {
                return element;
            }
        }
        return null;
    }
    
    public Integer getWidth() {
        return width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public String toString() {
        String s = "";
        
        for (ElementLayout element : elements) {
            s += element.toString() + "\n";
        }
        
        return s;
    }
}
