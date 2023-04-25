package ru.nsu.logic.lang.apps.smartcontract;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import ru.nsu.logic.lang.compilation.statements.NumberValueStatement;
import ru.nsu.logic.lang.execution.SmartContractVirtualMachine;
import ru.nsu.logic.lang.execution.blockchain.SmartContractId;
import ru.nsu.logic.lang.execution.blockchain.SmartContractMethodId;
import ru.nsu.logic.lang.execution.blockchain.TransactionInfo;
import ru.nsu.logic.lang.execution.blockchain.TransactionQueue;
import ru.nsu.logic.lang.execution.blockchain.common.ISmartContractMember;
import ru.nsu.logic.lang.execution.blockchain.common.ITransaction;
import ru.nsu.logic.lang.execution.blockchain.common.ITransactionInfo;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleSmartContractRunner {

    private static TransactionQueue queue;

    public static void main(String[] args) {
        final ArgumentParser parser = ArgumentParsers
                .newFor("L* Program translator")
                .build().defaultHelp(true);
        parser.addArgument("-i", "--input-file").required(true);
        parser.addArgument("-c", "--contract-name").required(true);

        try {
            final Namespace ns = parser.parseArgs(args);
            try (final InputStream in = new FileInputStream(ns.getString("input_file"))) {
                final ASTLLangProgram ast = new LStatement(in).LLangProgram();

                final Compiler compiler = Compiler.create();
                final CompiledProgram program = compiler.compile(ast);

                final Mocks.TestBlockchain blockchain = new Mocks.TestBlockchain(program);
                final Mocks.TestBlockController blockController = new Mocks.TestBlockController(blockchain);
                queue = new TransactionQueue(blockController);

                /* First of all, execute constructor */
                execute(new TransactionInfo(
                        UUID.randomUUID(),
                        new SmartContractId(ns.getString("contract_name")),
                        new SmartContractMethodId("__constructor__"),
                        new HashMap<>()));


                /* Process user requests */
                mainloop();

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

    private static void mainloop() throws ExecutionException {
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

                execute(new TransactionInfo(UUID.randomUUID(),
                                            new SmartContractId(contractName),
                                            new SmartContractMethodId(methodName),
                                            parseArgs(argsJson)));
            }
            else {
                System.err.println("Wrong syntax");
            }
        }
    }

    private static void execute(final ITransactionInfo transactionInfo) throws ExecutionException {
        queue.queryTransaction(transactionInfo);

        final ITransaction transaction = queue.getNext();
        IVirtualMachine vm = new SmartContractVirtualMachine(transaction);
        vm.run();
    }

    private static Map<String, IStatement> parseArgs(final String argsJson) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        final HashMap<String, Object> map = gson.fromJson(argsJson, HashMap.class);


        Map<String, IStatement> result = new HashMap<>();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            result.put(entry.getKey(), new NumberValueStatement(((Double) entry.getValue()).intValue(), null));
        }
        return result;
    }
}
