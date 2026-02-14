package org.clickenrent.paymentservice.client.multisafepay.model;

/**
 * Payout model for MultiSafepay Payout API
 * Used to send money from merchant account to bank accounts
 */
public class Payout {
    
    public String payout_id;
    public String currency;
    public Integer amount; // Amount in cents
    public BankAccount bank_account;
    public String description;
    public String reference;
    public String status;
    public String created;
    public String modified;
    
    public Payout() {
    }
    
    public Payout(String currency, Integer amount, BankAccount bankAccount, String description, String reference) {
        this.currency = currency;
        this.amount = amount;
        this.bank_account = bankAccount;
        this.description = description;
        this.reference = reference;
    }
}
