package org.example.antispamgorbushka.service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhoneChecker {

    public boolean containtPhone(String message) {
        String patterns
                = /*".(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}?"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$"
                + "|^\\d{11}$"
                + "|^(\\+\\d{1}( )?)?(\\d{3}[ ]?){2}\\d{3}"
                + */
                "((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$"
                ;
        Pattern pattern = Pattern.compile(patterns);
        Matcher matcher = pattern.matcher(message);
        int qq = 0;
        return matcher.matches();
    }
}
