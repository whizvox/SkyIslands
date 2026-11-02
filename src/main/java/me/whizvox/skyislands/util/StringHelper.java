package me.whizvox.skyislands.util;

import java.util.ArrayList;
import java.util.List;

public class StringHelper {

    public static List<String> wordWrap(String line, int length) {
        if (line.length() <= length) {
            return List.of(line);
        }
        List<String> result = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        StringBuilder currentToken = new StringBuilder();
        line.chars().forEach(i -> {
            if (Character.isWhitespace(i)) {
                if (currentLine.length() + currentToken.length() > length) {
                    result.add(currentLine.toString().trim());
                    currentLine.setLength(0);
                }
                currentLine.append(currentToken).append((char) i);
                currentToken.setLength(0);
            } else {
                currentToken.append((char) i);
            }
        });
        if (currentLine.length() + currentToken.length() > length) {
            result.add(currentLine.toString().trim());
            result.add(currentToken.toString().trim());
        } else {
            result.add(currentLine + currentToken.toString().trim());
        }
        return result;
    }

}
