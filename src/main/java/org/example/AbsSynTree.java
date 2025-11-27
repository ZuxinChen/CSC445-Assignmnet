package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Assignment 3 Abstract Syntax Tree
 */

public class AbsSynTree {
    NodeProgram nodeProgram;
    List<String> ASTProgram = new ArrayList<>();
    List<String> assemblyCode = new ArrayList<>();
    private int register = 1;
    private int labelCount = 1;
    private final String registerName = "ri";

    public NodeProgram getNodeProgram() {
        return nodeProgram;
    }

    public void setNodeProgram(NodeProgram nodeProgram) {
        this.nodeProgram = nodeProgram;
    }

    public void show(){
        nodeProgram.show();
        //System.out.println(String.join("\n", ASTProgram));
    }

    public String getCode(){
        nodeProgram.createCode();
        return String.join("\n", assemblyCode);
    }

    public abstract class NodeBase{
        public abstract void show();
        public abstract String createCode();
    }

    public abstract class NodeExpr extends NodeBase{
        public abstract void show();
    }
    public abstract class NodeStmt extends NodeBase{
        public abstract void show();
    }
    public class NodeId extends NodeExpr{
        public String name;

        public NodeId(String name){
            this.name = name;
        }

        @Override
        public void show(){
            ASTProgram.add("AST id: " +name);
        }

        @Override
        public String createCode() {
            String reg = registerName + register++;
            String code = String.format("loadintvar %s,%s", reg, name);
            assemblyCode.add(code);
            return reg;
        }
    }
    public class NodeIntLiteral extends NodeExpr{
        public int value;

        public NodeIntLiteral(int value){
            this.value = value;
        }

        @Override
        public void show(){
            ASTProgram.add("AST int literal " + value);
        }

        @Override
        public String createCode() {
            String reg = registerName + register++;
            String code = String.format("loadintliteral %s, %d", reg, value);
            assemblyCode.add(code);
            return reg;
        }
    }
    public class NodePlus extends NodeExpr{
        public NodeExpr left;
        public NodeExpr right;
        public NodePlus(NodeExpr left, NodeExpr right){
            this.left = left;
            this.right = right;
        }
        @Override
        public void show(){
            ASTProgram.add("AST plus");
            ASTProgram.add("LHS:");
            left.show();
            ASTProgram.add("RHS:");
            right.show();
        }

        @Override
        public String createCode() {
            String leftRegister = left.createCode();
            String rightRegister = right.createCode();
            String resultRegister = registerName + register++;
            String code = String.format("add %s,%s,%s", leftRegister, rightRegister, resultRegister);
            assemblyCode.add(code);
            return resultRegister;
        }
    }

    public class NodeWrite extends NodeStmt{
        public NodeId id;
        public NodeWrite(NodeId id){
            this.id = id;
        }

        @Override
        public void show() {
            ASTProgram.add("AST write");
            id.show();
        }

        @Override
        public String createCode() {
            String code = String.format("printi %s", id.name);
            assemblyCode.add(code);
            return "";
        }
    }

    public class NodeInit extends NodeStmt{
        public NodeId id;
        public NodeIntLiteral var;
        public NodeInit(NodeId id, NodeIntLiteral value){
            this.id = id;
            this.var = value;
        }

        @Override
        public void show() {
            ASTProgram.add("AST init");
            id.show();
            var.show();
        }

        @Override
        public String createCode() {
            String reg = var.createCode();

            String code = String.format("storeintvar %s, %s", reg, id.name);

            assemblyCode.add(code);

            return reg;
        }
    }

    public class NodeCalculate extends NodeStmt{
        public NodeId id;
        public NodeExpr expr;
        public NodeCalculate(NodeId id, NodeExpr expr){
            this.id = id;
            this.expr = expr;
        }

        @Override
        public void show() {
            ASTProgram.add("AST calculate");
            id.show();
            expr.show();
        }

        @Override
        public String createCode() {
            String reg = expr.createCode();
            String code = String.format("storeintvar %s, %s", reg, id.name);
            assemblyCode.add(code);
            return "";
        }
    }

    public class NodeStmts extends NodeBase{
        public ArrayList<NodeStmt> stmts = new ArrayList<>();

        public void add(NodeStmt stmt){
            stmts.addFirst(stmt);
        }
        @Override
        public void show() {
            for(NodeStmt stmt : stmts){
                stmt.show();
            }
        }

        @Override
        public String createCode() {

            for(NodeStmt stmt : stmts){
                assemblyCode.add("");
                stmt.createCode();
            }
            return "";
        }
    }

    public class NodeIf extends NodeStmt{
        public NodeId left;
        public NodeId right;
        public NodeStmts stmts;
        public NodeIf(NodeId left, NodeId right, NodeStmts stmts){
            this.left = left;
            this.right = right;
            this.stmts = stmts;
        }

        @Override
        public void show() {
            ASTProgram.add("\nAST if");
            ASTProgram.add("LHS:");
            left.show();
            ASTProgram.add("RHS:");
            right.show();
            ASTProgram.add("if body");
            stmts.show();
            ASTProgram.add("\nAST endif");
        }

        @Override
        public String createCode() {
            String reg1 = left.createCode();
            String reg2 = right.createCode();
            String endIfLabel = String.format("label%d", labelCount++);
            assemblyCode.add(";if");
            String code1 = String.format("bne %s, %s, %s", reg1, reg2, endIfLabel);
            assemblyCode.add(code1);

            String code2 = stmts.createCode();
            assemblyCode.add(code2);

            String code3 = String.format(":%s", endIfLabel);
            assemblyCode.add(code3);
            return "";
        }
    }

    public class NodeWhile extends NodeStmt{
        public NodeId left;
        public NodeId right;
        public NodeStmts stmts;
        public NodeWhile(NodeId left, NodeId right, NodeStmts stmts){
            this.left = left;
            this.right = right;
            this.stmts = stmts;
        }

        @Override
        public void show() {
            ASTProgram.add("\nAST While");
            ASTProgram.add("LHS: ");
            left.show();
            ASTProgram.add("RHS: ");
            right.show();
            ASTProgram.add("While body");
            stmts.show();
            ASTProgram.add("\nAST endwhile");
        }

        @Override
        public String createCode() {


            String whileLabel = String.format("label%d", labelCount++);
            String endWhileLabel = String.format("label%d", labelCount++);

            String code1 = String.format(":%s", whileLabel);
            assemblyCode.add(code1);

            String reg1 = left.createCode();
            String reg2 = right.createCode();
            assemblyCode.add(";While");
            String code2 = String.format("be %s, %s, %s", reg1, reg2, endWhileLabel);
            assemblyCode.add(code2);

            String code3 = stmts.createCode();
            assemblyCode.add(code3);

            String code4 = String.format("branch %s", whileLabel);
            assemblyCode.add(code4);

            String code5 = String.format(":%s", endWhileLabel);
            assemblyCode.add(code5);

            return "";
        }
    }

    public class NodeVars extends NodeBase{
        public ArrayList<NodeId> vars = new ArrayList<>();

        public NodeVars(){
        }

        public void add(NodeId id){
            vars.addFirst(id);
        }

        @Override
        public void show() {
            for(NodeId id : vars){
                id.show();
            }
        }

        @Override
        public String createCode() {
            for(NodeId id : vars){
                id.createCode();
            }
            return "";
        }
    }

    public class NodeProgram extends NodeBase{
        public NodeVars vars;
        public NodeStmts stmts;
        public NodeProgram(NodeVars vars, NodeStmts stmts){
            this.vars = vars;
            this.stmts = stmts;
        }


        @Override
        public void show() {
            ASTProgram.add("AST Variables");
            vars.show();
            ASTProgram.add("\nAST Statements");
            stmts.show();
        }

        @Override
        public String createCode() {
            assemblyCode.add("\n.code");
            vars.createCode();
            stmts.createCode();
            return "";
        }
    }
}
