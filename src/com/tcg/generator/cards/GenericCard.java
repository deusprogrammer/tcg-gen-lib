/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.cards;

import com.tcg.generator.cards.reflect.Card;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.generator.layouts.CardLayout;
import com.tcg.generator.layouts.ElementLayout;
import com.tcg.generator.layouts.ElementMapping;
import com.tcg.generator.util.ImageWriter;
import com.text.formatted.elements.MixedMediaText;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Michael
 */
public class GenericCard {
    private LinkedHashMap<String, Object> cardData;
    public String cardName = "Unknown";
    protected CardLayout layout;
    protected BufferedImage artwork;
    protected ObjectMapper mapper = new ObjectMapper();
    
    public GenericCard() {}
    
    public GenericCard(String name, String artworkFile, CardLayout layout, LinkedHashMap<String, Object> cardData) {
        this.cardData = cardData;
        
        setCardName(name);
        setLayout(layout);
        setArtwork(artworkFile);
    }
    
    public GenericCard(String name, String artworkFile, String layoutFile, LinkedHashMap<String, Object> cardData) {
        this.cardData = cardData;
        
        setCardName(name);
        setLayout(layoutFile);
        setArtwork(artworkFile);
    }
    
    public final GenericCard setCardName(String cardName) {
        this.cardName = cardName;
        return this;
    }
    
    public final GenericCard setLayout(CardLayout layout) {
        this.layout = layout;
        return this;
    }
    
    public final GenericCard setLayout(File layoutFile) {
        try {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            this.layout = mapper.readValue(layoutFile, CardLayout.class);
        } catch (IOException ex) {
            Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }
    
    public final GenericCard setLayout(String jsonData) {
        try {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            this.layout = mapper.readValue(jsonData, CardLayout.class);
        } catch (IOException ex) {
            Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }
    
    public final GenericCard setArtwork(String artworkFile) {
        try {
            this.artwork = ImageIO.read(new File(artworkFile));
           System.out.println("Successfully opened: " + artworkFile);
        } catch (IOException ex) {
            System.out.println("Failed to open:      " + artworkFile);
        }
        return this;
    }
    
    public final GenericCard setCardData(String jsonData) {
        try {
            this.cardData = mapper.readValue(jsonData, LinkedHashMap.class);
        } catch (IOException ex) {
            Logger.getLogger(GenericCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }
    
    public final GenericCard setCardData(LinkedHashMap<String, Object> cardData) {
        this.cardData = cardData;
        return this;
    }
    
    public void draw(String outputDirectory) {
        BufferedImage bi = new BufferedImage(layout.getWidth(), layout.getHeight(), BufferedImage.TYPE_INT_ARGB);
        ImageWriter.drawLayer(artwork, bi);
        
        ArrayList<MixedMediaText> mmtl;
        for (ElementLayout element: layout.getElements()) {
            
            // Test to see if layer should be drawn
            if (!element.shouldDraw(cardData)) {
                continue;
            }
            
            System.out.println("Drawing element: " + element.getName());
            
            ImageWriter.drawLayer(element, bi);
            if (element.getMappings() != null) {
                ArrayList<String> lines = new ArrayList<>();
                for (Entry<String, ElementMapping> entry : element.getMappings().entrySet()) {
                    ElementMapping mapping =  entry.getValue();
                    String field = entry.getKey();
                    Object value;
                    if (entry.getKey().equals("cardName") && cardData.get(field) == null) {
                        value = cardName;
                    }
                    else {
                        value = cardData.get(field);
                    } 

                    if (value != null) {
                        System.out.println("\tFIELD: " +  field + " (" + value.getClass().getSimpleName() + ")");

                        if (mapping.getLabel() != null) {
                            System.out.println("\t\tLABEL: " + mapping.getLabel());
                        }
                        if (mapping.getType() != null) {
                            System.out.println("\t\tTYPE:  " + mapping.getType());
                        }
                        
                        ArrayList<String> list;
                        Integer intValue;
                        Float floatValue;
                        String stringValue;

                        String line = "";
                        
                        if (mapping.getLabel() != null) {
                            line += mapping.getLabel() + ": ";
                        }
                        
                        switch (value.getClass().getSimpleName()) {
                            case "String":
                                stringValue = (String)value;
                                line += stringValue.toString();
                                lines.add(line);
                                break;
                            case "Integer":
                                intValue = (Integer)value;
                                line += intValue.toString();
                                lines.add(line);
                                break;
                            case "Float":
                                floatValue = (Float)value;
                                line += floatValue.toString();
                                lines.add(line);
                                break;
                            case "ArrayList":
                                list = (ArrayList<String>)value;
                                for (String aLine : list) {
                                    lines.add(aLine);
                                }
                                break;
                            default:
                                System.out.println("\t\tInvalid field type!");
                                break;
                        }
                        

                    }
                }
                
                System.out.println("\tLINES: " + lines);
                
                switch(element.getType()) {
                    case "text-box":
                        mmtl = ImageWriter.splitAndFitMixedText(bi, lines, element);
                        ImageWriter.drawMixedMediaText(bi, mmtl, element);
                        break;
                    case "table":
                        ImageWriter.drawTableCols(bi, lines, 2, element);
                        break;
                    case "image":
                    case "layer":
                        break;
                    default:
                        break;
                }
            }
        }
        
        try {
            ImageIO.write(bi, "png", new File(outputDirectory + cardName + ".png"));
        } catch (IOException ex) {
            System.out.println("Unable to write image!");
        }
    }
}
