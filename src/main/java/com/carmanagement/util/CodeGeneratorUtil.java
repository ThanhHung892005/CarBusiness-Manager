package com.carmanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CodeGeneratorUtil {

    private CodeGeneratorUtil() {}

    public static String generateCode(String prefix, long sequence) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        return prefix + date + String.format("%04d", sequence);
    }
}
