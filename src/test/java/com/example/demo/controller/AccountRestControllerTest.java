package com.example.demo.controller;

import com.example.demo.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void register_successful_returnsCreated() throws Exception {
        when(accountService.registerUser("John", "john@example.com", "123456")).thenReturn(true);

        mockMvc.perform(post("/api/account/register")
                        .param("name", "John")
                        .param("email", "john@example.com")
                        .param("password", "123456"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration successful"));
    }

    @Test
    void register_existingEmail_returnsBadRequest() throws Exception {
        when(accountService.registerUser("John", "john@example.com", "123456")).thenReturn(false);

        mockMvc.perform(post("/api/account/register")
                        .param("name", "John")
                        .param("email", "john@example.com")
                        .param("password", "123456"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    void login_successful_returnsOk() throws Exception {
        when(accountService.login("jane@example.com", "mypassword")).thenReturn(true);

        mockMvc.perform(post("/api/account/login")
                        .param("email", "jane@example.com")
                        .param("password", "mypassword"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    void login_invalidCredentials_returnsUnauthorized() throws Exception {
        when(accountService.login("jane@example.com", "wrongpassword")).thenReturn(false);

        mockMvc.perform(post("/api/account/login")
                        .param("email", "jane@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void changePassword_successful_returnsOk() throws Exception {
        when(accountService.changePassword("alex@example.com", "oldPass", "newPass")).thenReturn(true);

        mockMvc.perform(post("/api/account/changePassword")
                        .param("email", "alex@example.com")
                        .param("oldPassword", "oldPass")
                        .param("newPassword", "newPass"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));
    }

    @Test
    void changePassword_wrongOldPassword_returnsBadRequest() throws Exception {
        when(accountService.changePassword("alex@example.com", "wrongOld", "newPass")).thenReturn(false);

        mockMvc.perform(post("/api/account/changePassword")
                        .param("email", "alex@example.com")
                        .param("oldPassword", "wrongOld")
                        .param("newPassword", "newPass"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Incorrect old password"));
    }
}
