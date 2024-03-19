package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BNodeTest {
@Test
    public void testNodeFull() {
        BNode<Integer> node = new BNode<>(3);
        assertFalse(node.nodeFull());
        
        node.keys.add(1);
        node.keys.add(2);
        node.keys.add(3);
        assertTrue(node.nodeFull());
    }

    @Test
    public void testNodeEmpty() {
        BNode<Integer> node = new BNode<>(3);
        assertTrue(node.nodeEmpty());
        
        node.keys.add(1);
        assertFalse(node.nodeEmpty());
    }

    @Test
    public void testSearchNode() {
        BNode<Integer> node = new BNode<>(3);
        node.keys.add(1);
        node.keys.add(3);
        node.count = 2;

        int[] posResult = new int[1];
        assertFalse(node.searchNode(2, posResult));
        assertEquals(1, posResult[0]);

        assertTrue(node.searchNode(1, posResult));
        assertEquals(0, posResult[0]);
    }

    @Test
    public void testIsLeaf() {
        BNode<Integer> node = new BNode<Integer>(3);
        assertTrue(node.isLeaf());

        node.childs.set(0, new BNode<Integer>(3));
        assertFalse(node.isLeaf());
    }
  
}
