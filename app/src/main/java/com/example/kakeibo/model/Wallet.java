package com.example.kakeibo.model;

import java.util.Date;
import java.util.regex.Pattern;

public class Wallet {
    private String walletName, savingFor, startDate, endDate;
    private Double income, fixedExpenses, spendingCash;
    private Integer savingsGoal;


    public Wallet() {

    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getSavingFor() {
        return savingFor;
    }

    public void setSavingFor(String savingFor) {
        this.savingFor = savingFor;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getFixedExpenses() {
        return fixedExpenses;
    }

    public void setFixedExpenses(Double fixedExpenses) {
        this.fixedExpenses = fixedExpenses;
    }

    public Double getSpendingCash() {
        return spendingCash;
    }

    public void setSpendingCash(Double spendingCash) {
        this.spendingCash = spendingCash;
    }

    public Integer getSavingsGoal() {
        return savingsGoal;
    }

    public void setSavingsGoal(Integer savingsGoal) {
        this.savingsGoal = savingsGoal;
    }

    public static boolean isValidAmount(String amount) {
        {
            String regex = "[0-9]+([,.][0-9]{1,2})?";
            Pattern pattern = Pattern.compile(regex);
            if (amount == null)
                return false;
            return pattern.matcher(amount).matches();
        }
    }

        public static boolean isValidSavingsGoal(String amountIncome, String amountExpenses, String
        amountSavings) {
            {
                double amountIncomeDouble = Double.parseDouble(amountIncome);
                double amountExpenseDouble = Double.parseDouble(amountExpenses);
                double amountSavingsDouble = Integer.parseInt(amountSavings);
                double amountSpending = amountIncomeDouble - amountExpenseDouble - amountSavingsDouble;
                if (amountSpending < 0 ) {
                    return false;
                }
                else
                return true;
            }
        }
    }

