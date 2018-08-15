package by.issoft.opsapp.controller;

import by.issoft.opsapp.dto.Project;
import by.issoft.opsapp.service.ProjectService;
import by.issoft.opsapp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Void> saveProject(@Valid @RequestBody Project project, BindingResult br) {
        ValidationUtil.verifyBindingResultThrows(br);
        int projectId = projectService.saveProject(project);
        return ResponseEntity
                .created(URI.create("/projects/" + projectId))
                .build();
    }

    @GetMapping("/{id}")
    public Project retrieveProject(@PathVariable int id) {
        return projectService.retrieveProjectById(id);
    }

    @PutMapping("/{id}")
    public void updateProject(@Valid @RequestBody Project project, BindingResult br, @PathVariable int id) {
        ValidationUtil.verifyBindingResultThrows(br);
        projectService.updateProject(project, id);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable int id) {
        projectService.deleteProjectById(id);
    }



}
