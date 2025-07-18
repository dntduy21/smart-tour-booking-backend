package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Permission;
import com.dinhngoctranduy.model.Role;
import com.dinhngoctranduy.model.dto.Meta;
import com.dinhngoctranduy.model.dto.ResultPaginationDTO;
import com.dinhngoctranduy.repository.PermissionRepository;
import com.dinhngoctranduy.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role create(Role r) {
        // check permissions
        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(r);
    }

    public Role fetchById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent())
            return roleOptional.get();
        return null;
    }

    @Transactional
    public Role update(Role r) {
        Role roleDB = this.fetchById(r.getId());

        roleDB.setName(r.getName());
        roleDB.setDescription(r.getDescription());
        roleDB.setActive(r.isActive());

        if (r.getPermissions() != null) {
            if (r.getPermissions().isEmpty()) {
                roleDB.setPermissions(new ArrayList<>());
            } else {
                Set<Long> requestedPermissionIds = r.getPermissions()
                        .stream()
                        .filter(p -> p != null && p.getId() != 0L)
                        .map(Permission::getId)
                        .collect(Collectors.toSet());

                List<Permission> updatedPermissions = this.permissionRepository.findByIdIn(new ArrayList<>(requestedPermissionIds));
                roleDB.setPermissions(updatedPermissions);
            }
        }

        return this.roleRepository.save(roleDB);
    }


    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllRole(Pageable pageable) {
        Page<Role> page = this.roleRepository.findAll(pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(page.getNumber());
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(page.getContent());
        return resultPaginationDTO;
    }
}
