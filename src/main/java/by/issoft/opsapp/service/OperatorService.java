package by.issoft.opsapp.service;

import by.issoft.opsapp.dto.Operator;

public interface OperatorService {

    Integer saveOperator(Operator operator);
    Operator retrieveOperatorById(Integer id);

}
