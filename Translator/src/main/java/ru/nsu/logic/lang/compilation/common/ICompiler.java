package ru.nsu.logic.lang.compilation.common;

import ru.nsu.logic.lang.ast.ASTLLangProgram;

public interface ICompiler {
    ICompiledProgram compile(ASTLLangProgram program) throws CompilationException;
}
