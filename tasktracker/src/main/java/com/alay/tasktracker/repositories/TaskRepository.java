package com.alay.tasktracker.repositories;

import com.alay.tasktracker.entities.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
}
