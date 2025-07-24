package org.example.assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.example.assembler.code.Code;
import org.example.assembler.parser.InstructionType;
import org.example.assembler.parser.Parser;
import org.example.assembler.symbol.SymbolTable;

public class Assembler {

    private Parser firstPassParser;
    private Parser secondPassParser;
    private SymbolTable symbolTable;
    private int variableInsertAddress = 16;
    private BufferedWriter writer;

    public Assembler(String asmFilePath) throws IOException {
        firstPassParser = new Parser(asmFilePath);
        secondPassParser = new Parser(asmFilePath);
        symbolTable = new SymbolTable();

        String outputPath = asmFilePath.replace(".asm", ".hack");
        writer = new BufferedWriter(new FileWriter(outputPath));
    }

    public void assemble(String asmFilePath) throws IOException {
        firstPass();
        secondPass();

        close();
    }

    private void firstPass() throws IOException {
        int lineNum = 0;
        while (firstPassParser.hasMoreLines()) {
            firstPassParser.advance();

            if (firstPassParser.instructionType() == InstructionType.L_INSTRUCTION) {
                symbolTable.addEntry(firstPassParser.symbol(), lineNum);
            } else {
                lineNum++;
            }
        }
    }

    private void secondPass() throws IOException {
        while (secondPassParser.hasMoreLines()) {
            secondPassParser.advance();

            if (secondPassParser.instructionType() == InstructionType.A_INSTRUCTION) {
                handleAInstruction();
            } else if (secondPassParser.instructionType() == InstructionType.C_INSTRUCTION) {
                handleCInstruction();
            }
        }
    }

    private void handleAInstruction() throws IOException {
        String symbol = secondPassParser.symbol();

        int symbolAddress;

        if (!StringUtils.isNumeric(symbol)) {
            if (!symbolTable.contains(symbol)) {
                symbolTable.addEntry(symbol, variableInsertAddress);
                variableInsertAddress++;
            }

            symbolAddress = symbolTable.getAddress(symbol);
        } else {
            symbolAddress = Integer.parseInt(symbol);
        }

        String binaryCommand = StringUtils.leftPad(Integer.toBinaryString(symbolAddress), 16, "0");

        writeInstruction(binaryCommand);
    }

    private void handleCInstruction() throws IOException {
        String compBinary = Code.comp(secondPassParser.comp());
        String destBinary = Code.dest(secondPassParser.dest());
        String jumpBinary = Code.jump(secondPassParser.jump());

        String binaryCommand = "111" + compBinary + destBinary + jumpBinary;

        writeInstruction(binaryCommand);
    }

    private void writeInstruction(String binaryCommand) throws IOException {
        writer.write(binaryCommand);
        writer.newLine();
    }

    private void close() throws IOException {
        writer.close();
    }
}
