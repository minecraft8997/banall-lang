package ru.deewend.banalllang.funcresult;

public class BanAllResultTempban extends BanAllFunctionResult {
    private final String duration;

    public BanAllResultTempban(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }
}
