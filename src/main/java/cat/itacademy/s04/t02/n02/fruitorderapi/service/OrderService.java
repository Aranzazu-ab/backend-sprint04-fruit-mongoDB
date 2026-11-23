package cat.itacademy.s04.t02.n02.fruitorderapi.service;

import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto requestDTO);
    List<OrderResponseDto> getAllOrders();
    OrderResponseDto getOrderById(String id);
    OrderResponseDto updateOrder(String id, OrderRequestDto requestDto);
    void deleteOrder (String id);
}
