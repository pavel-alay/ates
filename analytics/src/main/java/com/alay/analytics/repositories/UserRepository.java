package com.alay.analytics.repositories;

import com.alay.analytics.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPublicId(String publicId);

    default User findOrCreate(String publicId) {
        return findByPublicId(publicId)
            .orElseGet(() -> saveAndFlush(User.builder()
                .publicId(publicId).build()));
    }

    default User updateOrCreate(String publicId, String username) {
        return findByPublicId(publicId)
            .map(value -> saveAndFlush(value
                .setUsername(username)))
            .orElseGet(() -> saveAndFlush(User.builder()
                .publicId(publicId).username(username).build()));
    }

    default void updateBalance(String publicId, long balance) {
        save(findOrCreate(publicId).setBalance(balance));
    }
}
