package tech.ailef.dbadmin.external.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.ailef.dbadmin.external.DbAdminProperties;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private DbAdminProperties props;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler(props.getResourcesPath() + "/**")
            .addResourceLocations("classpath:/static/");
    }
}
