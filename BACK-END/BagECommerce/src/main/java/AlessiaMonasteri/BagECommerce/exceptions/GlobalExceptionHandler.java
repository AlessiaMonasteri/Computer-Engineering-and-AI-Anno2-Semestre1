package AlessiaMonasteri.BagECommerce.exceptions;

import AlessiaMonasteri.BagECommerce.DTO.ErrorDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - BAD REQUEST

    @ExceptionHandler({
            ValidationException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequest(Exception ex) {

        // ValidationException con lista errori
        if (ex instanceof ValidationException ve) {
            return new ErrorDTO(ve.getMessage(), LocalDateTime.now(), ve.getErrorsList());
        }

        // sul DTO ottengo gli errori per campo
        if (ex instanceof MethodArgumentNotValidException manv) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError fe : manv.getBindingResult().getFieldErrors()) {
                errors.put(fe.getField(), fe.getDefaultMessage());
            }
            return new ErrorDTO("Validation failed", LocalDateTime.now(), errors);
        }

        // mismatch di parametri, enum e types
        if (ex instanceof MethodArgumentTypeMismatchException matm) {
            String expected = (matm.getRequiredType() != null) ? matm.getRequiredType().getSimpleName() : "unknown";
            Map<String, String> details = Map.of(matm.getName(), "Invalid value. Expected type: " + expected);
            return new ErrorDTO("Invalid request parameter", LocalDateTime.now(), details);
        }

        // Violazione dei vincoli su @RequestParam
        if (ex instanceof ConstraintViolationException cve) {
            Map<String, String> errors = new HashMap<>();
            cve.getConstraintViolations().forEach(v ->
                    errors.put(v.getPropertyPath().toString(), v.getMessage())
            );
            return new ErrorDTO("Validation failed", LocalDateTime.now(), errors);
        }

        // JSON malformato o tipo campo errato
        if (ex instanceof HttpMessageNotReadableException) {
            return new ErrorDTO("Malformed JSON or invalid field type", LocalDateTime.now(), null);
        }

        // IllegalArgumentException
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now(), null);
    }

    // 401 - UNAUTHORIZED

    @ExceptionHandler({
            UnauthorizedException.class,
            AuthenticationException.class
    })

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleUnauthorized(Exception ex) {
        if (ex instanceof UnauthorizedException ue) {
            return new ErrorDTO(ue.getMessage(), LocalDateTime.now(), null);
        }
        return new ErrorDTO("Authentication failed", LocalDateTime.now(), null);
    }

    // 403 - FORBIDDEN

    @ExceptionHandler({
            AuthorizationDeniedException.class,
            AccessDeniedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleForbidden(Exception ex) {
        return new ErrorDTO("You do not have permission to access the resource", LocalDateTime.now(), null);
    }

    // 404 - NOT FOUND

    @ExceptionHandler({
            NotFoundException.class,
            NoResourceFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFound(Exception ex) {
        if (ex instanceof NotFoundException nfe) {
            return new ErrorDTO(nfe.getMessage(), LocalDateTime.now(), null);
        }
        return new ErrorDTO("Resource not found", LocalDateTime.now(), null);
    }


    // 409 - CONFLICT

    @ExceptionHandler({
            ConflictException.class,
            DataIntegrityViolationException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleConflict(Exception ex) {
        if (ex instanceof ConflictException ce) {
            return new ErrorDTO(ce.getMessage(), LocalDateTime.now(), null);
        }
        return new ErrorDTO("Data integrity violation", LocalDateTime.now(), null);
    }

    // 500 - INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleGenericError(Exception ex) {
        return new ErrorDTO("INTERNAL SERVER ERROR", LocalDateTime.now(), null);
    }
}
