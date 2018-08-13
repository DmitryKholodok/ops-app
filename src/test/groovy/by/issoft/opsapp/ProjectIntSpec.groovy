package by.issoft.opsapp

import by.issoft.opsapp.dto.Project
import by.issoft.opsapp.repository.ProjectRepository
import by.issoft.opsapp.service.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityExistsException
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

import static groovy.json.JsonOutput.toJson
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProjectIntSpec extends Specification {

    @Autowired
    ProjectService projectService

    @Autowired
    ProjectRepository projectRepository

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    MockMvc mockMvc

    def "it should save the project, and after check count of rows and matching of fields"() {
        given:
            def project = retrieveProject()

        when:
            int projectId = projectService.createProject(project)
            flushAndClear()

        then:
            projectRepository.count() == old(projectRepository.count()) + 1
            with(projectRepository.findById(projectId).get()) {
                id >= 0
                name == project.name
                alternativeName == project.alternativeName
                peopleCount == project.peopleCount
            }
    }

    def "it should check a response after saving the project"() {
        given: "This regex should verify header 'Content-Location' in the response"
            def regex = /\/projects\/[1-9]{1}[0-9]*/

        when:
            def response = mockMvc.perform(post('/projects')
                    .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(retrieveProject()))).andReturn().response

        then:
            with(response) {
                status == HttpStatus.CREATED.value()
                getHeader("Content-Location") ==~ regex
            }
    }

    def "it should throw EntityExistsException while saving the project"() {
        given:
            def project = retrieveProject()

        when:
            projectService.createProject(project)
            flushAndClear()
            projectService.createProject(project)

        then:
            thrown EntityExistsException
    }

    def retrieveProject() {
        return new Project(0, "projectName", "alternativeName", 10)
    }

    def flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

}
