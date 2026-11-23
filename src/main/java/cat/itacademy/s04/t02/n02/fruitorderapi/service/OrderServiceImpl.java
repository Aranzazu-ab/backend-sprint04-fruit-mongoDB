package cat.itacademy.s04.t02.n02.fruitorderapi.service;

import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderResponseDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.exceptions.OrderNotFoundException;
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
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }

    @Override
    public OrderResponseDto getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID: " + id +" not found."));

        return orderMapper.toResponseDto(order);
    }

    @Override
    public OrderResponseDto updateOrder(String id, OrderRequestDto requestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID: " + id +" not found."));

        order.setClientName(requestDto.clientName());
        order.setDeliveryDate(requestDto.deliveryDate());

        order.setItems(requestDto.items().stream()
                .map(orderMapper::toItemEntity)
                .toList());

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toResponseDto(updatedOrder);
    }

    @Override
    public void deleteOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID: " + id +" not found."));

        orderRepository.delete(order);
    }

}
