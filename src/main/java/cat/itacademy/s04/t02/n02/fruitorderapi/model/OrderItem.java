package cat.itacademy.s04.t02.n02.fruitorderapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private String fruitName;
    private Integer quantityInKilos;
}
