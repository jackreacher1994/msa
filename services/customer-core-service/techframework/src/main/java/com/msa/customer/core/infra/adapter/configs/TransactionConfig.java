package com.msa.customer.core.infra.adapter.configs;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.List;

/**
 * Each use case (inbound port method) runs in one local ACID transaction, applied declaratively from the
 * outside so the application module stays free of {@code @Transactional}. Query methods are read-only.
 */
@Configuration(proxyBeanMethods = false)
public class TransactionConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static Advisor inboundPortTransactionAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public * com.msa.customer.core.application.ports.inbound..*(..))");

        RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
        readOnly.setReadOnly(true);
        RuleBasedTransactionAttribute readWrite = new RuleBasedTransactionAttribute();
        readWrite.setRollbackRules(List.of(new RollbackRuleAttribute(Exception.class)));

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.addTransactionalMethod("get*", readOnly);
        source.addTransactionalMethod("find*", readOnly);
        source.addTransactionalMethod("*", readWrite);

        // The transaction manager is resolved lazily from the BeanFactory on first invocation.
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionAttributeSource(source);
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }
}
