package net.petrikainulainen.springbatch.xml;

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
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class XmlFileToDatabaseJobConfig {

    private static final String PROPERTY_XML_SOURCE_FILE_PATH = "xml.job.source.file.path";

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
    ItemWriter<StudentDTO> xmlFileItemWriter() {
        return new LoggingStudentWriter();
    }

    @Bean
    Step xmlFileToDatabaseStep(ItemReader<StudentDTO> xmlFileItemReader,
                               ItemProcessor<StudentDTO, StudentDTO> xmlFileItemProcessor,
                               ItemWriter<StudentDTO> xmlFileItemWriter,
                               StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("xmlFileToDatabaseStep")
                .<StudentDTO, StudentDTO>chunk(1)
                .reader(xmlFileItemReader)
                .processor(xmlFileItemProcessor)
                .writer(xmlFileItemWriter)
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
