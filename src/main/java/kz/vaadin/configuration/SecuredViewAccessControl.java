package kz.vaadin.configuration;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.security.VaadinSecurityAware;

public class SecuredViewAccessControl implements VaadinSecurityAware, ApplicationContextAware, ViewAccessControl {

    private static final Logger logger = LoggerFactory.getLogger(SecuredViewAccessControl.class);

    private VaadinSecurity security;
    private ApplicationContext applicationContext;

    @Override
    public void setVaadinSecurity(VaadinSecurity vaadinSecurity) {
        security = vaadinSecurity;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean isAccessGranted(UI ui, String beanName) {
        final Secured viewSecured = applicationContext.findAnnotationOnBean(beanName, Secured.class);

        if (viewSecured == null) {
            logger.trace("No @Secured annotation found on view {}. Granting access.", beanName);
            return true;
        } else {
            final boolean result = security.hasAnyAuthority();
            logger.trace("Is access granted to view {}: {}", beanName, result);
            return result;
        }
    }
}
