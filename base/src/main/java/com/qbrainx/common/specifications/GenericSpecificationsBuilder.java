package com.qbrainx.common.specifications;

import com.qbrainx.common.identity.BaseEntity;
import com.qbrainx.common.search.SearchCriteria;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GenericSpecificationsBuilder<E extends BaseEntity> {

    private static final String BLANK = "";
    private static final String SPACER = ":";
    private static final String SEPARATOR = ",";

    private static final String FILTER_PATTERN = "(\\w+)(:gt:|:gte:|:lt:|:lte:|:eq:|:sw:|:ew:|:lk:|:in:)(((\\w+|\\[((\\\"(\\w+)\\\")+|,)*\\])+)|(\\w+ \\w+))" + SEPARATOR;
    private static final Pattern PATTERN = Pattern.compile(FILTER_PATTERN, Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern COMPILE = Pattern.compile(SPACER);

    private Optional<String> filter;
    private final List<SearchCriteria> params;

    public static <E extends BaseEntity> GenericSpecificationsBuilder<E> builder() {
        return new GenericSpecificationsBuilder<>();
    }

    private GenericSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public GenericSpecificationsBuilder<E> withFilter(final String theFilter) {
        this.filter = Optional.ofNullable(theFilter);
        return this;
    }

    public GenericSpecificationsBuilder<E> withCriteria(final SearchCriteria criteria) {
        if (criteria != null) {
            params.add(criteria);
        }
        return this;
    }

    public GenericSpecificationsBuilder<E> withCriteria(final String key, final String operation, final Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<E> build() {
        filter.ifPresent(this::mapSearchCriteria);

        if (params.isEmpty()) {
            return null;
        }
        return buildQuery();
    }

    private void mapSearchCriteria(final String theFilter) {
        final Matcher matcher = PATTERN.matcher(theFilter + SEPARATOR);
        while (matcher.find()) {
            withCriteria(matcher.group(1), COMPILE.matcher(matcher.group(2)).replaceAll(BLANK), matcher.group(3));
        }
    }

    private Specification<E> buildQuery() {
        final List<Specification<E>> specs = getSpecification();
        if (!specs.isEmpty()) {
            return extractSpecificationOf(specs);
        }
        return new NoFilterSpecification<>(); // Returns all values
    }

    private Specification<E> extractSpecificationOf(final List<Specification<E>> specs) {
        Specification<E> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }
        return result;
    }

    private List<Specification<E>> getSpecification() {
        return params.stream().map(this::parseSearchCriteria).collect(Collectors.toList());
    }

    private Specification<E> parseSearchCriteria(final SearchCriteria criteria) {
        return new GenericSpecification<>(criteria);
    }
}