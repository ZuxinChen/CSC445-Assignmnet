package org.example;

import org.junit.Test;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import static org.testng.AssertJUnit.assertEquals;

public class ScannerTest {


    @Test
    public void test() throws IOException{
        String input = """
                var score123
                init score = 600
                calculate newsalary = originalsalary + raise
                write salary
                if x = y then endif
                if x = y then write x endif
                while x != y do calculate x = x + 1 endwhile
                """;
        PushbackReader reader = new PushbackReader(new StringReader(input));
        Scanner scanner = new Scanner(reader);
        TOKEN token = scanner.scan();
        String buffer = scanner.getTokenBufferString();

        while(token != TOKEN.SCANEOF) {
            System.out.println(token+ ":" + buffer);
            token = scanner.scan();
            buffer = scanner.getTokenBufferString();
        }

        System.out.println(token); // print EOF
    }

    @Test
    public void emptyString_test() throws IOException {
        String input = "";
        PushbackReader reader = new PushbackReader(new StringReader(input));
        Scanner scanner = new Scanner(reader);
        TOKEN token = scanner.scan();
        String buffer = scanner.getTokenBufferString();

        assertEquals(TOKEN.SCANEOF, token);
        assertEquals("",buffer);
    }

}