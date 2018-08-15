package by.issoft.opsapp.project

import by.issoft.opsapp.dto.Project
import by.issoft.opsapp.exception.InvalidEntityException
import by.issoft.opsapp.repository.ProjectRepository
import by.issoft.opsapp.service.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceContext
import java.util.concurrent.ThreadLocalRandom

@SpringBootTest
@Transactional
class ProjectIntSpec extends Specification {

    @Autowired
    ProjectService projectService

    @Autowired
    ProjectRepository projectRepository

    @PersistenceContext
    EntityManager entityManager

    def "saving the project"() {
        when:
            int projectId = projectService.saveProject(project)
            flushAndClear()

        then:
        projectRepository.count() == old(projectRepository.count()) + 1

        and:
        with(projectRepository.findById(projectId).get()) {
                id >= 0
                name == project.name
                alternativeName == project.alternativeName
                peopleCount == project.peopleCount
            }

        where:
        [project]  << [
                 [new Project(null, "projectName", "altName", 10),
                  new Project(null, "projectName", null, 10)]
        ]

    }

    def "throws InvalidEntityException while saving the project"() {
        given:
            def project = anyProject()

        when: "throws exception "
            projectService.saveProject(project)
            flushAndClear()
            projectService.saveProject(project)

        then:
            thrown InvalidEntityException
    }

    def "getting the project by id"() {
        given:
            def project = anyProject()
            int projectId = projectService.saveProject(project)
            flushAndClear()

        when:
            def projectInDb = projectService.retrieveProjectById(projectId)

        then:
            with(projectInDb) {
                id == projectId
                name == project.name
                alternativeName == project.alternativeName
                peopleCount == project.peopleCount
            }
    }

    def "throws EntityNotFoundException while getting the project"() {
        given:
            int fakeId = Integer.MIN_VALUE

        when:
            projectService.retrieveProjectById(fakeId)

        then:
            thrown EntityNotFoundException
    }

    def 'updating the project'() {
        given:
            int projectId = projectService.saveProject(project)
            flushAndClear()

        when:
            projectService.updateProject(newProject, projectId)
            flushAndClear()

        then:
            with(projectRepository.findById(projectId).get()) {
                id == projectId
                name == newProject.name
                alternativeName == newProject.alternativeName
                peopleCount == newProject.peopleCount
            }

        where:
            [project, newProject]  << [
                    [new Project(null, "projectName", "altName", 10),
                     new Project(null, "testProjectName", "altName", 10)],

                    [new Project(null, "projectName", "altName", 10),
                     new Project(null, "projectName", null, 10)],

                    [new Project(null, "projectName", "altName", 10),
                     new Project(null, "projectName", "altName", 15)],
            ]
    }

    def 'throws EntityNotFoundException while updating the project'() {
        given:
        int fakeId = Integer.MIN_VALUE

        when:
        projectService.updateProject(anyProject(), fakeId)

        then:
        thrown EntityNotFoundException
    }

    def 'throws InvalidEntityException while updating the project'() {
        given:
        def projectOne = anyProject()
        projectService.saveProject(projectOne)
        flushAndClear()

        and:
        def projectTwo = anyProject()
        def projectId = projectService.saveProject(projectTwo)
        flushAndClear()

        when:
        projectTwo.setName(projectOne.getName())
        projectService.updateProject(projectTwo, projectId)

        then:
        thrown InvalidEntityException
    }

    def 'deleting the project'() {
        given:
        def project = anyProject()
        def projectId = projectService.saveProject(project)
        flushAndClear()

        when:
        projectService.deleteProjectById(projectId)
        flushAndClear()

        then:
        projectRepository.count() == old(projectRepository.count()) - 1
    }

    def 'throws EntityNotFoundException while deleting the project'() {
        given:
        def project = anyProject()
        projectService.saveProject(project)
        flushAndClear()

        and:
        def fakeId = Integer.MIN_VALUE

        when:
        projectService.deleteProjectById(fakeId)
        flushAndClear()

        then:
        thrown EntityNotFoundException

        and:
        projectRepository.count() == old(projectRepository.count())
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
