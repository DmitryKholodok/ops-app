package by.issoft.opsapp.repository;

import by.issoft.opsapp.model.IssueModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<IssueModel, Integer> {
}
