package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.repository.SettingRepository;
import com.mnewservice.mcontent.repository.entity.SettingEntity;
import com.mnewservice.mcontent.repository.entity.SettingEntity.SettingNameEnum;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@org.springframework.stereotype.Service
public class SettingManager {

    @Autowired
    private SettingRepository repository;

    public String getString(SettingNameEnum name) {
        return get(name, null);
    }

    public String getStringOrDefault(SettingNameEnum name, String defaultValue) {
        return get(name, defaultValue);
    }

    public Integer getIntegerOrDefault(SettingNameEnum name, Integer defaultValue) {
        String value = get(name, null);
        if (value == null) {
            return defaultValue;
        } else {
            return new Integer(value);
        }
    }

    private String get(SettingNameEnum name, String defaultValue) {
        SettingEntity entity = repository.findByName(name);
        if (entity == null) {
            return defaultValue;
        } else {
            return entity.getValue();
        }
    }
}
