package com.techelevator.view;


import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class ConsoleService {

    private PrintWriter out;
    private Scanner in;

    public ConsoleService(InputStream input, OutputStream output) {
        this.out = new PrintWriter(output, true);
        this.in = new Scanner(input);
    }

    public Object getChoiceFromOptions(Object[] options) {
        Object choice = null;
        while (choice == null) {
            displayMenuOptions(options);
            choice = getChoiceFromUserInput(options);
        }
        out.println();
        return choice;
    }

    private Object getChoiceFromUserInput(Object[] options) {
        Object choice = null;
        String userInput = in.nextLine();
        try {
            int selectedOption = Integer.valueOf(userInput);
            if (selectedOption > 0 && selectedOption <= options.length) {
                choice = options[selectedOption - 1];
            }
        } catch (NumberFormatException e) {
            // eat the exception, an error message will be displayed below since choice will be null
        }
        if (choice == null) {
            out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
        }
        return choice;
    }

    private void displayMenuOptions(Object[] options) {
        out.println();
        for (int i = 0; i < options.length; i++) {
            int optionNum = i + 1;
            out.println(optionNum + ") " + options[i]);
        }
        out.print(System.lineSeparator() + "Please choose an option >>> ");
        out.flush();
    }

    public int displayTransferList(Transfer[] transfers, int currentUser) {
        System.out.println("-----------------------------------------------------");
        System.out.println("Transfers");
        System.out.printf("%-10s%-20s%s\n", "ID", "From/To", "Amount");
        System.out.println("-----------------------------------------------------");
        for (Transfer transfer : transfers) {
            if (transfer.getUserIdFrom() == currentUser) {
                System.out.printf("%-10d%-20s$%.2f\n", transfer.getTransferId(), "To: " + transfer.getNameTo(), transfer.getAmount());
            } else {
                System.out.printf("%-10d%-20s$%.2f\n", transfer.getTransferId(), "From: " + transfer.getNameFrom(), transfer.getAmount());

            }
        }
        System.out.println("-----------------------------------------------------");
        System.out.println("Please enter transfer ID to view details (0 to cancel): ");
        return getUserInputInteger("Transfer Id");
    }

    public void displayTransferDetails(Transfer transfer, int currentUser) {
        System.out.println("-----------------------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("-----------------------------------------------------");
        System.out.println("Id: " + transfer.getTransferId());
        System.out.println("From: " + transfer.getNameFrom());
        System.out.println("To: " + transfer.getNameTo());
        if (transfer.getUserIdFrom() == currentUser) {
            System.out.println("Type: Send");
        } else {
            System.out.println("Type: Received");
        }

        System.out.println("Status: Approved");
        System.out.printf("Amount: $%.2f\n", transfer.getAmount());
        System.out.println("-----------------------------------------------------");
    }

    public String getUserInput(String prompt) {
        out.print(prompt + ": ");
        out.flush();
        return in.nextLine();
    }

    public Integer getUserInputInteger(String prompt) {
        Integer result = null;
        do {
            out.print(prompt + ": ");
            out.flush();
            String userInput = in.nextLine();
            try {
                result = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
            }
        } while (result == null);
        return result;
    }

    public Double getUserInputDouble(String prompt) {
        Double result = null;
        do {
            out.print(prompt + ": ");
            out.flush();
            String userInput = in.nextLine();
            try {
                result = Double.parseDouble(userInput);
                if (result < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
            }
        } while (result == null);
        return result;
    }

    public void printCurrentBalance(double balance) {
        System.out.printf("Your current account balance is: $%.2f", balance);
        System.out.println();
    }

    public void displayListOfUsers(User[] users, AuthenticatedUser currentUser) {
        System.out.println("----------------------");
        System.out.printf("%-10s%s\n", "Users ID", "Name");
        for (User user : users) {
            if (!user.getId().equals(currentUser.getUser().getId())) {
                System.out.printf("%-10d%s\n", user.getId(), user.getUsername());
            }
        }
        System.out.println("----------------------");

    }

    public int getUserIdToSendMoney() {
        System.out.println("Enter ID of user you are sending to (0 to cancel): ");
        return getUserInputInteger("User ID");
    }

    public double getAmountFromUser() {
        System.out.println("Enter amount: ");
        return getUserInputDouble("Amount");
    }

    public void printErrorMessage(String message) {
        System.out.println(message);
    }
}
