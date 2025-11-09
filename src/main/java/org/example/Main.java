package org.example;

import java.io.*;

public class Main {
    public static void main(String[] args) {


        String file = "src/main/java/org/example/input";
        try (FileReader fr = new FileReader(file)){
            BufferedReader br = new BufferedReader(fr);

            String line;
            StringBuilder input = new StringBuilder();
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n");
            }

            Parser parser = new Parser();
            parser.parse(input.toString());


        } catch (IOException e) {
            System.out.println("not finding the file");
            throw new RuntimeException(e);
        }

    }
}