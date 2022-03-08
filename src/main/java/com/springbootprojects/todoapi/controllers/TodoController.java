package com.springbootprojects.todoapi.controllers;

import com.springbootprojects.todoapi.model.Todo;
import com.springbootprojects.todoapi.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
public class TodoController {
    private TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return new ResponseEntity<>(todoService.getTodos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodo(@PathVariable Long id) {
        Todo todo = todoService.getTodoById(id);
        return todo != null ? new ResponseEntity<>(todo, HttpStatus.OK)
                            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Todo> saveTodo(@RequestBody Todo todo) {
        Todo newTodo = todoService.insertTodo(todo);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("todo","/api/v1/todo/" + newTodo.getId().toString());
        return new ResponseEntity<>(newTodo, httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todo) {
        Todo todoFromDb = todoService.getTodoById(id);
        if(todoFromDb != null) {
            todoService.updateTodo(id, todo);
        }
        return todoFromDb != null ? new ResponseEntity<>(todoService.getTodoById(id), HttpStatus.OK)
                                  : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Todo> deleteTodo(@PathVariable Long id) {
        Todo todoFromDb = todoService.getTodoById(id);
        if(todoFromDb != null) {
            todoService.deleteTodo(id);
        }
        return todoFromDb != null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                                  : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
