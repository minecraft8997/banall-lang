package ru.deewend.banalllang;

import ru.deewend.banalllang.funcresult.BanAllFunctionResult;

public abstract class BanAllLine {
    private final int lineNumber;

    public BanAllLine(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public final int getLineNumber() {
        return lineNumber;
    }

    public abstract BanAllFunctionResult executeCode();
}
