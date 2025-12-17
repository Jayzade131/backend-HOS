package com.org.hosply360.util.Others;

import com.ibm.icu.text.RuleBasedNumberFormat;
import java.util.Locale;

public class AmountToWordsUtil {
    public static String convertToWords(double amount) {
        RuleBasedNumberFormat formatter = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT);
        long rupees = (long) amount;
        int paise = (int) Math.round((amount - rupees) * 100);

        String result = formatter.format(rupees) + " rupees";
        if (paise > 0) {
            result += " and " + formatter.format(paise) + " paise";
        }
        return result + " only";
    }
}
