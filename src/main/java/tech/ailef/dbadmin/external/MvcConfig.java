package tech.ailef.dbadmin.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
	@Autowired
	private DbAdminProperties properties;
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	System.out.println("ADDING");
        registry.addResourceHandler("/" + properties.getBaseUrl() + "/**")
          		.addResourceLocations("classpath:/static/");	
    }
}

