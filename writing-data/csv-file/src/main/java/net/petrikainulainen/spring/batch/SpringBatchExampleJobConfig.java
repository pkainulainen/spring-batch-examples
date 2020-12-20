package net.petrikainulainen.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * This configuration class configures the Spring Batch job that
 * is used to demonstrate that our item writes writes the correct
 * information to the CSV file.
 */
@Configuration
public class SpringBatchExampleJobConfig {

    private static final String PROPERTY_CSV_EXPORT_FILE_HEADER = "batch.job.export.file.header";
    private static final String PROPERTY_CSV_EXPORT_FILE_PATH = "batch.job.export.file.path";

    @Bean
    public ItemReader<StudentDTO> itemReader() {
        return new FakeStudentItemReader();
    }

    @Bean
    public ItemWriter<StudentDTO> itemWriter(Environment environment) {
        String exportFilePath = environment.getRequiredProperty(PROPERTY_CSV_EXPORT_FILE_PATH);
        Resource exportFileResource = new FileSystemResource(exportFilePath);

        String exportFileHeader = environment.getRequiredProperty(PROPERTY_CSV_EXPORT_FILE_HEADER);
        StringHeaderWriter headerWriter = new StringHeaderWriter(exportFileHeader);

        LineAggregator<StudentDTO> lineAggregator = createStudentLineAggregator();

        return new FlatFileItemWriterBuilder<StudentDTO>()
                .name("studentWriter")
                .headerCallback(headerWriter)
                .lineAggregator(lineAggregator)
                .resource(exportFileResource)
                .build();
    }

    private LineAggregator<StudentDTO> createStudentLineAggregator() {
        DelimitedLineAggregator<StudentDTO> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(";");

        FieldExtractor<StudentDTO> fieldExtractor = createStudentFieldExtractor();
        lineAggregator.setFieldExtractor(fieldExtractor);

        return lineAggregator;
    }

    private FieldExtractor<StudentDTO> createStudentFieldExtractor() {
        BeanWrapperFieldExtractor<StudentDTO> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"name", "emailAddress", "purchasedPackage"});
        return extractor;
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
