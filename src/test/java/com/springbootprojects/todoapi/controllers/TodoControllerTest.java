package com.springbootprojects.todoapi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootprojects.todoapi.model.Todo;
import com.springbootprojects.todoapi.model.TodoStatus;
import com.springbootprojects.todoapi.services.TodoService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    TodoService todoService;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    Todo RECORD_1 = new Todo(1L, "Spring Boot", "Study Spring Boot",
            TodoStatus.NOT_COMPLETED, timestamp, timestamp);
    Todo RECORD_2 = new Todo(2L, "Junit", "Study Junit and write test",
            TodoStatus.COMPLETED, timestamp, timestamp);
    Todo RECORD_3 = new Todo(3L, "Deploy", "Deploy the Api to the server",
            TodoStatus.NOT_COMPLETED, timestamp, timestamp);

    @Test
    void getAllTodos() throws Exception {
        List<Todo> records = new ArrayList<>(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));
        Mockito.when(todoService.getTodos()).thenReturn(records);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/todo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].title", Matchers.is("Deploy")));
    }

    @Test
    void getTodoSuccess() throws Exception {
        Mockito.when(todoService.getTodoById(RECORD_1.getId())).thenReturn(RECORD_1);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/todo/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Spring Boot")));
    }

    @Test
    void getTodoFailure() throws Exception {
        Mockito.when(todoService.getTodoById(5L)).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/todo/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    void saveTodo() throws Exception {
        Todo record = Todo.builder()
                .id(5L)
                .title("Haskell")
                .description("Study Haskell and practice it using a project")
                .todoStatus(TodoStatus.NOT_COMPLETED)
                .dateCreated(timestamp)
                .lastModified(timestamp)
                .build();
        Mockito.when(todoService.insertTodo(record)).thenReturn(record);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/v1/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(record));
        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Haskell")));
    }

    @Test
    void updateTodoSuccess() throws Exception {
        Todo updatedRecord = Todo.builder()
                .id(1L)
                .title("Spring Boot")
                .description("Study Spring Boot")
                .todoStatus(TodoStatus.COMPLETED)
                .dateCreated(timestamp)
                .lastModified(timestamp)
                .build();
        Mockito.when(todoService.getTodoById(RECORD_1.getId())).thenReturn(RECORD_1).thenReturn(updatedRecord);
        Mockito.when(todoService.updateTodo(updatedRecord.getId(), updatedRecord)).thenReturn(updatedRecord);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/v1/todo/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedRecord));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.todoStatus", Matchers.is(Matchers.is("COMPLETED"))));
    }

    @Test
    void updateTodoFailure() throws Exception {
        Todo updatedRecord = Todo.builder()
                .id(5L)
                .title("Spring Boot")
                .description("Study Spring Boot")
                .todoStatus(TodoStatus.COMPLETED)
                .dateCreated(timestamp)
                .lastModified(timestamp)
                .build();
        Mockito.when(todoService.getTodoById(5L)).thenReturn(null);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/v1/todo/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedRecord));
        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }

    @Test
    void deleteTodoSuccess() throws Exception {
        Mockito.when(todoService.getTodoById(RECORD_1.getId())).thenReturn(RECORD_1);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/todo/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTodoFailure() throws Exception {
        Mockito.when(todoService.getTodoById(RECORD_1.getId())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/todo/1"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());
    }
}