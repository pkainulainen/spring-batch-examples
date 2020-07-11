package net.petrikainulainen.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

/**
 * This configuration class configures the Spring Batch job that
 * is used to demonstrate that our item reader reads the correct
 * information from the database by using a database cursor.
 */
@Configuration
public class DatabaseCursorExampleJobConfig {

    private static final String QUERY_FIND_STUDENTS =
            "SELECT " +
                    "email_address, " +
                    "name, " +
                    "purchased_package " +
            "FROM STUDENTS " +
            "ORDER BY email_address ASC";

    @Bean
    public ItemReader<StudentDTO> databaseCursorItemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<StudentDTO>()
                .name("cursorItemReader")
                .dataSource(dataSource)
                .sql(QUERY_FIND_STUDENTS)
                .rowMapper(new BeanPropertyRowMapper<>(StudentDTO.class))
                .build();
    }

    @Bean
    public ItemWriter<StudentDTO> databaseCursorItemWriter() {
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
    public Step databaseCursorStep(@Qualifier("databaseCursorItemReader") ItemReader<StudentDTO> reader,
                                   @Qualifier("databaseCursorItemWriter") ItemWriter<StudentDTO> writer,
                                   StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("databaseCursorStep")
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
    public Job databaseCursorJob(@Qualifier("databaseCursorStep") Step exampleJobStep,
                                 JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("databaseCursorJob")
                .incrementer(new RunIdIncrementer())
                .flow(exampleJobStep)
                .end()
                .build();
    }
}
