package net.petrikainulainen.springbatch.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * @author Petri Kainulainen
 */
public class HelloWorldWriter implements ItemWriter<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldWriter.class);

    @Override
    public void write(List<? extends String> items) throws Exception {
        LOGGER.info("Received messages: {}", items);
    }
}
