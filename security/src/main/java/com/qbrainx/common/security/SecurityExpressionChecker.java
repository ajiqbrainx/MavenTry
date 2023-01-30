

package com.qbrainx.common.security;

import io.vavr.API;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.util.SimpleMethodInvocation;

import java.lang.reflect.Method;

public class SecurityExpressionChecker {

    private final ExpressionParser parser;
    private final MethodSecurityExpressionHandler expressionHandler;
    private final SecurityObject securityObject;
    private final Method triggerCheck;

    public SecurityExpressionChecker() {
        this.parser = new SpelExpressionParser();
        this.expressionHandler = new DefaultMethodSecurityExpressionHandler();
        this.securityObject = new SecurityObject();
        this.triggerCheck = API.unchecked(() -> SecurityObject.class.getDeclaredMethod("triggerCheck")).get();
    }


    static class SecurityObject {
        void triggerCheck() { /*NOP*/ }
    }

    public Boolean check(String securityExpression, Authentication authentication) {
        EvaluationContext evaluationContext = expressionHandler.createEvaluationContext(authentication, new SimpleMethodInvocation(securityObject, triggerCheck));
        return ExpressionUtils.evaluateAsBoolean(parser.parseExpression(securityExpression), evaluationContext);
    }
}
