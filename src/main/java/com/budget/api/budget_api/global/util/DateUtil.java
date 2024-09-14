package com.budget.api.budget_api.global.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter formatter_day = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public static LocalDate convertStringToDate(String stringDate){
        if(stringDate==null){
            return LocalDate.now();
        }

        return LocalDate.parse(stringDate, formatter_day);

    }
}
