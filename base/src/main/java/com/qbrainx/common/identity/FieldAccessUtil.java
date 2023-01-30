package com.qbrainx.common.identity;

import java.lang.reflect.Field;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FieldAccessUtil {

    public static boolean isAccessible(final Field field) {
        boolean isAccessible = (field != null && field.getType() != null && field.getType().getPackage() != null);
        if (isAccessible) {
            final String packageName = field.getType().getPackage().getName();
            if (packageName != null) {
                isAccessible = !packageName.startsWith("java.lang")
                        && !packageName.startsWith("java.util.concurrent.atomic")
                        && !packageName.startsWith("jdk.")
                        && !packageName.startsWith("groovy.lang")
                        && !packageName.startsWith("org.codehaus.");
            } else {
                isAccessible =  false;
            }
        }
        return isAccessible;
    }

}
