package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class Main {
    public static void main(String[] args) {


        String file = "src/main/java/org/example/input";
        try (FileReader fr = new FileReader(file)){

            Scanner scanner = new Scanner(new PushbackReader(fr));

            while(true){
                TOKEN token = scanner.scan();
                String tokenString = scanner.getTokenString();
                if(token == TOKEN.SCANEOF) {
                    System.out.println(token);
                    break;
                }else{
                    System.out.println(token + ":" +tokenString);
                }
            }


        } catch (IOException e) {
            System.out.println("not finding the file");
            throw new RuntimeException(e);
        }

    }
}