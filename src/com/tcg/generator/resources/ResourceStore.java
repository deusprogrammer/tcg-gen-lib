/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.generator.cards.reflect.Card;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmain
 */
public class ResourceStore {
    protected HashMap<String, Resource> resources;
    
    protected static ObjectMapper mapper = new ObjectMapper();
    
    public static ResourceStore loadResourceStore(File resourceFile) {
        try {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            ResourceStore store = mapper.readValue(resourceFile, ResourceStore.class);
            return store;
        } catch (IOException ex) {
            Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static ResourceStore loadResourceStore(String resourceJson) {
        try {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            ResourceStore store = mapper.readValue(resourceJson, ResourceStore.class);
            return store;
        } catch (IOException ex) {
            Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public Resource getResource(String resourceName) {
        return resources.get(resourceName);
    }
    
    @JsonCreator
    public ResourceStore(@JsonProperty("resources") HashMap<String, Resource> resources) {
        this.resources = resources;
    }
    
    public Integer getMaxHeight() {
        Integer max = 0;
        for (Entry<String, Resource> entry : resources.entrySet()) {
            if (max < entry.getValue().getHeight()) {
                max = entry.getValue().getHeight();
            }
        }
        
        return max;
    }
    
    public String toString() {
        String s = "";
        
        for (Entry resource : resources.entrySet()) {
            s += "\t" + resource.getKey() + "\n";
            s += resource.getValue() + "\n";
        }
        
        return s;
    }
}
