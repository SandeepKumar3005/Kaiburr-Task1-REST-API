package com.kaiburr.Task_Manager.repository;

import com.kaiburr.Task_Manager.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByNameContaining(String name);
}