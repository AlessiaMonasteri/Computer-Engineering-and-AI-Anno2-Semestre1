package AlessiaMonasteri.BagECommerce.exceptions;

import java.util.List;

public class ValidationException extends RuntimeException {
    final List<String> errorsList;
    public ValidationException(String message) {
        super(message);
        this.errorsList = List.of();
    }

    public ValidationException(List<String> errorsList) {
        super("Payload errors");
        this.errorsList = errorsList;
    }

    public List<String> getErrorsList() {
        return errorsList;
    }
}
