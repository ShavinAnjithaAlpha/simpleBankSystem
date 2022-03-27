package com.chandrawansha.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class SavingAccountEngine {

    private Connection connection;
    private Statement statement;
    private PreparedStatement createAccountStatement;
    private PreparedStatement updateStatement;
    private PreparedStatement getAccountStatement;
    private PreparedStatement depositeRecordStatement;

    private PreparedStatement paymentRecordStatement;
    private PreparedStatement paymentStatement;
    private PreparedStatement transactionRecordStatement;

    // secure random object for get the random pin
    private static final SecureRandom secureRandom = new SecureRandom();
    static final int ACCESS_LEVEL_SECURITY_CODE = 4563254;

    private static final double MIN_INITIAL_BALANCE = 10.0;

    public SavingAccountEngine(){
        String url = "jdbc:mysql://localhost:3306/bank_system";
        String user = "root";
        String password = "Sha2002@vin";

        // create the connection
        try {
            connection = DriverManager.getConnection(url, user, password);
            // create the stateent
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            initializeStatements();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void initializeStatements() throws SQLException {
        createAccountStatement =
                connection.prepareStatement(
                        "INSERT INTO saving_accounts(person_id, initial_balance, balance, started_date, pin)"
                                + " VALUES (?, ? ,?, ?, ?)"
                );

        updateStatement = connection.prepareStatement(
                "UPDATE saving_accounts SET balance = ? WHERE account_id = ?"
        );

        getAccountStatement = connection.prepareStatement(
                "SELECT person_id, initial_balance, balance, started_date, pin, status"
                        + " FROM saving_accounts WHERE account_id = ?"
        );

        depositeRecordStatement = connection.prepareStatement(
                "INSERT INTO deposite_records(account_id , amount, date, time)"
                        + " VALUES (?, ?, ?, ?)"
        );

        paymentStatement = connection.prepareStatement(
                "UPDATE saving_accounts SET balance = ? WHERE account_id = ?"
        );

        paymentRecordStatement = connection.prepareStatement(
                "INSERT INTO payment_records(account_id, amount, comment, date, time)"
                        + " VALUES (?, ?, ?, ?, ?)"
        );

        transactionRecordStatement = connection.prepareStatement(
                "INSERT INTO transaction_records(from_id, to_id, amount, date, time)"
                        + " VALUES (?, ?, ?, ?, ?)"
        );
    }

    public SavingAccount getAccount(int accId, int pin){
        try{
            // get the account detail from the sql database
            getAccountStatement.setInt(1, accId);
            ResultSet resultSet = getAccountStatement.executeQuery();

            if (resultSet.next()){
                // get the data from result set
                int accPin = resultSet.getInt(5);
                if (pin == accPin){
                    SavingAccount savingAccount = new SavingAccount(
                            accId,
                            resultSet.getInt(1),
                            resultSet.getDouble(3),
                            resultSet.getDouble(2),
                            resultSet.getDate(4),
                            resultSet.getInt(5)
                    );

                    return savingAccount;
                }
                else{
                    System.out.println("pin does not match...");
                    return null;
                }
            }
            else{
                System.out.println("cannot find account...");
                return null;
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        System.out.println("access denied...");
        return null;
    }

    public SavingAccount getAccount(int accId){
        try{
            // get the account detail from the sql database
            getAccountStatement.setInt(1, accId);
            ResultSet resultSet = getAccountStatement.executeQuery();

            if (resultSet.next()){
                SavingAccount savingAccount = new SavingAccount(
                        accId,
                        resultSet.getInt(1),
                        resultSet.getDouble(2),
                        resultSet.getDouble(3),
                        resultSet.getDate(4),
                        resultSet.getInt(5)
                );
                savingAccount.setAccessLevel(ACCESS_LEVEL_SECURITY_CODE, false);

                return savingAccount;
            }
            else{
                System.out.println("cannot find account...");
                return null;
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        System.out.println("access denied...");
        return null;
    }

    public SavingAccount createAccount(Person person, double initialBalance){
        // try to create the saving account to this person
        if (initialBalance < MIN_INITIAL_BALANCE)
            throw new IllegalArgumentException(String.format(
                    "Initial Balance must greater than the %s",
                    NumberFormat.getInstance(Locale.US).format(initialBalance)));
        try{
            // get the pin first
            int pin = secureRandom.nextInt(10000);
            // create the new account in the account table
            createAccountStatement.setInt(1, person.getPersonId());
            createAccountStatement.setDouble(2, initialBalance);
            createAccountStatement.setDouble(3, initialBalance);
            createAccountStatement.setDate(4, new Date(System.currentTimeMillis()));
            createAccountStatement.setInt(5, pin);
            // execute the statement
            createAccountStatement.executeUpdate();
            // get the account id from the sql query
            ResultSet resultSet = statement.executeQuery("SELECT account_id FROM saving_accounts");
            resultSet.last();
            int accId = resultSet.getInt(1);

            System.out.println(String.format("successfully create saving account for %s", person.getFirstName()));
            // create the new Saving Account Instance
            return new SavingAccount(
                    accId,
                    person,
                    initialBalance,
                    initialBalance,
                    new Date(System.currentTimeMillis()),
                    pin
            );

        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    // method for deposite the money to account
    public boolean deposite(SavingAccount savingAccount, double amount){
        // first set the account access level
        boolean state = savingAccount.getAccessLevel();
        savingAccount.setAccessLevel(ACCESS_LEVEL_SECURITY_CODE, true);
        try{
            // first update the saving account
            // get the current balance
            savingAccount.credit(new BigDecimal(amount).setScale(2, RoundingMode.UP));
            // get the new balance
            double newBalance = savingAccount.getCurrentBalance().doubleValue();
            updateStatement.setDouble(1, newBalance);
            updateStatement.setInt(2,savingAccount.getAccountId());
            // execute the statement
            updateStatement.executeUpdate();

            // insert record to deposites record table
            depositeRecordStatement.setInt(1, savingAccount.getAccountId());
            depositeRecordStatement.setDouble(2, savingAccount.getCurrentBalanceAsDouble());
            depositeRecordStatement.setDate(3, new Date(System.currentTimeMillis()));
            depositeRecordStatement.setTime(4, new Time(System.currentTimeMillis()));

            // execute the query
            depositeRecordStatement.execute();

            // set the access level of the to normal
            savingAccount.setAccessLevel(ACCESS_LEVEL_SECURITY_CODE, state);
            return true;

        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public boolean payments(SavingAccount savingAccount, double amount, String comment){

        boolean state = savingAccount.getAccessLevel();
        savingAccount.setAccessLevel(ACCESS_LEVEL_SECURITY_CODE, true);
        try{
            savingAccount.debit(
                    new BigDecimal(amount).setScale(2, RoundingMode.UP)
            );
            paymentStatement.setDouble(1, savingAccount.getCurrentBalanceAsDouble());
            paymentStatement.setInt(2, savingAccount.getAccountId());
            // execute the statement
            paymentStatement.execute();

            paymentRecordStatement.setInt(1, savingAccount.getAccountId());
            paymentRecordStatement.setDouble(2, amount);
            paymentRecordStatement.setString(3, comment);
            paymentRecordStatement.setDate(4, new Date(System.currentTimeMillis()));
            paymentRecordStatement.setTime(5, new Time(System.currentTimeMillis()));
            // execute the statement
            paymentRecordStatement.executeUpdate();
            System.out.println("[INFO] payment successful...");
            savingAccount.setAccessLevel(ACCESS_LEVEL_SECURITY_CODE, state);
            return true;

        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    // method for transaction between the two local saving account
    public boolean transaction(SavingAccount from, SavingAccount to, double amount){
        // set the saving account object access level
        boolean fromState = from.getAccessLevel();
        boolean toState = to.getAccessLevel();
        // set the level to true
        from.setAccessLevel(ACCESS_LEVEL_SECURITY_CODE, true);
        to.setAccessLevel(ACCESS_LEVEL_SECURITY_CODE, true);
        try{
            if (from.getCurrentBalanceAsDouble() >= amount){
                from.setCurrentBalance(from.getCurrentBalance()
                        .subtract(new BigDecimal(amount).setScale(2, RoundingMode.UP)));
                to.setCurrentBalance(to.getCurrentBalance()
                        .add(new BigDecimal(amount).setScale(2, RoundingMode.UP)));
                // update the database tables
                updateStatement.setDouble(1, from.getCurrentBalanceAsDouble());
                updateStatement.setInt(2, from.getAccountId());
                updateStatement.execute();

                updateStatement.setDouble(1, to.getCurrentBalanceAsDouble());
                updateStatement.setInt(2, to.getAccountId());
                updateStatement.execute();

                transactionRecordStatement.setInt(1, from.getAccountId());
                transactionRecordStatement.setInt(2, to.getAccountId());
                transactionRecordStatement.setDouble(3, amount);
                transactionRecordStatement.setDate(4, new Date(System.currentTimeMillis()));
                transactionRecordStatement.setTime(5, new Time(System.currentTimeMillis()));
                // execute the transaction record query
                transactionRecordStatement.executeUpdate();
                System.out.println(String.format("[INFO] transaction successful from Acc %d to Acc %d"
                        , from.getAccountId(), to.getAccountId()));
                return true;


            }
            else {
                throw new IllegalStateException("Cannot Proceed transaction");
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

        return false;
    }

    public void close(){
        try{
            connection.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
            System.exit(1);

        }
    }
}
