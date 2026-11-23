package cat.itacademy.s04.t02.n02.fruitorderapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDto(
        @NotBlank(message = "Fruit name is required")
        String fruitName,

        @Positive(message = "Quantity must be greater than zero")
        int quantityInKilos
) {
}
