package cat.itacademy.s04.t02.n02.fruitorderapi.service;

import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderResponseDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.mapper.OrderMapper;
import cat.itacademy.s04.t02.n02.fruitorderapi.model.Order;
import cat.itacademy.s04.t02.n02.fruitorderapi.repository.OrderRepository;

public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    public OrderResponseDto create(OrderRequestDto request){
        Order order = orderMapper.toEntity(request);
        Order orderSaved = orderRepository.save(order);

        return orderMapper.toResponseDto(orderSaved);
    }
}
