package com.org.hosply360.dao.OPD;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
@Component
public class ReceiptNumberGenerator {

    private final AtomicInteger counter = new AtomicInteger(0);


    public synchronized String generateReceiptNo() {
        int startYear = getFinancialYearStart();
        int endYear = startYear + 1;

        String financialYear = startYear + "-" + endYear;
        String number = String.format("%04d", counter.incrementAndGet());

        return "OPD/" + financialYear + "/" + number;
    }

    private int getFinancialYearStart() {
        LocalDate now = LocalDate.now();
        return (now.getMonthValue() >= 4) ? now.getYear() : now.getYear() - 1;
    }
}
