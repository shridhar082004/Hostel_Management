package com.hostel.ui;

public class InputClosedException extends RuntimeException {

    public InputClosedException() {
        super("Input stream closed.");
    }
}
