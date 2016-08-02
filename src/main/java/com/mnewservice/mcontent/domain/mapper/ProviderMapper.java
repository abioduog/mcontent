package com.mnewservice.mcontent.domain.mapper;

import com.mnewservice.mcontent.domain.Provider;
import com.mnewservice.mcontent.repository.entity.ProviderEntity;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Component
public class ProviderMapper extends AbstractMapper<Provider, ProviderEntity> {

    @Autowired
    private PhoneNumberMapper phoneNumberMapper;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BinaryContentMapper binaryContentMapper;

    @Override
    public Provider toDomain(ProviderEntity entity) {
        if (entity == null) {
            return null;
        }
        Provider domain = new Provider();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setAddress(entity.getAddress());
        domain.setZipcode(entity.getZipcode());
        domain.setCity(entity.getCity());        
        domain.setState(entity.getState());
        domain.setCountry(entity.getCountry());
//        domain.setPhone(phoneNumberMapper.toDomain(entity.getPhone()));
//        domain.setEmail(emailMapper.toDomain(entity.getEmail()));
        domain.setPhone(entity.getPhone());
        domain.setEmail(entity.getEmail());
        domain.setFax(entity.getFax());
        domain.setCompanyname(entity.getCompanyname());
        domain.setUser(userMapper.toDomain(entity.getUser()));
        domain.setNameOfContactPerson(entity.getNameOfContactPerson());
        domain.setPositionOfContactPerson(entity.getPositionOfContactPerson());
//        domain.setPhoneOfContactPerson(phoneNumberMapper.toDomain(entity.getPhoneOfContactPerson()));
//        domain.setEmailOfContactPerson(emailMapper.toDomain(entity.getEmailOfContactPerson()));
        domain.setPhoneOfContactPerson(entity.getPhoneOfContactPerson());
        domain.setEmailOfContactPerson(entity.getEmailOfContactPerson());
        domain.setContentName(entity.getContentName());
        domain.setContentDescription(entity.getContentDescription());
        domain.setCorrespondences(
                binaryContentMapper.toDomain(
                        entity.getCorrespondences()
                ));

        return domain;
    }

    @Override
    public ProviderEntity toEntity(Provider domain) {
        if (domain == null) {
            return null;
        }
        ProviderEntity entity = new ProviderEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setAddress(domain.getAddress());
        entity.setZipcode(domain.getZipcode());
        entity.setCity(domain.getCity());
        entity.setState(domain.getState());
        entity.setCountry(domain.getCountry());
//        entity.setPhone(phoneNumberMapper.toEntity(domain.getPhone()));
//        entity.setEmail(emailMapper.toEntity(domain.getEmail()));
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setFax(domain.getFax());
        entity.setCompanyname(domain.getCompanyname());
        entity.setUser(userMapper.toEntity(domain.getUser()));
        entity.setNameOfContactPerson(domain.getNameOfContactPerson());
        entity.setPositionOfContactPerson(domain.getPositionOfContactPerson());
//        entity.setPhoneOfContactPerson(phoneNumberMapper.toEntity(domain.getPhoneOfContactPerson()));
//        entity.setEmailOfContactPerson(emailMapper.toEntity(domain.getEmailOfContactPerson()));
        entity.setPhoneOfContactPerson(domain.getPhoneOfContactPerson());
        entity.setEmailOfContactPerson(domain.getEmailOfContactPerson());
        entity.setContentName(domain.getContentName());
        entity.setContentDescription(domain.getContentDescription());
        entity.setCorrespondences(
                binaryContentMapper.toEntity(
                        domain.getCorrespondences()
                ).stream().collect(Collectors.toSet())
        );
        return entity;
    }
}
