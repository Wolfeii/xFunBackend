package se.xfunserver.xfunbackend.assets.letters;

import se.xfunserver.xfunbackend.assets.letters.object.Letter;
import se.xfunserver.xfunbackend.assets.letters.object.Number;

import java.util.ArrayList;
import java.util.List;

public class LetterGenerator {

    public List<List<StringBuilder>> generateFromText(String text){
        List<List<StringBuilder>> final_builder = new ArrayList<>();
        for (String word : text.split(" ")) {
            List<StringBuilder> word_builder = new ArrayList<>();
            String[] letterArray = word.split("");
            for (int i = 0; i < letterArray.length; i++) {
                StringBuilder b;

                if (letterArray[i].matches(".*\\d+.*")) {
                    Number number = Number.getNumber(Integer.parseInt(letterArray[i]));
                    for (int c = 0; c <= number.getConverted().length - 1; c++) {
                        if (!word_builder.isEmpty() &&
                                i != 0) {
                            b = word_builder.get(c);
                        } else {
                            b = new StringBuilder();
                        }
                        b.append(number.getConverted()[c]);

                        if (i == 0) {
                            word_builder.add(b);
                        }
                    }
                } else {
                    Letter letter = Letter.valueOf(letterArray[i].toUpperCase());
                    for (int c = 0; c <= letter.getConverted().length - 1; c++) {
                        if (!word_builder.isEmpty() &&
                                i != 0) {
                            b = word_builder.get(c);
                        } else {
                            b = new StringBuilder();
                        }
                        b.append(letter.getConverted()[c]);

                        if (i == 0) {
                            word_builder.add(b);
                        }
                    }
                }
            }
            final_builder.add(word_builder);
        }
        return final_builder;
    }
}
