package net.petrikainulainen.spring.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * This class demonstrates how we can read the input of our batch job from an
 * external REST API.
 *
 * @author Petri Kainulainen
 */
class RESTStudentReader implements ItemReader<StudentDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTStudentReader.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextStudentIndex;
    private List<StudentDTO> studentData;

    RESTStudentReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextStudentIndex = 0;
    }

    @Override
    public StudentDTO read() throws Exception {
        LOGGER.info("Reading the information of the next student");

        if (studentDataIsNotInitialized()) {
            studentData = fetchStudentDataFromAPI();
        }

        StudentDTO nextStudent = null;

        if (nextStudentIndex < studentData.size()) {
            nextStudent = studentData.get(nextStudentIndex);
            nextStudentIndex++;
        }
        else {
            nextStudentIndex = 0;
            studentData = null;
        }

        LOGGER.info("Found student: {}", nextStudent);

        return nextStudent;
    }

    private boolean studentDataIsNotInitialized() {
        return this.studentData == null;
    }

    private List<StudentDTO> fetchStudentDataFromAPI() {
        LOGGER.debug("Fetching student data from an external API by using the url: {}", apiUrl);

        ResponseEntity<StudentDTO[]> response = restTemplate.getForEntity(apiUrl, StudentDTO[].class);
        StudentDTO[] studentData = response.getBody();
        LOGGER.debug("Found {} students", studentData.length);

        return Arrays.asList(studentData);
    }
}
