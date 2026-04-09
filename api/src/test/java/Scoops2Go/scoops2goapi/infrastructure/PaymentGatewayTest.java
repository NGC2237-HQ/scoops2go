package Scoops2Go.scoops2goapi.infrastructure;

import Scoops2Go.scoops2goapi.dto.OrderDTO;
import Scoops2Go.scoops2goapi.dto.PaymentDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaymentGateway Tests (Helper - No Test Case ID)")
class PaymentGatewayTest {

    @Test
    @DisplayName("StubPaymentGateway should always return successful payment")
    void testStubPaymentGateway_AlwaysReturnsSuccess() {
        // Arrange
        PaymentGateway gateway = new StubPaymentGateway();
        OrderDTO order = new OrderDTO(100L, LocalDateTime.now(),
                BigDecimal.valueOf(22.50), BigDecimal.valueOf(2.50), 30, null, List.of());

        // Act
        PaymentDTO result = gateway.processPayment(order);

        // Assert
        assertTrue(result.success());
        assertNotNull(result.transactionId());
        assertEquals("Payment successful.", result.message());
    }
}
