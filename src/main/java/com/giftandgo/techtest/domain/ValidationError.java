package com.giftandgo.techtest.domain;

public class ValidationError {

    private final String errorMsg;
    private final int rowNumber;
    private final String inputLine;


    public ValidationError(String errorMsg, int rowNumber, String inputLine) {
        this.errorMsg = errorMsg;
        this.rowNumber = rowNumber;
        this.inputLine = inputLine;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getInputLine() {
        return inputLine;
    }
}
