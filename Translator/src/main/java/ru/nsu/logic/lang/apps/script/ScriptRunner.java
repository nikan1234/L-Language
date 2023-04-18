package ru.nsu.logic.lang.apps.local;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import ru.nsu.logic.lang.ast.ASTLLangProgram;
import ru.nsu.logic.lang.ast.LStatement;
import ru.nsu.logic.lang.ast.ParseException;
import ru.nsu.logic.lang.compilation.common.CompilationException;
import ru.nsu.logic.lang.compilation.compiler.Compiler;
import ru.nsu.logic.lang.execution.ScriptVirtualMachine;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ScriptRunner {
    public static void main(String[] args) {
        final ArgumentParser parser = ArgumentParsers
                .newFor("L* Program executor")
                .build().defaultHelp(true);
        parser.addArgument("-i", "--input-file").required(true);

        try {
            final Namespace ns = parser.parseArgs(args);
            try (final InputStream in = new FileInputStream(ns.getString("input_file"))) {
                final ASTLLangProgram ast = new LStatement(in).LLangProgram();

                final Compiler compiler = Compiler.create();
                final IVirtualMachine machine = new ScriptVirtualMachine(compiler.compile(ast));
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
