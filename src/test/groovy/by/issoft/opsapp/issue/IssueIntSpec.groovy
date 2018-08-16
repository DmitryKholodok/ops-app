package by.issoft.opsapp.issue

import by.issoft.opsapp.dto.Issue
import by.issoft.opsapp.dto.Project
import by.issoft.opsapp.repository.IssueRepository
import by.issoft.opsapp.service.IssueService
import by.issoft.opsapp.service.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceContext
import java.util.concurrent.ThreadLocalRandom

@SpringBootTest
@Transactional
class IssueIntSpec extends Specification {

    @Autowired
    IssueService issueService

    @Autowired
    ProjectService projectService

    @Autowired
    IssueRepository issueRepository

    @PersistenceContext
    EntityManager entityManager

    def "saving the issue"() {
        given:
        def projectId = projectService.saveProject(anyProject())
        flushAndClear()

        and:
        def issue = anyIssue(projectId)

        when:
        def issueId = issueService.saveIssue(issue)
        flushAndClear()

        then:
        issueRepository.count() == old(issueRepository.count()) + 1

        and:
        with(issueRepository.findById(issueId).get()) {
            id >= 0
            description == issue.description
            projectId == issue.projectId
        }
    }

    def "saving the issue with invalid fields"() {
        given:
        def fakeProjectId = Integer.MIN_VALUE

        and:
        def issue = anyIssue(fakeProjectId)

        when:
        issueService.saveIssue(issue)

        then:
        thrown DataIntegrityViolationException
        entityManager.clear()

        and:
        issueRepository.count() == old(issueRepository.count())
    }

    def 'getting the issue'() {
        given:
        def projectId = projectService.saveProject(anyProject())
        flushAndClear()

        and:
        def issue = anyIssue(projectId)
        def issueId = issueService.saveIssue(issue)
        flushAndClear()

        when:
        def issueFromDb = issueService.retrieveIssueById(issueId)

        then:
        with(issueFromDb) {
            id == issueId
            projectId == issue.projectId
            description == issue.description
        }
    }

    def 'getting the issue with invalid id'() {
        given:
        def projectId = projectService.saveProject(anyProject())
        flushAndClear()

        and:
        def issue = anyIssue(projectId)
        issueService.saveIssue(issue)
        flushAndClear()

        and:
        def fakeIssueId = Integer.MIN_VALUE

        when:
        issueService.retrieveIssueById(fakeIssueId)

        then:
        thrown EntityNotFoundException
    }

    def anyIssue(Integer projectId) {
        Issue issue = new Issue()
        issue.setDescription(UUID.randomUUID().toString())
        issue.setProjectId(projectId)
        return issue
    }

    def anyProject() {
        return new Project(
                0,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                ThreadLocalRandom.current().nextInt(100))
    }

    def flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }
}
