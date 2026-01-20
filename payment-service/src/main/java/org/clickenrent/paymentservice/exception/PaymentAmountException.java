package org.clickenrent.paymentservice.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when payment amount doesn't meet method requirements
 */
public class PaymentAmountException extends RuntimeException {
    
    private final BigDecimal amount;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final String paymentMethod;
    
    public PaymentAmountException(BigDecimal amount, BigDecimal minAmount, String paymentMethod) {
        super(String.format("Payment amount %.2f is below minimum %.2f for %s", 
            amount, minAmount, paymentMethod));
        this.amount = amount;
        this.minAmount = minAmount;
        this.maxAmount = null;
        this.paymentMethod = paymentMethod;
    }
    
    public PaymentAmountException(BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount, String paymentMethod) {
        super(String.format("Payment amount %.2f must be between %.2f and %.2f for %s", 
            amount, minAmount, maxAmount, paymentMethod));
        this.amount = amount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.paymentMethod = paymentMethod;
    }
    
    public PaymentAmountException(String message) {
        super(message);
        this.amount = null;
        this.minAmount = null;
        this.maxAmount = null;
        this.paymentMethod = "unknown";
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public BigDecimal getMinAmount() {
        return minAmount;
    }
    
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
}
