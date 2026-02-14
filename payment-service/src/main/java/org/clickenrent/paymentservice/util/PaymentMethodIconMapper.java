package org.clickenrent.paymentservice.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for mapping payment method codes to icon URLs
 * Provides fallback icons for common payment methods when MultiSafePay doesn't provide them
 */
public class PaymentMethodIconMapper {

    // CDN URLs for payment method icons (you can replace these with your own CDN or local assets)
    private static final String ICON_BASE_URL = "https://cdn.multisafepay.com/img/methods/";
    
    private static final Map<String, String> ICON_MAP = new HashMap<>();

    static {
        // ========================================
        // Card Schemes
        // ========================================
        ICON_MAP.put("CREDITCARD", ICON_BASE_URL + "creditcard.svg");
        ICON_MAP.put("DEBIT_CARD", ICON_BASE_URL + "creditcard.svg");
        ICON_MAP.put("VISA", ICON_BASE_URL + "visa.svg");
        ICON_MAP.put("MASTERCARD", ICON_BASE_URL + "mastercard.svg");
        ICON_MAP.put("MAESTRO", ICON_BASE_URL + "maestro.svg");
        ICON_MAP.put("AMEX", ICON_BASE_URL + "amex.svg");
        
        // Co-branded cards
        ICON_MAP.put("DANKORT", ICON_BASE_URL + "dankort.svg");
        ICON_MAP.put("CARTEBANCAIRE", ICON_BASE_URL + "cartebleue.svg");
        ICON_MAP.put("POSTEPAY", ICON_BASE_URL + "postepay.svg");
        
        // ========================================
        // Banking Methods
        // ========================================
        ICON_MAP.put("IDEAL", ICON_BASE_URL + "ideal.svg");
        ICON_MAP.put("IDEALQR", ICON_BASE_URL + "ideal.svg");
        ICON_MAP.put("BANCONTACT", ICON_BASE_URL + "bancontact.svg");
        ICON_MAP.put("BANCONTACTQR", ICON_BASE_URL + "bancontact.svg");
        ICON_MAP.put("BELFIUS", ICON_BASE_URL + "belfius.svg");
        ICON_MAP.put("BIZUM", ICON_BASE_URL + "bizum.svg");
        ICON_MAP.put("CBC", ICON_BASE_URL + "cbc.svg");
        ICON_MAP.put("KBC", ICON_BASE_URL + "kbc.svg");
        ICON_MAP.put("DIRDEB", ICON_BASE_URL + "dirdeb.svg");
        ICON_MAP.put("DIRECTBANK", ICON_BASE_URL + "directbank.svg");
        ICON_MAP.put("DOTPAY", ICON_BASE_URL + "dotpay.svg");
        ICON_MAP.put("EPS", ICON_BASE_URL + "eps.svg");
        ICON_MAP.put("GIROPAY", ICON_BASE_URL + "giropay.svg");
        ICON_MAP.put("MBWAY", ICON_BASE_URL + "mbway.svg");
        ICON_MAP.put("MULTIBANCO", ICON_BASE_URL + "multibanco.svg");
        ICON_MAP.put("MYBANK", ICON_BASE_URL + "mybank.svg");
        ICON_MAP.put("SOFORT", ICON_BASE_URL + "sofort.svg");
        ICON_MAP.put("TRUSTLY", ICON_BASE_URL + "trustly.svg");
        ICON_MAP.put("BANKTRANS", ICON_BASE_URL + "banktransfer.svg");
        ICON_MAP.put("BANK_TRANSFER", ICON_BASE_URL + "banktransfer.svg");
        ICON_MAP.put("SEPA", ICON_BASE_URL + "sepa.svg");
        
        // ========================================
        // BNPL (Buy Now Pay Later)
        // ========================================
        ICON_MAP.put("KLARNA", ICON_BASE_URL + "klarna.svg");
        ICON_MAP.put("AFTERPAY", ICON_BASE_URL + "afterpay.svg");
        ICON_MAP.put("PAYAFTER", ICON_BASE_URL + "payafter.svg");
        ICON_MAP.put("EINVOICE", ICON_BASE_URL + "einvoice.svg");
        ICON_MAP.put("IN3", ICON_BASE_URL + "in3.svg");
        ICON_MAP.put("BILLINK", ICON_BASE_URL + "billink.svg");
        
        // ========================================
        // Prepaid Cards
        // ========================================
        ICON_MAP.put("EDENRED", ICON_BASE_URL + "edenred.svg");
        ICON_MAP.put("MONIZZE", ICON_BASE_URL + "monizze.svg");
        ICON_MAP.put("PAYSAFECARD", ICON_BASE_URL + "paysafecard.svg");
        ICON_MAP.put("SODEXO", ICON_BASE_URL + "sodexo.svg");
        
        // Gift Cards
        ICON_MAP.put("BEAUTYANDWELLNESS", ICON_BASE_URL + "beautywellness.svg");
        ICON_MAP.put("BOEKENBON", ICON_BASE_URL + "boekenbon.svg");
        ICON_MAP.put("FASHIONCHEQUE", ICON_BASE_URL + "fashioncheque.svg");
        ICON_MAP.put("FASHIONGIFTCARD", ICON_BASE_URL + "fashiongiftcard.svg");
        ICON_MAP.put("VVVGIFTCARD", ICON_BASE_URL + "vvvgiftcard.svg");
        ICON_MAP.put("WEBSHOPGIFTCARD", ICON_BASE_URL + "webshopgiftcard.svg");
        
        // ========================================
        // E-Wallets
        // ========================================
        ICON_MAP.put("PAYPAL", ICON_BASE_URL + "paypal.svg");
        ICON_MAP.put("APPLEPAY", ICON_BASE_URL + "applepay.svg");
        ICON_MAP.put("GOOGLEPAY", ICON_BASE_URL + "googlepay.svg");
        ICON_MAP.put("ALIPAY", ICON_BASE_URL + "alipay.svg");
        ICON_MAP.put("ALIPAYPLUS", ICON_BASE_URL + "alipayplus.svg");
        ICON_MAP.put("AMAZONPAY", ICON_BASE_URL + "amazonpay.svg");
        ICON_MAP.put("WECHAT", ICON_BASE_URL + "wechat.svg");
        ICON_MAP.put("DIGITAL_WALLET", ICON_BASE_URL + "wallet.svg");
        
        // ========================================
        // Other
        // ========================================
        ICON_MAP.put("CASH", ICON_BASE_URL + "cash.svg");
    }

    /**
     * Get icon URL for a payment method code
     * Returns null if no icon is found
     * 
     * @param methodCode Payment method code (e.g., "IDEAL", "PAYPAL")
     * @return Icon URL or null if not found
     */
    public static String getIconUrl(String methodCode) {
        if (methodCode == null) {
            return null;
        }
        return ICON_MAP.get(methodCode.toUpperCase());
    }

    /**
     * Get icon URL with fallback
     * Returns a default icon if specific icon is not found
     * 
     * @param methodCode Payment method code
     * @param fallbackUrl Fallback URL if method code is not found
     * @return Icon URL or fallback URL
     */
    public static String getIconUrlWithFallback(String methodCode, String fallbackUrl) {
        String iconUrl = getIconUrl(methodCode);
        return iconUrl != null ? iconUrl : fallbackUrl;
    }

    /**
     * Check if an icon exists for a payment method
     * 
     * @param methodCode Payment method code
     * @return true if icon exists, false otherwise
     */
    public static boolean hasIcon(String methodCode) {
        return methodCode != null && ICON_MAP.containsKey(methodCode.toUpperCase());
    }

    /**
     * Get default payment icon URL
     * Used as a fallback when no specific icon is available
     * 
     * @return Default icon URL
     */
    public static String getDefaultIconUrl() {
        return ICON_BASE_URL + "default.svg";
    }

    /**
     * Private constructor to prevent instantiation
     */
    private PaymentMethodIconMapper() {
        throw new UnsupportedOperationException("Utility class");
    }
}
