package cat.itacademy.s04.t02.n02.fruitorderapi.service;

import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderItemRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderResponseDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.mapper.OrderMapper;
import cat.itacademy.s04.t02.n02.fruitorderapi.model.Order;
import cat.itacademy.s04.t02.n02.fruitorderapi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void create_withValidRequest_savesOrderAndReturnsResponseWithId() {
        LocalDate deliveryDate = LocalDate.now().plusDays(3);
        OrderRequestDto request = new OrderRequestDto(
                "Maria",
                deliveryDate,
                List.of(new OrderItemRequestDto("Apple", 5)));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId("abc123");
            return order;
        });

        OrderResponseDto response = orderService.create(request);

        assertEquals("abc123", response.id());
        assertEquals("Maria", response.clientName());
        assertEquals(deliveryDate, response.deliveryDate());
        assertEquals(1, response.items().size());
        assertEquals("Apple", response.items().get(0).fruitName());
        assertEquals(5, response.items().get(0).quantityInKilos());
        verify(orderRepository).save(any(Order.class));
    }

}
