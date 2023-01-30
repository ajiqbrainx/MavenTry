package com.qbrainx.common.identity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class CustomSequencePersistEventListener implements PersistEventListener {

    private final Metadata metadata;
    private final SessionFactoryImplementor sessionFactory;
    private final SessionFactoryServiceRegistry serviceRegistry;
    private final Map<String, List<FieldAndGenerator>> configurationCache;
    private final CustomSequenceEventListener customSequenceEventListener;
    private final Map<Object, Object> objectMap;

    public CustomSequencePersistEventListener(final Metadata metadata,
                                              final SessionFactoryImplementor sessionFactory,
                                              final SessionFactoryServiceRegistry serviceRegistry) {
        this.metadata = metadata;
        this.sessionFactory = sessionFactory;
        this.serviceRegistry = serviceRegistry;
        this.configurationCache = new ConcurrentHashMap<>();
        this.customSequenceEventListener = new CustomSequenceEventListener(metadata, sessionFactory, serviceRegistry, configurationCache);
        this.objectMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onPersist(PersistEvent event) throws HibernateException {
        final FieldAndGeneratorCacheHelper helper = new FieldAndGeneratorCacheHelper();
        helper.computeConfigurationCache(metadata, sessionFactory, serviceRegistry, configurationCache);
        processFields(event, event.getObject(), new ArrayList<>());
        Object modifiedObject = objectMap.get(event.getObject());
        if (modifiedObject != null) {
            event.setObject(modifiedObject);
        } else {
            modifiedObject = customSequenceEventListener.getEntityWithMutatedFieldMap().get(event.getObject());
            if (modifiedObject != null) {
                event.setObject(modifiedObject);
            }
        }
    }

    @SneakyThrows
    private void processFields(final PersistEvent event,
                               final Object entity,
                               final List<Object> processedList) {
        if (entity != null && !processedList.contains(entity)) {
            processedList.add(entity);
            final Field[] fields = FieldUtils.getAllFields(entity.getClass());
            for (Field field : fields) {
                if (field.isAnnotationPresent(CustomSequence.class)) {
                    final String tableName = field.getDeclaredAnnotation(CustomSequence.class).tableName();
                    final List<FieldAndGenerator> fieldAndGeneratorList = configurationCache.get(tableName);
                    fieldAndGeneratorList.stream().forEach(fieldAndGenerator -> mutateSequence(event, entity, fieldAndGenerator));
                    field.setAccessible(true);
                    final Object childEntity = field.get(entity);
                    processFields(event, childEntity, processedList);
                } else if (FieldAccessUtil.isAccessible(field)) {
                    field.setAccessible(true);
                    final Object object = field.get(entity);
                    if (object instanceof Iterable) {
                        for (Object obj : (Iterable) object) {
                            if (obj != null) {
                                processFields(event, obj, processedList);
                            }
                        }
                    } else if (object != null) {
                        processFields(event, object, processedList);
                    }
                }
            }
        }

    }

    @Override
    public void onPersist(PersistEvent event, Map createdAlready) throws HibernateException {

    }

    @SneakyThrows
    private void mutateSequence(final PersistEvent event,
                                final Object entity,
                                final FieldAndGenerator fieldAndGenerator) {
        final Field field = fieldAndGenerator.getField();
        field.setAccessible(true);
        final List<Object> entityWithMutatedFieldList = customSequenceEventListener.getEntityWithMutatedFieldList();
        if (!entityWithMutatedFieldList.contains(entity)) {
            final Object originalEntity = entity;
            final Serializable nextSequence = fieldAndGenerator.getTableGenerator().generate(event.getSession(), entity);
            field.set(entity, nextSequence);
            objectMap.put(originalEntity, entity);
            customSequenceEventListener.setEntityWithMutatedFieldList(entity);
        }
    }

    public CustomSequenceEventListener getCustomSequenceEventListener() {
        return customSequenceEventListener;
    }

}
