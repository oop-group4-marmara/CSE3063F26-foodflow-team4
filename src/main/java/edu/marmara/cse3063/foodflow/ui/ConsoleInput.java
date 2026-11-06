package edu.marmara.cse3063.foodflow.ui;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;

public final class ConsoleInput
{
    private final Scanner scanner;
    private final PrintStream output;

    public ConsoleInput(Scanner scanner, PrintStream output)
    {
        this.scanner = Objects.requireNonNull(scanner, "scanner");
        this.output = Objects.requireNonNull(output, "output");
    }

    public Optional<String> readLine(String prompt)
    {
        output.print(prompt);
        output.flush();
        return scanner.hasNextLine() ? Optional.of(scanner.nextLine()) : Optional.empty();
    }

    public OptionalInt readChoice(String prompt, int... allowedChoices)
    {
        while (true)
        {
            Optional<String> line = readLine(prompt);
            if (line.isEmpty())
            {
                return OptionalInt.empty();
            }

            try
            {
                int choice = Integer.parseInt(line.get().trim());
                for (int allowedChoice : allowedChoices)
                {
                    if (choice == allowedChoice)
                    {
                        return OptionalInt.of(choice);
                    }
                }
            }
            catch (Exception exception) { }

            output.println("Please enter one of the available options.");
        }
    }
}
