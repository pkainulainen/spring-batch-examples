package net.petrikainulainen.springbatch.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author Petri Kainulainen
 */
public class HelloWorldProcessor implements ItemProcessor<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldProcessor.class);

    @Override
    public String process(String item) throws Exception {
        LOGGER.info("Processing message: {}", item);

        item = item.toUpperCase();

        LOGGER.info("Processed message is: {}", item);

        return item;
    }
}
