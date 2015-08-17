package com.mnewservice.mcontent.domain.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 * @param <DOMAIN>
 * @param <ENTITY>
 */
public abstract class AbstractMapper<DOMAIN, ENTITY> {

    public abstract DOMAIN toDomain(ENTITY entity);

    public Collection<DOMAIN> toDomain(Collection<ENTITY> entities) {
        return entities.stream().map(e -> toDomain(e)).collect(Collectors.toList());
    }

    public abstract ENTITY toEntity(DOMAIN domain);

    public Collection<ENTITY> toEntity(Collection<DOMAIN> domains) {
        return domains.stream().map(d -> toEntity(d)).collect(Collectors.toList());
    }

    public Collection<ENTITY> makeCollection(Iterable<ENTITY> iterable) {
        Collection<ENTITY> list = new ArrayList<>();
        for (ENTITY e : iterable) {
            list.add(e);
        }
        return list;
    }
}
