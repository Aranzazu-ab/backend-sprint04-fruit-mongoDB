package cat.itacademy.s04.t02.n02.fruitorderapi.controllers;


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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void create_withValidData_returns201AndSavesOrder() throws Exception {
        postOrder(json("Felipe", LocalDate.now().plusDays(5), itemJson("Apple", 5)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.clientName").value("Felipe"))
                .andExpect(jsonPath("$.items[0].fruitName").value("Apple"))
                .andExpect(jsonPath("$.items[0].quantityInKilos").value(5));

        assertEquals(1, orderRepository.count());
    }

    @Test
    void create_withBlankClientName_returns400AndSavesNothing() throws Exception {
        postOrder(json("", LocalDate.now().plusDays(5), itemJson("Apple", 5)))
                .andExpect(status().isBadRequest());

        assertEquals(0, orderRepository.count());
    }

    @Test
    void create_withTodayAsDeliveryDate_returns400() throws Exception {
        postOrder(json("Felipe", LocalDate.now(), itemJson("Apple", 5)))
                .andExpect(status().isBadRequest());

        assertEquals(0, orderRepository.count());
    }

    @Test
    void create_withEmptyItems_returns400() throws Exception {
        postOrder(json("Felipe", LocalDate.now().plusDays(5), ""))
                .andExpect(status().isBadRequest());

        assertEquals(0, orderRepository.count());
    }

    @Test
    void create_withZeroQuantity_returns400() throws Exception {
        postOrder(json("Felipe", LocalDate.now().plusDays(5), itemJson("Apple", 0)))
                .andExpect(status().isBadRequest());

        assertEquals(0, orderRepository.count());
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

    private String itemJson(String fruitName, int quantityInKilos) {
        return """
                { "fruitName": "%s", "quantityInKilos": %d }
                """.formatted(fruitName, quantityInKilos);
    }

}
