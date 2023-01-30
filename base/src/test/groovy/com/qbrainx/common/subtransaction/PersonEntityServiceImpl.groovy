package com.dijta.common.subtransaction

import com.dijta.common.identity.DijtaConverter
import com.dijta.common.identity.DijtaConverters
import com.dijta.common.multitenant.AbstractMultiTenantEntityService
import com.dijta.common.multitenant.MultiTenantEntityRepository
import com.dijta.common.security.ISecurityContext
import org.dozer.DozerBeanMapper
import org.springframework.stereotype.Service

@Service
class PersonEntityServiceImpl extends AbstractMultiTenantEntityService<PersonEntityDto, PersonEntity>
        implements PersonEntityService{

    protected PersonEntityServiceImpl(PersonEntityRepository repository,
                                      DozerBeanMapper dozerBeanMapper,
                                      ISecurityContext securityContext) {
        super(repository, DijtaConverters.converter(dozerBeanMapper, PersonEntityDto.class, PersonEntity.class), securityContext)
    }

    @Override
    protected PersonEntity getEntityForExample() {
        return new PersonEntity();
    }
}
