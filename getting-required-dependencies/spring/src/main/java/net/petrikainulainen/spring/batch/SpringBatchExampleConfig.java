package net.petrikainulainen.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * This configuration class configures our example application.
 */
@Configuration
@ComponentScan
@EnableBatchProcessing
@EnableScheduling
@Import({
        DataSourceConfig.class,
        SpringBatchExampleJobConfig.class,
        SpringBatchWebAppConfig.class
})
@PropertySource("classpath:application.properties")
public class SpringBatchExampleConfig {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
