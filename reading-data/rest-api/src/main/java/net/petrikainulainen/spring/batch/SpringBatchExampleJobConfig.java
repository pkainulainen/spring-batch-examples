package net.petrikainulainen.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringBatchExampleJobConfig {

    private static final String PROPERTY_REST_API_URL = "rest.api.url";

    @Bean
    public ItemReader<StudentDTO> itemReader(Environment environment, RestTemplate restTemplate) {
        return new RESTStudentReader(environment.getRequiredProperty(PROPERTY_REST_API_URL), restTemplate);
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
