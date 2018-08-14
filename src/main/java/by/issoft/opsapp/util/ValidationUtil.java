package by.issoft.opsapp.util;

import by.issoft.opsapp.exception.BadRequestException;
import org.springframework.validation.BindingResult;

public class ValidationUtil {

    public static void verifyBindingResultThrows(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().toString());
        }
    }

}
