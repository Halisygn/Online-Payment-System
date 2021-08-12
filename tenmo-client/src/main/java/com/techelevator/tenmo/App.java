package com.techelevator.tenmo;

import com.techelevator.tenmo.models.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private TransferService transferService;

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService,
               TransferService transferService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.transferService = transferService;
    }

    public static void main(String[] args) throws UserServiceException {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL),
                new AccountService(API_BASE_URL), new TransferService(API_BASE_URL));
        app.run();
    }

    public void run() throws UserServiceException {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() throws UserServiceException {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() throws UserServiceException {
        Account account = accountService.retrieveAccountByUserId(currentUser.getUser().getId());
        console.printCurrentBalance(account.getBalance());
    }

    private void viewTransferHistory() {

        try {
            Transfer[] transfers = transferService.retrieveTransferList(currentUser.getUser().getId());
            int transferId = console.displayTransferList(transfers, currentUser.getUser().getId());
            Transfer requestedTransfer = checkIfTransferInTransfers(transfers, transferId);
            if (transferId == 0) {

            } else if (requestedTransfer != null) {
                console.displayTransferDetails(requestedTransfer, currentUser.getUser().getId());
            } else {
                console.printErrorMessage("Invalid Transfer Id!");
            }
        } catch (TransferException e) {
            console.printErrorMessage("Invalid Attempt");
        }
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    private void sendBucks() {
        Transfer transfer = new Transfer();
        User[] users = transferService.retrieveUsers();
        console.displayListOfUsers(users, currentUser);

        int userIdToSend = console.getUserIdToSendMoney();
        if (checkIfUserInUsers(users, userIdToSend)) {
            double amountToSend = console.getAmountFromUser();
            try {
                transfer.setAmount(amountToSend);
                transfer.setUserIdTo(userIdToSend);
                transfer.setUserIdFrom(currentUser.getUser().getId());
                transferService.makeTransfer(transfer);
            } catch (TransferException e) {
                console.printErrorMessage(e.getMessage());
            }
        } else if (userIdToSend == 0) {
        } else {
            console.printErrorMessage("Invalid User Id!");
        }
    }

    private boolean checkIfUserInUsers(User[] users, int userIdToSend) {
        for (User user : users) {
            if (user.getId() == userIdToSend) {
                return true;
            }
        }
        return false;
    }

    private Transfer checkIfTransferInTransfers(Transfer[] transfers, int transferId) {
        Transfer newTransfer = null;
        for (Transfer transfer : transfers) {
            if (transfer.getTransferId() == transferId) {
                newTransfer = transfer;
            }
        }
        return newTransfer;
    }

    private void requestBucks() {
        // TODO Auto-generated method stub

    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
                accountService.setAUTH_TOKEN(currentUser.getToken());
                transferService.setAUTH_TOKEN(currentUser.getToken());
                System.out.println(currentUser.getToken());
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
