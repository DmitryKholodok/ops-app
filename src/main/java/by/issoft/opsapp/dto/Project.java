package by.issoft.opsapp.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Project {

    private int id;
    private String name;
    private String alternativeName;
    private int peopleCount;

    public Project(int id, String name, String alternativeName, int peopleCount) {
        this.id = id;
        this.name = name;
        this.alternativeName = alternativeName;
        this.peopleCount = peopleCount;
    }

    public Project() {}
}
