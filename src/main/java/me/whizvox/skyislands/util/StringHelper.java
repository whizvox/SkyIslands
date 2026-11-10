package me.whizvox.skyislands.util;

import java.util.ArrayList;
import java.util.List;

public class StringHelper {

    public static String snakeCaseToLowerCamelCase(String str) {
        StringBuilder sb = new StringBuilder();
        boolean uppercaseNext = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                uppercaseNext = true;
            } else {
                if (uppercaseNext) {
                    sb.append(Character.toUpperCase(c));
                    uppercaseNext = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        return sb.toString();
    }

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
