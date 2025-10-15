package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.seg.RolDto;
import com.nexttechstore.nexttech_backend.model.entity.RolEntity;
import com.nexttechstore.nexttech_backend.repository.orm.RolJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/seg/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolJpaRepository rolRepo;

    @GetMapping
    public ResponseEntity<List<RolDto>> listar() {
        List<RolEntity> roles = rolRepo.findAll();
        var list = roles.stream()
                .map(r -> new RolDto(r.getId(), r.getNombre()))
                .toList();
        return ResponseEntity.ok(list);
    }
}
