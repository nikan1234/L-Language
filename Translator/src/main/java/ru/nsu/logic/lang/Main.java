package ru.nsu.logic.lang;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import ru.nsu.logic.lang.ast.ASTLLangProgram;
import ru.nsu.logic.lang.ast.LStatement;
import ru.nsu.logic.lang.ast.ParseException;
import ru.nsu.logic.lang.compilation.common.CompilationException;
import ru.nsu.logic.lang.compilation.compiler.Compiler;
import ru.nsu.logic.lang.execution.VirtualMachine;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        final ArgumentParser parser = ArgumentParsers
                .newFor("L* Program translator")
                .build().defaultHelp(true);
        parser.addArgument("-i", "--input-file").required(true);

        try {
            final Namespace ns = parser.parseArgs(args);
            try (final InputStream in = new FileInputStream(ns.getString("input_file"))) {
                final ASTLLangProgram ast = new LStatement(in).LLangProgram();

                final Compiler compiler = Compiler.create();
                final VirtualMachine machine = VirtualMachine.create(compiler.compile(ast));
                machine.run();

            } catch (final FileNotFoundException | ParseException | CompilationException | ExecutionException e) {
                System.err.println(e.getMessage());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        catch (final ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }
}
