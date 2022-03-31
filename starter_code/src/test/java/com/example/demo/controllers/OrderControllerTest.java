package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitOrder() {
        when(userRepository.findByUsername("name")).thenReturn(sampleUserWithTwoSampleItemsInCart());

        ResponseEntity<UserOrder> response = orderController.submit("name");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals("name", order.getUser().getUsername());
        assertEquals(BigDecimal.valueOf(5.00), order.getTotal());
    }

    @Test
    public void getHistory() {
        when(userRepository.findByUsername("name")).thenReturn(sampleUserWithTwoSampleItemsInCart());

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("name");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

    }

    private User sampleUserWithTwoSampleItemsInCart() {
        User user = new User();
        user.setUsername("name");
        Cart cart = new Cart();
        cart.addItem(sampleItem());
        cart.addItem(sampleItem());
        cart.setUser(user);
        user.setCart(cart);
        return user;
    }

    private Item sampleItem() {
        Item item = new Item();
        item.setId(4L);
        item.setPrice(BigDecimal.valueOf(2.50));
        return item;
    }
}
