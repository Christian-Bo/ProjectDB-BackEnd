package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.rrhh.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RrhhService {

    // Dashboard
    DashboardSummaryDto getDashboardSummary();

    // Departamentos
    List<DepartamentoDto> listarDepartamentos();
    DepartamentoDto crearDepartamento(DepartamentoDto dto);
    DepartamentoDto actualizarDepartamento(Integer id, DepartamentoDto dto);
    void eliminarDepartamento(Integer id);

    // Puestos
    List<PuestoDto> listarPuestos(Integer departamentoId);
    PuestoDto crearPuesto(PuestoDto dto);
    PuestoDto actualizarPuesto(Integer id, PuestoDto dto);
    void eliminarPuesto(Integer id);

    // Empleados
    Page<EmpleadoDto> listarEmpleados(EmpleadoFilter filter, Pageable pageable);
    EmpleadoDto obtenerEmpleado(Integer id);
    EmpleadoDto crearEmpleado(EmpleadoCreateRequest req);
    EmpleadoDto actualizarEmpleado(Integer id, EmpleadoUpdateRequest req);
    void eliminarEmpleado(Integer id);
}
