package org.example;

import program.PseudoAssemblyWithStringProgram;

import java.io.*;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Parser parser = new Parser();
        String inputFile = "src/main/java/org/example/input";
        String outputFile = "src/main/java/org/example/output";

        String input = readFile(inputFile);
        parser.parse(input);

        String data = data(parser.collection);
        String code = parser.getAbsSynTree().getCode();
        String assembleCode = data + code;
        pushToFile(assembleCode, outputFile);

        String output = readFile(outputFile);
        //System.out.println(output);
        runAssembler(output);

    }

    public static String readFile(String file){
        try (FileReader fr = new FileReader(file)){
            BufferedReader br = new BufferedReader(fr);

            String line;
            StringBuilder input = new StringBuilder();
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n");
            }

            return input.toString();

        } catch (IOException e) {
            System.out.println("not finding the file");
            throw new RuntimeException(e);
        }

    }

    public static void pushToFile(String assembleCode, String outputFile){
        try (FileWriter fw = new FileWriter(outputFile)){
            fw.write(assembleCode);
        } catch (IOException e) {
            System.out.println("not finding the file");
            throw new RuntimeException(e);
        }
    }


    public static String data(Map<String, SymbolTableItem> collection){
        StringBuilder data = new StringBuilder(".data\n");
        for(String key : collection.keySet()){
            SymbolTableItem item = collection.get(key);
            if(item.type == TYPE.INTDATATYPE){
                data.append("var int ").append(item.name).append("\n");
            }
        }

        //System.out.println(data);
        return data.toString();
    }

    public static void runAssembler(String code){
//        String code = "";
//        code += ".data\n";
//        code += "var int x\n";
//        code += ".code\n";
//        code += "loadintliteral ri1, 88\n";
//        code += "storeintvar ri1, x\n";
//        code += "printi x\n";
//        code += "printi ri1\n";


        int numVirtualRegistersInt = 32;
        int numVirtualRegistersString = 32;
        String outputClassName = "MyLabProgram";
        String outputPackageNameDot = "mypackage";
        String classRootDir = System.getProperty("user.dir") + "/" + "target/classes";
        PseudoAssemblyWithStringProgram pseudoAssemblyWithStringProgram = new
                PseudoAssemblyWithStringProgram(
                code,
                outputClassName,
                outputPackageNameDot,
                classRootDir,
                numVirtualRegistersInt,
                numVirtualRegistersString
        );
        boolean parseSuccessful;
        parseSuccessful = pseudoAssemblyWithStringProgram.parse();
        if (parseSuccessful) {
            // Creates a Java bytecode class file
            pseudoAssemblyWithStringProgram.generateBytecode();
            // Run the Java bytecode class file and show output on the console
            PrintStream outstream = new PrintStream(System.out);
            pseudoAssemblyWithStringProgram.run(outstream);
        }
    }

}