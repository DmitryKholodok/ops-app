package by.issoft.opsapp.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Operator {

    @Min(1)
    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    private Boolean isBillable;

    public Operator(Integer id, String name, Boolean isBillable) {
        this.id = id;
        this.name = name;
        this.isBillable = isBillable;
    }

    public Operator() {
    }
}

