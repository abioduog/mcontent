package com.mnewservice.mcontent.domain.mapper;

import org.springframework.stereotype.Component;

@Component
public class ThemeMapper extends AbstractMapper<String, String> {

    private static final String ENTITY_THEME_FASHION = "fashion";
    private static final String ENTITY_THEME_COMICS = "comics";
    private static final String DOMAIN_THEME_FASHION = "Default";
    private static final String DOMAIN_THEME_COMICS = "Comics";

    @Override
    public String toDomain(String entity) {
        switch (entity) {
            case ENTITY_THEME_FASHION:
                return DOMAIN_THEME_FASHION;
            case ENTITY_THEME_COMICS:
                return DOMAIN_THEME_COMICS;
            default:
                throw new UnsupportedOperationException("unknown theme: " + entity);
        }
    }

    @Override
    public String toEntity(String domain) {
        switch (domain) {
            case DOMAIN_THEME_FASHION:
                return ENTITY_THEME_FASHION;
            case DOMAIN_THEME_COMICS:
                return ENTITY_THEME_COMICS;
            default:
                throw new UnsupportedOperationException("unknown theme: " + domain);
        }
    }
}
