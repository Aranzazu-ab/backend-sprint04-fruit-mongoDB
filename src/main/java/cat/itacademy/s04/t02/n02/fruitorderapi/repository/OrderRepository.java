package cat.itacademy.s04.t02.n02.fruitorderapi.repository;

import cat.itacademy.s04.t02.n02.fruitorderapi.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
