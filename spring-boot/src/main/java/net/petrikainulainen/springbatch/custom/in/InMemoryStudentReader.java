package net.petrikainulainen.springbatch.custom.in;

import net.petrikainulainen.springbatch.student.StudentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class demonstrates how we can create a custom item reader.
 *
 * @author Petri Kainulainen
 */
public class InMemoryStudentReader implements ItemReader<StudentDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryStudentReader.class);

    private int nextStudentIndex;
    private List<StudentDTO> studentData;

    InMemoryStudentReader() {
        initialize();
    }

    private void initialize() {
        StudentDTO tony = new StudentDTO();
        tony.setEmailAddress("tony.tester@gmail.com");
        tony.setName("Tony Tester");
        tony.setPurchasedPackage("master");

        StudentDTO nick = new StudentDTO();
        nick.setEmailAddress("nick.newbie@gmail.com");
        nick.setName("Nick Newbie");
        nick.setPurchasedPackage("starter");

        StudentDTO ian = new StudentDTO();
        ian.setEmailAddress("ian.intermediate@gmail.com");
        ian.setName("Ian Intermediate");
        ian.setPurchasedPackage("intermediate");

        studentData = Collections.unmodifiableList(Arrays.asList(tony, nick, ian));
        nextStudentIndex = 0;
    }

    @Override
    public StudentDTO read() throws Exception {
        LOGGER.info("Reading the information of the next student");

        StudentDTO nextStudent = null;

        if (nextStudentIndex < studentData.size()) {
            nextStudent = studentData.get(nextStudentIndex);
            nextStudentIndex++;
        }

        LOGGER.info("Found student: {}", nextStudent);

        return nextStudent;
    }
}
