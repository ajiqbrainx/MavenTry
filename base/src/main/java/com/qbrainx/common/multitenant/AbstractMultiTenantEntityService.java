package com.qbrainx.common.multitenant;

import com.qbrainx.common.exception.CustomException;
import com.qbrainx.common.exception.NotFoundException;
import com.qbrainx.common.identity.AbstractBaseEntityService;
import com.qbrainx.common.identity.CustomConverter;
import com.qbrainx.common.identity.PaginationRequest;
import com.qbrainx.common.message.MessageCode;
import com.qbrainx.common.message.MessageConstants;
import com.qbrainx.common.message.MessageParam;
import com.qbrainx.common.security.ISecurityContext;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.Predicate;
import lombok.SneakyThrows;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

public abstract class AbstractMultiTenantEntityService<V extends MulitTenantDTO, T extends MultiTenantEntity>
    extends AbstractBaseEntityService<V, T> implements MultiTenantEntityService<V, T> {

    private final MultiTenantEntityRepository<T> repository;
    private final ISecurityContext securityContext;

    protected AbstractMultiTenantEntityService(
        final MultiTenantEntityRepository<T> repository,
        final CustomConverter<V, T> converter,
        final ISecurityContext securityContext) {

        super(repository, converter);
        this.repository = repository;
        this.securityContext = securityContext;
    }

    protected abstract T getEntityForExample();

    @Override
    public List<V> getAll() {

        return this.converter.convertEntityToVo(repository.findAll());
    }

    @Override
    public Page<T> findAll(final PaginationRequest paginationRequest) {
        final PageRequest paging = paginationRequest.toPageRequest();

        final Specification<T> specification =
            paginationRequest.getSearch()
                .map(this::buildSpecification)
                .orElse(emptySpecification());

        return repository.findAll(specification, paging);
    }

    @Override
    public Optional<V> getById(final Long id) {
        final T t = getEntityForExample();
        t.setPkId(id);
        final Specification<T> specification = (root, query, criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("pkId"), id));
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
        return repository.findOne(specification).map(this.converter::convertEntityToVo);
    }

    @Override
    public V save(final V v) {
        setTenantIdIfNotPresent(v);
        validateRequestTenantIdWithContext(v);
        return super.save(v);
    }

    @Override
    public List<V> saveAll(final List<V> v) {

        v.forEach(o -> {
            this.setTenantIdIfNotPresent(o);
            this.validateRequestTenantIdWithContext(o);
        });

        return super.saveAll(v);
    }

    @Override
    public V delete(final V v) {
        final Optional<T> t = checkUserCanModify(v.getPkId());
        if (t.isPresent()) {
            v.setDeletedAt(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            return super.save(v);
        } else {
            throw new NotFoundException(MessageCode.error(MessageConstants.ID_NOT_FOUND, "pkId", "Request Id Not Found"));
        }
    }

    @Override
    public V update(final V v) {
        final Optional<T> savedV = checkUserCanModify(v.getPkId());
        setTenantIdIfNotPresent(v);
        validateRequestTenantIdWithContext(v);
        if (savedV.isPresent()) {
            final T updated = repository.save(this.converter.updateEntityFromVo(savedV.get(), v));
            return this.converter.convertEntityToVo(updated);
        } else {
            throw new NotFoundException(MessageCode.error(MessageConstants.ID_NOT_FOUND, "pkId", "Request Id Not Found"));
        }
    }

    protected Optional<T> checkUserCanModify(final Long pkId) {
        final Optional<T> t = repository.findById(pkId);
        if (t.isPresent() && !securityContext.getPrincipal().getTenantId().equals(t.get().getTenantId())) {
            final MessageParam param = new MessageParam("403", "Requested Tenant user can't perform the action intended");
            throw new CustomException(HttpStatus.FORBIDDEN, MessageConstants.FORBIDDEN_ACTION, "Forbidden Action: {}", param);
        }
        return t;
    }

    @SneakyThrows
    @Override
    public Optional<V> deleteById(final long entityId) {
        checkUserCanModify(entityId);
        final T t = getEntityForExample();
        t.setPkId(entityId);
        final Optional<T> result = repository.findOne(Example.of(t));
        if (result.isPresent()) {
            final T safeDelete = result.get();
            safeDelete.setDeletedAt(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            repository.save(safeDelete);
        } else {
            final MessageParam param = new MessageParam("404", "No Records found for " + entityId);
            throw new NotFoundException(MessageCode.error(MessageConstants.NOT_FOUND, "Not Found: {}", param));
        }
        return result.map(this.converter::convertEntityToVo);
    }

    private Specification<T> emptySpecification() {
        return (root, query, criteriaBuilder) -> null;
    }

    private void setTenantIdIfNotPresent(final V v) {
        if (v.getTenantId() == null) {
            v.setTenantId(this.securityContext.getPrincipal().getTenantId());
        }
    }

    private void validateRequestTenantIdWithContext(final V v) {
        if (!this.securityContext.getPrincipal().getTenantId().equals(v.getTenantId())) {
            final MessageParam param =
                new MessageParam("403", "Requested Tenant user can't perform the action intended");
            throw new CustomException(HttpStatus.FORBIDDEN, MessageConstants.FORBIDDEN_ACTION, "Forbidden Action: {}", param);
        }
    }
}
