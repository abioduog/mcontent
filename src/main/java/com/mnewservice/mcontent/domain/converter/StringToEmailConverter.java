package com.mnewservice.mcontent.domain.converter;

import com.mnewservice.mcontent.domain.Email;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class StringToEmailConverter implements Converter<String, Email> {

    @Override
    public Email convert(String address) {
        Email email = new Email();
        email.setAddress(address);
        return email;
    }

}
