package cat.itacademy.s04.t02.n02.fruitorderapi.service;

import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderResponseDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.mapper.OrderMapper;
import cat.itacademy.s04.t02.n02.fruitorderapi.model.Order;
import cat.itacademy.s04.t02.n02.fruitorderapi.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderResponseDto createOrder(OrderRequestDto requestDTO) {
        Order order = orderMapper.toEntity(requestDTO);
        Order orderSaved = orderRepository.save(order);

        return orderMapper.toResponseDto(orderSaved);
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return List.of();
    }
}
