package com.qbrainx.common.security;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CustomSecurityConfigProperties {

    private List<String> excludePaths;
    private String entrySecurityExpression;
    private String adminRoleSecurityExpression;

    public boolean isExcludePathPresent() {
        return this.excludePaths != null && !this.getExcludePaths().isEmpty();
    }

    public String[] getExcludePathAsArray() {
        return isExcludePathPresent() ? this.excludePaths.toJavaArray(String[]::new) : new String[0];
    }
}
