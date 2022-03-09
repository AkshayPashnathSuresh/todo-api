package com.springbootprojects.todoapi.services;

import com.springbootprojects.todoapi.model.Todo;

import java.util.List;

public interface TodoService {
    List<Todo> getTodos();
    Todo getTodoById(Long id);
    Todo insertTodo(Todo todo);
    Todo updateTodo(Long id, Todo todo);
    void deleteTodo(Long id);
}
