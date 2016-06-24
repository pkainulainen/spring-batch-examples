package net.petrikainulainen.springbatch.xml.in;

import net.petrikainulainen.springbatch.common.LoggingStudentProcessor;
import net.petrikainulainen.springbatch.student.StudentDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class XmlFileToDatabaseJobConfig {

    private static final String PROPERTY_XML_SOURCE_FILE_PATH = "xml.to.database.job.source.file.path";
    private static final String QUERY_INSERT_STUDENT = "INSERT " +
            "INTO students(email_address, name, purchased_package) " +
            "VALUES (?, ?, ?)";

    @Bean
    ItemReader<StudentDTO> xmlFileItemReader(Environment environment) {
        StaxEventItemReader<StudentDTO> xmlFileReader = new StaxEventItemReader<>();
        xmlFileReader.setResource(new ClassPathResource(environment.getRequiredProperty(PROPERTY_XML_SOURCE_FILE_PATH)));
        xmlFileReader.setFragmentRootElementName("student");

        Jaxb2Marshaller studentMarshaller = new Jaxb2Marshaller();
        studentMarshaller.setClassesToBeBound(StudentDTO.class);

        xmlFileReader.setUnmarshaller(studentMarshaller);
        return xmlFileReader;
    }

    @Bean
    ItemProcessor<StudentDTO, StudentDTO> xmlFileItemProcessor() {
        return new LoggingStudentProcessor();
    }

    @Bean
    ItemWriter<StudentDTO> xmlFileDatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        JdbcBatchItemWriter<StudentDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
        databaseItemWriter.setDataSource(dataSource);
        databaseItemWriter.setJdbcTemplate(jdbcTemplate);

        databaseItemWriter.setSql(QUERY_INSERT_STUDENT);

        ItemPreparedStatementSetter<StudentDTO> studentPreparedStatementSetter = new StudentPreparedStatementSetter();
        databaseItemWriter.setItemPreparedStatementSetter(studentPreparedStatementSetter);

        return databaseItemWriter;
    }
    @Bean
    Step xmlFileToDatabaseStep(ItemReader<StudentDTO> xmlFileItemReader,
                               ItemProcessor<StudentDTO, StudentDTO> xmlFileItemProcessor,
                               ItemWriter<StudentDTO> xmlFileDatabaseItemWriter,
                               StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("xmlFileToDatabaseStep")
                .<StudentDTO, StudentDTO>chunk(1)
                .reader(xmlFileItemReader)
                .processor(xmlFileItemProcessor)
                .writer(xmlFileDatabaseItemWriter)
                .build();
    }

    @Bean
    Job xmlFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
                             @Qualifier("xmlFileToDatabaseStep") Step xmlStudentStep) {
        return jobBuilderFactory.get("xmlFileToDatabaseJob")
                .incrementer(new RunIdIncrementer())
                .flow(xmlStudentStep)
                .end()
                .build();
    }
}
