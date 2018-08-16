package by.issoft.opsapp.advice;

import by.issoft.opsapp.exception.InvalidEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Slf4j
@ControllerAdvice(basePackages = "by.issoft.opsapp.controller")
@ResponseBody
public class ExceptionsHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler({InvalidEntityException.class, DataAccessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(Exception e) {
        log.error(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleEntityExistsException(EntityExistsException e) {
        log.error(e.getMessage());
        return e.getMessage();
    }



}
