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
    String buffer;
    AbsSynTree absSynTree = new AbsSynTree();


    public AbsSynTree getAbsSynTree(){
        return absSynTree;
    }


    /**
     * match next token with expected token
     */
    private boolean match(TOKEN expectedToken){
        buffer = scanner.getTokenBufferString();
        try {
            if (nextToken == expectedToken) {
                //System.out.println(expectedToken + ": " + buffer);
                nextToken = scanner.scan();
                return true;
            }else {
                String str = "Parse Error" +
                        "\nExpected: " + expectedToken +
                        "\nReceived: " + nextToken +
                        "\nBuffer: " + buffer;
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
        item.name = buffer;
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
            absSynTree.setNodeProgram(Program());
            absSynTree.show();
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
    private AbsSynTree.NodeProgram Program(){
        AbsSynTree.NodeVars vars;
        AbsSynTree.NodeStmts stmts;

        vars = Vars();
        stmts = Stmts();

        if(nextToken == TOKEN.SCANEOF){
            System.out.println("Parsing completed successfully");
        }else {
            error("Unmatched EOF, unexpected token " + nextToken);
        }

        return absSynTree. new NodeProgram(vars, stmts);
    }

    /**
     * <Vars> ::= <Var> <Vars>
     * <Vars> ::= ""
     * First+(Vars) = {var, eof}
     */
    private AbsSynTree.NodeVars Vars(){

        if(nextToken == TOKEN.VAR){
            AbsSynTree.NodeId id = Var();
            AbsSynTree.NodeVars vars = Vars();
            vars.add(id);

            return vars;
        }
        return absSynTree. new NodeVars();
    }

    /**
     * <Var> ::= var id
     * First+(Var) = {var}
     */
    private AbsSynTree.NodeId Var(){
        if(match(TOKEN.VAR)){
            if(match(TOKEN.ID)){
                addVarToTable();
                String name = buffer;
                return absSynTree.new NodeId(name);
            }
        }

        error("Var() failed, Stop on Token: " + nextToken);

        return null;
    }

    /**
     * <Stmts> ::= <Stmt> <Stmts>
     * <Stmts> ::= ""
     * First+(Stmts) = {write, init, if, while, calculate, eof}
     */
    private AbsSynTree.NodeStmts Stmts(){
        AbsSynTree.NodeStmt stmt;
        AbsSynTree.NodeStmts stmts;
        if(nextToken == TOKEN.WRITE || nextToken == TOKEN.INIT ||
                nextToken == TOKEN.IF || nextToken == TOKEN.WHILE ||
                nextToken == TOKEN.CALCULATE){
            stmt = Stmt();
            stmts = Stmts();
            stmts.add(stmt);

            return stmts;
        }
        
        return absSynTree. new NodeStmts();
    }


    /**
     * <Stmt> ::= write id
     * <Stmt> ::= init id equals intliteral
     * <Stmt> ::= if id equals id then <Stmts> endif
     * <Stmt> ::= while id notequals id do <Stmts> endwhile
     * <Stmt> ::= calculate id equals <Add>
     * First+(Stmt) = {write, init, if, while, calculate}
     */
    private AbsSynTree.NodeStmt Stmt(){
        if(nextToken == TOKEN.WRITE){
            if(match(TOKEN.WRITE)){
                if(match(TOKEN.ID)){
                    AbsSynTree.NodeId id = absSynTree.new NodeId(buffer);
                    return absSynTree.new NodeWrite(id);
                }
            }
        }

        else if(nextToken == TOKEN.INIT){
            if(match(TOKEN.INIT)){
                if(match(TOKEN.ID)){
                    AbsSynTree.NodeId id = absSynTree.new NodeId(buffer);

                    if(match(TOKEN.EQUALS)){
                        if(match(TOKEN.INTLITERAL)){
                            int intLiteral = Integer.parseInt(buffer);
                            AbsSynTree.NodeIntLiteral value = absSynTree.new NodeIntLiteral(intLiteral);
                            return absSynTree.new NodeInit(id, value);
                        }
                    }
                }
            }
        }

        else if(nextToken == TOKEN.IF){
            if(match(TOKEN.IF)){
                if(match(TOKEN.ID)){
                    AbsSynTree.NodeId id1 = absSynTree.new NodeId(buffer);
                    if(match(TOKEN.EQUALS)){
                        if(match(TOKEN.ID)){
                            AbsSynTree.NodeId id2 = absSynTree.new NodeId(buffer);

                            if(match(TOKEN.THEN)){
                                AbsSynTree.NodeStmts stmts = Stmts();
                                if(match(TOKEN.ENDIF)){
                                    return absSynTree.new NodeIf(id1, id2, stmts);
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
                    AbsSynTree.NodeId id1 = absSynTree.new NodeId(buffer);

                    if(match(TOKEN.NOTEQUALS)){
                        if(match(TOKEN.ID)){
                            AbsSynTree.NodeId id2 = absSynTree.new NodeId(buffer);

                            if(match(TOKEN.DO)){
                                AbsSynTree.NodeStmts stmts = Stmts();
                                if(match(TOKEN.ENDWHILE)){
                                    return absSynTree. new NodeWhile(id1, id2, stmts);
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
                    AbsSynTree.NodeId id = absSynTree.new NodeId(buffer);

                    if(match(TOKEN.EQUALS)){
                        AbsSynTree.NodeExpr expr = Add();
                        return absSynTree.new NodeCalculate(id, expr);
                    }
                }
            }
        }

        error("Stmt() failed, Stop on Token: " + nextToken);


        return null;
    }

    /**
     * <Add> ::= <Value> <AddEnd>
     * First+(Add) = {id, intliteral}
     */
    private AbsSynTree.NodePlus Add(){
        AbsSynTree.NodeExpr id1 = null;
        AbsSynTree.NodeExpr id2 = null;
        if(nextToken == TOKEN.ID || nextToken == TOKEN.INTLITERAL){
            id1 = Value();
            id2 = AddEnd();
        }else {
            error("Add() failed, Stop on Token: " + nextToken);
        }

        return absSynTree. new NodePlus(id1, id2);
    }

    /**
     * <AddEnd> ::= plus <Value> <AddEnd>
     * <AddEnd> ::= ""
     * First+(AddEnd) = {plus, eof}
     */
    private AbsSynTree.NodeExpr AddEnd(){
        AbsSynTree.NodeExpr expr1;
        AbsSynTree.NodeExpr expr2;
        if(nextToken == TOKEN.PLUS){
            if(match(TOKEN.PLUS)){
                expr1 = Value();
                expr2 = AddEnd();

                if(expr2 == null){
                    return expr1;
                }

                return absSynTree.new NodePlus(expr1, expr2);
            }
        }
        return null;
    }

    /**
     * <Value> ::= id
     * <Value> ::= intliteral
     * First+(Value) = {id, intliteral}
     */
    private AbsSynTree.NodeExpr Value(){
        if(nextToken == TOKEN.ID || nextToken == TOKEN.INTLITERAL){

            if (nextToken == TOKEN.ID){
                match(TOKEN.ID);
                String name = buffer;
                return absSynTree.new NodeId(name);
            } else {

                match(TOKEN.INTLITERAL);
                int intLiteral = Integer.parseInt(buffer);
                return absSynTree.new NodeIntLiteral(intLiteral);
            }

        } else {
            error("Value() failed, Stop on Token: " + nextToken);
        }

        return null;
    }

}
