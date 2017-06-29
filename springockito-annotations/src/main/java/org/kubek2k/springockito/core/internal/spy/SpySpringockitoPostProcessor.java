package org.kubek2k.springockito.core.internal.spy;

import org.kubek2k.springockito.core.internal.ResettableSpringockito;
import org.mockito.Mockito;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class SpySpringockitoPostProcessor implements BeanPostProcessor,ResettableSpringockito {

    private String beanName;
    private Object spy;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (this.beanName.equals(beanName)) {
            Object targetObject = getTargetObject(bean);
            spy = Mockito.spy(targetObject);
            return spy;
        } else {
            return bean;
        }
    }

    public static <T> T getTargetObject(Object proxy) {
        if ((AopUtils.isJdkDynamicProxy(proxy))) {
            try {
                return (T) getTargetObject(((Advised) proxy).getTargetSource().getTarget());
            } catch (Exception e) {
                throw new RuntimeException("Failed to unproxy target.", e);
            }
        }
        return (T) proxy;
    }

    public void setBeanName(String matchingName) {
        this.beanName = matchingName;
    }

    public void reset() {
        try {
            Mockito.reset(spy);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
