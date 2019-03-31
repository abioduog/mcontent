package com.mnewservice.mcontent.domain.mapper;

import org.springframework.stereotype.Component;

@Component
public class ThemeMapper extends AbstractMapper<String, String> {

    private static final String ENTITY_THEME_FASHION = "fashion";
    private static final String ENTITY_THEME_COMICS = "comics";
    private static final String DOMAIN_THEME_FASHION = "default";
    private static final String DOMAIN_THEME_COMICS = "comics";

    @Override
    public String toDomain(String entity) {
        if (entity == null) {
            return null;
        }
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
        if (domain == null) {
            return null;
        }
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
