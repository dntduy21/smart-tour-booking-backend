package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.Role;
import com.dinhngoctranduy.model.dto.ResultPaginationDTO;
import com.dinhngoctranduy.service.RoleService;
import com.dinhngoctranduy.util.error.IdInValidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) throws IdInValidException {
        if (this.roleService.existByName(role.getName())) {
            throw new IdInValidException("Role với name = " + role.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    public ResponseEntity<Role> update(@Valid @RequestBody Role role) throws IdInValidException {
        if (this.roleService.fetchById(role.getId()) == null) {
            throw new IdInValidException("Role với id = " + role.getId() + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws IdInValidException {
        if (this.roleService.fetchById(id) == null) {
            throw new IdInValidException("Role với id = " + id + " không tồn tại");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
        String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
        int current = Integer.parseInt(sCurrent);
        int pageSize = Integer.parseInt(sPageSize);
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.fetchAllRole(pageable));
    }
}
