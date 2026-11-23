package cat.itacademy.s04.t02.n02.fruitorderapi.controllers;

import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderRequestDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.dtos.OrderResponseDto;
import cat.itacademy.s04.t02.n02.fruitorderapi.service.OrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderServiceImpl orderService;

    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> create (@Valid @RequestBody OrderRequestDto request){
        OrderResponseDto createOrder = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createOrder);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable String id) {
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable String id,
            @Valid @RequestBody OrderRequestDto requestDto) {

        OrderResponseDto updatedOrder = orderService.updateOrder(id, requestDto);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

