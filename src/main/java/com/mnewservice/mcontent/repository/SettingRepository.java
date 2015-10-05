package com.mnewservice.mcontent.repository;

import com.mnewservice.mcontent.repository.entity.SettingEntity;
import com.mnewservice.mcontent.repository.entity.SettingEntity.SettingNameEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Repository
public interface SettingRepository extends CrudRepository<SettingEntity, Long> {

    SettingEntity findByName(SettingNameEnum name);
}
