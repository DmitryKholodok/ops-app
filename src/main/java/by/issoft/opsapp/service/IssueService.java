package by.issoft.opsapp.service;

import by.issoft.opsapp.dto.Issue;

public interface IssueService {

   Integer saveIssue(Issue issue);
   Issue retrieveIssueById(Integer id);
}
