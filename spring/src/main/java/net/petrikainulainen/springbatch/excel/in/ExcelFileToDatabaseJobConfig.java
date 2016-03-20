package net.petrikainulainen.springbatch.excel.in;

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
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.mapping.BeanWrapperRowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Petri Kainulainen
 */
@Configuration
public class ExcelFileToDatabaseJobConfig {

    private static final String PROPERTY_EXCEL_SOURCE_FILE_PATH = "excel.to.database.job.source.file.path";

    @Bean
    ItemReader<StudentDTO> excelStudentReader(Environment environment) {
        PoiItemReader<StudentDTO> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource(environment.getRequiredProperty(PROPERTY_EXCEL_SOURCE_FILE_PATH)));
        reader.setRowMapper(excelRowMapper());
        return reader;
    }

    private RowMapper<StudentDTO> excelRowMapper() {
        BeanWrapperRowMapper<StudentDTO> rowMapper = new BeanWrapperRowMapper<>();
        rowMapper.setTargetType(StudentDTO.class);
        return rowMapper;
    }

    /**
     * If your Excel document has no header, you have to create a custom
     * row mapper and configure it here.
     */
    /*private RowMapper<StudentDTO> excelRowMapper() {
       return new StudentExcelRowMapper();
    }*/

    @Bean
    ItemProcessor<StudentDTO, StudentDTO> excelStudentProcessor() {
        return new LoggingStudentProcessor();
    }

    @Bean
    ItemWriter<StudentDTO> excelStudentWriter() {
        return new LoggingStudentWriter();
    }

    @Bean
    Step excelFileToDatabaseStep(ItemReader<StudentDTO> excelStudentReader,
                         ItemProcessor<StudentDTO, StudentDTO> excelStudentProcessor,
                         ItemWriter<StudentDTO> excelStudentWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("excelFileToDatabaseStep")
                .<StudentDTO, StudentDTO>chunk(1)
                .reader(excelStudentReader)
                .processor(excelStudentProcessor)
                .writer(excelStudentWriter)
                .build();
    }

    @Bean
    Job excelFileToDatabaseJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("excelFileToDatabaseStep") Step excelStudentStep) {
        return jobBuilderFactory.get("excelFileToDatabaseJob")
                .incrementer(new RunIdIncrementer())
                .flow(excelStudentStep)
                .end()
                .build();
    }
}
