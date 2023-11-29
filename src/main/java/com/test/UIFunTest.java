package com.test;

import java.io.File;
import java.io.IOException;

import static javafx.application.Application.launch;

public class UIFunTest {
    public static void main(String[] args) throws IOException {
        String filePath = "";
        File fileNow = new File(filePath);
        fileNow.createNewFile();
    }
}
