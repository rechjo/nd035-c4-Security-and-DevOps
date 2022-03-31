package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addToCart_happyPath() {
        when(userRepository.findByUsername("name")).thenReturn(sampleUserWithTwoSampleItemsInCart());
        when(itemRepository.findById(4L)).thenReturn(sampleItem());

        ModifyCartRequest request = newModifyCartRequest("name", 4L, 3);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals("name", cart.getUser().getUsername());
        assertEquals(BigDecimal.valueOf(12.50), cart.getTotal());
    }

    @Test
    public void addToCart_badPath() {
        when(userRepository.findByUsername("name")).thenReturn(sampleUserWithTwoSampleItemsInCart());
        when(itemRepository.findById(3L)).thenReturn(Optional.empty());

        ModifyCartRequest request = newModifyCartRequest("name", 3L, 3);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCart_happyPath() {
        when(userRepository.findByUsername("name")).thenReturn(sampleUserWithTwoSampleItemsInCart());
        when(itemRepository.findById(4L)).thenReturn(sampleItem());

        ModifyCartRequest request = newModifyCartRequest("name", 4L, 1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals("name", cart.getUser().getUsername());
        assertEquals(BigDecimal.valueOf(2.50), cart.getTotal());
    }

    @Test
    public void removeFromCart_badPath() {
        when(userRepository.findByUsername("wrongName")).thenReturn(null);
        when(itemRepository.findById(4L)).thenReturn(sampleItem());

        ModifyCartRequest request = newModifyCartRequest("wrongName", 4L, 1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private ModifyCartRequest newModifyCartRequest(String user, long itemId, int quantity) {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user);
        request.setItemId(itemId);
        request.setQuantity(quantity);
        return request;
    }

    private User sampleUserWithTwoSampleItemsInCart() {
        User user = new User();
        user.setUsername("name");
        Cart cart = new Cart();
        cart.addItem(sampleItem().get());
        cart.addItem(sampleItem().get());
        cart.setUser(user);
        user.setCart(cart);
        return user;
    }

    private Optional<Item> sampleItem() {
        Item item = new Item();
        item.setId(4L);
        item.setPrice(BigDecimal.valueOf(2.50));
        return Optional.of(item);
    }
}
