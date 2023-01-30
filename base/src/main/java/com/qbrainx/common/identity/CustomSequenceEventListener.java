package com.qbrainx.common.identity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

@Log4j2
public class CustomSequenceEventListener implements PreInsertEventListener {

    private static final long serialVersionUID = 2096855137271487121L;

    private final Metadata metadata;
    private final SessionFactoryImplementor sessionFactory;
    private final SessionFactoryServiceRegistry serviceRegistry;

    private final Map<String, List<FieldAndGenerator>> configurationCache;
    private List<Object> entityWithMutatedFieldList;
    private Map<Object, Object> entityWithMutatedFieldMap;


    public CustomSequenceEventListener(final Metadata metadata,
                                       final SessionFactoryImplementor sessionFactory,
                                       final SessionFactoryServiceRegistry serviceRegistry,
                                       final Map<String, List<FieldAndGenerator>> configurationCache) {
        this.metadata = metadata;
        this.sessionFactory = sessionFactory;
        this.serviceRegistry = serviceRegistry;

        this.configurationCache = configurationCache;
        this.entityWithMutatedFieldList = new CopyOnWriteArrayList<>();
        this.entityWithMutatedFieldMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean onPreInsert(final PreInsertEvent event) {
        final FieldAndGeneratorCacheHelper helper = new FieldAndGeneratorCacheHelper();
        if (configurationCache == null || configurationCache.size() == 0) {
            helper.computeConfigurationCache(metadata, sessionFactory, serviceRegistry, configurationCache);
        }
        helper.findSequenceColumn(event.getEntity().getClass())
                .ifPresent(entityName -> {
                    if (configurationCache != null && configurationCache.containsKey(entityName)) {
                        final List<FieldAndGenerator> fieldAndGeneratorList = configurationCache.get(entityName);
                        if (fieldAndGeneratorList != null) {
                            fieldAndGeneratorList.forEach(fieldAndGenerator -> mutateSequence(event, fieldAndGenerator));
                        }
                    }
                });
        return false;
    }

    @SneakyThrows
    private void mutateSequence(final PreInsertEvent event,
                                final FieldAndGenerator fieldAndGenerator) {
        final Object entity = event.getEntity();
        final String entityName = event.getEntityName();
        final Field field = fieldAndGenerator.getField();
        field.setAccessible(true);
        Object nextSequence = null;
        final List<Object> entityWithMutatedFieldList = this.getEntityWithMutatedFieldList();
        final Map<Object, Object> entityWithMutatedFieldMap = this.getEntityWithMutatedFieldMap();
        if (entityWithMutatedFieldList.contains(entity) || entityWithMutatedFieldMap.containsValue(entity)) {
            nextSequence = field.get(entity);
        }
        if (nextSequence == null) {
            nextSequence = fieldAndGenerator.getTableGenerator().generate(event.getSession(), entity);
            final Object originalEntity = entity;
            field.set(entity, nextSequence);
            this.setEntityWithMutatedFieldList(entity);
            this.setEntityWithMutatedFieldMap(originalEntity, entity);
        }
        final String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
        final Object[] state = event.getState();
        final String propertyToSet = field.getName();
        final int index = ArrayUtils.indexOf(propertyNames, propertyToSet);
        if (index >= 0) {
            state[index] = nextSequence;
        } else {
            log.error("Field '" + propertyToSet + "' not found on entity '" + entityName + "'.");
        }
    }

    public List<Object> getEntityWithMutatedFieldList() {
        return entityWithMutatedFieldList == null ? new CopyOnWriteArrayList<>() : entityWithMutatedFieldList;
    }

    public void setEntityWithMutatedFieldList(final Object entityWithMutatedField) {
        if (this.entityWithMutatedFieldList == null) {
            this.entityWithMutatedFieldList = new CopyOnWriteArrayList<>();
        }
        entityWithMutatedFieldList.add(entityWithMutatedField);
    }

    public Map<Object, Object> getEntityWithMutatedFieldMap() {
        return entityWithMutatedFieldMap == null ? new ConcurrentHashMap<>() : entityWithMutatedFieldMap;
    }

    public void setEntityWithMutatedFieldMap(final Object originalEntity,
                                             final Object entityWithMutatedField) {
        if (this.entityWithMutatedFieldMap == null) {
            this.entityWithMutatedFieldMap = new ConcurrentHashMap<>();
        }
        entityWithMutatedFieldMap.put(originalEntity, entityWithMutatedField);
    }

}
