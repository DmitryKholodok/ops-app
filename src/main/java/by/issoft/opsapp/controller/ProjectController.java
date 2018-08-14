package by.issoft.opsapp.controller;

import by.issoft.opsapp.dto.Project;
import by.issoft.opsapp.service.ProjectService;
import by.issoft.opsapp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveProject(@Valid @RequestBody Project project, BindingResult br, HttpServletResponse response) {
        ValidationUtil.verifyBindingResultThrows(br);
        int projectId = projectService.saveProject(project);
        response.addHeader("Content-Location", "/projects/" + projectId);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Project retrieveProject(@PathVariable int id) {
        return projectService.retrieveProjectById(id);
    }

}
