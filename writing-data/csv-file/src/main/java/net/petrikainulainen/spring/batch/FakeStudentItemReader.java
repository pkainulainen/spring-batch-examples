package net.petrikainulainen.spring.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@code ItemReader} uses a hard-coded list as
 * its input data.
 */
public class FakeStudentItemReader implements ItemReader<StudentDTO> {

    private int nextStudentIndex;
    private List<StudentDTO> students;

    public FakeStudentItemReader() {
        nextStudentIndex = 0;

        students = new ArrayList<>();

        StudentDTO tony = new StudentDTO();
        tony.setEmailAddress("tony.tester@gmail.com");
        tony.setName("Tony Tester");
        tony.setPurchasedPackage("master");
        students.add(tony);

        StudentDTO nick = new StudentDTO();
        nick.setEmailAddress("nick.newbie@gmail.com");
        nick.setName("Nick Newbie");
        nick.setPurchasedPackage("starter");
        students.add(nick);

        StudentDTO ian = new StudentDTO();
        ian.setEmailAddress("ian.intermediate@gmail.com");
        ian.setName("Ian Intermediate");
        ian.setPurchasedPackage("intermediate");
        students.add(ian);
    }

    @Override
    public StudentDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        StudentDTO next = null;
        if (nextStudentIndex < students.size()) {
            next = students.get(nextStudentIndex);
            nextStudentIndex++;
        }
        else {
            nextStudentIndex = 0;
        }
        return next;
    }
}
