package org.example;

import java.io.IOException;
import java.io.PushbackReader;

public class Scanner {

    private final String[] reservedWords;
    private final PushbackReader pbr;
    private final StringBuilder TokenString = new StringBuilder();

    // constructor
    public Scanner (PushbackReader pbr){
        this.pbr = pbr;
        reservedWords = new String[]{"var", "write", "init", "if", "then","endif", "while","do","endwhile","calculate"};
    }

    public String getTokenString() {
        return TokenString.toString();
    }

    private int readNextChar() {
        try {
            return pbr.read();
        } catch (IOException e) {
            System.out.println("Error reading from input");
            throw new RuntimeException(e);
        }
    }

    private void unreadChar(int c) {
        try {
            pbr.unread(c);
        } catch (IOException e) {
            System.out.println("can't bush back");
            throw new RuntimeException(e);
        }
    }

    // scan one token at a time
    public TOKEN scan() throws IOException {

        int c;
        while((c = readNextChar())!= -1){
            if (isWhitespace(c)) {
                continue;
            }

            if (Character.isDigit(c)) {
                return digitToken(c);
            }else if (Character.isLetter(c)) {
                return charaterToken(c);
            }else{
                return operatorToken(c);
            }


        }

        return TOKEN.SCANEOF;
    }


    public String getTokenBufferString(){
        return TokenString.toString();
    }

    private TOKEN operatorToken(int c){
        //operator
        TokenString.setLength(0); // clear the buffer
        TokenString.append((char) c);
        switch (c) {
            case '+':
                return TOKEN.PLUS;
            case '-':
                return TOKEN.MINUS;
            case '=':
                return TOKEN.EQUALS;
            case '"':
                return StringToken(c);
            case '!':
                c = readNextChar();
                TokenString.append((char) c);
                if(c == '=')
                    return TOKEN.NOTEQUALS;
                else
                    unreadChar(c);
                break;
        }

        return null;
    }


    // read string between " " all characters include
    private TOKEN StringToken(int c){
        TokenString.setLength(0); // clear the buffer

        do{
            TokenString.append((char) c);
            c = readNextChar();

        }while (c != -1 && c != '"');

        TokenString.append((char) c); // add end " to string buffer

        return TOKEN.STRING;
    }


    private TOKEN digitToken(int c){
        TokenString.setLength(0); // clear the buffer
        do{
            TokenString.append((char) c);
            c = readNextChar();
        }while (c != -1 && Character.isDigit(c));
        unreadChar(c);
        return TOKEN.INTLITERAL;
    }

    private TOKEN charaterToken(int c){
        TokenString.setLength(0); // clear the buffer
        do{

            TokenString.append((char) c);
            c = readNextChar();
        }while (c != -1 && (Character.isLetter(c) || Character.isDigit(c)));
        unreadChar(c);
        for (String reservedWord : reservedWords) {
            if (TokenString.toString().equals(reservedWord)) {
                return TOKEN.valueOf(TokenString.toString().toUpperCase());
            }
        }
        return TOKEN.ID;
    }

    /**
     * check if a character is whitespace
     * - 32 is a blank space
     * - 9 is a tab
     * - 10 is a linefeed (this means go to the next line)
     * - 13 is a carriage return (go to the start of the line
     * @param c character to check
     * @return true if whitespace, false otherwise
     */
    private boolean isWhitespace(int c) {
        return (c == 32) || (c == 9) || (c == 10) || (c == 13);
    }


}
