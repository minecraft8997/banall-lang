package ru.deewend.banalllang.funcresult;

public class BanAllReturnStatement extends BanAllFunctionResult {
    private BanAllFunctionResult returnValue;

    public BanAllReturnStatement(BanAllFunctionResult returnValue) {
        this.returnValue = returnValue;
    }

    public BanAllFunctionResult getReturnValue() {
        return returnValue;
    }
}
