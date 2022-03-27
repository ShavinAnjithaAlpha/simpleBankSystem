package com.chandrawansha.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;

public class Account {

    private int accountId;
    private int personId;
    private Person accountOwner;
    protected BigDecimal currentBalance;

    protected boolean accessLevel;

    public Account(int accountId, Person person, Double balance){
        this.accountId = accountId;
        personId = person.getPersonId();
        this.accountOwner = person;
        this.currentBalance = new BigDecimal(balance).setScale(2, RoundingMode.UP);
        accessLevel = true;
    }

    public Account(int accountId, int personId, Double balance){
        this.accountId = accountId;
        this.personId = personId;
        this.currentBalance = new BigDecimal(balance).setScale(2, RoundingMode.UP);
        accessLevel = true;
    }

    public Account(int accountId, Person person, BigDecimal balance){
        this.accountId = accountId;
        this.accountOwner = person;
        this.currentBalance = balance.setScale(2, RoundingMode.UP);
        accessLevel = true;
    }

    // setter and getter
    public boolean getAccessLevel(){
        return accessLevel;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public Person getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(Person accountOwner) {
        this.accountOwner = accountOwner;
    }

    public BigDecimal getCurrentBalance() {
        if (accessLevel) {
            return currentBalance;
        }
        throw new SecurityException("Access Granted");
    }

    public Double getCurrentBalanceAsDouble(){
        if (accessLevel) {
            return currentBalance.doubleValue();
        }
        throw new SecurityException("Access Granted");
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void setCurrentBalance(Double currentBalance){
        if (currentBalance < 0)
            throw new IllegalArgumentException("Balance cannot be negative value.");
        this.currentBalance = new BigDecimal(currentBalance);
    }

    public void setAccessLevel(int code, boolean accessLevel) {
        if (code  == SavingAccountEngine.ACCESS_LEVEL_SECURITY_CODE)
            this.accessLevel = accessLevel;
    }
}
