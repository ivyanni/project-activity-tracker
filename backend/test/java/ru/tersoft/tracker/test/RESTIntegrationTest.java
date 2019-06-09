package ru.tersoft.tracker.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.tersoft.tracker.Application;
import ru.tersoft.tracker.entity.*;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Ilia Vianni
 * Created on 09.05.2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class RESTIntegrationTest {
    @Autowired
    private MockMvc mvc;
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        mapper.setDateFormat(df);
    }

    private Project createProject() throws Exception {
        MvcResult result = mvc.perform(post("/api/projects/")
                .param("name", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readValue(result.getResponse().getContentAsByteArray(), Project.class);
    }

    private Layer createLayer(Project project) throws Exception {
        Layer layer = new Layer();
        layer.setName("Test");
        layer.setFinishDate(LocalDate.now());
        layer.setProjectId(project.getId());
        MvcResult result = mvc.perform(post("/api/layers/")
                .content(mapper.writeValueAsBytes(layer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readValue(result.getResponse().getContentAsByteArray(), Layer.class);
    }

    private Stream createStream(Layer layer) throws Exception {
        Stream stream = new Stream();
        stream.setName("TestStream");
        stream.setOrder(0);
        stream.setLayerId(layer.getId());
        MvcResult result = mvc.perform(post("/api/streams/")
                .content(mapper.writeValueAsBytes(stream))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readValue(result.getResponse().getContentAsByteArray(), Stream.class);
    }

    private Phase createPhase(Layer layer) throws Exception {
        Phase phase = new Phase();
        phase.setName("TestPhase");
        phase.setColor("#000000");
        phase.setStartDate(LocalDate.now());
        phase.setEndDate(LocalDate.now());
        phase.setLayerId(layer.getId());
        MvcResult result = mvc.perform(post("/api/phases/")
                .content(mapper.writeValueAsBytes(phase))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readValue(result.getResponse().getContentAsByteArray(), Phase.class);
    }

    @Test
    @Transactional
    @Rollback
    public void testProjectApi() throws Exception {
        Project project = createProject();
        mvc.perform(get("/api/projects/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test")));
        mvc.perform(get("/api/projects/" + project.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Test")))
                .andReturn();
        project.setName("Test2");
        mvc.perform(post("/api/projects/" + project.getId())
                .content(mapper.writeValueAsBytes(project))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Test2")));
        mvc.perform(delete("/api/projects/" + project.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @Rollback
    public void testLayerApi() throws Exception {
        Project project = createProject();
        Layer layer = createLayer(project);
        mvc.perform(get("/api/layers/" + layer.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Test")))
                .andReturn();
        layer.setName("Test2");
        mvc.perform(post("/api/layers/" + layer.getId())
                .content(mapper.writeValueAsBytes(layer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Test2")));
        mvc.perform(delete("/api/layers/" + layer.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @Rollback
    public void testPhaseApi() throws Exception {
        Project project = createProject();
        Layer layer = createLayer(project);
        Phase phase = createPhase(layer);
        mvc.perform(get("/api/phases/" + phase.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("TestPhase")))
                .andReturn();
        phase.setName("TestPhase2");
        mvc.perform(post("/api/phases/" + phase.getId())
                .content(mapper.writeValueAsBytes(phase))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("TestPhase2")));
        mvc.perform(delete("/api/phases/" + phase.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @Rollback
    public void testStreamApi() throws Exception {
        Project project = createProject();
        Layer layer = createLayer(project);
        Stream stream = createStream(layer);
        mvc.perform(get("/api/streams/" + stream.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("TestStream")))
                .andReturn();
        stream.setName("TestStream2");
        mvc.perform(post("/api/streams/" + stream.getId())
                .content(mapper.writeValueAsBytes(stream))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("TestStream2")));
        mvc.perform(delete("/api/streams/" + stream.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @Rollback
    public void testEventApi() throws Exception {
        Project project = createProject();
        Layer layer = createLayer(project);
        Stream stream = createStream(layer);
        Phase phase = createPhase(layer);
        Event event = new Event();
        event.setName("TestEvent");
        event.setPhase(phase);
        event.setLayerId(layer.getId());
        event.setCompletionDate(LocalDate.now());
        event.setDescription("Desc");
        event.setDeliverable(true);
        event.setStream(stream);
        MvcResult result = mvc.perform(post("/api/events/")
                .content(mapper.writeValueAsBytes(event))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        event = mapper.readValue(result.getResponse().getContentAsByteArray(), Event.class);
        mvc.perform(get("/api/events/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("TestEvent")))
                .andReturn();
        event.setName("TestEvent2");
        mvc.perform(post("/api/events/" + event.getId())
                .content(mapper.writeValueAsBytes(event))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("TestEvent2")));
        mvc.perform(delete("/api/events/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
