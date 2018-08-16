package by.issoft.opsapp.service.impl;

import by.issoft.opsapp.dto.Issue;
import by.issoft.opsapp.model.IssueModel;
import by.issoft.opsapp.repository.IssueRepository;
import by.issoft.opsapp.service.IssueService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;

    @Override
    @Transactional
    public Integer saveIssue(Issue issue) {
        return issueRepository.save(toIssueModel(issue)).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Issue retrieveIssueById(Integer id) {
        Optional<IssueModel> issueModel = issueRepository.findById(id);
        return toIssue(issueModel.orElseThrow(EntityNotFoundException::new));
    }

    private IssueModel toIssueModel(Issue issue) {
        IssueModel issueModel = new IssueModel();
        issueModel.setId(issue.getId());
        issueModel.setDescription(issue.getDescription());
        issueModel.setProjectId(issue.getProjectId());
        return issueModel;
    }

    private Issue toIssue(IssueModel issueModel) {
        Issue issue = new Issue();
        issue.setId(issueModel.getId());
        issue.setDescription(issueModel.getDescription());
        issue.setProjectId(issueModel.getProjectId());
        return issue;
    }
}
