package com.mnewservice.mcontent.domain.converter;

import com.mnewservice.mcontent.domain.PhoneNumber;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class StringToPhoneNumberConverter implements Converter<String, PhoneNumber> {

    @Override
    public PhoneNumber convert(String number) {
        PhoneNumber phone = new PhoneNumber();
        phone.setNumber(number);
        return phone;
    }

}
