package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void retrieveItems() {
        when(itemRepository.findAll()).thenReturn(testList());

        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> allItems = response.getBody();
        assertNotNull(allItems);
        assertEquals(4, allItems.size());
    }

    @Test
    public void retrieveItemsByName_happyPath() {
        when(itemRepository.findByName("Cube")).thenReturn(findByName(testList(), "Cube"));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Cube");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals(BigDecimal.valueOf(3.49), items.get(0).getPrice());
        assertEquals(BigDecimal.valueOf(1.99), items.get(1).getPrice());
    }

    @Test
    public void retrieveItemsByName_badPath() {
        when(itemRepository.findByName("Square")).thenReturn(findByName(testList(), "Square"));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Square");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private List<Item> testList() {
        Item item1 = createItem(5L, "Ball", 4.99, "It's a ball");
        Item item2 = createItem(7L, "Cube", 3.49, "A cube - unlimited fun");
        Item item3 = createItem(53L, "Hexagon", 8.99, "One shape to rule them all");
        Item item4 = createItem(54L, "Cube", 1.99, "Discounted Cube");
        return Arrays.asList(item1, item2, item3, item4);
    }

    private List<Item> findByName(List<Item> items, String name) {
        return items.stream().filter(item -> item.getName().equals(name)).collect(Collectors.toList());
    }

    private Item createItem(Long id, String name, Double price, String description) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(price));
        item.setDescription(description);
        return item;
    }
}
