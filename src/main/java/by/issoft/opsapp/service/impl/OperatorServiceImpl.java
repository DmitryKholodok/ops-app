package by.issoft.opsapp.service.impl;

import by.issoft.opsapp.dto.Operator;
import by.issoft.opsapp.model.OperatorModel;
import by.issoft.opsapp.repository.OperatorRepository;
import by.issoft.opsapp.service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperatorServiceImpl implements OperatorService {

    private final OperatorRepository operatorRepository;

    @Override
    @Transactional
    public Integer saveOperator(Operator operator) {
        verifyOperatorIdThrows(operator);
        return operatorRepository.save(toOperatorModel(operator)).getId();
    }

    @Override
    @Transactional
    public Operator retrieveOperatorById(Integer id) {
        Optional<OperatorModel> model = operatorRepository.findById(id);
        return toOperator(model.orElseThrow(EntityNotFoundException::new));
    }

    private Operator toOperator(OperatorModel model) {
        Operator operator = new Operator();
        operator.setId(model.getId());
        operator.setIsBillable(model.getIsBillable());
        operator.setName(model.getName());
        return operator;
    }

    private OperatorModel toOperatorModel(Operator operator) {
        OperatorModel model = new OperatorModel();
        model.setId(operator.getId());
        model.setName(operator.getName());
        model.setIsBillable(operator.getIsBillable());
        return model;
    }

    private void verifyOperatorIdThrows(Operator operator) {
        if (operator.getId() != null) {
            Optional<OperatorModel> model = operatorRepository.findById(operator.getId());
            if (model.isPresent()) {
                throw new EntityExistsException("Operator with id = " + operator.getId() + " already exists!");
            }
        }
    }

}
