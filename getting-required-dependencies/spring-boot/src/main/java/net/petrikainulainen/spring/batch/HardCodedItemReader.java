package net.petrikainulainen.spring.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Arrays;
import java.util.List;

/**
 * This {@code ItemReader} returns the words: "Hello" and "World"
 * before returning {@code null}. The goal of this component
 * is to help us to demonstrate that our Maven and Gradle projects
 * declare all required dependencies.
 */
class HardCodedItemReader implements ItemReader<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HardCodedItemReader.class);

    private final List<String> ITEMS = Arrays.asList("Hello", "World");

    private int itemIndex = 0;

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        LOGGER.info("Reading the next item");
        if (itemIndex >= ITEMS.size()) {
            LOGGER.info("The next item wasn't found. Returning: null.");
            return null;
        }

        String item = ITEMS.get(itemIndex);
        LOGGER.info("The next item was found. Returning: {}", item);

        itemIndex++;
        return item;
    }
}
