package cat.itacademy.s04.t02.n02.fruitorderapi.service;

import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderItemRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderResponseDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.exceptions.OrderNotFoundException;
import cat.itacademy.s04.t02.n02.fruitorderapi.mapper.OrderMapper;
import cat.itacademy.s04.t02.n02.fruitorderapi.model.Order;
import cat.itacademy.s04.t02.n02.fruitorderapi.model.OrderItem;
import cat.itacademy.s04.t02.n02.fruitorderapi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp(){
        orderService = new OrderServiceImpl(orderRepository, new OrderMapper());
    }

    @Test
    void createReturnsResponseAndSavesWhenRequestIsValid() {
        LocalDate deliveryDate = LocalDate.now().plusDays(3);
        OrderRequestDto request = new OrderRequestDto(
                "Carla",
                deliveryDate,
                List.of(new OrderItemRequestDto("Apple", 5)));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId("abc123");
            return order;
        });

        OrderResponseDto response = orderService.createOrder(request);

        assertEquals("abc123", response.id());
        assertEquals("Carla", response.clientName());
        assertEquals(deliveryDate, response.deliveryDate());
        assertEquals(1, response.items().size());
        assertEquals("Apple", response.items().get(0).fruitName());
        assertEquals(5, response.items().get(0).quantityInKilos());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getAllOrdersReturnsEmptyListWhenNoOrders() {
        when(orderRepository.findAll()).thenReturn(List.of());

        List<OrderResponseDto> responses = orderService.getAllOrders();

        assertTrue(responses.isEmpty());
        verify(orderRepository).findAll();
    }

    @Test
    void getAllOrdersReturnsListWhenOrdersInLists() {
        LocalDate date1 = LocalDate.now().plusDays(1);
        Order order1 = new Order("abc123", "Carla", date1,
                List.of(new OrderItem("Apple", 5)));

        when(orderRepository.findAll()).thenReturn(List.of(order1));

        List<OrderResponseDto> responses = orderService.getAllOrders();

        assertEquals(1, responses.size());
        assertEquals("Carla", responses.get(0).clientName());
        assertEquals(date1, responses.get(0).deliveryDate());
        assertEquals(1, responses.get(0).items().size());
        assertEquals("Apple", responses.get(0).items().get(0).fruitName());

        verify(orderRepository).findAll();
    }

    @Test
    void getByIdReturnsOrderResponseWhenItExists () {
        String orderId = "abc123";
        LocalDate date = LocalDate.now().plusDays(2);
        Order mockOrder = new Order(orderId, "Felipe", date,
                List.of(new OrderItem("Apple", 5)));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        OrderResponseDto response = orderService.getOrderById(orderId);

        assertEquals(orderId, response.id());
        assertEquals("Felipe", response.clientName());
        assertEquals(1, response.items().size());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getByIdThrowsExceptionWhenOrderNotExists() {
        String orderId = "nf-id";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(orderId);
        });

        verify(orderRepository).findById(orderId);
    }
}
