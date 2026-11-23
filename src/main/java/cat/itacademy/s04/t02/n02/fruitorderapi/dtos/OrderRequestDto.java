package cat.itacademy.s04.t02.n02.fruitorderapi.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record OrderRequestDto(
        @NotBlank(message = "Client name is required")
        String clientName,

        @NotNull(message = "Delivery date is required")
        @Future(message = "Delivery date must be at least tomorrow")
        LocalDate deliveryDate,

        @NotEmpty(message = "At least one item is required")
        @Valid
        List<OrderItemRequestDto> items
) {
}
