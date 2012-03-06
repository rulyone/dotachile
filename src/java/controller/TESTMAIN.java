/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author rulyone
 */
public class TESTMAIN {

    public static void main(String[] args) {
        System.out.println(cuentaValida("asdsada__"));
    }

    public static void test(String w3username) {

        if (!(w3username.contains("-") || w3username.contains("_")
                || w3username.contains("[") || w3username.contains("]")
                || w3username.contains(".") || w3username.contains("^"))
                && w3username.matches("\\w+")) {
            System.out.println("HOLA");
        }
    }

    public static void test2(String str) {
        System.out.println(str.matches("\\w+"));
    }
    
    public static boolean cuentaValida(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!(c == '-' || c == '_' || c == '^' || c == '[' || c == ']' || c == '.' || Character.isLetter(c) || Character.isDigit(c))) {
                return false;
            }
        }
        return true;
    }
}
