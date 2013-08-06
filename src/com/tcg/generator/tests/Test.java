/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcg.generator.tests;

import com.tcg.generator.cards.GenericCard;
import com.tcg.generator.config.ConfigHolder;
import java.io.File;

/**
 *
 * @author Michael
 */
public class Test {
    public static void main(String[] args) {
        ConfigHolder.setConfig("rootDirectory", "C:/tmp/tcg/");
        GenericCard vanguard = new GenericCard();
        vanguard.setCardName("Fluttershy")
                .setLayout(new File("C:/tmp/tcg/layouts/vanguard.layout"))
                .setArtwork(new File("C:/tmp/tcg/art/fluttershy-art.png"))
                .setCardData(
                "{" +
                    "\"grade\": 0," +
                    "\"trigger\": \"heal\"," +
                    "\"shield\": 10000," +
                    "\"power\": 10000," +
                    "\"clan\": \"green\"," +
                    "\"effects\": [" +
                        "\"[icon:cont] If you have a unit named [bold:Twilight Sparkle] on [icon:vanguard], then [bold:Fluttershy] gains [icon:sword]+5000 until end of turn.\"" +
                    "]" +
                "}");
        vanguard.draw(new File("card.png"));
    }
}
