package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.rrhh.*;
import com.nexttechstore.nexttech_backend.security.AllowedRoles;
import com.nexttechstore.nexttech_backend.service.api.RrhhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllowedRoles({"RRHH"})
@RestController
@RequestMapping("/api/rrhh")
@RequiredArgsConstructor
public class RrhhController {

    private final RrhhService rrhhService;

    // ===== Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryDto> dashboard() {
        return ResponseEntity.ok(rrhhService.getDashboardSummary());
    }

    // ===== Departamentos
    @GetMapping("/departamentos")
    public ResponseEntity<List<DepartamentoDto>> listarDepartamentos() {
        return ResponseEntity.ok(rrhhService.listarDepartamentos());
    }

    @PostMapping("/departamentos")
    public ResponseEntity<DepartamentoDto> crearDepartamento(@Valid @RequestBody DepartamentoDto dto) {
        return ResponseEntity.ok(rrhhService.crearDepartamento(dto));
    }

    @PutMapping("/departamentos/{id}")
    public ResponseEntity<DepartamentoDto> actualizarDepartamento(@PathVariable Integer id, @Valid @RequestBody DepartamentoDto dto) {
        return ResponseEntity.ok(rrhhService.actualizarDepartamento(id, dto));
    }

    @DeleteMapping("/departamentos/{id}")
    public ResponseEntity<Void> eliminarDepartamento(@PathVariable Integer id) {
        rrhhService.eliminarDepartamento(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Puestos
    @GetMapping("/puestos")
    public ResponseEntity<List<PuestoDto>> listarPuestos(@RequestParam(required = false) Integer departamentoId) {
        return ResponseEntity.ok(rrhhService.listarPuestos(departamentoId));
    }

    @PostMapping("/puestos")
    public ResponseEntity<PuestoDto> crearPuesto(@Valid @RequestBody PuestoDto dto) {
        return ResponseEntity.ok(rrhhService.crearPuesto(dto));
    }

    @PutMapping("/puestos/{id}")
    public ResponseEntity<PuestoDto> actualizarPuesto(@PathVariable Integer id, @Valid @RequestBody PuestoDto dto) {
        return ResponseEntity.ok(rrhhService.actualizarPuesto(id, dto));
    }

    @DeleteMapping("/puestos/{id}")
    public ResponseEntity<Void> eliminarPuesto(@PathVariable Integer id) {
        rrhhService.eliminarPuesto(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Empleados
    @GetMapping("/empleados")
    public ResponseEntity<Page<EmpleadoDto>> listarEmpleados(
            @RequestParam(required = false) Integer departamentoId,
            @RequestParam(required = false) Integer puestoId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        Sort sortObj = Sort.by(
                sort.contains(",") ? sort.split(",")[0] : sort
        ).ascending();
        if (sort.toLowerCase().endsWith(",desc")) sortObj = sortObj.descending();

        Pageable pageable = PageRequest.of(page, size, sortObj);
        var filter = new EmpleadoFilter(departamentoId, puestoId, estado, q);
        return ResponseEntity.ok(rrhhService.listarEmpleados(filter, pageable));
    }

    @GetMapping("/empleados/{id}")
    public ResponseEntity<EmpleadoDto> obtenerEmpleado(@PathVariable Integer id) {
        return ResponseEntity.ok(rrhhService.obtenerEmpleado(id));
    }

    @PostMapping("/empleados")
    public ResponseEntity<EmpleadoDto> crearEmpleado(@Valid @RequestBody EmpleadoCreateRequest req) {
        return ResponseEntity.ok(rrhhService.crearEmpleado(req));
    }

    @PutMapping("/empleados/{id}")
    public ResponseEntity<EmpleadoDto> actualizarEmpleado(@PathVariable Integer id, @Valid @RequestBody EmpleadoUpdateRequest req) {
        return ResponseEntity.ok(rrhhService.actualizarEmpleado(id, req));
    }

    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Integer id) {
        rrhhService.eliminarEmpleado(id);
        return ResponseEntity.noContent().build();
    }
}
