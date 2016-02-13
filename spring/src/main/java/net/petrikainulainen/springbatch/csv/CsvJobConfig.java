package net.petrikainulainen.springbatch.csv;

import net.petrikainulainen.springbatch.student.StudentDTO;
import net.petrikainulainen.springbatch.common.LoggingStudentProcessor;
import net.petrikainulainen.springbatch.common.LoggingStudentWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class CsvJobConfig {

    private static final String PROPERTY_CSV_SOURCE_FILE_PATH = "csv.job.source.file.path";

    @Bean
    ItemReader<StudentDTO> csvItemReader(Environment environment) {
        FlatFileItemReader<StudentDTO> csvFileReader = new FlatFileItemReader<>();
        csvFileReader.setResource(new ClassPathResource(environment.getRequiredProperty(PROPERTY_CSV_SOURCE_FILE_PATH)));
        csvFileReader.setLinesToSkip(1);

        LineMapper<StudentDTO> studentLineMapper = createStudentLineMapper();
        csvFileReader.setLineMapper(studentLineMapper);

        return csvFileReader;
    }

    private LineMapper<StudentDTO> createStudentLineMapper() {
        DefaultLineMapper<StudentDTO> studentLineMapper = new DefaultLineMapper<>();

        LineTokenizer studentLineTokenizer = createStudentLineTokenizer();
        studentLineMapper.setLineTokenizer(studentLineTokenizer);

        FieldSetMapper<StudentDTO> studentInformationMapper = createStudentInformationMapper();
        studentLineMapper.setFieldSetMapper(studentInformationMapper);

        return studentLineMapper;
    }

    private LineTokenizer createStudentLineTokenizer() {
        DelimitedLineTokenizer studentLineTokenizer = new DelimitedLineTokenizer();
        studentLineTokenizer.setDelimiter(";");
        studentLineTokenizer.setNames(new String[]{"name", "emailAddress", "purchasedPackage"});
        return studentLineTokenizer;
    }

    private FieldSetMapper<StudentDTO> createStudentInformationMapper() {
        BeanWrapperFieldSetMapper<StudentDTO> studentInformationMapper = new BeanWrapperFieldSetMapper<>();
        studentInformationMapper.setTargetType(StudentDTO.class);
        return studentInformationMapper;
    }

    @Bean
    ItemProcessor<StudentDTO, StudentDTO> csvItemProcessor() {
        return new LoggingStudentProcessor();
    }

    @Bean
    ItemWriter<StudentDTO> csvItemWriter() {
        return new LoggingStudentWriter();
    }

    @Bean
    Step csvStudentStep(ItemReader<StudentDTO> csvItemReader,
                 ItemProcessor<StudentDTO, StudentDTO> csvItemProcessor,
                 ItemWriter<StudentDTO> csvItemWriter,
                 StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("csvStudentStep")
                .<StudentDTO, StudentDTO>chunk(1)
                .reader(csvItemReader)
                .processor(csvItemProcessor)
                .writer(csvItemWriter)
                .build();
    }

    @Bean
    Job csvStudentJob(JobBuilderFactory jobBuilderFactory, Step csvStudentStep) {
        return jobBuilderFactory.get("csvStudentJob")
                .incrementer(new RunIdIncrementer())
                .flow(csvStudentStep)
                .end()
                .build();
    }
}
