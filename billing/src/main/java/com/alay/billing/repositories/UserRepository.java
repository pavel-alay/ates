package com.alay.billing.repositories;

import com.alay.billing.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPublicId(String publicId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default User debitBalance(User user, long debit) {
        return saveAndFlush(
            user.setBalance(
                user.getBalance() - debit
            )
        );
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default User creditBalance(User user, long credit) {
        return saveAndFlush(
            user.setBalance(
                user.getBalance() + credit
            )
        );
    }

    default User findOrCreateUser(String publicId) {
        return findByPublicId(publicId)
            .orElseGet(() -> saveAndFlush(User.builder()
                .publicId(publicId).build()));
    }

    default User updateOrCreateUser(String publicId, String username) {
        return findByPublicId(publicId)
            .map(value -> saveAndFlush(value
                .setUsername(username)))
            .orElseGet(() -> saveAndFlush(User.builder()
                .publicId(publicId).username(username).build()));
    }
}
