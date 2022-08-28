package ru.deewend.banalllang.funcresult;

public class BanAllException extends BanAllFunctionResult {
    private final String clazz;
    private final String message;

    public BanAllException(String clazz, String message) {
        this.clazz = clazz;
        this.message = message;
    }

    public String getClazz() {
        return clazz;
    }

    public String getMessage() {
        return message;
    }
}
