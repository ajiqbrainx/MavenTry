package com.qbrainx.common.multitenant;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "custom.multitenant")
@Getter
@Setter
public class MultiTenantConfigProperties {

  private AdditionalTenantProviderType type;

  private Optional<Long> tenantId;

}
