package com.CS22S4.hehe.entity;

import java.util.Stack;

public class EntityStack<T> {
    private Stack<T> stack;

    public EntityStack() {
        stack = new Stack<>();
    }

    // Push an entity onto the stack based on the added amount
    public void pushEntity(T entity) {
        stack.push(entity);
    }

    // Pop an entity from the stack
    public T popEntity() {
        if (!stack.isEmpty()) {
            return stack.pop();
        }
        return null; // Return null if the stack is empty
    }

    // Peek at the top entity without removing it
    public T peekEntity() {
        if (!stack.isEmpty()) {
            return stack.peek();
        }
        return null;
    }

    // Check if the stack is empty
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    // Get the size of the stack
    public int getSize() {
        return stack.size();
    }
}
