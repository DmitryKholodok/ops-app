package by.issoft.opsapp.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
public class Project {

    @NotNull
    @Min(0)
    private Integer id;

    @NotNull
    private String name;

    private String alternativeName;

    @NotNull
    @Min(0)
    private Integer peopleCount;

    public Project(int id, String name, String alternativeName, int peopleCount) {
        this.id = id;
        this.name = name;
        this.alternativeName = alternativeName;
        this.peopleCount = peopleCount;
    }

    public Project() {}
}
