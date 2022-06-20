/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.utils;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
public class StringUtils {
    public static String pad(String input, char pad, String sep, int len) {
        input = len > 0 ? sep + input : input + sep;
        return String.format("%" + len + "s", input).replace(' ', pad);
    }

    public static String pad(String input, char pad, int len) {
        return pad(input, pad, "", len);
    }
}
