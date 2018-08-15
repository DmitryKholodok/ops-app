package by.issoft.opsapp.util;

import by.issoft.opsapp.exception.InvalidEntityException;
import org.springframework.validation.BindingResult;

public class ValidationUtil {

    public static void verifyBindingResultThrows(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidEntityException(bindingResult.getAllErrors().toString());
        }
    }

}
