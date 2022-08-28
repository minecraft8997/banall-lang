package ru.deewend.banalllang;

import ru.deewend.banalllang.funcresult.BanAllEmptyResult;
import ru.deewend.banalllang.funcresult.BanAllException;
import ru.deewend.banalllang.funcresult.BanAllFunctionResult;
import ru.deewend.banalllang.funcresult.BanAllReturnStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BanAllFunction {
    public static final int STATE_CONSTRUCTING = 0;
    public static final int STATE_CONSTRUCTED = 1;

    private int state;
    private final String name;
    private List<BanAllLine> lines;

    public BanAllFunction(String name) {
        this.state = STATE_CONSTRUCTING;
        this.name = name;
        this.lines = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addLine(BanAllLine line) {
        if (state != STATE_CONSTRUCTING)
            throw new IllegalStateException("Function is already constructed");

        lines.add(line);
    }

    public void finishConstructing() {
        if (state == STATE_CONSTRUCTED)
            throw new IllegalStateException("Function is already constructed");

        lines = Collections.unmodifiableList(lines);
        state = STATE_CONSTRUCTED;
    }

    public BanAllFunctionResult invoke() {
        if (state != STATE_CONSTRUCTED)
            throw new IllegalStateException("The function hasn't been constructed yet");

        for (BanAllLine line : lines) {
            BanAllFunctionResult result = line.executeCode();
            if (result instanceof BanAllReturnStatement) {
                return ((BanAllReturnStatement) result).getReturnValue();
            }
            if (result instanceof BanAllException) {
                BanAllException exception = (BanAllException) result;

                System.err.println(
                        "Exception in thread \"" + Thread
                                .currentThread().getName() + "\"" + System.lineSeparator() +
                        "Method being executed: " + getName() + System.lineSeparator() +
                        "Line number: " + line.getLineNumber() + System.lineSeparator() +
                        "Details: [" + exception.getClazz() + ": " + exception.getMessage() + "]");

                return exception;
            }
        }

        return new BanAllEmptyResult();
    }
}
