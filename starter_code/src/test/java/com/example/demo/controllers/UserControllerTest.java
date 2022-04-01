package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUser_happyPath() {
        when(encoder.encode("testPassword")).thenReturn("hashedPassword");

        CreateUserRequest request = newCreateUserRequest("testUser", "testPassword", "testPassword");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void createUser_badPath_shortPassword() {
        CreateUserRequest request = newCreateUserRequest("test", "pw", "pw");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUser_badPath_differentPasswords() {
        CreateUserRequest request = newCreateUserRequest("test", "onePassword", "anotherPassword");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUser_badPath_usernameTaken() {
        when(userRepository.findByUsername("test")).thenReturn(new User());

        CreateUserRequest request = newCreateUserRequest("test", "testPassword", "testPassword");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    private CreateUserRequest newCreateUserRequest(String name, String password, String confirmPassword) {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(name);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        return request;
    }
}
