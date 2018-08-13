package by.issoft.opsapp.service;

import by.issoft.opsapp.dto.Project;
import by.issoft.opsapp.model.ProjectModel;
import by.issoft.opsapp.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public int createProject(Project project) {
        if (projectRepository.existsByName(project.getName())) {
            throw new EntityExistsException("Project '" + project.getName() + "' already exists!");
        }
        ProjectModel projectModel = projectRepository.save(toProjectModel(project));
        return projectModel.getId();
    }

    private ProjectModel toProjectModel(Project project) {
        ProjectModel projectModel = new ProjectModel();
        projectModel.setName(project.getName());
        projectModel.setAlternativeName(project.getAlternativeName());
        projectModel.setPeopleCount(project.getPeopleCount());
        return projectModel;
    }

}
