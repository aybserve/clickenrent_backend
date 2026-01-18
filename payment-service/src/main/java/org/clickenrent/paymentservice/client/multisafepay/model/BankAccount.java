package org.clickenrent.paymentservice.client.multisafepay.model;

/**
 * Bank account information for MultiSafepay Payout API
 */
public class BankAccount {
    
    public String account_holder_name;
    public String iban;
    public String bic;
    
    public BankAccount() {
    }
    
    public BankAccount(String accountHolderName, String iban, String bic) {
        this.account_holder_name = accountHolderName;
        this.iban = iban;
        this.bic = bic;
    }
}
