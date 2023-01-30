package com.qbrainx.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;


public class ReflectionUtils {

    public static <X extends Object> X copyFields(X object, String... skipFields) {
        Constructor constructorToUse = null;
        for (Constructor constructor : object.getClass().getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                constructorToUse = constructor;
                constructorToUse.setAccessible(true);
                break;
            }
        }
        if (constructorToUse == null) {
            throw new IllegalStateException(object + " must have a zero arg constructor in order to be copied");
        }
        X copy;
        try {
            copy = (X) constructorToUse.newInstance();

            for (Field field : FieldUtils.getAllFields(object.getClass())) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                //Avoid the fields that you don't want to copy. Note, if you pass in "id", it will skip any field with "id" in it. So be careful.
                if (StringUtils.containsAny(field.getName(), skipFields)) {
                    continue;
                }

                field.setAccessible(true);
                Object valueToCopy = field.get(object);
                //TODO add here other special types of fields, like Maps, Lists, etc.
                field.set(copy, valueToCopy);

            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException("Could not copy " + object, e);
        }
        return copy;
    }

}
