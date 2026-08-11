package com.example.registration.controller;

import com.example.registration.dto.UserRegistrationRequest;
import com.example.registration.dto.UserRegistrationResponse;
import com.example.registration.exception.DuplicateResourceException;
import com.example.registration.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_Success() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("Asha Rao");
        request.setEmail("asha@example.com");
        request.setPassword("StrongPass123!");

        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setId(1);
        response.setFullName("Asha Rao");
        response.setEmail("asha@example.com");

        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Asha Rao"));
    }



    @Test
    void register_DuplicateResource() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("Asha Rao");
        request.setEmail("asha@example.com");
        request.setPassword("StrongPass123!");

        when(userService.registerUser(any(UserRegistrationRequest.class)))
                .thenThrow(new DuplicateResourceException("Email is already in use"));

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email is already in use"));
    }
}
