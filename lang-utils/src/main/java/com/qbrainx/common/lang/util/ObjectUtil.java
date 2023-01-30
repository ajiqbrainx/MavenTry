package com.qbrainx.common.lang.util;

import com.qbrainx.common.exception.BadRequestException;
import com.qbrainx.common.lang.helper.NullAwareBeanUtilsBean;
import com.qbrainx.common.message.MessageCode;
import com.qbrainx.common.message.MessageParam;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Log4j2
@UtilityClass
public final class ObjectUtil {
    public static <T> boolean isEmpty(List<T> list) {
        return null == list || list.isEmpty();
    }

    public static boolean isEmpty(String str) {
        return null == str || str.isEmpty();
    }

    public static boolean isNumeric(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public static boolean isGreaterThanZero(Number value) {
        return value != null && value.longValue() > 0;
    }

    public static void copyNotNullProperties(Object destination, Object source) {
        try {
            NullAwareBeanUtilsBean nullAwareBeanUtilsBean = new NullAwareBeanUtilsBean();
            nullAwareBeanUtilsBean.copyProperties(destination, source);
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException occurred while copying source properties: {}, {}", source.toString(), e.getLocalizedMessage());
            throw new BadRequestException(
                    MessageCode.error("QMSM_TIS_UID", "sourceDto",
                    "IllegalAccessException occurred while copying source properties: {}, {}",
                            MessageParam.messageParamList(source.toString(), e.getLocalizedMessage())));
        } catch (InvocationTargetException e) {
            log.error("InvocationTargetException occurred while copying source properties: {}, {}", source.toString(), e.getLocalizedMessage());
            throw new BadRequestException(
                    MessageCode.error("QMSM_TIS_UID", "sourceDto",
                            "InvocationTargetException occurred while copying source properties: {}, {}",
                            MessageParam.messageParamList(source.toString(), e.getLocalizedMessage())));
        }
    }

    public static void excludingKeys(Map<String, Object> map) {
        List<String> excludingKeys = Arrays.asList("pkId", "createdAt", "createdBy", "changedAt", "changedBy", "tenantId");
        excludingKeys.forEach(map::remove);
    }
    
    /**
     * 
     * @param <T>   :class type
     * @param value :value
     * @return
     */
    public static <T extends Object> T getValueOrDefault(T value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     *
     * @param map map of elements
     * @param value value to  be search
     * @param <T> Generic
     * @param <E> Generic
     * @return set of values
     */
    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {

        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Method to validate number of elements in the list.
     * @param items List of values
     * @param <T> Generic
     * @return Generic Object
     */
    public static <T> T findFirst(List<T> items) {
        if (isEmpty(items) || items.size() > 1) {
            throw new RuntimeException("Values present in the list are either empty or greater than 1");
        } else {
            return items.get(0);
        }
    }
    
    public static boolean isJsonArray(String input) {
        try {
            new JSONArray(input);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public static boolean isJsonObject(String input) {
        try {
            new JSONObject(input);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public static boolean isValidJson(String input) {

        if (isEmpty(input)) {
            return false;
        }

        try {
            new JSONObject(input);
        } catch (JSONException e) {
            try {
                new JSONArray(input);
            } catch (JSONException ex) {
                return false;
            }
        }
        return true;
    }
}
