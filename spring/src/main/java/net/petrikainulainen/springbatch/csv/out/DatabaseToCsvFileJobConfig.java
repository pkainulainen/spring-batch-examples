package net.petrikainulainen.springbatch.csv.out;

import net.petrikainulainen.springbatch.common.LoggingStudentProcessor;
import net.petrikainulainen.springbatch.common.LoggingStudentWriter;
import net.petrikainulainen.springbatch.student.StudentDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
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
 * @author Petri Kainulainen
 */
@Configuration
public class DatabaseToCsvFileJobConfig {

    @Bean
    ItemReader<StudentDTO> databaseCsvItemReader(DataSource dataSource) {
        JdbcPagingItemReader<StudentDTO> databaseReader = new JdbcPagingItemReader<>();

        databaseReader.setDataSource(dataSource);
        databaseReader.setPageSize(1);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(StudentDTO.class));

        PagingQueryProvider queryProvider = createQueryProvider();
        databaseReader.setQueryProvider(queryProvider);

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
    ItemProcessor<StudentDTO, StudentDTO> databaseCsvItemProcessor() {
        return new LoggingStudentProcessor();
    }

    @Bean
    ItemWriter<StudentDTO> databaseCsvItemWriter() {
        return new LoggingStudentWriter();
    }

    @Bean
    Step databaseToCsvFileStep(ItemReader<StudentDTO> databaseCsvItemReader,
                               ItemProcessor<StudentDTO, StudentDTO> databaseCsvItemProcessor,
                               ItemWriter<StudentDTO> databaseCsvItemWriter,
                               StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("databaseToCsvFileStep")
                .<StudentDTO, StudentDTO>chunk(1)
                .reader(databaseCsvItemReader)
                .processor(databaseCsvItemProcessor)
                .writer(databaseCsvItemWriter)
                .build();
    }

    @Bean
    Job databaseToCsvFileJob(JobBuilderFactory jobBuilderFactory,
                             @Qualifier("databaseToCsvFileStep") Step csvStudentStep) {
        return jobBuilderFactory.get("databaseToCsvFileJob")
                .incrementer(new RunIdIncrementer())
                .flow(csvStudentStep)
                .end()
                .build();
    }
}
