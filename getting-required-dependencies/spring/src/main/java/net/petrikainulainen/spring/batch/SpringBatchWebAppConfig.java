package net.petrikainulainen.spring.batch;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *  This configuration class enables the Spring MVC framework.
 */
@Configuration
@EnableWebMvc
public class SpringBatchWebAppConfig implements WebMvcConfigurer {

}
