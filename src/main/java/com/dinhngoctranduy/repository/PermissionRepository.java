package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);
    List<Permission> findByIdIn(List<Long> id);
}
