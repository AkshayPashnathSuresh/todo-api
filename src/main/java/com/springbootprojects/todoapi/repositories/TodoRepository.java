package com.springbootprojects.todoapi.repositories;

import com.springbootprojects.todoapi.model.Todo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends CrudRepository<Todo, Long> {
}
