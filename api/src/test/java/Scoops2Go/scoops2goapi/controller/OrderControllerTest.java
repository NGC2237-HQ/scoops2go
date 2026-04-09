package Scoops2Go.scoops2goapi.controller;

import Scoops2Go.scoops2goapi.dto.OrderDTO;
import Scoops2Go.scoops2goapi.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderController Tests")
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    @DisplayName("TC_API_003: GET /api/order/{id} with valid ID should return order")
    void testGetOrder_ValidId_ShouldReturnOrder() {
        // Arrange
        long orderId = 100L;
        OrderDTO mockOrder = new OrderDTO(orderId, LocalDateTime.now(),
                BigDecimal.valueOf(22.50), BigDecimal.valueOf(2.50), 30, null, List.of());
        when(orderService.getOrderById(orderId)).thenReturn(mockOrder);

        // Act
        ResponseEntity<OrderDTO> response = orderController.getOrder(orderId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderId, response.getBody().orderId());
        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    @DisplayName("TC_API_004: POST /api/order with valid payload should return 201 Created")
    void testCreateOrder_ValidOrder_ShouldReturnCreated() {
        // Arrange
        OrderDTO incoming = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, List.of());
        OrderDTO created = new OrderDTO(100L, LocalDateTime.now(),
                BigDecimal.valueOf(22.50), BigDecimal.valueOf(2.50), 30, null, List.of());
        when(orderService.createOrder(incoming)).thenReturn(created);

        // Act
        ResponseEntity<OrderDTO> response = orderController.createOrder(incoming);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(100L, response.getBody().orderId());
    }

    @Test
    @DisplayName("DELETE /api/order/{id} should return 204 No Content")
    void testDeleteOrder_ValidId_ShouldReturnNoContent() {
        // Arrange
        long orderId = 100L;
        doNothing().when(orderService).deleteOrder(orderId);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(orderId);
    }
}
