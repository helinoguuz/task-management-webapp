package com.example.demo.service;

import com.example.demo.dao.IPasswordHasher;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;


import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private DatabaseManager databaseManager;
    private EmailVerificationService emailVerificationService;
    private IPasswordHasher passwordHasher;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        databaseManager = mock(DatabaseManager.class);
        emailVerificationService = mock(EmailVerificationService.class);
        passwordHasher = mock(IPasswordHasher.class);

        accountService = new AccountService(databaseManager, emailVerificationService, passwordHasher);
    }

    @Test
    void testRegisterUser_successful() {
        when(databaseManager.checkEmailExists("john@example.com")).thenReturn(false);
        when(passwordHasher.hashPassword("123456")).thenReturn("hashed123");
        when(databaseManager.storeUser(any(User.class))).thenReturn(true);

        boolean result = accountService.registerUser("John", "john@example.com", "123456");

        assertTrue(result);
        verify(emailVerificationService).sendVerificationEmail(any(User.class));
    }

    @Test
    void testRegisterUser_emailExists() {
        when(databaseManager.checkEmailExists("john@example.com")).thenReturn(true);

        boolean result = accountService.registerUser("John", "john@example.com", "123456");

        assertFalse(result);
        verify(emailVerificationService, never()).sendVerificationEmail(any());
    }

    @Test
    void testLogin_correctPassword() {
        User user = User.builder()
                .email("jane@example.com")
                .password("hashedpass")
                .build();

        when(databaseManager.getUserByEmail("jane@example.com")).thenReturn(user);
        when(passwordHasher.verifyPassword("mypassword", "hashedpass")).thenReturn(true);

        boolean result = accountService.login("jane@example.com", "mypassword");

        assertTrue(result);
    }

    @Test
    void testLogin_wrongPassword() {
        User user = User.builder()
                .email("jane@example.com")
                .password("hashedpass")
                .build();

        when(databaseManager.getUserByEmail("jane@example.com")).thenReturn(user);
        when(passwordHasher.verifyPassword("wrongpass", "hashedpass")).thenReturn(false);

        boolean result = accountService.login("jane@example.com", "wrongpass");

        assertFalse(result);
    }

    @Test
    void testChangePassword_successful() {
        User user = User.builder()
                .email("alex@example.com")
                .password("oldHashed")
                .build();

        when(databaseManager.getUserByEmail("alex@example.com")).thenReturn(user);
        when(passwordHasher.verifyPassword("oldPass", "oldHashed")).thenReturn(true);
        when(passwordHasher.hashPassword("newPass")).thenReturn("newHashed");
        when(databaseManager.updateUser(user)).thenReturn(true);

        boolean result = accountService.changePassword("alex@example.com", "oldPass", "newPass");

        assertTrue(result);
        assertEquals("newHashed", user.getPassword());
    }

    @Test
    void testChangePassword_wrongOldPassword() {
        User user = User.builder()
                .email("alex@example.com")
                .password("oldHashed")
                .build();

        when(databaseManager.getUserByEmail("alex@example.com")).thenReturn(user);
        when(passwordHasher.verifyPassword("wrongOld", "oldHashed")).thenReturn(false);

        boolean result = accountService.changePassword("alex@example.com", "wrongOld", "newPass");

        assertFalse(result);
    }

    @Test
    void testUpdateProfile_successful() {
        User user = new User();
        user.setEmail("maria@example.com");
        user.setName("Maria");

        when(databaseManager.getUserByEmail("maria@example.com")).thenReturn(user);
        when(databaseManager.updateUser(user)).thenReturn(true);

        boolean result = accountService.updateProfile("maria@example.com", "Maria Smith", "maria.smith@example.com");

        assertTrue(result);
        assertEquals("Maria Smith", user.getName());
        assertEquals("maria.smith@example.com", user.getEmail());
    }
    @Test
    void testEmailExists_true() {
        when(databaseManager.checkEmailExists("john@example.com")).thenReturn(true);

        boolean result = accountService.emailExists("john@example.com");

        assertTrue(result);
    }

    @Test
    void testEmailExists_false() {
        when(databaseManager.checkEmailExists("john@example.com")).thenReturn(false);

        boolean result = accountService.emailExists("john@example.com");

        assertFalse(result);
    }

    @Test
    void testGetUserByEmail_found() {
        User user = User.builder()
                .email("jane@example.com")
                .name("Jane")
                .build();

        when(databaseManager.getUserByEmail("jane@example.com")).thenReturn(user);

        User result = accountService.getUserByEmail("jane@example.com");

        assertNotNull(result);
        assertEquals("Jane", result.getName());
    }

    @Test
    void testGetUserByEmail_notFound() {
        when(databaseManager.getUserByEmail("jane@example.com")).thenReturn(null);

        User result = accountService.getUserByEmail("jane@example.com");

        assertNull(result);
    }

    @Test
    void testUpdatePassword_successful() {
        User user = User.builder()
                .email("alex@example.com")
                .build();

        when(databaseManager.getUserByEmail("alex@example.com")).thenReturn(user);
        when(passwordHasher.hashPassword("newPassword")).thenReturn("hashedNewPassword");

        accountService.updatePassword("alex@example.com", "newPassword");

        assertEquals("hashedNewPassword", user.getPassword());
        verify(databaseManager).storeUser(user);
    }

    @Test
    void testUpdatePassword_userNotFound() {
        when(databaseManager.getUserByEmail("nonexistent@example.com")).thenReturn(null);

        accountService.updatePassword("nonexistent@example.com", "irrelevant");

        verify(databaseManager, never()).storeUser(any());
    }
    @Test
    void testFindUsersByName_returnsMatchingUsers() {
        User user1 = User.builder().name("Alice").build();
        User user2 = User.builder().name("Alicia").build();

        when(databaseManager.findByName("Ali")).thenReturn(List.of(user1, user2));

        List<User> result = accountService.findUsersByName("Ali");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> u.getName().toLowerCase().contains("ali")));
    }


}
