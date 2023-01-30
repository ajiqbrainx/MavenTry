package com.qbrainx.common.identity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.persistence.metamodel.Type;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Component;

@Component
public class FieldAndGeneratorCacheHelper {

    void computeConfigurationCache(final Metadata metadata,
                                   final SessionFactoryImplementor sessionFactory,
                                   final SessionFactoryServiceRegistry serviceRegistry,
                                   final Map<String, List<FieldAndGenerator>> configurationCache) {
        final MetamodelImplementor metamodel = sessionFactory.getMetamodel();
        metamodel.getEntities()
                .stream()
                .map(Type::getJavaType)
                .forEach(javaType ->
                        findSequenceColumn(javaType)
                                .ifPresent(entityName ->
                                        configurationCache.computeIfAbsent(entityName, name -> build(metadata, sessionFactory, serviceRegistry, javaType))));
    }

    private List<FieldAndGenerator> build(final Metadata metadata,
                                          final SessionFactoryImplementor sessionFactory,
                                          final SessionFactoryServiceRegistry serviceRegistry,
                                          final Class<?> entityClass) {
        return Arrays.stream(FieldUtils.getAllFields(entityClass))
                .filter(field -> field.isAnnotationPresent(CustomSequence.class))
                .map(field -> build(metadata, sessionFactory, serviceRegistry, field, entityClass))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<FieldAndGenerator> build(final Metadata metadata,
                                              final SessionFactoryImplementor sessionFactory,
                                              final SessionFactoryServiceRegistry serviceRegistry,
                                              final Field field,
                                              final Class<?> entityClass) {
        final MetamodelImplementor metamodel = sessionFactory.getMetamodel();
        final EntityPersister entityPersister = metamodel.entityPersister(entityClass.getName());
        if (entityPersister instanceof AbstractEntityPersister) {
            final AbstractEntityPersister abstractEntityPersister = (AbstractEntityPersister) entityPersister;
            final String[] fieldNames = abstractEntityPersister.getPropertyColumnNames(field.getName());
            final String columnName = fieldNames.length > 0 ? fieldNames[0] : field.getName();
            final CustomIdGenerator tableGenerator = build(metadata, serviceRegistry, columnName, field.getAnnotation(CustomSequence.class));
            return Optional.of(new FieldAndGenerator(field, tableGenerator));
        }
        return Optional.empty();
    }

    private CustomIdGenerator build(final Metadata metadata,
                                    final SessionFactoryServiceRegistry serviceRegistry,
                                    final String columnName,
                                    final CustomSequence sequence) {
        final Properties properties = new Properties();
        properties.setProperty(IdentifierGenerator.GENERATOR_NAME, CustomIdGenerator.ID_TABLE);
        properties.setProperty(CustomIdGenerator.TARGET_TABLE, sequence.tableName());
        properties.setProperty(CustomIdGenerator.TARGET_COLUMN, columnName);
        properties.setProperty(TableGenerator.INITIAL_PARAM, String.valueOf(sequence.initialValue()));
        properties.setProperty(TableGenerator.INCREMENT_PARAM, String.valueOf(sequence.incrementValue()));

        final Database database = metadata.getDatabase();
        final CustomIdGenerator tableGenerator = new CustomIdGenerator();
        tableGenerator.configure(LongType.INSTANCE, properties, serviceRegistry);
        tableGenerator.registerExportables(database);
        return tableGenerator;
    }

    Optional<String> findSequenceColumn(final Class<?> clazz) {
        return Arrays.stream(FieldUtils.getAllFields(clazz)) //Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CustomSequence.class))
                .map(field -> field.getAnnotation(CustomSequence.class).tableName())
                .findAny();
    }

}
