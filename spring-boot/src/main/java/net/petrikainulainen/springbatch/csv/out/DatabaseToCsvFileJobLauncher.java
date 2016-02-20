package net.petrikainulainen.springbatch.csv.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Petri Kainulainen
 */
@Component
public class DatabaseToCsvFileJobLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseToCsvFileJobLauncher.class);

    private final Job job;

    private final JobLauncher jobLauncher;

    @Autowired
    DatabaseToCsvFileJobLauncher(@Qualifier("databaseToCsvFileJob") Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "${database.to.csv.job.cron}")
    void launchDatabaseToCsvFileJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        LOGGER.info("Starting databaseToCsvFile job");

        jobLauncher.run(job, newExecution());

        LOGGER.info("Stopping databaseToCsvFile job");
    }

    private JobParameters newExecution() {
        Map<String, JobParameter> parameters = new HashMap<>();

        JobParameter parameter = new JobParameter(new Date());
        parameters.put("currentTime", parameter);

        return new JobParameters(parameters);
    }
}
