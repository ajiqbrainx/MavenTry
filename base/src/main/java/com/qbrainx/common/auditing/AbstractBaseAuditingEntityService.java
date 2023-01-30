package com.qbrainx.common.auditing;

import com.qbrainx.common.identity.AbstractBaseEntityService;
import com.qbrainx.common.identity.CustomConverter;

public abstract class AbstractBaseAuditingEntityService<V extends BaseAuditingDTO, T extends BaseAuditingEntity>
    extends AbstractBaseEntityService<V, T> implements BaseAuditingEntityService<V, T> {

    protected AbstractBaseAuditingEntityService(
        final BaseAuditingEntityRepository<T> repository,
        final CustomConverter<V, T> converter) {

        super(repository, converter);
    }
}
