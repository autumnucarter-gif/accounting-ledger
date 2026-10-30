package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {
    private LocalDate date;
    private LocalTime time;
    private String depositType;
    private String companyName;
    private double price;

    public Transaction(LocalDate date, LocalTime time, String depositType, String companyName, double price) {
        this.date = date;
        this.time = time;
        this.depositType = depositType;
        this.companyName = companyName;
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getDepositType() {
        return depositType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", time=" + time +
                ", depositType='" + depositType + '\'' +
                ", companyName='" + companyName + '\'' +
                ", price=" + price +
                '}';
    }
}
