package net.petrikainulainen.springbatch.xml.out;

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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class DatabaseToXmlFileJobConfig {

    private static final String PROPERTY_XML_EXPORT_FILE_PATH = "database.to.xml.job.export.file.path";
    private static final String QUERY_FIND_STUDENTS =
            "SELECT " +
                "email_address, " +
                "name, " +
                "purchased_package " +
            "FROM STUDENTS " +
            "ORDER BY email_address ASC";

    @Bean
    ItemReader<StudentDTO> databaseXmlItemReader(DataSource dataSource) {
        JdbcCursorItemReader<StudentDTO> databaseReader = new JdbcCursorItemReader<>();

        databaseReader.setDataSource(dataSource);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(StudentDTO.class));
        databaseReader.setSql(QUERY_FIND_STUDENTS);

        return databaseReader;
    }

    @Bean
    ItemProcessor<StudentDTO, StudentDTO> databaseXmlItemProcessor() {
        return new LoggingStudentProcessor();
    }

    @Bean
    ItemWriter<StudentDTO> databaseXmlItemWriter(Environment environment) {
        StaxEventItemWriter<StudentDTO> xmlFileWriter = new StaxEventItemWriter<>();

        String exportFilePath = environment.getRequiredProperty(PROPERTY_XML_EXPORT_FILE_PATH);
        xmlFileWriter.setResource(new FileSystemResource(exportFilePath));

        xmlFileWriter.setRootTagName("students");

        Jaxb2Marshaller studentMarshaller = new Jaxb2Marshaller();
        studentMarshaller.setClassesToBeBound(StudentDTO.class);
        xmlFileWriter.setMarshaller(studentMarshaller);

        return xmlFileWriter;
    }

    @Bean
    Step databaseToXmlFileStep(ItemReader<StudentDTO> databaseXmlItemReader,
                               ItemProcessor<StudentDTO, StudentDTO> databaseXmlItemProcessor,
                               ItemWriter<StudentDTO> databaseXmlItemWriter,
                               StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("databaseToXmlFileStep")
                .<StudentDTO, StudentDTO>chunk(1)
                .reader(databaseXmlItemReader)
                .processor(databaseXmlItemProcessor)
                .writer(databaseXmlItemWriter)
                .build();
    }

    @Bean
    Job databaseToXmlFileJob(JobBuilderFactory jobBuilderFactory,
                             @Qualifier("databaseToXmlFileStep") Step csvStudentStep) {
        return jobBuilderFactory.get("databaseToXmlFileJob")
                .incrementer(new RunIdIncrementer())
                .flow(csvStudentStep)
                .end()
                .build();
    }
}
