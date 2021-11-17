package com.alay.tasktracker.repositories;

import com.alay.tasktracker.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query(value = "SELECT * FROM \"user\" WHERE role = 'USER' ORDER BY random() LIMIT 1",
            nativeQuery = true)
    Optional<User> findRandomUser();

    @Query(value = "SELECT * FROM \"user\" WHERE role = 'USER' ORDER BY random()",
            nativeQuery = true)
    List<User> findRandomUsers();
}
