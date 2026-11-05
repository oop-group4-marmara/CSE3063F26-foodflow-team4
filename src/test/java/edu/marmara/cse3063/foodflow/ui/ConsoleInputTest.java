package edu.marmara.cse3063.foodflow.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(5)
class ConsoleInputTest
{
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "2147483648", "3", "-1"})
    void invalidChoiceConsumesTheLineAndAllowsRetry(String invalid)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(invalid + "\n 2 \n"))
        {
            ConsoleInput input = new ConsoleInput(scanner, new PrintStream(bytes, true, StandardCharsets.UTF_8));

            assertEquals(2, input.readChoice("Your choice: ", 0, 1, 2).orElseThrow());
            assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("Please enter one of the available options."));
        }
    }

    @Test
    void validZeroIsReturnedAsAChoice()
    {
        try (Scanner scanner = new Scanner("0\n"))
        {
            ConsoleInput input = new ConsoleInput(scanner, new PrintStream(new ByteArrayOutputStream()));
            assertEquals(0, input.readChoice("Your choice: ", 0, 1).orElseThrow());
        }
    }

    @Test
    void readsCompleteLinesWithoutTrimmingFieldsOrClosingTheReader()
    {
        try (Scanner scanner = new Scanner("  Bob Smith  \nKadıköy\n"))
        {
            ConsoleInput input = new ConsoleInput(scanner, new PrintStream(new ByteArrayOutputStream()));

            assertEquals("  Bob Smith  ", input.readLine("Name: ").orElseThrow());
            assertEquals("Kadıköy", scanner.nextLine());
        }
    }

    @Test
    void endOfInputReturnsEmptyInsteadOfRetrying()
    {
        try (Scanner scanner = new Scanner("abc\n"))
        {
            ConsoleInput input = new ConsoleInput(scanner, new PrintStream(new ByteArrayOutputStream()));
            assertTrue(input.readChoice("Your choice: ", 0, 1).isEmpty());
            assertTrue(input.readLine("Name: ").isEmpty());
        }
    }
}
