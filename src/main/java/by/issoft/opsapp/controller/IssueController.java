package by.issoft.opsapp.controller;

import by.issoft.opsapp.dto.Issue;
import by.issoft.opsapp.service.IssueService;
import by.issoft.opsapp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<Void> saveIssue(@RequestBody @Valid Issue issue, BindingResult br) {
        ValidationUtil.verifyBindingResultThrows(br);
        Integer issueId = issueService.saveIssue(issue);
        return ResponseEntity
                .created(URI.create("/issues/" + issueId))
                .build();
    }

    @GetMapping("/{id}")
    public Issue retrieveIssue(@PathVariable Integer id) {
        return issueService.retrieveIssueById(id);
    }

}
