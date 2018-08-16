package by.issoft.opsapp.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Issue {

    @Min(1)
    private Integer id;

    @NotBlank
    private String description;

    @Min(1)
    @NotNull
    private Integer projectId;

    public Issue(Integer id, String description, Integer projectId) {
        this.id = id;
        this.description = description;
        this.projectId = projectId;
    }

    public Issue() {
    }
}
