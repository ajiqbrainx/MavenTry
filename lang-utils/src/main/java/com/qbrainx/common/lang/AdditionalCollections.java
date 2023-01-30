package com.qbrainx.common.lang;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.qbrainx.common.exception.RequestValidationException;
import com.qbrainx.common.message.MessageCode;

import java.util.Map;

/**
 * Additional Collection Utils to those defined in Collections and other packages.
 */
public final class AdditionalCollections {

    public static <T> ImmutableList<T> asImmutableList(final Iterable<? extends T> iterable) {
        return (iterable == null) ? ImmutableList.of() : ImmutableList.copyOf(iterable);
    }

    public static <T> ImmutableSet<T> asImmutableSet(final Iterable<? extends T> iterable) {
        return (iterable == null) ? ImmutableSet.of() : ImmutableSet.copyOf(iterable);
    }

    public static <K, V> ImmutableMap<K, V> asImmutableMap(final Map<? extends K, ? extends V> map) {
        return (map == null) ? ImmutableMap.of() : ImmutableMap.copyOf(map);
    }

    public static <K, V> ImmutableMultimap<K, V> asImmutableMultimap(final Multimap<? extends K, ? extends V> map) {
        return (map == null) ? ImmutableMultimap.of() : ImmutableMultimap.copyOf(map);
    }

    public static <T extends Map<?, ?>> T notEmpty(final T map, final MessageCode code) {

        if (map == null || map.isEmpty()) {
            throw new RequestValidationException(code);
        }

        return map;
    }

    private AdditionalCollections() {
    }

}
