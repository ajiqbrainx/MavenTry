package com.qbrainx.common.identity;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface CustomConverter<V extends BaseDTO, T extends BaseEntity> {

    @Nonnull
    V convertEntityToVo(T e);

    @Nonnull
    T convertVoToEntity(V v);

    @Nonnull
    T updateEntityFromVo(T t, V v);

    default List<V> convertEntityToVo(final Collection<T> tList) {
        return tList.stream().map(this::convertEntityToVo).collect(Collectors.toList());
    }

    @Nonnull
    default List<T> convertVoToEntity(final Collection<V> vList) {
        return vList.stream().map(this::convertVoToEntity).collect(Collectors.toList());
    }

}
