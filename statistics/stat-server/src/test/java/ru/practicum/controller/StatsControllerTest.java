package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.EndpointHit;
import ru.practicum.service.StatsServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
class StatsControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StatsServiceImpl statsService;

    @Test
    @SneakyThrows
    void post_whenCorrect_thenReturn201() {
       EndpointHit incomeDtoToCreate = new EndpointHit("ewm-main-service",
               "/events/1",
               "192.163.0.1",
               "2022-09-06 11:00:23");

       String result = mockMvc.perform(post("/hit")
                       .contentType("application/json")
                       .content(objectMapper.writeValueAsString(incomeDtoToCreate)))
               .andExpect(status().isCreated())
               .andReturn()
               .getResponse()
               .getContentAsString();
}

//    @Test
//    @SneakyThrows
//    void get_whenCorrect_thenReturn200() {
//        mockMvc.perform(get("/stats"))
//                .andExpect(status().isOk());
//    }

}