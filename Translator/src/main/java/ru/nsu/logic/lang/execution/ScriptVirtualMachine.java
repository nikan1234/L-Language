package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.compilation.common.ICompiledProgram;
import ru.nsu.logic.lang.execution.common.IPipeline;

import java.util.HashMap;


/**
 * Virtual machine implementation to run user scripts on L* language
 */
public class ScriptVirtualMachine extends VirtualMachineBase {

    private final ICompiledProgram program;

    public ScriptVirtualMachine(final ICompiledProgram program) {
        super(program);
        this.program = program;
    }

    @Override
    protected void initializePipeline(final IPipeline pipeline) {
        pipeline.pushEntry(
                new PipelineEntry(Context.CreateForGlobal(), new HashMap<>(), program.getStatements()));
    }
}
