package com.example;

@SuppressWarnings("serial")
public class ItemNoFound extends Exception {
    public ItemNoFound(String msg) {
        super(msg);
    }
}