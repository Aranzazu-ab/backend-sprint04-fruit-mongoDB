package cat.itacademy.s04.t02.n02.fruitorderapi.dtos;

import java.time.LocalDate;
import java.util.List;

public record OrderResponseDto (
        String id,
        String clientName,
        LocalDate deliveryDate,
        List<OrderItemResponseDto> items
){
}
