package ru.nsu.logic.lang;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import ru.nsu.logic.lang.grammar.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers
                .newFor("L* Program translator")
                .build().defaultHelp(true);
        parser.addArgument("-i", "--input-file").required(true);

        try {
            final Namespace ns = parser.parseArgs(args);
            try (InputStream in = new FileInputStream(ns.getString("input_file"))) {
                LStatement statement = new LStatement(in);
                ASTLLangProgram program = statement.LLangProgram();
                program.dump("");

            } catch (FileNotFoundException | ParseException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }
}
