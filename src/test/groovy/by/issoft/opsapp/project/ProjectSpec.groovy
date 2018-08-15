package by.issoft.opsapp.project

import by.issoft.opsapp.dto.Project
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

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.util.concurrent.ThreadLocalRandom

import static groovy.json.JsonOutput.toJson
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProjectSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    ProjectService projectService

    def "saving the project with invalid fields"() {
        given:
        def project = new Project(id, projectName, alternativeName, peopleCount)

        when:
        def response = mockMvc.perform(post('/projects')
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(project))).andReturn().response

        then:
        response.status == HttpStatus.BAD_REQUEST.value()

        where:
        id << [null, null, null]
        projectName << [null, "projectName", "projectName"]
        alternativeName << ["altName", "altName", null]
        peopleCount << [10, -15, null]
    }

    def "saving the project"() {
        given: "This regex should verify header 'Content-Location' in the response"
        def regex = /\/projects\/[1-9]{1}[0-9]*/

        when:
        def response = mockMvc.perform(post('/projects')
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(anyProject()))).andReturn().response

        then:
        with(response) {
            status == HttpStatus.CREATED.value()
            getHeader("Location") ==~ regex
        }
    }

    def "getting the project"() {
        given:
        def project = anyProject()
        int projectId = projectService.saveProject(project)
        flushAndClear()

        when:
        def response = mockMvc.perform(get('/projects/' + projectId)).andReturn().getResponse()

        then:
        response.status == HttpStatus.OK.value()

        and:
        def projectFromJson = retrieveProjectFromJson(response.getContentAsString())
        projectFromJson.id == projectId
        projectFromJson.peopleCount == project.peopleCount
        projectFromJson.name == project.name
        projectFromJson.alternativeName == project.alternativeName
    }

    def "incorrect id for getting the project"() {
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

    def 'updating the project'() {
        given:
        int projectId = projectService.saveProject(project)
        flushAndClear()

        when:
        project.setName(UUID.randomUUID().toString())

        and:
        def response = mockMvc.perform(put('/projects/' + projectId)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(project))).andReturn().getResponse()

        then:
        response.status == HttpStatus.OK.value()

        where:
        [project]  << [
                [new Project(null, "projectName", "altName", 10)],
                [new Project(null, "projectName", null, 10)]
        ]
    }

    def 'updating the project with invalid fields'() {
        given:
        int projectId = projectService.saveProject(project)
        flushAndClear()

        when:
        def response = mockMvc.perform(put('/projects/' + projectId)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(updatedProj))).andReturn().getResponse()

        then:
        response.status == HttpStatus.BAD_REQUEST.value()

        where:
            [project, updatedProj]  << [
                    [new Project(null, "projectName", "altName", 10),
                     new Project(null, null, "altName", 10)],

                    [new Project(null, "projectName", "altName", 10),
                     new Project(null, "projectName", null, null)],

                    [new Project(null, "projectName", "altName", 10),
                     new Project(null, "projectName", "altName", -15)],
            ]
    }

    def flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

    def anyProject() {
        return new Project(
                0,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                ThreadLocalRandom.current().nextInt(100))
    }

    def retrieveProjectFromJson(String json) {
        def mapper = new ObjectMapper()
        return mapper.readValue(json, Project.class)
    }

}
