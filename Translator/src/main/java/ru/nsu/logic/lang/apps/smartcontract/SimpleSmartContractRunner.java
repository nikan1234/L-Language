package ru.nsu.logic.lang.apps.blockchain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import ru.nsu.logic.lang.ast.ASTLLangProgram;
import ru.nsu.logic.lang.ast.LStatement;
import ru.nsu.logic.lang.ast.ParseException;
import ru.nsu.logic.lang.compilation.common.CompilationException;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.compiler.CompiledProgram;
import ru.nsu.logic.lang.compilation.compiler.Compiler;
import ru.nsu.logic.lang.compilation.statements.NumberValue;
import ru.nsu.logic.lang.execution.SmartContractVirtualMachine;
import ru.nsu.logic.lang.execution.blockchain.TransactionInfo;
import ru.nsu.logic.lang.execution.blockchain.common.ITransaction;
import ru.nsu.logic.lang.execution.blockchain.common.ITransactionInfo;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleSmartContractRunner {

    private static IStatement stmt = new NumberValue(0, null);

    static final class Transaction implements ITransaction {

        @Getter
        private final ITransactionInfo transactionInfo;

        @Override
        public void startTransaction() {}

        @Override
        public void endTransaction() {}

        public Transaction(final ITransactionInfo info) {
            this.transactionInfo = info;
        }

        @Override
        public IStatement getContractMember(final String name) {
            return stmt;
        }

        @Override
        public void setContractMember(final String name, final IStatement statement) {
            stmt = statement;
        }
    }

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
                final CompiledProgram program = compiler.compile(ast);

                final String inputRegex = "(\\w+)\\.(\\w+)\\s+(\\{.*\\})";
                final Pattern pattern = Pattern.compile(inputRegex);

                final Scanner scanner = new Scanner(System.in);
                while (scanner.hasNext()) {
                    final String line = scanner.nextLine();

                    final Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        final String contractName = matcher.group(1);
                        final String methodName = matcher.group(2);
                        final String argsJson = matcher.group(3);

                        final TransactionInfo info =  new TransactionInfo(
                                UUID.randomUUID(), contractName, methodName, new HashMap<>());
                        IVirtualMachine vm = new SmartContractVirtualMachine(program, new Transaction(info));
                        vm.run();
                    }
                }


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
