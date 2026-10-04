package com.example.register;

import com.example.register.dto.CreateUserRequest;
import com.example.register.dto.UpdateUserRequest;
import com.example.register.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ValidPayload_Returns201Created() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Alice Smith", "alice@example.com", 28);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Alice Smith")))
                .andExpect(jsonPath("$.email", is("alice@example.com")))
                .andExpect(jsonPath("$.age", is(28)));
    }

    @Test
    void createUser_DuplicateEmail_Returns400BadRequest() throws Exception {
        CreateUserRequest request1 = new CreateUserRequest("User One", "duplicate@example.com", 25);
        CreateUserRequest request2 = new CreateUserRequest("User Two", "duplicate@example.com", 30);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void createUser_InvalidEmail_Returns400BadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Alice Smith", "invalid-email", 28);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.errors.email", notNullValue()));
    }

    @Test
    void getAllUsers_ReturnsList() throws Exception {
        CreateUserRequest request1 = new CreateUserRequest("User One", "one@example.com", 20);
        CreateUserRequest request2 = new CreateUserRequest("User Two", "two@example.com", 30);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getUserById_ExistingUser_Returns200OK() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Bob", "bob@example.com", 35);
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String userId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is("Bob")))
                .andExpect(jsonPath("$.email", is("bob@example.com")));
    }

    @Test
    void getUserById_NonExistentUser_Returns404NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/users/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void updateUser_ExistingUser_ReturnsUpdatedUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Charlie", "charlie@example.com", 40);
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String userId = objectMapper.readTree(response).get("id").asText();

        UpdateUserRequest updateRequest = new UpdateUserRequest("Charlie Updated", "charlie.updated@example.com", 41);

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is("Charlie Updated")))
                .andExpect(jsonPath("$.email", is("charlie.updated@example.com")))
                .andExpect(jsonPath("$.age", is(41)));
    }

    @Test
    void deleteUser_ExistingUser_Returns204NoContent() throws Exception {
        CreateUserRequest request = new CreateUserRequest("David", "david@example.com", 25);
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String userId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }
}
