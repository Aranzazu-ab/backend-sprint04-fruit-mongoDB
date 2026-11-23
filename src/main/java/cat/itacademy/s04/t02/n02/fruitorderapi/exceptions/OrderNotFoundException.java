package cat.itacademy.s04.t02.n02.fruitorderapi.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
