package org.example;

import java.util.ArrayList;

/**
 * Assignment 3 Abstract Syntax Tree
 */

public class AbsSynTree {
    NodeProgram nodeProgram;

    public NodeProgram getNodeProgram() {
        return nodeProgram;
    }

    public void setNodeProgram(NodeProgram nodeProgram) {
        this.nodeProgram = nodeProgram;
    }

    public void show(){
        nodeProgram.show();
    }

    public abstract class NodeBase{
        public abstract void show();
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
            System.out.println("AST id: " +name);
        }
    }
    public class NodeIntLiteral extends NodeExpr{
        public int value;

        public NodeIntLiteral(int value){
            this.value = value;
        }

        @Override
        public void show(){
            System.out.println("AST int literal " + value);
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
            System.out.println("AST plus");
            System.out.print("LHS:");
            left.show();
            System.out.print("RHS:");
            right.show();
        }
    }

    public class NodeWrite extends NodeStmt{
        public NodeId id;
        public NodeWrite(NodeId id){
            this.id = id;
        }

        @Override
        public void show() {
            System.out.println("AST write");
            id.show();
        }
    }

    public class NodeInit extends NodeStmt{
        public NodeId id;
        public NodeIntLiteral value;
        public NodeInit(NodeId id, NodeIntLiteral value){
            this.id = id;
            this.value = value;
        }

        @Override
        public void show() {
            System.out.println("AST init");
            id.show();
            value.show();
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
            System.out.println("AST calculate");
            id.show();
            expr.show();
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
            System.out.println("\nAST if");
            System.out.print("LHS:");
            left.show();
            System.out.print("RHS:");
            right.show();
            System.out.println("if body");
            stmts.show();
            System.out.println("\nAST endif");
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
            System.out.println("\nAST While");
            System.out.print("LHS: ");
            left.show();
            System.out.print("RHS: ");
            right.show();
            System.out.println("While body");
            stmts.show();
            System.out.println("\nAST endwhile");
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
            System.out.println("AST Variables");
            vars.show();
            System.out.println("\nAST Statements");
            stmts.show();
        }
    }
}
