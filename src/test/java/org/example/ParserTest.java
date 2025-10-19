package org.example;

import org.junit.Test;
import java.io.IOException;

public class ParserTest {

    @Test
    public void test1_CorrectProgram(){
        Parser parser = new Parser();
        String correctProgram =
                """
                var a
                var b
                init a = 1
                init b = 5
                while a != b do
                    calculate a = a + 1
                    write a
                    if a=b then
                        write a
                    endif
                endwhile
                """;
        parser.parse(correctProgram);
    }

    @Test
    public void test2_IncorrectProgram(){
        Parser parser = new Parser();
        String incorrectProgram =
                """
                var a
                var b
                init a = 5
                init b = 10
                write a
                write b
                if a b then
                    write a
                endif
                """;
        parser.parse(incorrectProgram);

    }

}
