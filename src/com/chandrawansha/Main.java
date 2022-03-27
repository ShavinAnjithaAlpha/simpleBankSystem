package com.chandrawansha;

import com.chandrawansha.util.*;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    // create the new person
    final static Scanner scanner = new Scanner(System.in);
    // create the new person using person engine object
    final static PersonEngine personEngine = new PersonEngine();
    // create the saving account engine instance
    final static SavingAccountEngine savingAccountEngine = new SavingAccountEngine();

    public static void main(String[] args) {

        int isContinue = 1;
        int option;

        while (isContinue == 1){
            System.out.printf("Choose Option :%n?ADD PERSON - 1%n?DISPLAY PERSON -2%n?CREATE ACC - 3%n?DEPOSITE - 4%nDISPLAY ACC - 5%nPAYMENTS - 6%n??");
            option = scanner.nextInt();

            switch (option){
                case 1:{
                    createperson();
                    break;
                }
                case 2:{
                    displayPerson();
                    break;
                }
                case 3:{
                    createSavingAccount();
                    break;
                }
                case 4:{
                    deposite();
                    break;
                }
                case 5:{
                    displayAccount();
                    break;
                }
                case 6:{
                    payments();
                    break;
                }
                default:{
                    System.out.println("Choose right option...");
                    break;
                }
            }
            System.out.printf("Do you want to continue(1/0) ?");
            isContinue = scanner.nextInt();

        }
    }

    public static void createperson(){

        scanner.nextLine();
        System.out.printf("?First Name : ");
        String firstName = scanner.nextLine();
        System.out.printf("?Last Name : ");
        String lastName = scanner.nextLine();
        System.out.printf("?Address : ");
        String address = scanner.nextLine();
        System.out.printf("?Birth Day : ");
        String birthDay = scanner.nextLine();
        System.out.printf("?Phone Number : ");
        String phoneNumber = scanner.nextLine();
        System.out.printf("?Email : ");
        String email = scanner.nextLine();
        System.out.printf("?NIC Number : ");
        String idNumber = scanner.nextLine();

        // create the new person
        Person person = personEngine.createPerson(firstName, lastName, address, birthDay,
                phoneNumber, email, idNumber);
        // print hte person
        System.out.println("[INFO] successfully create person.");
        System.out.println("----------------");
        System.out.println(person);
        System.out.println("----------------");

    }

    public static void displayPerson(){
        System.out.printf("Enter person id(-1 for searching) : ");
        int i = scanner.nextInt();
        if (i == -1){
            scanner.nextLine();
            System.out.printf("Enter search name : ");
            String name = scanner.nextLine();
            // searching
            List<Person> list = personEngine.searchPersons(name);
            int j = 1;
            System.out.println("Search results :");
            for (Person person : list) {
                System.out.println("Search results No." + j);
                System.out.println("------------");
                System.out.println(person);
                System.out.println("------------");
                j++;
            }
            return;
        }
        else {
            // get the person object from the person engine
            Person person = personEngine.getPerson(i);
            if (person == null){
                System.out.println("[WARNING] person id is not valid...");
            }
            else{
                System.out.println("------------");
                System.out.println(person);
                System.out.println("------------");
            }
        }
    }

    public static void createSavingAccount(){
        System.out.printf("enter person id : ");
        int personId = scanner.nextInt();
        System.out.printf("Enter deposite amount : $");
        double amount = scanner.nextDouble();

        SavingAccount account = savingAccountEngine.createAccount(personEngine.getPerson(personId), amount);
        // show the saving account information
        System.out.println("----------------");
        System.out.println(account);
        System.out.printf("Account PIN : %d%n", account.getPin());
        System.out.println("----------------");
    }

    public static void displayAccount(){
        System.out.printf("Enter account Id : ");
        int accId = scanner.nextInt();
        System.out.printf("Enter Pin : ");
        int pin = scanner.nextInt();

        SavingAccount account = savingAccountEngine.getAccount(accId, pin);
        if (account == null){
            System.out.println("[WARNING] account id or pin is invalid...");
        }
        else{
            System.out.println("-----------");
            System.out.println(account);
            System.out.println("-----------");
        }
    }

    public static void deposite(){
        System.out.printf("Enter the Account Id : ");
        int accId = scanner.nextInt();
        System.out.printf("Amount : $");
        double amount = scanner.nextDouble();

        SavingAccount account = savingAccountEngine.getAccount(accId);
        if (account != null){
            savingAccountEngine.deposite(account, amount);
            System.out.printf("Deposite %s to Account owner with %S",
                    NumberFormat.getCurrencyInstance().format(amount),
                    personEngine.getPerson(account.getPersonId()).getFirstName());

            try{
                System.out.println(account.getCurrentBalanceInUS());
            }
            catch (SecurityException ex){
                System.out.println(ex.getMessage());
            }
        }
        else {
            System.out.println("?wrong account number...try again...");
        }


    }

    public static void payments(){
        System.out.printf("Enter account Id : ");
        int accId = scanner.nextInt();
        System.out.printf("Enter Pin : ");
        int pin = scanner.nextInt();
        System.out.printf("Enter the amount : $");
        double amount = scanner.nextDouble();
        System.out.printf("Enter Comment : ");
        String comment = scanner.nextLine();
        scanner.nextLine();

        SavingAccount account = savingAccountEngine.getAccount(accId, pin);
        savingAccountEngine.payments(account, amount, comment);
        System.out.println("[INFO] successful payment proceed.");

    }

}
