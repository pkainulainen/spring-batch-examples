package net.petrikainulainen.springbatch.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * @author Petri Kainulainen
 */
public class HelloWorldReader implements ItemReader<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldReader.class);

    private boolean messageRead = false;

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        LOGGER.info("Reading message");

        String message = "Hello World!";
        if (messageRead) {
            message = null;
        }

        messageRead = true;
        LOGGER.info("Read message: {}", message);

        return message;
    }
}
