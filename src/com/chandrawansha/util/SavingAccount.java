package com.chandrawansha.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.Locale;

import static com.chandrawansha.util.SavingAccountEngine.ACCESS_LEVEL_SECURITY_CODE;

public class SavingAccount extends Account{

    private BigDecimal initialBalance;
    private Date startedDate;
    private int pin;

    public SavingAccount(int accId, Person person, double balance, double initialBalance, Date startedDate, int pin){
        super(accId, person, balance);
        this.initialBalance = new BigDecimal(initialBalance)
                .setScale(2, RoundingMode.UP);
        this.startedDate = startedDate;
        this.pin = pin;
    }

    public SavingAccount(int accId, Person person, BigDecimal balance, BigDecimal initialBalance, Date startedDate, int pin){
        super(accId, person, balance);
        this.initialBalance = balance.setScale(2, RoundingMode.UP);
        this.startedDate = startedDate;
        this.pin = pin;
    }

    public SavingAccount(int accId, int personId, double balance, double initialBalance, Date startedDate, int pin){
        super(accId, personId, balance);
        this.initialBalance = new BigDecimal(initialBalance).setScale(2, BigDecimal.ROUND_UP);
        this.startedDate = startedDate;
        this.pin = pin;
    }

    // getter for fields

    public BigDecimal getInitialBalance() {
        if (accessLevel) {
            return initialBalance;
        }
        throw new SecurityException("Access Granted");
    }

    public Date getStartedDate() {
        if (accessLevel) {
            return startedDate;
        }
        throw new SecurityException("Access Granted");
    }

    // add method
    public void credit(BigDecimal amount){
        currentBalance = currentBalance.add(amount);
    }

    public void debit(BigDecimal amount){
        if (amount.compareTo(currentBalance) > 0)
            throw new IllegalArgumentException(String.format("Cannot debit %s from savings account %d", NumberFormat.getInstance(Locale.US).format(amount.doubleValue()), getAccountId()));
        currentBalance = currentBalance.subtract(amount);
    }

    // method for get the current balance in standard representation
    public String getCurrentBalanceInUS(){
        if (accessLevel) {
            return NumberFormat.getCurrencyInstance(Locale.US).format(currentBalance.doubleValue());
        }
        throw new SecurityException("Access Granted");
    }

    public String getCurrentBalance(Locale locale){
        if (accessLevel) {
            return NumberFormat.getCurrencyInstance(locale).format(currentBalance.doubleValue());
        }
        throw new SecurityException("Access Granted");
    }

    // for initial balance
    public String getinitialBalanceInUs(){
        if (accessLevel) {
            return NumberFormat.getCurrencyInstance(Locale.US).format(initialBalance.doubleValue());
        }
        throw new SecurityException("Access Granted");
    }

    public String getInitialBalance(Locale locale){
        if (accessLevel) {
            return NumberFormat.getCurrencyInstance(locale).format(initialBalance.doubleValue());
        }
        throw new SecurityException("Access Granted");
    }

    // getter for pin
    public int getPin(){
        if (accessLevel){
            return pin;
        }
        throw new SecurityException("Access Denied");
    }

    @Override
    public String toString() {
        if (accessLevel) {
            return String.format("Account ID : %s%nPerson ID : %s%nCurrent Balance : %s%nStarted Date : %s%n",
                    getAccountId(), getPersonId(),
                    NumberFormat.getCurrencyInstance(Locale.US).format(currentBalance.doubleValue()),
                    startedDate);
        } else {
            return String.format("Account ID : %s%nPerson ID : %s%n",
                    getAccountId(), getPersonId());
        }
    }

    // set the access level for account object
    public void setAccessLevel(int code, boolean state){
        if (code == ACCESS_LEVEL_SECURITY_CODE)
            accessLevel = state;
        else
            throw new SecurityException("Unauthrized access");
    }
}
