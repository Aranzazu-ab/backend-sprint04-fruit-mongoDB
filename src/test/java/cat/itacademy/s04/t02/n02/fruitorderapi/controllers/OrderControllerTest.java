package cat.itacademy.s04.t02.n02.fruitorderapi.controllers;


import cat.itacademy.s04.t02.n02.fruitorderapi.model.Order;
import cat.itacademy.s04.t02.n02.fruitorderapi.model.OrderItem;
import cat.itacademy.s04.t02.n02.fruitorderapi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.data.mongodb.uri=mongodb://localhost:27017/orders_test_db")
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void createReturns201AndSavesOrderWhenDataValid() throws Exception {
        postOrder(json("Felipe", LocalDate.now().plusDays(5), itemJson("Apple", 5)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.clientName").value("Felipe"))
                .andExpect(jsonPath("$.items[0].fruitName").value("Apple"))
                .andExpect(jsonPath("$.items[0].quantityInKilos").value(5));

        assertEquals(1, orderRepository.count());
    }

    @Test
    void createReturns400NoSaveWhenBlankClientName() throws Exception {
        postOrder(json("", LocalDate.now().plusDays(5), itemJson("Apple", 5)))
                .andExpect(status().isBadRequest());

        assertEquals(0, orderRepository.count());
    }

    @Test
    void createReturns400WhenDeliveryDateIsToday() throws Exception {
        postOrder(json("Felipe", LocalDate.now(), itemJson("Apple", 5)))
                .andExpect(status().isBadRequest());

        assertEquals(0, orderRepository.count());
    }

    @Test
    void createReturns400WhenItemsAreEmpty() throws Exception {
        postOrder(json("Felipe", LocalDate.now().plusDays(5), ""))
                .andExpect(status().isBadRequest());

        assertEquals(0, orderRepository.count());
    }

    @Test
    void createReturns400WhenQuantityIsZero() throws Exception {
        postOrder(json("Felipe", LocalDate.now().plusDays(5), itemJson("Apple", 0)))
                .andExpect(status().isBadRequest());

        assertEquals(0, orderRepository.count());
    }

    @Test
    void getAllReturns200WhenNoOrders() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllReturns200AndListWhenExistingOrders() throws Exception {
        Order order1 = new Order(null, "Felipe", LocalDate.now().plusDays(1),
                List.of(new OrderItem("Apple", 5)));
        Order order2 = new Order(null, "Carla", LocalDate.now().plusDays(2),
                List.of(new OrderItem("Pear", 3)));

        orderRepository.saveAll(List.of(order1, order2));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].clientName").value("Felipe"))
                .andExpect(jsonPath("$[1].clientName").value("Carla"));
    }

    @Test
    void getByIdReturns200WhenOrderExists() throws Exception {
        Order order = new Order(null, "Felipe", LocalDate.now().plusDays(5),
                List.of(new OrderItem("Apple", 5)));
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(get("/orders/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.clientName").value("Felipe"))
                .andExpect(jsonPath("$.items[0].fruitName").value("Apple"));
    }

    @Test
    void getByIdReturns404WhenOrderNotExist() throws Exception {
        mockMvc.perform(get("/orders/not-id-123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateReturns200AndUpdateWhenDataIsValid() throws Exception {
        Order original = new Order(null, "Original", LocalDate.now().plusDays(5),
                List.of(new OrderItem("Apple", 5)));
        Order saved = orderRepository.save(original);

        String updateBody = json("Updated", LocalDate.now().plusDays(10), itemJson("Banana", 8));

        mockMvc.perform(put("/orders/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName").value("Updated"))
                .andExpect(jsonPath("$.items[0].fruitName").value("Banana"));
    }

    @Test
    void updateReturns404WhenInvalidId() throws Exception {
        String updateBody = json("Updated", LocalDate.now().plusDays(10), itemJson("Banana", 8));

        mockMvc.perform(put("/orders/not-id-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());
    }

    private ResultActions postOrder(String body) throws Exception {
        return mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private String json(String clientName, LocalDate deliveryDate, String items) {
        return """
                {
                  "clientName": "%s",
                  "deliveryDate": "%s",
                  "items": [%s]
                }
                """.formatted(clientName, deliveryDate, items);
    }

    private String itemJson(String fruitName, Integer quantityInKilos) {
        return """
                { "fruitName": "%s", "quantityInKilos": %d }
                """.formatted(fruitName, quantityInKilos);
    }

}
