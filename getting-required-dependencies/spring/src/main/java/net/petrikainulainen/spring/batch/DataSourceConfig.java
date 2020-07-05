package net.petrikainulainen.spring.batch;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * This configuration class configures the beans which communicate with
 * the used database.
 */
@Configuration
public class DataSourceConfig {

    private static final String PROPERTY_NAME_DB_DRIVER_CLASS = "db.driver";
    private static final String PROPERTY_NAME_DB_PASSWORD = "db.password";
    private static final String PROPERTY_NAME_DB_URL = "db.url";
    private static final String PROPERTY_NAME_DB_USER = "db.username";
    private static final String PROPERTY_NAME_LIQUIBASE_CHANGE_LOG = "liquibase.change-log";

    /**
     * Creates and configures the HikariCP datasource bean.
     * @param env   The runtime environment of our application.
     * @return
     */
    @Bean(destroyMethod = "close")
    public DataSource dataSource(Environment env) {
        HikariConfig dataSourceConfig = new HikariConfig();

        dataSourceConfig.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DB_DRIVER_CLASS));
        dataSourceConfig.setJdbcUrl(env.getRequiredProperty(PROPERTY_NAME_DB_URL));
        dataSourceConfig.setUsername(env.getRequiredProperty(PROPERTY_NAME_DB_USER));
        dataSourceConfig.setPassword(env.getRequiredProperty(PROPERTY_NAME_DB_PASSWORD));

        return new HikariDataSource(dataSourceConfig);
    }

    /**
     * Configures the bean which runs the specified change log file when the
     * Spring application context is loaded. This change log file creates the log
     * tables used by Spring Batch.
     * @param dataSource    The data source which allows us to connect to the used database.
     * @param env           The runtime environment of our application.
     * @return
     */
    @Bean
    public SpringLiquibase springLiquibase(DataSource dataSource, Environment env) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(env.getRequiredProperty(PROPERTY_NAME_LIQUIBASE_CHANGE_LOG));
        return liquibase;
    }
}
