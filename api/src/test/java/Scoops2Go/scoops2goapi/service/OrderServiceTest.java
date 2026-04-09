package Scoops2Go.scoops2goapi.service;

import Scoops2Go.scoops2goapi.dto.CheckoutDTO;
import Scoops2Go.scoops2goapi.dto.OrderDTO;
import Scoops2Go.scoops2goapi.dto.PaymentDTO;
import Scoops2Go.scoops2goapi.dto.ProductDTO;
import Scoops2Go.scoops2goapi.dto.TreatDTO;
import Scoops2Go.scoops2goapi.exception.InvalidBasketException;
import Scoops2Go.scoops2goapi.exception.InvalidPromotionException;
import Scoops2Go.scoops2goapi.exception.InvalidTreatException;
import Scoops2Go.scoops2goapi.exception.ResourceNotFoundException;
import Scoops2Go.scoops2goapi.infrastructure.OrderRepository;
import Scoops2Go.scoops2goapi.infrastructure.PaymentGateway;
import Scoops2Go.scoops2goapi.infrastructure.ProductRepository;
import Scoops2Go.scoops2goapi.model.Order;
import Scoops2Go.scoops2goapi.model.Product;
import Scoops2Go.scoops2goapi.model.ProductType;
import Scoops2Go.scoops2goapi.model.Treat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests - Scoops2Go")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private OrderService orderService;

    private Product testCone;
    private Product testFlavour;
    private Product testTopping;
    private Product testFlavour2;
    private Product testFlavour3;
    private Product testFlavour4;

    @BeforeEach
    void setUp() {
        testCone = new Product("Waffle Cone", BigDecimal.valueOf(2.00),
                "Classic crispy waffle cone", List.of("flour", "sugar"), ProductType.CONE);

        testFlavour = new Product("Vanilla", BigDecimal.valueOf(1.00),
                "Smooth and creamy vanilla", List.of("milk", "cream", "sugar"), ProductType.FLAVOR);

        testTopping = new Product("Sprinkles", BigDecimal.valueOf(0.50),
                "Colorful candy sprinkles", List.of("sugar", "food coloring"), ProductType.TOPPING);

        testFlavour2 = new Product("Chocolate", BigDecimal.valueOf(1.00),
                "Rich chocolate", List.of("milk", "cocoa"), ProductType.FLAVOR);

        testFlavour3 = new Product("Strawberry", BigDecimal.valueOf(1.00),
                "Sweet strawberry", List.of("strawberry puree"), ProductType.FLAVOR);

        testFlavour4 = new Product("Mint", BigDecimal.valueOf(1.20),
                "Refreshing mint", List.of("mint extract"), ProductType.FLAVOR);
    }

    // ==================== Helper Methods ====================
    private ProductDTO createProductDTO(Long id, Product product) {
        return new ProductDTO(
                id,
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getIngredients(),
                product.getProductType().name()
        );
    }

    // ==================== TC_TC: Treat Creation Tests ====================
    @Nested
    @DisplayName("Treat Creation Tests (TC_TC)")
    class TreatCreationTests {

        @Test
        @DisplayName("TC_TC_001: 1 cone + 1 flavour + 0 toppings should create order")
        void testCreateOrder_OneConeOneFlavour_ShouldCreateOrder() {
            // Arrange
            ProductDTO coneDTO = createProductDTO(1L, testCone);
            ProductDTO flavourDTO = createProductDTO(4L, testFlavour);
            TreatDTO treat = new TreatDTO(List.of(coneDTO, flavourDTO));
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, List.of(treat));

            when(productRepository.findById(1L)).thenReturn(Optional.of(testCone));
            when(productRepository.findById(4L)).thenReturn(Optional.of(testFlavour));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            OrderDTO result = orderService.createOrder(incomingOrder);

            // Assert
            assertNotNull(result);
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC_TC_002: 1 cone + 3 flavours + 0 toppings should create order")
        void testCreateOrder_OneConeThreeFlavours_ShouldCreateOrder() {
            // Arrange
            ProductDTO coneDTO = createProductDTO(1L, testCone);
            ProductDTO flavour1DTO = createProductDTO(4L, testFlavour);
            ProductDTO flavour2DTO = createProductDTO(5L, testFlavour2);
            ProductDTO flavour3DTO = createProductDTO(6L, testFlavour3);

            TreatDTO treat = new TreatDTO(List.of(coneDTO, flavour1DTO, flavour2DTO, flavour3DTO));
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, List.of(treat));

            when(productRepository.findById(1L)).thenReturn(Optional.of(testCone));
            when(productRepository.findById(4L)).thenReturn(Optional.of(testFlavour));
            when(productRepository.findById(5L)).thenReturn(Optional.of(testFlavour2));
            when(productRepository.findById(6L)).thenReturn(Optional.of(testFlavour3));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            OrderDTO result = orderService.createOrder(incomingOrder);

            // Assert
            assertNotNull(result);
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC_TC_003: 1 cone + 1 flavour + 5 toppings should create order")
        void testCreateOrder_OneConeOneFlavourFiveToppings_ShouldCreateOrder() {
            // Arrange
            Product topping2 = new Product("Chocolate Chips", BigDecimal.valueOf(0.75), "", List.of(), ProductType.TOPPING);
            Product topping3 = new Product("Caramel Sauce", BigDecimal.valueOf(0.80), "", List.of(), ProductType.TOPPING);
            Product topping4 = new Product("Hot Fudge", BigDecimal.valueOf(0.85), "", List.of(), ProductType.TOPPING);
            Product topping5 = new Product("Whipped Cream", BigDecimal.valueOf(0.60), "", List.of(), ProductType.TOPPING);

            ProductDTO coneDTO = createProductDTO(1L, testCone);
            ProductDTO flavourDTO = createProductDTO(4L, testFlavour);
            ProductDTO topping1DTO = createProductDTO(10L, testTopping);
            ProductDTO topping2DTO = createProductDTO(11L, topping2);
            ProductDTO topping3DTO = createProductDTO(12L, topping3);
            ProductDTO topping4DTO = createProductDTO(13L, topping4);
            ProductDTO topping5DTO = createProductDTO(14L, topping5);

            TreatDTO treat = new TreatDTO(List.of(coneDTO, flavourDTO, topping1DTO, topping2DTO,
                    topping3DTO, topping4DTO, topping5DTO));
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, List.of(treat));

            when(productRepository.findById(1L)).thenReturn(Optional.of(testCone));
            when(productRepository.findById(4L)).thenReturn(Optional.of(testFlavour));
            when(productRepository.findById(10L)).thenReturn(Optional.of(testTopping));
            when(productRepository.findById(11L)).thenReturn(Optional.of(topping2));
            when(productRepository.findById(12L)).thenReturn(Optional.of(topping3));
            when(productRepository.findById(13L)).thenReturn(Optional.of(topping4));
            when(productRepository.findById(14L)).thenReturn(Optional.of(topping5));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            OrderDTO result = orderService.createOrder(incomingOrder);

            // Assert
            assertNotNull(result);
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("TC_TC_004: 0 cones should throw InvalidTreatException")
        void testCreateOrder_NoCone_ShouldThrowInvalidTreatException() {
            // Arrange
            ProductDTO flavourDTO = createProductDTO(4L, testFlavour);
            TreatDTO invalidTreat = new TreatDTO(List.of(flavourDTO));
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, List.of(invalidTreat));

            when(productRepository.findById(4L)).thenReturn(Optional.of(testFlavour));

            // Act & Assert
            assertThrows(InvalidTreatException.class, () -> orderService.createOrder(incomingOrder));
        }

        @Test
        @DisplayName("TC_TC_005: 0 flavours should throw InvalidTreatException")
        void testCreateOrder_NoFlavour_ShouldThrowInvalidTreatException() {
            // Arrange
            ProductDTO coneDTO = createProductDTO(1L, testCone);
            TreatDTO invalidTreat = new TreatDTO(List.of(coneDTO));
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, List.of(invalidTreat));

            when(productRepository.findById(1L)).thenReturn(Optional.of(testCone));

            // Act & Assert
            assertThrows(InvalidTreatException.class, () -> orderService.createOrder(incomingOrder));
        }

        @Test
        @DisplayName("TC_TC_006: 4 flavours (exceeds limit) should throw InvalidTreatException")
        void testCreateOrder_FourFlavours_ShouldThrowInvalidTreatException() {
            // Arrange
            ProductDTO coneDTO = createProductDTO(1L, testCone);
            ProductDTO flavour1DTO = createProductDTO(4L, testFlavour);
            ProductDTO flavour2DTO = createProductDTO(5L, testFlavour2);
            ProductDTO flavour3DTO = createProductDTO(6L, testFlavour3);
            ProductDTO flavour4DTO = createProductDTO(7L, testFlavour4);

            TreatDTO invalidTreat = new TreatDTO(List.of(coneDTO, flavour1DTO, flavour2DTO, flavour3DTO, flavour4DTO));
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, List.of(invalidTreat));

            when(productRepository.findById(1L)).thenReturn(Optional.of(testCone));
            when(productRepository.findById(4L)).thenReturn(Optional.of(testFlavour));
            when(productRepository.findById(5L)).thenReturn(Optional.of(testFlavour2));
            when(productRepository.findById(6L)).thenReturn(Optional.of(testFlavour3));
            when(productRepository.findById(7L)).thenReturn(Optional.of(testFlavour4));

            // Act & Assert
            assertThrows(InvalidTreatException.class, () -> orderService.createOrder(incomingOrder));
        }

        @Test
        @DisplayName("TC_TC_007: 6 toppings (exceeds limit) should throw InvalidTreatException")
        void testCreateOrder_SixToppings_ShouldThrowInvalidTreatException() {
            // Arrange
            Product topping2 = new Product("Chocolate Chips", BigDecimal.valueOf(0.75), "", List.of(), ProductType.TOPPING);
            Product topping3 = new Product("Caramel Sauce", BigDecimal.valueOf(0.80), "", List.of(), ProductType.TOPPING);
            Product topping4 = new Product("Hot Fudge", BigDecimal.valueOf(0.85), "", List.of(), ProductType.TOPPING);
            Product topping5 = new Product("Whipped Cream", BigDecimal.valueOf(0.60), "", List.of(), ProductType.TOPPING);
            Product topping6 = new Product("Chopped Nuts", BigDecimal.valueOf(0.70), "", List.of(), ProductType.TOPPING);

            ProductDTO coneDTO = createProductDTO(1L, testCone);
            ProductDTO flavourDTO = createProductDTO(4L, testFlavour);
            ProductDTO topping1DTO = createProductDTO(10L, testTopping);
            ProductDTO topping2DTO = createProductDTO(11L, topping2);
            ProductDTO topping3DTO = createProductDTO(12L, topping3);
            ProductDTO topping4DTO = createProductDTO(13L, topping4);
            ProductDTO topping5DTO = createProductDTO(14L, topping5);
            ProductDTO topping6DTO = createProductDTO(15L, topping6);

            TreatDTO invalidTreat = new TreatDTO(List.of(coneDTO, flavourDTO, topping1DTO, topping2DTO,
                    topping3DTO, topping4DTO, topping5DTO, topping6DTO));
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, List.of(invalidTreat));

            when(productRepository.findById(1L)).thenReturn(Optional.of(testCone));
            when(productRepository.findById(4L)).thenReturn(Optional.of(testFlavour));
            when(productRepository.findById(10L)).thenReturn(Optional.of(testTopping));
            when(productRepository.findById(11L)).thenReturn(Optional.of(topping2));
            when(productRepository.findById(12L)).thenReturn(Optional.of(topping3));
            when(productRepository.findById(13L)).thenReturn(Optional.of(topping4));
            when(productRepository.findById(14L)).thenReturn(Optional.of(topping5));
            when(productRepository.findById(15L)).thenReturn(Optional.of(topping6));

            // Act & Assert
            assertThrows(InvalidTreatException.class, () -> orderService.createOrder(incomingOrder));
        }
    }

    // ==================== TC_BC: Basket & Checkout Tests ====================
    @Nested
    @DisplayName("Basket & Checkout Tests (TC_BC)")
    class BasketCheckoutTests {

        @Test
        @DisplayName("TC_BC_001: Adding 1 item to empty basket should create order")
        void testCreateOrder_OneItem_ShouldCreateOrderWithOneTreat() {
            // Arrange
            ProductDTO coneDTO = createProductDTO(1L, testCone);
            ProductDTO flavourDTO = createProductDTO(4L, testFlavour);
            TreatDTO treat = new TreatDTO(List.of(coneDTO, flavourDTO));
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, List.of(treat));

            when(productRepository.findById(1L)).thenReturn(Optional.of(testCone));
            when(productRepository.findById(4L)).thenReturn(Optional.of(testFlavour));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            OrderDTO result = orderService.createOrder(incomingOrder);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.basketItems().size());
        }

        @Test
        @DisplayName("TC_BC_002: Adding 9 items to basket should succeed")
        void testCreateOrder_NineItems_ShouldSucceed() {
            // Arrange
            ProductDTO coneDTO = createProductDTO(1L, testCone);
            ProductDTO flavourDTO = createProductDTO(4L, testFlavour);
            TreatDTO treat = new TreatDTO(List.of(coneDTO, flavourDTO));

            List<TreatDTO> nineTreats = List.of(treat, treat, treat, treat, treat, treat, treat, treat, treat);
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, nineTreats);

            when(productRepository.findById(1L)).thenReturn(Optional.of(testCone));
            when(productRepository.findById(4L)).thenReturn(Optional.of(testFlavour));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            OrderDTO result = orderService.createOrder(incomingOrder);

            // Assert
            assertNotNull(result);
            assertEquals(9, result.basketItems().size());
        }

        @Test
        @DisplayName("TC_BC_003: Adding 10th item (max allowed) should succeed")
        void testCreateOrder_TenItems_ShouldSucceed() {
            ProductDTO coneDTO = createProductDTO(1L, testCone);
            ProductDTO flavourDTO = createProductDTO(4L, testFlavour);
            TreatDTO treat = new TreatDTO(List.of(coneDTO, flavourDTO));

            List<TreatDTO> tenTreats = List.of(treat, treat, treat, treat, treat, treat, treat, treat, treat, treat);
            OrderDTO incomingOrder = new OrderDTO(0, LocalDateTime.now(), null, null, 0, null, tenTreats);

            when(productRepository.findById(1L)).thenReturn(Optional.of(testCone));
            when(productRepository.findById(4L)).thenReturn(Optional.of(testFlavour));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            OrderDTO result = orderService.createOrder(incomingOrder);

            assertNotNull(result, "Order should be created with 10 items");
            assertEquals(10, result.basketItems().size(),
                    "Basket should contain exactly 10 items");
        }

        @Test
        @DisplayName("TC_BC_004: Adding 11th item (exceeds limit) should throw InvalidBasketException")
        void testValidateBasketSize_ElevenTreats_ShouldReturnFalse() {
            assertFalse(orderService.validateBasketSize(11));
        }

        @Test
        @DisplayName("TC_BC_005: Total calculation for normal season (no surcharge)")
        void testCalcSurcharge_WinterDate_ShouldReturnZero() {
            LocalDateTime winterDate = LocalDateTime.of(2025, 12, 15, 12, 0);
            BigDecimal surcharge = orderService.calcSurcharge(winterDate);
            assertEquals(BigDecimal.valueOf(0.00), surcharge);
        }

        @Test
        @DisplayName("TC_BC_006: Total calculation for summer season (with £3 surcharge)")
        void testCalcSurcharge_SummerDate_ShouldReturnThreePounds() {
            LocalDateTime summerDate = LocalDateTime.of(2025, 6, 15, 12, 0);
            BigDecimal surcharge = orderService.calcSurcharge(summerDate);
            assertEquals(BigDecimal.valueOf(3.00), surcharge);
        }

        @Test
        @DisplayName("TC_BC_007: Successful checkout should process payment")
        void testCheckoutOrder_ValidOrder_ShouldProcessPayment() {
            // Arrange
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(22.50),
                    BigDecimal.valueOf(2.50), 30, null);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            when(paymentGateway.processPayment(any(OrderDTO.class)))
                    .thenReturn(new PaymentDTO(true, "tx-12345", "Payment successful."));

            // Act
            CheckoutDTO result = orderService.checkoutOrder(1L);

            // Assert
            assertTrue(result.paid());
            assertNotNull(result.transactionId());
            verify(paymentGateway, times(1)).processPayment(any(OrderDTO.class));
        }
    }

    // ==================== TC_OT: Order Tracking Tests ====================
    @Nested
    @DisplayName("Order Tracking Tests (TC_OT)")
    class OrderTrackingTests {

        @Test
        @DisplayName("TC_OT_001: Searching existing order should return order details")
        void testGetOrderById_ValidId_ShouldReturnOrder() {
            // Arrange
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(22.50),
                    BigDecimal.valueOf(2.50), 30, null);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            // Act
            OrderDTO result = orderService.getOrderById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(22.50).setScale(2),
                    result.orderTotal().setScale(2));
        }

        @Test
        @DisplayName("TC_OT_002: Searching non-existent order should throw ResourceNotFoundException")
        void testGetOrderById_InvalidId_ShouldThrowResourceNotFoundException() {
            when(orderRepository.findById(99999L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(99999L));
        }

        @Test
        @DisplayName("TC_OT_003: Estimated delivery time should be calculated and positive")
        void testCalcEstDeliveryMinutes_ShouldReturnPositive() {
            int treatCount = 3;
            int productCount = 9;

            int result = orderService.calcEstDeliveryMinutes(treatCount, productCount);

            assertTrue(result > 0, "Estimated delivery time should be positive");
        }
    }

    // ==================== TC_PR: Promotion Tests ====================
    @Nested
    @DisplayName("Promotion Tests (TC_PR)")
    class PromotionTests {

        @Test
        @DisplayName("TC_PR_001: LuckyForSome applies 13% discount at £13.00")
        void testLuckyForSome_AtThreshold_ShouldApplyDiscount() {
            // Arrange
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(13.00),
                    BigDecimal.valueOf(2.50), 25, null);

            // Act
            orderService.luckyForSome(order);

            // Assert
            // Expected value: 13.00 × (1 - 0.13) = 11.31
            // This is a fixed expected value from SRS Appendix D logic
            BigDecimal expectedTotal = new BigDecimal("11.31");
            assertEquals(expectedTotal.setScale(2), order.getOrderTotal().setScale(2));
        }

        @Test
        @DisplayName("TC_PR_002: LuckyForSome does not apply below £13.00")
        void testLuckyForSome_BelowThreshold_ShouldThrowException() {
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(12.99),
                    BigDecimal.valueOf(2.50), 25, null);
            assertThrows(InvalidPromotionException.class, () -> orderService.luckyForSome(order));
        }

        @Test
        @DisplayName("TC_PR_003: MegaMelt100 applies £20 discount above £100.00")
        void testMegaMelt100_AboveThreshold_ShouldApplyDiscount() {
            // Arrange
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.01),
                    BigDecimal.valueOf(2.50), 25, null);

            // Act
            orderService.megaMelt100(order);

            // Assert
            // Expected value: 100.01 - 20.00 = 80.01 (SRS Appendix E)
            BigDecimal expectedTotal = new BigDecimal("80.01");
            assertEquals(expectedTotal.setScale(2), order.getOrderTotal().setScale(2));
        }

        @Test
        @DisplayName("TC_PR_004: MegaMelt100 should NOT apply at exactly £100.00")
        void testMegaMelt100_AtThreshold_ShouldNotApplyDiscount() {
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(100.00),
                    BigDecimal.valueOf(2.50), 25, null);
            BigDecimal originalTotal = order.getOrderTotal();

            assertThrows(InvalidPromotionException.class,
                    () -> orderService.megaMelt100(order),
                    "MegaMelt100 should throw exception when total = £100.00 (SRS requires strictly greater)");

            assertEquals(originalTotal, order.getOrderTotal(),
                    "Order total should remain unchanged when promotion is not applied");
        }

        @Test
        @DisplayName("TC_PR_005: Frozen40 applies with 4 treats and total £40.00")
        void testFrozen40_Valid_ShouldApplyDiscount() {
            // Arrange
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(40.00),
                    BigDecimal.valueOf(2.50), 25, null);
            Treat treat1 = new Treat();
            Treat treat2 = new Treat();
            Treat treat3 = new Treat();
            Treat treat4 = new Treat();
            order.setTreats(List.of(treat1, treat2, treat3, treat4));

            // Act
            orderService.frozen40(order);

            // Assert
            // Expected value: 40.00 × (1 - 0.4) = 24.00 (SRS Appendix F)
            BigDecimal expectedTotal = new BigDecimal("24.00");
            assertEquals(expectedTotal.setScale(2), order.getOrderTotal().setScale(2));
        }

        @Test
        @DisplayName("TC_PR_006: Frozen40 does not apply with 3 treats")
        void testFrozen40_ThreeTreats_ShouldThrowException() {
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(40.00),
                    BigDecimal.valueOf(2.50), 25, null);
            Treat treat1 = new Treat();
            Treat treat2 = new Treat();
            Treat treat3 = new Treat();
            order.setTreats(List.of(treat1, treat2, treat3));
            assertThrows(InvalidPromotionException.class, () -> orderService.frozen40(order));
        }

        @Test
        @DisplayName("TC_PR_007: Frozen40 does not apply with total below £40.00")
        void testFrozen40_TotalBelowThreshold_ShouldThrowException() {
            // Arrange
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(39.99),
                    BigDecimal.valueOf(2.50), 25, null);
            Treat treat1 = new Treat();
            Treat treat2 = new Treat();
            Treat treat3 = new Treat();
            Treat treat4 = new Treat();
            order.setTreats(List.of(treat1, treat2, treat3, treat4));

            // Act & Assert
            // SRS Appendix F requires total cost of 40.00 GBP or more
            assertThrows(InvalidPromotionException.class,
                    () -> orderService.frozen40(order),
                    "Frozen40 should throw exception when total is below £40.00");
        }

        @Test
        @DisplayName("TC_PR_008: TripleTreat3 applies with exactly 3 treats")
        void testTripleTreat3_ThreeTreats_ShouldApplyDiscount() {
            // Arrange
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(20.00),
                    BigDecimal.valueOf(2.50), 25, null);
            Treat treat1 = new Treat();
            Treat treat2 = new Treat();
            Treat treat3 = new Treat();
            order.setTreats(List.of(treat1, treat2, treat3));

            // Act
            orderService.tripleTreat3(order);

            // Assert
            // Expected value: 20.00 - 3.00 = 17.00 (SRS Appendix G)
            BigDecimal expectedTotal = new BigDecimal("17.00");
            assertEquals(expectedTotal.setScale(2), order.getOrderTotal().setScale(2));
        }

        @Test
        @DisplayName("TC_PR_009: TripleTreat3 should NOT apply with 4 treats")
        void testTripleTreat3_FourTreats_ShouldNotApplyDiscount() {
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(20.00),
                    BigDecimal.valueOf(2.50), 25, null);
            Treat treat1 = new Treat();
            Treat treat2 = new Treat();
            Treat treat3 = new Treat();
            Treat treat4 = new Treat();
            order.setTreats(List.of(treat1, treat2, treat3, treat4));
            BigDecimal originalTotal = order.getOrderTotal();

            assertThrows(InvalidPromotionException.class,
                    () -> orderService.tripleTreat3(order),
                    "TripleTreat3 should throw exception when basket has 4 treats (SRS requires exactly 3)");

            assertEquals(originalTotal, order.getOrderTotal(),
                    "Order total should remain unchanged when promotion is not applied");
        }

        @Test
        @DisplayName("TC_PR_010: ScoopThereItIs! applies £1 discount to any order")
        void testScoopThereItIs_AnyOrder_ShouldApplyDiscount() {
            // Arrange
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(15.00),
                    BigDecimal.valueOf(2.50), 25, null);

            // Act
            orderService.scoopThereItIs(order);

            // Assert
            // Expected value: 15.00 - 1.00 = 14.00 (SRS Appendix H)
            BigDecimal expectedTotal = new BigDecimal("14.00");
            assertEquals(expectedTotal.setScale(2), order.getOrderTotal().setScale(2));
        }

        @Test
        @DisplayName("TC_PR_011: Invalid promotion code rejected")
        void testApplyPromotion_InvalidCode_ShouldThrowException() {
            Order order = new Order(LocalDateTime.now(), BigDecimal.valueOf(20.00),
                    BigDecimal.valueOf(2.50), 25, null);
            assertThrows(InvalidPromotionException.class,
                    () -> orderService.applyPromotion(order, "INVALID_CODE"));
        }
    }

    // ==================== Additional Helper Tests ====================
    @Nested
    @DisplayName("Price Calculation Tests")
    class PriceCalculationTests {

        @Test
        @DisplayName("Subtotal calculation should sum all product prices")
        void testCalcSubtotal_MultipleProducts_ShouldReturnSum() {
            Treat treat = new Treat();
            treat.setProducts(List.of(testCone, testFlavour, testTopping));
            BigDecimal subtotal = orderService.calcSubtotal(List.of(treat));
            assertEquals(BigDecimal.valueOf(3.50).setScale(2),
                    subtotal.setScale(2));
        }

        @Test
        @DisplayName("Empty treat list should return zero subtotal")
        void testCalcSubtotal_EmptyList_ShouldReturnZero() {
            BigDecimal subtotal = orderService.calcSubtotal(List.of());
            assertEquals(BigDecimal.ZERO, subtotal);
        }

        @Test
        @DisplayName("Null treat list should return zero subtotal")
        void testCalcSubtotal_NullList_ShouldReturnZero() {
            BigDecimal subtotal = orderService.calcSubtotal(null);
            assertEquals(BigDecimal.ZERO, subtotal);
        }
    }
}