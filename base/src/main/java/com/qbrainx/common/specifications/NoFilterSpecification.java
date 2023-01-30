package com.qbrainx.common.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.qbrainx.common.identity.BaseEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@SuppressWarnings("rawtypes")
public class NoFilterSpecification<E extends BaseEntity> implements Specification<E> {

    private static final long serialVersionUID = 1L;

    private static final String ID = "pkId";

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return builder.equal(root.get(ID), root.get(ID));
    }
}