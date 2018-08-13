package by.issoft.opsapp.controller;

import by.issoft.opsapp.dto.Project;
import by.issoft.opsapp.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping(value = "/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProject(@RequestBody Project project, HttpServletResponse response) {
        int projectId = projectService.createProject(project);
        response.addHeader("Content-Location", "/projects/" + projectId);
    }

}
