package by.issoft.opsapp.service;

import by.issoft.opsapp.dto.Project;
import by.issoft.opsapp.exception.InvalidEntityException;
import by.issoft.opsapp.model.ProjectModel;
import by.issoft.opsapp.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public int saveProject(Project project) {
        if (projectRepository.existsByName(project.getName())) {
            throw new InvalidEntityException("Project with name '" + project.getName() + "' already exists!");
        }
        ProjectModel projectModel = projectRepository.save(toProjectModel(project));
        return projectModel.getId();
    }

    @Transactional(readOnly = true)
    public Project retrieveProjectById(int id) {
        Optional<ProjectModel> optionalProjectModel = projectRepository.findById(id);
        if (!optionalProjectModel.isPresent()) {
            throw new EntityNotFoundException("Project with id = " + id + " was not found!");
        }
        return toProject(optionalProjectModel.get());
    }

    @Transactional
    public void updateProject(Project updatedProject, Integer updatedProjectId) {
        ProjectModel updatedPm = projectRepository.getOne(updatedProjectId); // can throw EntityNotFoundException
        ProjectModel pm = projectRepository.findByName(updatedProject.getName());
        if (pm != null && !pm.getId().equals(updatedProjectId)) {
            throw new InvalidEntityException("Project with name '" + updatedPm.getName() + "' already exists!");
        }
        updatedPm.setName(updatedProject.getName());
        updatedPm.setAlternativeName(updatedProject.getAlternativeName());
        updatedPm.setPeopleCount(updatedProject.getPeopleCount());
        projectRepository.save(updatedPm);
    }

    private ProjectModel toProjectModel(Project project) {
        ProjectModel projectModel = new ProjectModel();
        projectModel.setName(project.getName());
        projectModel.setAlternativeName(project.getAlternativeName());
        projectModel.setPeopleCount(project.getPeopleCount());
        return projectModel;
    }

    private Project toProject(ProjectModel projectModel) {
        return new Project(
            projectModel.getId(),
            projectModel.getName(),
            projectModel.getAlternativeName(),
            projectModel.getPeopleCount()
        );
    }

}
