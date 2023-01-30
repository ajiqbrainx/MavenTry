package com.qbrainx.common.identity;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.annotation.Validated;

@Validated
public interface BaseEntityService<V extends BaseDTO, T extends BaseEntity> {

    List<V> findByExample(@Valid V e);

    List<V> getAll();

    List<V> saveAll(List<V> v);

    Optional<V> getById(@Valid Long id);

    V save(@Valid V v);

    V delete(@Valid V v);

    V update(@Valid V v);

    Optional<V> deleteById(@Valid long entityId);

    boolean deleteAll(List<Long> pkIds);

    Page<T> getAll(@Valid final PaginationRequest paginationRequest);

    Page<T> findAll(@Valid PaginationRequest paginationRequest);

    Specification<T> buildSpecification(@Valid final String filter);

    Long count(Optional<String> search);
}
