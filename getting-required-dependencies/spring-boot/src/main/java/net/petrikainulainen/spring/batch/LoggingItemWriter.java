package net.petrikainulainen.spring.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * This {@code ItemWriter} writes the received {@code String} objects
 * to a log file. The goal of this component is to help us to demonstrate
 * that our Maven and Gradle projects declare all required dependencies.
 */
class LoggingItemWriter implements ItemWriter<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingItemWriter.class);

    @Override
    public void write(List<? extends String> list) throws Exception {
        LOGGER.info("Writing strings: {}", list);
    }
}
