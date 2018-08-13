package by.issoft.opsapp

import by.issoft.opsapp.dto.Project
import by.issoft.opsapp.repository.ProjectRepository
import by.issoft.opsapp.service.ProjectService
import com.fasterxml.jackson.databind.ObjectMapper
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
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceContext

import static groovy.json.JsonOutput.toJson
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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
            def project = new Project(id, projectName, alternativeName, peopleCount)

        when:
            int projectId = projectService.saveProject(project)
            flushAndClear()

        then:
            projectRepository.count() == old(projectRepository.count()) + 1
            with(projectRepository.findById(projectId).get()) {
                id >= 0
                name == project.name
                alternativeName == project.alternativeName
                peopleCount == project.peopleCount
            }

        where:
            id << [0, 0]
            projectName << ["projectName", "projectName"]
            alternativeName << ["altName", null]
            peopleCount << [10, 15]

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

    def "it should throw EntityExistsException while saving the project when project's name already exists in the db"() {
        given:
            def project = retrieveProject()

        when: "throws exception "
            projectService.saveProject(project)
            flushAndClear()
            projectService.saveProject(project)

        then:
            thrown EntityExistsException
    }

    def "it should return the project by id"() {
        given:
            def project = retrieveProject()
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

    def "it should throw EntityNotFoundException when project's id does not exists in the db"() {
        given:
            int fakeId = Integer.MIN_VALUE

        when:
            projectService.retrieveProjectById(fakeId)

        then:
            thrown EntityNotFoundException
    }

    def "it should check response body after getting the project"() {
        given:
            def project = retrieveProject()
            int projectId = projectService.saveProject(project)
            project.setId(projectId)
            flushAndClear()

        when:
            def response = mockMvc.perform(get('/projects/' + projectId)).andReturn().getResponse()

        then:
            with(response) {
                status == HttpStatus.OK.value()
                project == retrieveProjectFromJson(getContentAsString())
            }
    }

    def "it should check response status of the incorrect get request"() {
        given:
            int fakeId = Integer.MIN_VALUE

        when:
            def response = mockMvc.perform(get('/projects/' + fakeId)).andReturn().getResponse()

        then:
            with(response) {
                status == HttpStatus.NOT_FOUND.value()
                !getContentAsString().empty
            }
    }

    def retrieveProject() {
        return new Project(0, "projectName", "altName", 10)
    }

    def flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

    def retrieveProjectFromJson(String json) {
        def mapper = new ObjectMapper()
        return mapper.readValue(json, Project.class)
    }

}
