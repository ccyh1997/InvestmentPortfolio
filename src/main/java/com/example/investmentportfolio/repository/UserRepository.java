package com.example.investmentportfolio.repository;

import com.example.investmentportfolio.model.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);

    @Transactional
    void deleteByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    @Query(value = "SELECT user_id FROM users WHERE UPPER(username) = ?1", nativeQuery = true)
    Optional<Long> findIdByUsername(String username);

    @Query(value = "SELECT user_id FROM users", nativeQuery = true)
    List<Long> findAllUserIds();

    @Query(value = "SELECT roles FROM users WHERE UPPER(username) = ?1", nativeQuery = true)
    List<String> findRolesByUsername(String username);
}
