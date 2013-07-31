/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.util;

import com.tcg.generator.layouts.CardFont;
import com.tcg.generator.layouts.ElementLayout;
import com.tcg.generator.resources.Resource;
import com.tcg.generator.resources.ResourceStore;
import static com.tcg.generator.util.ImageWriter.resourceStore;
import static com.tcg.generator.util.ImageWriter.splitAndFitMixedText;
import static com.tcg.generator.util.ImageWriter.splitAndFitText;
import com.text.formatted.elements.*;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author mmain
 */
public class ImageWriter {
    protected static ResourceStore resourceStore = null;
    
    public static void setResourceStore(File resourceStoreFile) {
        ImageWriter.resourceStore = ResourceStore.loadResourceStore(resourceStoreFile);
    }
    
    public static void setResourceStore(String resourceStoreJson) {
        ImageWriter.resourceStore = ResourceStore.loadResourceStore(resourceStoreJson);
    }
    
    public static ArrayList<String> splitAndFitText(BufferedImage target, ArrayList<String> lines, ElementLayout layout) {
        ArrayList<String> returnLines = new ArrayList<>();
        
        for (String line : lines) {
            ArrayList<String> moreLines = splitAndFitText(target, line, layout);
            returnLines.addAll(moreLines);
        }
        
        return returnLines;
    }
    
    public static ArrayList<String> splitAndFitText(BufferedImage target, String text, ElementLayout layout) {
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setFont(layout.getFont());
        
        FontMetrics fm = g.getFontMetrics();
        
        String[] words = text.split(" ");
        
        String line = "";
        String lastLine = "";
        String lastWord = "";
        ArrayList<String> lines = new ArrayList<>();
        
        boolean first = true;
        for (String word : words) {
            lastLine = line;
            lastWord = word;
            if (first) {
                line += word;
                first = false;
            } else {
                line += " " + word;
            }
            
            int width = fm.getStringBounds(line, g).getBounds().width;
            
            if (width > layout.getWidth() - layout.getMargin() * 2) {
                lines.add(lastLine);
                line = lastWord;
            }
        }
        
        lines.add(line);
        
        for (String element : lines) {
            System.out.println("LINE: " + element);
        }
        
        return lines;
    }
    
    public static ArrayList<MixedMediaText> splitAndFitMixedText(BufferedImage target, ArrayList<String> lines, ElementLayout layout) {
        ArrayList<MixedMediaText> returnLines = new ArrayList<>();
        
        for (String line : lines) {
            ArrayList<MixedMediaText> moreLines = splitAndFitMixedText(target, new MixedMediaText(line), layout);
            returnLines.addAll(moreLines);
        }
        
        return returnLines;
    }
    
    public static ArrayList<MixedMediaText> splitAndFitMixedText(BufferedImage target, MixedMediaText text, ElementLayout layout) {
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setFont(layout.getFont());
        
        FontMetrics fm = g.getFontMetrics();
        
        MixedMediaText line = new MixedMediaText();
        MarkupElement lastElement = null;
        MarkupElement lastIcon = null;
        String alteredLine = "";
        int nIcons = 0;
        int iconWidth = 0;
        int formattedTextWidth = 0;
        
        ArrayList<MixedMediaText> lines = new ArrayList<>();
        
        MarkupElement element;
        int maxWidth = (layout.getWidth() - layout.getMargin() * 2);
        
        while((element = text.next()) != null) {
            if (element instanceof ImageInsert) {
                iconWidth += resourceStore.getResource(element.getText()).getWidth();
                nIcons++;
            } else if (element instanceof BoldTextInsert) {
                formattedTextWidth += g.getFontMetrics(new Font(layout.getCardFont().getFamily(), Font.BOLD, layout.getCardFont().getSize())).getStringBounds(" " + element.getText(), g).getBounds().width;
            } else if (element instanceof ItalicTextInsert) {
                formattedTextWidth += g.getFontMetrics(new Font(layout.getCardFont().getFamily(), Font.ITALIC, layout.getCardFont().getSize())).getStringBounds(" " + element.getText(), g).getBounds().width;
            }else {
                alteredLine += " " + element.getText();
            }
            
            line.addElement(element);
            
            int textWidth = fm.getStringBounds(alteredLine, g).getBounds().width;
            int width = textWidth + iconWidth + formattedTextWidth;
            
            if (width >= maxWidth) {
                System.out.println("TEXT WIDTH:     " + textWidth);
                System.out.println("FTEXT WIDTH:    " + formattedTextWidth);
                System.out.println("ICONS WIDTH:    " + iconWidth);
                System.out.println("TOTAL WIDTH:    " + width);
                System.out.println("MAX WIDTH:      " + maxWidth);
                System.out.println("ELEMENT WIDTH:  " + layout.getWidth());
                System.out.println("ELEMENT MARGIN: " + layout.getMargin());
                
                lastElement = line.popElement();
                
                if (line.peekElement() instanceof ImageInsert) {
                    lastIcon = line.popElement();
                }
                
                System.out.println("LINE: " + line);
                
                lines.add(line);
                
                line = new MixedMediaText();
                nIcons = 0;
                iconWidth = 0;
                formattedTextWidth = 0;
                alteredLine = "";
                                
                if (lastIcon != null) {
                    iconWidth += resourceStore.getResource(lastIcon.getText()).getWidth();
                    line.addElement(lastIcon);
                    lastIcon = null;
                }
                line.addElement(lastElement);
                
                if (lastElement instanceof ImageInsert) {
                    iconWidth += resourceStore.getResource(lastElement.getText()).getWidth();
                } else if (lastElement instanceof BoldTextInsert) {
                    formattedTextWidth = g.getFontMetrics(new Font(layout.getCardFont().getFamily(), Font.BOLD, layout.getCardFont().getSize())).getStringBounds(" " + lastElement.getText(), g).getBounds().width;
                } else if (lastElement instanceof ItalicTextInsert) {
                    formattedTextWidth = g.getFontMetrics(new Font(layout.getCardFont().getFamily(), Font.ITALIC, layout.getCardFont().getSize())).getStringBounds(" " + lastElement.getText(), g).getBounds().width;
                } else {
                    alteredLine = lastElement.getText();
                }
            }
        }
        System.out.println("LINE: " + line);
        lines.add(line);
        
        return lines;
    }
    
    public static void drawLayer(ElementLayout layout, BufferedImage target) {
        if (layout.getLayer() == null) {
            return;
        }
        
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        Float alpha = layout.getTransparency().floatValue();
        
        int rule = AlphaComposite.SRC_OVER;
        Composite comp = AlphaComposite.getInstance(rule, alpha);
        g.setComposite(comp);
        
        //g.drawImage(layout.getLayer(), null, 0, 0);
        
        // Use this when we change the way we store card layers.
        g.drawImage(layout.getLayer(), null, layout.getX(), layout.getY());
    }
    
    public static void drawLayer(BufferedImage layerImage, BufferedImage target) {
        if (layerImage == null) {
            return;
        }
        
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        g.drawImage(layerImage, null, 0, 0);
    }
    
    public static void drawText(BufferedImage target, String text, ElementLayout layout) {
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setFont(layout.getFont());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        FontMetrics fm = g.getFontMetrics();
        
        int lineHeight = fm.getHeight();
        
        g.setColor(layout.getCardFont().getColor());
        g.drawString(text, layout.getX() + layout.getMargin(), layout.getY() + layout.getMargin() + lineHeight);
    }
    
    public static void drawMixedMediaText(BufferedImage target, ArrayList<MixedMediaText> lines, ElementLayout layout) {
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setFont(layout.getFont());
        g.setColor(layout.getCardFont().getColor());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        FontMetrics fm = g.getFontMetrics();
        int actualHeight = fm.getMaxAscent();
        int lineHeight = fm.getHeight();
        int index = 0;
        
        for (MixedMediaText mmt: lines) {
            MarkupElement me;
        
            int offset = 0;
            int spaceWidth = 0;
            int textBottom = (layout.getY() + layout.getMargin() + lineHeight) + (index * lineHeight);
            
            // Debugging lines
            /*
            g.setColor(Color.RED);
            g.drawLine(layout.getX(), textBottom, layout.getWidth(), textBottom);
            g.drawLine(layout.getX(), textBottom - actualHeight, layout.getWidth(), textBottom - actualHeight);
            g.setColor(layout.getCardFont().getColor());
            */
            
            while ((me = mmt.next()) != null) {
                if (me instanceof TextInsert) {
                    g.setFont(layout.getFont());
                    fm = g.getFontMetrics();
                    spaceWidth = fm.getStringBounds(" ", g).getBounds().width;
                    
                    g.drawString(me.getText(), layout.getX() + layout.getMargin() + offset, textBottom);
                    offset += fm.getStringBounds(me.getText(), g).getBounds().width + spaceWidth;
                } else if (me instanceof ImageInsert) {
                    Resource r = resourceStore.getResource(me.getText());
                    Integer larger = Math.max(r.getHeight(), actualHeight);
                    Integer diff = Math.abs(r.getHeight() - actualHeight);
                    Integer correction = (int)Math.round((double)diff/2.0);
                    Integer imageDelta = larger - correction;
                    g.drawImage(r.getImage(), null, layout.getX() + layout.getMargin() + offset, (textBottom - imageDelta));
                    offset += r.getWidth() + spaceWidth;
                } else if (me instanceof BoldTextInsert) {
                    CardFont cf = layout.getCardFont();
                    Font f = new Font(cf.getFamily(), Font.BOLD, cf.getSize());
                    
                    g.setFont(f);
                    fm = g.getFontMetrics();
                    spaceWidth = 0;
                    
                    g.drawString(me.getText(), layout.getX() + layout.getMargin() + offset, (layout.getY() + layout.getMargin() + lineHeight) + (index * lineHeight));
                    offset += fm.getStringBounds(me.getText(), g).getBounds().width + spaceWidth;
                } else if (me instanceof ItalicTextInsert) {
                    CardFont cf = layout.getCardFont();
                    Font f = new Font(cf.getFamily(), Font.ITALIC, cf.getSize());
                    
                    g.setFont(f);
                    fm = g.getFontMetrics();
                    //spaceWidth = fm.getStringBounds(" ", g).getBounds().width;
                    spaceWidth = 0;
                    
                    g.drawString(me.getText(), layout.getX() + layout.getMargin() + offset, (layout.getY() + layout.getMargin() + lineHeight) + (index * lineHeight));
                    offset += fm.getStringBounds(me.getText(), g).getBounds().width + spaceWidth;
                }
            }
            index++;
        }
    }
    
    public static void drawText(BufferedImage target, ArrayList<String> lines, ElementLayout layout) {
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setFont(layout.getFont());
        g.setColor(layout.getCardFont().getColor());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        FontMetrics fm = g.getFontMetrics();
        
        int index = 0;
        for (String line: lines) {
            int lineHeight = fm.getHeight();

            g.drawString(line, layout.getX() + layout.getMargin(), (layout.getY() + layout.getMargin() + lineHeight) + (index * lineHeight));
            
            index++;
        }
    }
    
    public static void drawTableCols(BufferedImage target, ArrayList<String> cells, int nCols, ElementLayout layout) {
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setFont(layout.getFont());
        g.setColor(layout.getCardFont().getColor());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        FontMetrics fm = g.getFontMetrics();
        
        //Get max width
        int maxWidth = 0;
        for (String cell: cells) {
            int width = fm.getStringBounds(cell, g).getBounds().width;
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        
        int nRows = (int)Math.ceil((double)cells.size()/(double)nCols);
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                String cell = cells.get((row * nCols) + col);
                
                int lineHeight = fm.getHeight();

                g.drawString(cell, (layout.getX() + layout.getMargin()) + ((maxWidth + layout.getMargin()) * col), (layout.getY() + layout.getMargin() + lineHeight) + (row * lineHeight));
            }
        }
    }
}