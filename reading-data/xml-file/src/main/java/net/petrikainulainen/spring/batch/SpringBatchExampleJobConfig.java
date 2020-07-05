package net.petrikainulainen.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * This configuration class configures the Spring Batch job that
 * is used to demonstrate that our Maven and Gradle projects declare
 * all required dependencies.
 */
@Configuration
public class SpringBatchExampleJobConfig {

    @Bean
    public ItemReader<StudentDTO> itemReader() {
        StaxEventItemReader<StudentDTO> xmlFileReader = new StaxEventItemReader<>();
        xmlFileReader.setResource(new ClassPathResource("data/students.xml"));
        xmlFileReader.setFragmentRootElementName("student");

        Jaxb2Marshaller studentMarshaller = new Jaxb2Marshaller();
        studentMarshaller.setClassesToBeBound(StudentDTO.class);
        xmlFileReader.setUnmarshaller(studentMarshaller);

        return xmlFileReader;
    }

    @Bean
    public ItemWriter<StudentDTO> itemWriter() {
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
    public Step exampleJobStep(ItemReader<StudentDTO> reader,
                        ItemWriter<StudentDTO> writer,
                        StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("exampleJobStep")
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
    public Job exampleJob(Step exampleJobStep,
                   JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("exampleJob")
                .incrementer(new RunIdIncrementer())
                .flow(exampleJobStep)
                .end()
                .build();
    }
}
