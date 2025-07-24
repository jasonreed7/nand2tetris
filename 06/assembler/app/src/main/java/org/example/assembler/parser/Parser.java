package org.example.assembler.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final Pattern ADDRESS_REGEX = Pattern.compile("^@([\\w.$]+)$");
    private static final Pattern LABEL_REGEX = Pattern.compile("^\\(\\s*([\\w.$]+)\\s*\\)$");

    private String currLine;
    private String nextLine;

    private BufferedReader reader;

    public Parser(String asmFilePath) throws IOException {
        reader = new BufferedReader(new FileReader(asmFilePath));

        findNextLine();
    }

    public boolean hasMoreLines() {
        return nextLine != null;
    }

    public void advance() throws IOException {
        currLine = nextLine;
        nextLine = null;
        findNextLine();
    }

    public InstructionType instructionType() {
        if (currLine.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        } else if (currLine.startsWith("(")) {
            return InstructionType.L_INSTRUCTION;
        } else {
            return InstructionType.C_INSTRUCTION;
        }
    }

    public String symbol() {
        Matcher matcher;

        switch (instructionType()) {
            case A_INSTRUCTION:
                matcher = ADDRESS_REGEX.matcher(currLine);
                matcher.find();
                return matcher.group(1);
            case L_INSTRUCTION:
                matcher = LABEL_REGEX.matcher(currLine);
                matcher.find();
                return matcher.group(1);
            default:
                return null;
        }
    }

    public String comp() {
        int startIndex = 0;
        if (currLine.contains("=")) {
            startIndex = currLine.indexOf("=") + 1;
        }

        int endIndex = currLine.length();
        if (currLine.contains(";")) {
            endIndex = currLine.indexOf(";");
        }
        return currLine.substring(startIndex, endIndex).trim().replace(" ", "");
    }

    public String dest() {
        if (!currLine.contains("=")) {
            return null;
        }
        return currLine.substring(0, currLine.indexOf("=")).trim();
    }

    public String jump() {
        if (!currLine.contains(";")) {
            return null;
        }
        return currLine.substring(currLine.indexOf(";") + 1, currLine.length()).trim();
    }

    private void findNextLine() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (isCommentOrWhitespace(line)) {
                continue;
            } else {
                nextLine = line;
                if (nextLine.contains("//")) {
                    nextLine = nextLine.substring(0, nextLine.indexOf("//")).trim();
                }
                break;
            }
        }
    }

    private boolean isCommentOrWhitespace(String line) {
        return line.isBlank() || line.startsWith("//");
    }

}
