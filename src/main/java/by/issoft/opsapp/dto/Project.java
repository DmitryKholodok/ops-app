package by.issoft.opsapp.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
public class Project {

    @Min(1)
    private Integer id;

    @NotBlank
    private String name;

    private String alternativeName;

    @NotNull
    @Min(1)
    private Integer peopleCount;

    public Project(Integer id, String name, String alternativeName, Integer peopleCount) {
        this.id = id;
        this.name = name;
        this.alternativeName = alternativeName;
        this.peopleCount = peopleCount;
    }

    public Project() {}
}
