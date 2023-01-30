package com.qbrainx.common.rest;

import com.qbrainx.common.rest.CustomAnnotationsDeserializer;
import com.qbrainx.common.rest.MappedParentRef;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Class implementing the functionality of the {@link MappedParentRef} annotation.
 */
@Log4j2
public class CustomAnnotationsDeserializer extends DelegatingDeserializer {

    private final DeserializationConfig config;
    private final BeanDescription beanDescription;

    public CustomAnnotationsDeserializer(DeserializationConfig config, JsonDeserializer<?> delegate, BeanDescription beanDescription) {
        super(delegate);
        this.config = config;
        this.beanDescription = beanDescription;
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
        return new CustomAnnotationsDeserializer(config, newDelegatee, beanDescription);
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        Object deserializedObject = super.deserialize(parser, ctxt);
        deserializedObject = callPostDeserializeMethods(deserializedObject);
        return deserializedObject;
    }


    private Object callPostDeserializeMethods(Object deserializedObject) {
        final Class beanClass = beanDescription.getBeanClass();
        final List<Field> allMappedParentRefFieldsList = FieldUtils.getFieldsListWithAnnotation(beanClass, MappedParentRef.class);
        if (allMappedParentRefFieldsList != null) {
            for (Field field : allMappedParentRefFieldsList) {
                try {
                    log.debug("-------------------------field--------------------:" + field);
                    final String mappedBy = field.getAnnotation(MappedParentRef.class).mappedBy();
                    final Object childObjects = FieldUtils.readField(deserializedObject, field.getName(), true);
                    log.debug("-------------------------childObjects--------------------:" + childObjects);
                    if (childObjects instanceof Collection) {
                        for (Object childObject : (Collection) childObjects) {
                            final Field childField = FieldUtils.getField(childObject.getClass(), mappedBy, true);
                            FieldUtils.writeField(childObject, childField.getName(), deserializedObject, true);
                        }
                    } else if (childObjects instanceof  Object) {
                        final Field childField = FieldUtils.getField(childObjects.getClass(), mappedBy, true);
                        FieldUtils.writeField(childObjects, childField.getName(), deserializedObject, true);
                    }
                } catch (IllegalAccessException | IllegalArgumentException exception) {
                    log.info("Exception:" + exception.getLocalizedMessage());
                }
            }
        }
        return deserializedObject;
    }

}
