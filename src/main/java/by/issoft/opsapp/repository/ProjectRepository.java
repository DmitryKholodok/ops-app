package by.issoft.opsapp.repository;

import by.issoft.opsapp.model.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectModel, Integer> {

    boolean existsByName(String name);
    ProjectModel findByName(String name);

}
