package me.pignol.swift.api.util;

public class TextUtil {

    public static final String SECTIONSIGN = "\u00A7"; //§

    public static final String BLACK = SECTIONSIGN + "0";
    public static final String DARK_BLUE = SECTIONSIGN + "1";
    public static final String DARK_GREEN = SECTIONSIGN + "2";
    public static final String DARK_AQUA = SECTIONSIGN + "3";
    public static final String DARK_RED = SECTIONSIGN + "4";
    public static final String DARK_PURPLE = SECTIONSIGN + "5";
    public static final String GOLD = SECTIONSIGN + "6";
    public static final String GRAY = SECTIONSIGN + "7";
    public static final String DARK_GRAY = SECTIONSIGN + "8";
    public static final String BLUE = SECTIONSIGN + "9";
    public static final String GREEN = SECTIONSIGN + "a";
    public static final String AQUA = SECTIONSIGN + "b";
    public static final String RED = SECTIONSIGN + "c";
    public static final String LIGHT_PURPLE = SECTIONSIGN + "d";
    public static final String YELLOW = SECTIONSIGN + "e";
    public static final String WHITE = SECTIONSIGN + "f";
    public static final String OBFUSCATED = SECTIONSIGN + "k";
    public static final String BOLD = SECTIONSIGN + "l";
    public static final String STRIKE = SECTIONSIGN + "m";
    public static final String UNDERLINE = SECTIONSIGN + "n";
    public static final String ITALIC = SECTIONSIGN + "o";
    public static final String RESET = SECTIONSIGN + "r";

    public enum Color {
        NONE, WHITE, BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW
    }

    public static String coloredString(String string, Color color) {
        String coloredString = string;
        switch (color) {
            case AQUA:
                coloredString = AQUA + coloredString + RESET;
                break;
            case WHITE:
                coloredString = WHITE + coloredString + RESET;
                break;
            case BLACK:
                coloredString = BLACK + coloredString + RESET;
                break;
            case DARK_BLUE:
                coloredString = DARK_BLUE + coloredString + RESET;
                break;
            case DARK_GREEN:
                coloredString = DARK_GREEN + coloredString + RESET;
                break;
            case DARK_AQUA:
                coloredString = DARK_AQUA + coloredString + RESET;
                break;
            case DARK_RED:
                coloredString = DARK_RED + coloredString + RESET;
                break;
            case DARK_PURPLE:
                coloredString = DARK_PURPLE + coloredString + RESET;
                break;
            case GOLD:
                coloredString = GOLD + coloredString + RESET;
                break;
            case DARK_GRAY:
                coloredString = DARK_GRAY + coloredString + RESET;
                break;
            case GRAY:
                coloredString = GRAY + coloredString + RESET;
                break;
            case BLUE:
                coloredString = BLUE + coloredString + RESET;
                break;
            case RED:
                coloredString = RED + coloredString + RESET;
                break;
            case GREEN:
                coloredString = GREEN + coloredString + RESET;
                break;
            case LIGHT_PURPLE:
                coloredString = LIGHT_PURPLE + coloredString + RESET;
                break;
            case YELLOW:
                coloredString = YELLOW + coloredString + RESET;
                break;
            default :
        }
        return coloredString;
    }

}
