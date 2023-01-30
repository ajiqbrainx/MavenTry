package com.qbrainx.common.identity;

import com.qbrainx.common.exception.NotFoundException;
import com.qbrainx.common.message.MessageCode;
import com.qbrainx.common.message.MessageConstants;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractBaseEntityService<V extends BaseDTO, T extends BaseEntity>
        implements BaseEntityService<V, T> {

    private final BaseEntityRepository<T> repository;
    protected final CustomConverter<V, T> converter;

    protected AbstractBaseEntityService(
            final BaseEntityRepository<T> repository,
            final CustomConverter<V, T> converter) {

        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public List<V> saveAll(List<V> v) {
        List<T> tList = this.converter.convertVoToEntity(v);
        return this.converter.convertEntityToVo(repository.saveAll(tList));
    }

    @Override
    public List<V> findByExample(final V v) {
        final T e = this.converter.convertVoToEntity(v);
        return this.converter.convertEntityToVo(repository.findAll(Example.of(e)));
    }

    @Override
    public List<V> getAll() {
        return this.converter.convertEntityToVo(repository.findAll());
    }

    @Override
    public Optional<V> getById(final Long id) {
        return repository.findById(id).map(this.converter::convertEntityToVo);
    }

    @Override
    public V save(final V v) {
        final T t = this.converter.convertVoToEntity(v);
        final T saved = repository.save(t);
        return this.converter.convertEntityToVo(saved);
    }

    @Override
    public V delete(final V v) {
        v.setDeletedAt(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        return update(v);
    }

    @Override
    public V update(final V v) {
        return repository.findById(v.getPkId())
                .map(it -> {
                    final T updated = repository.save(this.converter.updateEntityFromVo(it, v));
                    return this.converter.convertEntityToVo(updated);
                })
                .orElseThrow(() -> new NotFoundException(
                        MessageCode.error(MessageConstants.ID_NOT_FOUND, "pkId", "Requested Id Not Found")));
    }

    @Override
    public Optional<V> deleteById(final long entityId) {
        return Optional.of(repository.findById(entityId)
                .map(it -> {
                    it.setDeletedAt(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
                    repository.save(it);
                    return this.converter.convertEntityToVo(it);
                })
                .orElseThrow(() -> new NotFoundException(
                        MessageCode.error(MessageConstants.ID_NOT_FOUND, "pkId", "Requested Id Not Found"))));

    }

    @Override
    public Page<T> getAll(final PaginationRequest paginationRequest) {
        return findAll(paginationRequest);
    }

    @Override
    public Page<T> findAll(final PaginationRequest paginationRequest) {
        final PageRequest paging = paginationRequest.toPageRequest();

        return paginationRequest.getSearch()
                .map(this::buildSpecification)
                .map(it -> repository.findAll(it, paging))
                .orElseGet(() -> repository.findAll(paging));
    }

//    @Override
//    public Specification<T> buildSpecification(final String filter) {
//   return Specification<T>;
//     }

    @Override
    public boolean deleteAll(List<Long> pkIds) {
        List<T> tList = repository.findAllById(pkIds);
        tList.forEach(x -> x.setDeletedAt(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()));
        repository.saveAll(tList);
        return true;
    }

    @Override
    public Long count(final Optional<String> search) {
        if (search.isPresent()) {
            return repository.count(this.buildSpecification(search.get()));
        }
        return repository.count();
    }


}
