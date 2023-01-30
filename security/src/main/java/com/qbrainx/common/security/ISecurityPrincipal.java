package com.qbrainx.common.security;


import io.vavr.collection.Set;

import java.io.Serializable;

public interface ISecurityPrincipal extends Serializable {

    Long getId();

    Long getTenantId();

    String getName();

    Set<String> getPrivileges();
}
