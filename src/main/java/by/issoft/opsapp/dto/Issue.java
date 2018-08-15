package by.issoft.opsapp.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Issue {

    @Min(1)
    private Integer id;

    @NotNull
    private String description;

    @Min(1)
    @NotNull
    private Integer projectId;

}
