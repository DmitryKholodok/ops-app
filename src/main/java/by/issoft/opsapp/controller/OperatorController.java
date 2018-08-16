package by.issoft.opsapp.controller;

import by.issoft.opsapp.dto.Operator;
import by.issoft.opsapp.service.OperatorService;
import by.issoft.opsapp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/operators")
public class OperatorController {

    private final OperatorService operatorService;

    @PostMapping
    public ResponseEntity<Void> saveOperator(@RequestBody @Valid Operator operator, BindingResult br) {
        ValidationUtil.verifyBindingResultThrows(br);
        Integer issueId = operatorService.saveOperator(operator);
        return ResponseEntity
                .created(URI.create("/operators/" + issueId))
                .build();
    }

    @GetMapping("/{id}")
    public Operator retrieveOperator(@PathVariable Integer id) {
        return operatorService.retrieveOperatorById(id);
    }

}
