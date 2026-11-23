package cat.itacademy.s04.t02.n02.fruitorderapi.mapper;

import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderItemRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderItemResponseDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderResponseDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.model.Order;
import cat.itacademy.s04.t02.n02.fruitorderapi.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDto request){
        List<OrderItem> items = request.items().stream()
                .map(this::toItemEntity)
                .toList();

        return new Order(null, request.clientName(), request.deliveryDate(),items);
    }

    public OrderResponseDto toResponseDto (Order order){
        List<OrderItemResponseDto> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponseDto(order.getId(), order.getClientName(), order.getDeliveryDate(), items);
    }

    private OrderItem toItemEntity(OrderItemRequestDto dto) {
        return new OrderItem(dto.fruitName(), dto.quantityInKilos());
    }

    private OrderItemResponseDto toItemResponse(OrderItem dto) {
        return new OrderItemResponseDto(dto.getFruitName(), dto.getQuantityInKilos());
    }
}
