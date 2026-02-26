package org.clickenrent.paymentservice.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.clickenrent.paymentservice.dto.mobile.MobileBankDTO;
import org.clickenrent.paymentservice.dto.mobile.MobilePaymentMethodDTO;
import org.clickenrent.paymentservice.exception.MultiSafepayIntegrationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MobilePaymentServiceTest {

    @Mock
    private MultiSafepayService multiSafepayService;

    @InjectMocks
    private MobilePaymentService mobilePaymentService;

    @Test
    void getAvailablePaymentMethods_WhenApiReturnsMethods_ReturnsTransformedList() {
        JsonObject pm = new JsonObject();
        pm.addProperty("id", "IDEAL");
        pm.addProperty("name", "iDEAL");
        JsonArray data = new JsonArray();
        data.add(pm);
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", data);

        when(multiSafepayService.listPaymentMethods()).thenReturn(response);

        List<MobilePaymentMethodDTO> result = mobilePaymentService.getAvailablePaymentMethods();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("IDEAL", result.get(0).getCode());
        assertEquals("iDEAL", result.get(0).getName());
    }

    @Test
    void getAvailablePaymentMethods_WhenApiReturnsEmptyData_ReturnsEmptyList() {
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", new JsonArray());

        when(multiSafepayService.listPaymentMethods()).thenReturn(response);

        List<MobilePaymentMethodDTO> result = mobilePaymentService.getAvailablePaymentMethods();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAvailablePaymentMethods_WhenApiThrows_ThrowsMultiSafepayIntegrationException() {
        when(multiSafepayService.listPaymentMethods()).thenThrow(new RuntimeException("Network error"));

        assertThrows(MultiSafepayIntegrationException.class, () ->
                mobilePaymentService.getAvailablePaymentMethods());
    }

    @Test
    void getIdealBanks_WhenApiReturnsIssuers_ReturnsTransformedList() {
        JsonObject issuer = new JsonObject();
        issuer.addProperty("code", "3151");
        issuer.addProperty("name", "ABN AMRO");
        JsonArray data = new JsonArray();
        data.add(issuer);
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", data);

        when(multiSafepayService.getIdealIssuers()).thenReturn(response);

        List<MobileBankDTO> result = mobilePaymentService.getIdealBanks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("3151", result.get(0).getIssuerId());
        assertEquals("ABN AMRO", result.get(0).getName());
    }

    @Test
    void getIdealBanks_WhenApiReturnsEmptyData_ReturnsEmptyList() {
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", new JsonArray());

        when(multiSafepayService.getIdealIssuers()).thenReturn(response);

        List<MobileBankDTO> result = mobilePaymentService.getIdealBanks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getIdealBanks_WhenApiThrows_ThrowsMultiSafepayIntegrationException() {
        when(multiSafepayService.getIdealIssuers()).thenThrow(new RuntimeException("API error"));

        assertThrows(MultiSafepayIntegrationException.class, () ->
                mobilePaymentService.getIdealBanks());
    }
}
