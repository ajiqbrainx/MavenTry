// Copyright 2014-2019 Xvela. All Rights Reserved.

package com.qbrainx.common.identity;

import lombok.AllArgsConstructor;
import org.dozer.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@AllArgsConstructor
public abstract class AbstractCustomConverter<V extends BaseDTO, T extends BaseEntity> implements CustomConverter<V, T> {

    private final Mapper mapper;
    private final Class<V> dtoClazz;
    private final Class<T> entityClazz;

    @Override
    @Nonnull
    public V convertEntityToVo(T t) {
        return mapEntityToVo(mapper.map(t, dtoClazz), t);
    }


    @Override
    @Nonnull
    public T convertVoToEntity(V v) {
        return mapVoToEntity(mapper.map(v, entityClazz), v);
    }

    @Override
    @Nonnull
    public T updateEntityFromVo(T t, V v) {
        mapper.map(v, mapVoToEntity(t, v));
        return t;
    }

    public V mapEntityToVo(V v, T t) {
        return v;
    }

    public T mapVoToEntity(T t, V v) {
        return t;
    }


}
