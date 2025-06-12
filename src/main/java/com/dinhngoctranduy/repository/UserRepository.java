package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String username);

    User findByEmailContainingIgnoreCase(String emailKeyword);

    @Query("SELECT u FROM User u WHERE MONTH(u.birthDate) = :month AND DAY(u.birthDate) = :day AND u.deleted = false AND u.emailVerified = true")
    List<User> findUsersByBirthday(@Param("month") int month, @Param("day") int day);
}
