package com.org.hosply360.util.Others;

import com.org.hosply360.dao.frontDeskDao.PatientPersonalInformation;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

public class AgeUtil {

    private AgeUtil() {
    }

    public static String getAge(PatientPersonalInformation personalInfo) {
        if (personalInfo == null) {
            return "";
        }

        String dobStr = personalInfo.getDateOfBirth();
        if (!StringUtils.hasText(dobStr)) {
            return "0";
        }

        try {
            LocalDate dob = LocalDate.parse(dobStr);
            return String.valueOf(Period.between(dob, LocalDate.now()).getYears());
        } catch (DateTimeParseException e) {
            return "0";
        }
    }

    public static String getAge(String dateOfBirth) {
        if (!StringUtils.hasText(dateOfBirth)) {
            return "0";
        }

        try {
            LocalDate dob = LocalDate.parse(dateOfBirth);
            return String.valueOf(Period.between(dob, LocalDate.now()).getYears());
        } catch (DateTimeParseException e) {
            return "0";
        }
    }
}
