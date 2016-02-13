package net.petrikainulainen.springbatch.common;

import net.petrikainulainen.springbatch.student.StudentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * This custom {@code ItemWriter} writes the information of the student to
 * the log.
 *
 * @author Petri Kainulainen
 */
public class LoggingStudentWriter implements ItemWriter<StudentDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingStudentWriter.class);

    @Override
    public void write(List<? extends StudentDTO> items) throws Exception {
        LOGGER.info("Received the information of {} students", items.size());

        items.forEach(i -> LOGGER.debug("Received the information of a student: {}", i));
    }
}
