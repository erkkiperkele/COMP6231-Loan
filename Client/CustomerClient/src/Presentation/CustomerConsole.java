package Presentation;

import Contracts.ICustomerService;
import Data.Bank;
import Data.Customer;
import Data.Loan;
import Services.CustomerService;
import Services.SessionService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CustomerConsole {

    private static ICustomerService _customerService;
    private static Console _console;

    public static void main(String[] args) {

        //TODO: Don't forget about logging!

        //QUESTIONS:
        //- Does the client have to execute asynchronous requests?

        _customerService = new CustomerService();
        _console = new Console(System.in);

        boolean isExiting = false;

        while (!isExiting) {
            displayChoices();
            isExiting = executeChoice();
        }

    }

    private static void displayChoices() {

        String message = String.format(
                "Please chose an option:"
                        + "%1$s 1: Open an account"
                        + "%1$s 2: Get Loan"
                        + "%1$s Press any other key to exit."
                , _console.newLine());

        _console.println(message);
    }

    private static boolean executeChoice() {

        char choice = _console.readChar();
        boolean isExiting = false;

        switch (choice) {
            case '1':
                displayOpenAccount();
                break;
            case '2':
                displayGetLoan();
//                displaySignin();
                break;
            default:
                _console.println("See you!");
                isExiting = true;
                break;
        }
        return isExiting;
    }

    private static void displayGetLoan() {
        displaySignin();

        Customer customer = SessionService.getInstance().getCurrentCustomer();
        long loanAmount = askLoanAmount();

        getLoan(customer.getBank(), customer.getAccountNumber(), customer.getPassword(), loanAmount);
        SessionService.getInstance().log().info(String.format("Requested a loan of %1$s $", loanAmount));
    }

    private static void displaySignin() {
        Bank bank = askBankId();
        String email = askEmail();
        String password = askPassword();

        Customer customer = _customerService.getCustomer(bank, email, password);

        SessionService.getInstance().signIn(customer);
        SessionService.getInstance().log().info("User logged in");

        displayCurrentCustomerInfo();
    }

    private static void displayCurrentCustomerInfo() {

        Customer currentCustomer = SessionService.getInstance().getCurrentCustomer();

        _console.println("Customer logged in as: " + _console.newLine());
        _console.println(currentCustomer.getFirstName() + _console.newLine());
        _console.println(currentCustomer.getLastName() + _console.newLine());
        _console.println(currentCustomer.getBank().toString() + _console.newLine());
    }


    private static void displayOpenAccount() {

        Bank bankId = askBankId();
        String firstName = askFirstName();
        String lastName = askLastName();
        String email = askEmail();
        String phone = askPhone();
        String password = askPassword();

        SessionService.getInstance().log().info("Requesting server to open a new account:");

        int accountNumber = openAccount(bankId, firstName, lastName, email, phone, password);

        //TODO: Create ServerSide (getCustomer(accountNumber)
        Customer newCustomer = new Customer(42, accountNumber, firstName, lastName, password, bankId, email);

        SessionService.getInstance().signIn(newCustomer);
        SessionService.getInstance().log().info(String.format("Account #%d created.", accountNumber));
    }

    private static Bank askBankId() {

        _console.println("Enter bankId");
        _console.println(String.format("(1 - %1$s, 2 - %2$s, 3 %3$s): ", Bank.Royal, Bank.National, Bank.Dominion));
        int userAnswer = _console.readint();
        Bank answer = userAnswer == 0
                ? Bank.Royal
                : Bank.fromInt(userAnswer);

        displayAnswer(answer.toString());
        return answer;
    }

    private static String askFirstName() {

        _console.println("Enter firstName: ");
        String userAnswer = _console.readString();
        String answer = userAnswer.equals("")
                ? "Aymeric"
                : userAnswer;

        displayAnswer(answer);
        return answer;
    }

    private static String askLastName() {

        _console.println("Enter lastName: ");
        String userAnswer = _console.readString();
        String answer = userAnswer.equals("")
                ? "Grail"
                : userAnswer;

        displayAnswer(answer);
        return answer;
    }

    private static String askEmail() {
        _console.println("Enter email: ");
        String userAnswer = _console.readString();
        String answer = userAnswer.equals("")
                ? "Aymeric.Grail@gmail.com"
                : userAnswer;

        displayAnswer(answer);
        return answer;
    }

    private static String askPhone() {
        _console.println("Enter phone: ");
        String userAnswer = _console.readString();
        String answer = userAnswer.equals("")
                ? "514.660.2812"
                : userAnswer;

        displayAnswer(answer);
        return answer;
    }

    private static String askPassword() {
        _console.println("Enter password: ");
        String userAnswer = _console.readString();
        String answer = userAnswer.equals("")
                ? "zaza"
                : userAnswer;

        displayAnswer(answer);
        return answer;
    }

    private static long askLoanAmount() {
        _console.println("Enter required amount for the loan: ");
        String userAnswer = _console.readString();
        long answer = userAnswer.equals("")
                ? 100
                : Long.getLong(userAnswer);

        displayAnswer(String.valueOf(answer));
        return answer;
    }

    private static int openAccount(
            Bank bankId,
            String firstName,
            String lastName,
            String email,
            String phone,
            String password) {

        int accountNumber = _customerService.openAccount(bankId, firstName, lastName, email, phone, password);
        return accountNumber;
    }

    private static Loan getLoan(
            Bank bankId,
            int accountNumber,
            String password,
            long loanAmount) {
        return _customerService.getLoan(bankId, accountNumber, password, loanAmount);
    }

    private static void displayAnswer(String answer) {
        _console.println("Value Entered: " + answer + _console.newLine());
    }
}
