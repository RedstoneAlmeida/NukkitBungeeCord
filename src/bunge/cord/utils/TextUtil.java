package bunge.cord.utils;

/**
 * Created by ASUS on 21/02/2018.
 */
public class TextUtil {

    public static void print(String msg){
        print(msg, false);
    }

    public static void print(String msg, boolean firstEmpty){
        if(firstEmpty)
            System.out.println(" ");
        System.out.println(msg);
    }

    public static void printFirstEnd(String msg){
        print(msg, true);
        System.out.println(" ");
    }

}
