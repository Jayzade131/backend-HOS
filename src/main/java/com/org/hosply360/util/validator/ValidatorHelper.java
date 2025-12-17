package com.org.hosply360.util.validator;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.exception.GlobalMasterException;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class ValidatorHelper {
    public static void validateObject(Object obj) {
        if (obj==null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
    }

    public static void ValidateAllObject(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.NOT_FOUND);
            }
        }
    }
    public static void validateOrThorw(Object obj, RuntimeException ex) {
        if (obj == null) {
            throw ex;
        }
    }

    public static void validateAllOrThrow(RuntimeException ex, Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                throw ex;
            }
        }
    }

    public static void validateDateRange(LocalDate fromDate, LocalDate toDate, RuntimeException ex) {
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw ex;
        }
    }



}

