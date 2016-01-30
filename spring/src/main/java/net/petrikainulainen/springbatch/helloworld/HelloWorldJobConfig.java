package net.petrikainulainen.springbatch.helloworld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class HelloWorldJobConfig {

    @Bean
    ItemReader<String> helloWorldReader() {
        return new HelloWorldReader();
    }

    @Bean
    ItemProcessor<String, String> helloWorldProcessor() {
        return new HelloWorldProcessor();
    }

    @Bean
    ItemWriter<String> helloWorldWriter() {
        return new HelloWorldWriter();
    }

    @Bean
    Step helloWorldStep(ItemReader<String> helloWorldReader,
                        ItemProcessor<String, String> helloWorldProcessor,
                        ItemWriter<String> helloWorldWriter,
                        StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("helloWorldStep")
                .<String, String>chunk(1)
                .reader(helloWorldReader)
                .processor(helloWorldProcessor)
                .writer(helloWorldWriter)
                .build();
    }

    @Bean
    Job helloWorldJob(JobBuilderFactory jobBuilderFactory, Step helloWorldStep) {
        return jobBuilderFactory.get("helloWorldJob")
                .incrementer(new RunIdIncrementer())
                .flow(helloWorldStep)
                .end()
                .build();
    }
}
