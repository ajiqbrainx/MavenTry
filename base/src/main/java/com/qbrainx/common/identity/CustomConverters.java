package com.qbrainx.common.identity;

import lombok.experimental.UtilityClass;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

@UtilityClass
public class CustomConverters {

    public <V extends BaseDTO, T extends BaseEntity> CustomConverter<V, T> converter(
        final DozerBeanMapper mapper,
        final Class<V> dtoClass,
        final Class<T> entityClass) {

        return new GenericCustomConverter<>(mapper, dtoClass, entityClass);
    }

    private static class GenericCustomConverter<V extends BaseDTO, T extends BaseEntity>
        extends AbstractCustomConverter<V, T> {

        private GenericCustomConverter(final Mapper mapper, final Class<V> dtoClazz, final Class<T> entityClazz) {
            super(mapper, dtoClazz, entityClazz);
        }
    }
}
