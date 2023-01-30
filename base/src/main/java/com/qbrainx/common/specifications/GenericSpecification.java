package com.qbrainx.common.specifications;

import com.qbrainx.common.identity.BaseEntity;
import com.qbrainx.common.search.SearchCriteria;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Log4j2
public class GenericSpecification<E extends BaseEntity> implements Specification<E> {

    private static final long serialVersionUID = 1L;

    private final SearchCriteria criteria;

    private static final String LIKE = "%";

    public GenericSpecification(final SearchCriteria criteria) {
        if (criteria == null) {
            throw new IllegalStateException("Specification criteria must not be null!");
        }
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(final Root<E> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {

        query.distinct(true);
        log.debug("criteria.getKey():" + criteria.getKey() + ",  criteria.getOperation():" + criteria.getOperation() + ", criteria.getValue(): " + criteria.getValue());
        switch (criteria.getOperation().toLowerCase()) {
            case "gt":
                return builder.greaterThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case "gte":
                return builder.greaterThanOrEqualTo(
                    root.<String>get(criteria.getKey()),
                    criteria.getValue().toString());
            case "lt":
                return builder.lessThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case "lte":
                return builder.lessThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case "eq":
                return getEqualPredicate(root, builder);
            case "sw":
                return builder.like(root.get(criteria.getKey()), criteria.getValue().toString() + LIKE);
            case "ew":
                return builder.like(root.get(criteria.getKey()), LIKE + criteria.getValue().toString());
            case "lk":
                return builder.like(root.get(criteria.getKey()), LIKE + criteria.getValue().toString() + LIKE);
            case "in":
                final String[] values = criteria.getValue().toString()
                        .replace("[", "")
                        .replaceAll("\\\"", "")
                        .replace("]", "")
                        .split(",");
                log.debug("values:" + values);
                return builder.and(root.get(criteria.getKey()).in((Object[])values));
            default:
                throw new UnsupportedOperationException(
                    String.format("Could not recognize operation %s", criteria.getOperation().toLowerCase()));
        }
    }

    protected Predicate getEqualPredicate(final Root<E> root, final CriteriaBuilder builder) {
        if (root.get(criteria.getKey()).getJavaType().equals(String.class)) {
            return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
        } else {
            return builder.equal(root.get(criteria.getKey()), criteria.getValue());
        }
    }

}
