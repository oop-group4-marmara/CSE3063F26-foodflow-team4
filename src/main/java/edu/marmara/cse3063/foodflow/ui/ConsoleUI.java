package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.RegisterCustomerDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public final class ConsoleUI
{
    private final ICustomerService customerService;
    private final ConsoleInput input;
    private final PrintStream output;

    public ConsoleUI(ICustomerService customerService, ConsoleInput input, PrintStream output)
    {
        this.customerService = Objects.requireNonNull(customerService, "customerService");
        this.input = Objects.requireNonNull(input, "input");
        this.output = Objects.requireNonNull(output, "output");
    }

    public void run()
    {
        boolean running = true;
        while (running)
        {
            Optional<CustomerDto> customer = customerService.getActiveCustomer();
            running = customer.isPresent()
                    ? showCustomerMenu(customer.get())
                    : showWelcomeMenu();
            output.println();
        }
        output.println("Goodbye.");
        output.flush();
    }

    private boolean showWelcomeMenu()
    {
        printHeader();
        output.println("================================");
        output.println("1. Register a new customer");
        output.println("2. Continue with an existing e-mail");
        output.println("0. Exit");
        output.println();

        OptionalInt choice = input.readChoice("Your choice: ", 0, 1, 2);
        if (choice.isEmpty() || choice.getAsInt() == 0)
        {
            return false;
        }
        return choice.getAsInt() == 1 ? registerCustomer() : loginCustomer();
    }

    private boolean showCustomerMenu(CustomerDto customer)
    {
        printHeader();
        output.println("Customer: " + customer.getName());
        output.println("E-mail: " + customer.getEmail());
        output.println("================================");
        output.println("1. Change customer");
        output.println("0. Exit");
        output.println();

        OptionalInt choice = input.readChoice("Your choice: ", 0, 1);
        if (choice.isEmpty() || choice.getAsInt() == 0)
        {
            return false;
        }
        customerService.logout();
        return true;
    }

    private boolean registerCustomer()
    {
        output.println();
        output.println("Register a new customer");
        output.println("Enter 0 to go back.");

        String[] prompts = {"Name: ", "E-mail: ", "District: ", "Street: "};
        String[] fields = new String[prompts.length];
        try
        {
            for (int index = 0; index < prompts.length; index++)
            {
                Optional<String> value = input.readLine(prompts[index]);
                if (value.isEmpty())
                {
                    return false;
                }
                if (value.get().trim().equals("0"))
                {
                    return true;
                }
                fields[index] = value.get();
                if (index == 1)
                {
                    customerService.validateRegistrationEmail(fields[index]);
                }
            }
            customerService.registerCustomer(new RegisterCustomerDto(fields[0], fields[1], fields[2], fields[3]));
        }
        catch (IllegalArgumentException | IllegalStateException exception)
        {
            output.println("Error: " + exception.getMessage());
        }
        return true;
    }

    private boolean loginCustomer()
    {
        output.println();
        output.println("Continue with an existing e-mail");
        output.println("Enter 0 to go back.");

        Optional<String> email = input.readLine("E-mail: ");
        if (email.isEmpty())
        {
            return false;
        }
        if (email.get().trim().equals("0"))
        {
            return true;
        }

        try
        {
            customerService.loginExisting(email.get());
        }
        catch (IllegalArgumentException | IllegalStateException exception)
        {
            output.println("Error: " + exception.getMessage());
        }
        return true;
    }

    private void printHeader()
    {
        output.println("================================");
        output.println("            FOODFLOW");
    }
}
