package org.example;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * CSC-Assignment 2 , for parsing , to analyte the stream from scanner
 */

public class Parser {
    Map<String, SymbolTableItem> collection = new HashMap<>();
    Scanner scanner;
    TOKEN nextToken;



    /**
     * match next token with expected token
     *
     * @return ture when token is match, and print token and its string;
     *        false when it is not match, and print error message
     */
    private boolean match(TOKEN expectedToken){

        try {
            if (nextToken == expectedToken) {
                System.out.println(expectedToken + ": " + scanner.getTokenBufferString());
                nextToken = scanner.scan();
                return true;
            }else {
                String str = "Parse Error" +
                        "\nExpected: " + expectedToken +
                        "\nReceived: " + nextToken +
                        "\nBuffer: " + scanner.getTokenBufferString();
                error(str);
            }
        }catch (IOException e){
            error("Error Scanning");
        }

        return false;

    }

    /**
     * print error message and exit
     */
    private void error(String message){
        System.out.println(message);
        System.exit(1);
    }

    /**
     * add variable to symbol table, when it is not exist
     */
    private void addVarToTable(){
        SymbolTableItem item = new SymbolTableItem();
        item.name = scanner.getTokenBufferString();
        item.type = TYPE.INTDATATYPE;
        if(!collection.containsKey(item.name)) {
            collection.put(item.name, item);
        }else {
            error("Duplicate Variable " + item.name);
        }

    }

    /**
     * parse program， Program is state method
     */
    public boolean parse(String program) {
        scanner = new Scanner(new PushbackReader(new StringReader(program)));
        try {
            nextToken = scanner.scan();
            Program();
            return true;
        } catch (IOException e) {
            error("Error parsing program");
            return false;
        }
    }

    //-----------------------------Grammar-----------------------------

    /**
     * <Program> ::= <Vars> <Stmts> $
     * $ stands for end of file (SCANEOF).
     */
    private void Program(){
        Vars();
        Stmts();

        if(nextToken == TOKEN.SCANEOF){
            System.out.println("Parsing completed successfully");
        }else {
            error("Unmatched EOF, unexpected token " + nextToken);
        }
    }

    /**
     * <Vars> ::= <Var> <Vars>
     * <Vars> ::= ""
     * First+(Vars) = {var, eof}
     */
    private void Vars(){
        if(nextToken == TOKEN.VAR){
            Var();
            Vars();
        }

    }

    /**
     * <Var> ::= var id
     * First+(Var) = {var}
     */
    private void Var(){
        if(match(TOKEN.VAR)){
            if(match(TOKEN.ID)){
                addVarToTable();
                return;
            }
        }

        error("Var() failed, Stop on Token: " + nextToken);

    }

    /**
     * <Stmts> ::= <Stmt> <Stmts>
     * <Stmts> ::= ""
     * First+(Stmts) = {write, init, if, while, calculate, eof}
     */
    private void Stmts(){
        if(nextToken == TOKEN.WRITE || nextToken == TOKEN.INIT ||
                nextToken == TOKEN.IF || nextToken == TOKEN.WHILE ||
                nextToken == TOKEN.CALCULATE){
            Stmt();
            Stmts();
        }

    }


    /**
     * <Stmt> ::= write id
     * <Stmt> ::= init id equals intliteral
     * <Stmt> ::= if id equals id then <Stmts> endif
     * <Stmt> ::= while id notequals id do <Stmts> endwhile
     * <Stmt> ::= calculate id equals <Add>
     * First+(Stmt) = {write, init, if, while, calculate}
     */
    private void Stmt(){
        if(nextToken == TOKEN.WRITE){
            if(match(TOKEN.WRITE)){
                if(match(TOKEN.ID)){
                    return;
                }
            }
        }

        else if(nextToken == TOKEN.INIT){
            if(match(TOKEN.INIT)){
                if(match(TOKEN.ID)){
                    if(match(TOKEN.EQUALS)){
                        if(match(TOKEN.INTLITERAL)){
                            return;
                        }
                    }
                }
            }
        }

        else if(nextToken == TOKEN.IF){
            if(match(TOKEN.IF)){
                if(match(TOKEN.ID)){
                    if(match(TOKEN.EQUALS)){
                        if(match(TOKEN.ID)){
                            if(match(TOKEN.THEN)){
                                Stmts();
                                if(match(TOKEN.ENDIF)){
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        else if(nextToken == TOKEN.WHILE){
            if(match(TOKEN.WHILE)){
                if(match(TOKEN.ID)){
                    if(match(TOKEN.NOTEQUALS)){
                        if(match(TOKEN.ID)){
                            if(match(TOKEN.DO)){
                                Stmts();
                                if(match(TOKEN.ENDWHILE)){
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        else if(nextToken == TOKEN.CALCULATE){
            if(match(TOKEN.CALCULATE)){
                if(match(TOKEN.ID)){
                    if(match(TOKEN.EQUALS)){
                        Add();
                        return;
                    }
                }
            }
        }

        error("Stmt() failed, Stop on Token: " + nextToken);



    }

    /**
     * <Add> ::= <Value> <AddEnd>
     * First+(Add) = {id, intliteral}
     */
    private void Add(){
        if(nextToken == TOKEN.ID || nextToken == TOKEN.INTLITERAL){
            Value();
            AddEnd();
        }else {
            error("Add() failed, Stop on Token: " + nextToken);
        }
    }

    /**
     * <AddEnd> ::= plus <Value> <AddEnd>
     * <AddEnd> ::= ""
     * First+(AddEnd) = {plus, eof}
     */
    private void AddEnd(){
        if(nextToken == TOKEN.PLUS){
            if(match(TOKEN.PLUS)){
                Value();
                AddEnd();
            }
        }
    }

    /**
     * <Value> ::= id
     * <Value> ::= intliteral
     * First+(Value) = {id, intliteral}
     */
    private void Value(){
        if(nextToken == TOKEN.ID || nextToken == TOKEN.INTLITERAL){
            match(nextToken);
        } else {
            error("Value() failed, Stop on Token: " + nextToken);
        }

    }

}
