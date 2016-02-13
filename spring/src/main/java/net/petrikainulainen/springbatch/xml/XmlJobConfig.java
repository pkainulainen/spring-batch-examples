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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class XmlJobConfig {

    private static final String PROPERTY_XML_SOURCE_FILE_PATH = "xml.job.source.file.path";

    @Bean
    ItemReader<StudentDTO> xmlItemReader(Environment environment) {
        StaxEventItemReader<StudentDTO> xmlFileReader = new StaxEventItemReader<>();
        xmlFileReader.setResource(new ClassPathResource(environment.getRequiredProperty(PROPERTY_XML_SOURCE_FILE_PATH)));
        xmlFileReader.setFragmentRootElementName("student");

        Jaxb2Marshaller studentMarshaller = new Jaxb2Marshaller();
        studentMarshaller.setClassesToBeBound(StudentDTO.class);

        xmlFileReader.setUnmarshaller(studentMarshaller);
        return xmlFileReader;
    }

    @Bean
    ItemProcessor<StudentDTO, StudentDTO> xmlItemProcessor() {
        return new LoggingStudentProcessor();
    }

    @Bean
    ItemWriter<StudentDTO> xmlItemWriter() {
        return new LoggingStudentWriter();
    }

    @Bean
    Step xmlStudentStep(ItemReader<StudentDTO> xmlItemReader,
                        ItemProcessor<StudentDTO, StudentDTO> xmlItemProcessor,
                        ItemWriter<StudentDTO> xmlItemWriter,
                        StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("xmlStudentStep")
                .<StudentDTO, StudentDTO>chunk(1)
                .reader(xmlItemReader)
                .processor(xmlItemProcessor)
                .writer(xmlItemWriter)
                .build();
    }

    @Bean
    Job xmlStudentJob(JobBuilderFactory jobBuilderFactory, Step csvStudentStep) {
        return jobBuilderFactory.get("xmlStudentJob")
                .incrementer(new RunIdIncrementer())
                .flow(csvStudentStep)
                .end()
                .build();
    }
}
