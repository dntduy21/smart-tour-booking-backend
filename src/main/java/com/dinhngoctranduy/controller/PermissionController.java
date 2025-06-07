package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.Permission;
import com.dinhngoctranduy.model.dto.ResultPaginationDTO;
import com.dinhngoctranduy.service.PermissionService;
import com.dinhngoctranduy.util.error.IdInValidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission permission) throws IdInValidException {
        //check exists
        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInValidException("Permission đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission permission) throws IdInValidException {
        if (this.permissionService.fetchById(permission.getId()) == null) {
            throw new IdInValidException("Permission với id = " + permission.getId() + " không tồn tại");
        }

        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInValidException("Permission đã tồn tại");
        }

        return ResponseEntity.ok().body(this.permissionService.update(permission));
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws IdInValidException {
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInValidException("Permission với id = " + " không tồn tại");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
        String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
        int current = Integer.parseInt(sCurrent);
        int pageSize = Integer.parseInt(sPageSize);
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.fetchAllPermission(pageable));
    }
}
