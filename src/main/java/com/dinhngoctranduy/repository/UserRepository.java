package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String username);

    User findByEmailContainingIgnoreCase(String emailKeyword);
}
