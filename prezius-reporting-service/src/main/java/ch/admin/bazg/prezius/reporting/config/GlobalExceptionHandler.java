package ch.admin.bazg.prezius.reporting.config;

import ch.admin.bit.jeap.starter.application.web.FrontendRouteRedirectExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends FrontendRouteRedirectExceptionHandler {

    private static final String REST_CALL_EXCEPTION_MESSAGE = "REST call error: ";

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(final EntityNotFoundException exception) {
        log.error(REST_CALL_EXCEPTION_MESSAGE + exception.getMessage(), exception);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Object> handleException(final AccessDeniedException exception) {
        log.error(REST_CALL_EXCEPTION_MESSAGE + exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleWrongRequestParams(final IllegalArgumentException exception) {
        log.error(REST_CALL_EXCEPTION_MESSAGE + exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
