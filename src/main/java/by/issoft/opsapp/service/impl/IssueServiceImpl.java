package by.issoft.opsapp.service.impl;

import by.issoft.opsapp.dto.Issue;
import by.issoft.opsapp.model.IssueModel;
import by.issoft.opsapp.repository.IssueRepository;
import by.issoft.opsapp.service.IssueService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;

    @Override
    @Transactional
    public Integer saveIssue(Issue issue) {
        IssueModel issueModel = toIssueModel(issue);
        return issueRepository.save(issueModel).getId();
    }

    private IssueModel toIssueModel(Issue issue) {
        IssueModel issueModel = new IssueModel();
        issueModel.setDescription(issue.getDescription());
        issueModel.setProjectId(issue.getProjectId());
        return issueModel;
    }
}
