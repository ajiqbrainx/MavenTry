package com.dijta.common.subtransaction

import com.dijta.common.multitenant.MultiTenantEntityService

interface PersonEntityService extends MultiTenantEntityService<PersonEntityDto, PersonEntity> {

}