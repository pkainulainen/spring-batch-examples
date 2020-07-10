package net.petrikainulainen.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * This configuration class configures the Spring Batch job that
 * is used to demonstrate that our item reader reads the correct
 * information from the database by using JDBC pagination.
 */
@Configuration
public class JDBCPaginationExampleJobConfig {

    @Bean
    public ItemReader<StudentDTO> jdbcPaginationItemReader(DataSource dataSource) {
        JdbcPagingItemReader<StudentDTO> databaseReader = new JdbcPagingItemReader<>();

        databaseReader.setDataSource(dataSource);
        databaseReader.setPageSize(1);

        PagingQueryProvider queryProvider = createQueryProvider();
        databaseReader.setQueryProvider(queryProvider);

        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(StudentDTO.class));

        return databaseReader;
    }

    private PagingQueryProvider createQueryProvider() {
        H2PagingQueryProvider queryProvider = new H2PagingQueryProvider();

        queryProvider.setSelectClause("SELECT email_address, name, purchased_package");
        queryProvider.setFromClause("FROM students");
        queryProvider.setSortKeys(sortByEmailAddressAsc());

        return queryProvider;
    }

    private Map<String, Order> sortByEmailAddressAsc() {
        Map<String, Order> sortConfiguration = new HashMap<>();
        sortConfiguration.put("email_address", Order.ASCENDING);
        return sortConfiguration;
    }

    @Bean
    public ItemWriter<StudentDTO> jdbcPaginationItemWriter() {
        return new LoggingItemWriter();
    }

    /**
     * Creates a bean that represents the only step of our batch job.
     * @param reader
     * @param writer
     * @param stepBuilderFactory
     * @return
     */
    @Bean
    public Step jdbcPaginationStep(@Qualifier("jdbcPaginationItemReader") ItemReader<StudentDTO> reader,
                                   @Qualifier("jdbcPaginationItemWriter") ItemWriter<StudentDTO> writer,
                                   StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("jdbcPaginationStep")
                .<StudentDTO, StudentDTO>chunk(1)
                .reader(reader)
                .writer(writer)
                .build();
    }

    /**
     * Creates a bean that represents our example batch job.
     * @param exampleJobStep
     * @param jobBuilderFactory
     * @return
     */
    @Bean
    public Job jdbcPaginationJob(@Qualifier("jdbcPaginationStep") Step exampleJobStep,
                                 JobBuilderFactory jobBuilderFactory) {
        Job databaseCursorJob = jobBuilderFactory.get("jdbcPaginationJob")
                .incrementer(new RunIdIncrementer())
                .flow(exampleJobStep)
                .end()
                .build();
        return databaseCursorJob;
    }
}
