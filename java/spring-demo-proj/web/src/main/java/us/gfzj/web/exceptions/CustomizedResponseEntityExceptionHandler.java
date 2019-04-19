package us.gfzj.web.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * @author zhoujing
 *         Create on 19-2-3
 */
@RestControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private Logger logger = LogManager.getLogger(CustomizedResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {InvalidUserParametersException.class})
    public ResponseEntity<ApiErrorResponse> handleUserInvalidParametersExceptions(Exception ex, WebRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(new Date(),
                ex.getMessage(),
                request.getDescription(false));
        logger.error(ex.getMessage(), ex);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ApiErrorResponse> handleInternalError(Exception ex, WebRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(new Date(),
                ex.getMessage(),
                request.getDescription(false));
        logger.error(ex.getMessage(), ex);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundError(Exception ex, WebRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(new Date(),
                ex.getMessage(),
                request.getDescription(false));
        logger.error(ex.getMessage(), ex);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }
}
