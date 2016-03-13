package net.petrikainulainen.springbatch.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author Petri Kainulainen
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

    @RequestMapping(method = RequestMethod.GET)
    public List<StudentDTO> findStudents() {
        LOGGER.info("Finding all students");

        List<StudentDTO> students = createStudents();
        LOGGER.info("Found {} students", students.size());

        return students;
    }

    private List<StudentDTO> createStudents() {
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

        return Arrays.asList(tony, nick, ian);
    }
}
