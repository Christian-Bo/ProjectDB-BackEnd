package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.rrhh.*;
import com.nexttechstore.nexttech_backend.mapper.RrhhMapper;
import com.nexttechstore.nexttech_backend.model.entity.DepartamentoEntity;
import com.nexttechstore.nexttech_backend.model.entity.EmpleadoEntity;
import com.nexttechstore.nexttech_backend.model.entity.PuestoEntity;
import com.nexttechstore.nexttech_backend.repository.orm.DepartamentoJpaRepository;
import com.nexttechstore.nexttech_backend.repository.orm.EmpleadoJpaRepository;
import com.nexttechstore.nexttech_backend.repository.orm.PuestoJpaRepository;
import com.nexttechstore.nexttech_backend.service.api.RrhhService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RrhhServiceImpl implements RrhhService {

    private final DepartamentoJpaRepository deptoRepo;
    private final PuestoJpaRepository puestoRepo;
    private final EmpleadoJpaRepository empleadoRepo;
    private final RrhhMapper mapper;

    /* ===================== DASHBOARD ===================== */
    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummary() {
        long totalDepartamentos = deptoRepo.countByActivoTrue();
        long totalPuestos       = puestoRepo.countByActivoTrue();
        long totalEmpleados     = empleadoRepo.count();
        long activos            = empleadoRepo.countByEstado("A");
        long inactivos          = empleadoRepo.countByEstado("I");
        long suspendidos        = empleadoRepo.countByEstado("S");

        // Usamos fetch-join para poder acceder a p.getDepartamento().getNombre() sin LAZY
        var puestos = puestoRepo.findAllWithDepartamento();

        Map<String, Long> porDepto = puestos.stream().collect(Collectors.toMap(
                p -> p.getDepartamento().getNombre(),
                p -> empleadoRepo.countByPuesto_Departamento_Id(p.getDepartamento().getId()),
                Long::sum
        ));

        Map<String, Long> porPuesto = puestos.stream().collect(Collectors.toMap(
                PuestoEntity::getNombre,
                p -> empleadoRepo.findByPuesto_Id(p.getId(), PageRequest.of(0, 1)).getTotalElements(),
                Long::sum
        ));

        return new DashboardSummaryDto(
                totalDepartamentos,
                totalPuestos,
                totalEmpleados,
                activos,
                inactivos,
                suspendidos,
                porDepto,
                porPuesto
        );
    }

    /* =================== DEPARTAMENTOS =================== */
    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoDto> listarDepartamentos() {
        return deptoRepo.findAll(Sort.by("nombre").ascending())
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public DepartamentoDto crearDepartamento(DepartamentoDto dto) {
        deptoRepo.findByNombreIgnoreCase(dto.nombre())
                .ifPresent(d -> { throw new IllegalArgumentException("El nombre de departamento ya existe"); });

        var e = DepartamentoEntity.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .activo(dto.activo() != null ? dto.activo() : true)
                .fechaCreacion(OffsetDateTime.now())
                .build();

        return mapper.toDto(deptoRepo.save(e));
    }

    @Override
    @Transactional
    public DepartamentoDto actualizarDepartamento(Integer id, DepartamentoDto dto) {
        var e = deptoRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Departamento no encontrado"));

        if (dto.nombre() != null && !dto.nombre().equalsIgnoreCase(e.getNombre())) {
            deptoRepo.findByNombreIgnoreCase(dto.nombre())
                    .ifPresent(d -> { throw new IllegalArgumentException("El nombre de departamento ya existe"); });
            e.setNombre(dto.nombre());
        }
        e.setDescripcion(dto.descripcion());
        if (dto.activo() != null) e.setActivo(dto.activo());

        return mapper.toDto(deptoRepo.save(e));
    }

    @Override
    @Transactional
    public void eliminarDepartamento(Integer id) {
        // (opcional) validar referencias antes de borrar
        deptoRepo.deleteById(id);
    }

    /* ======================== PUESTOS ===================== */
    @Override
    @Transactional(readOnly = true)
    public List<PuestoDto> listarPuestos(Integer departamentoId) {
        var list = (departamentoId == null)
                ? puestoRepo.findAllWithDepartamento()                // fetch-join
                : puestoRepo.findAllWithDepartamentoByDepto(departamentoId);

        // MapStruct asigna departamentoId y departamentoNombre (ver RrhhMapper)
        return list.stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public PuestoDto crearPuesto(PuestoDto dto) {
        var depto = deptoRepo.findById(dto.departamentoId())
                .orElseThrow(() -> new NoSuchElementException("Departamento no encontrado"));

        var e = PuestoEntity.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .activo(dto.activo() != null ? dto.activo() : true)
                .departamento(depto)
                .fechaCreacion(OffsetDateTime.now())
                .build();

        return mapper.toDto(puestoRepo.save(e));
    }

    @Override
    @Transactional
    public PuestoDto actualizarPuesto(Integer id, PuestoDto dto) {
        var e = puestoRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Puesto no encontrado"));

        if (dto.nombre() != null) e.setNombre(dto.nombre());
        e.setDescripcion(dto.descripcion());
        if (dto.activo() != null) e.setActivo(dto.activo());

        if (dto.departamentoId() != null &&
                (e.getDepartamento() == null || !dto.departamentoId().equals(e.getDepartamento().getId()))) {
            var depto = deptoRepo.findById(dto.departamentoId())
                    .orElseThrow(() -> new NoSuchElementException("Departamento no encontrado"));
            e.setDepartamento(depto);
        }

        return mapper.toDto(puestoRepo.save(e));
    }

    @Override
    @Transactional
    public void eliminarPuesto(Integer id) {
        // (opcional) validar empleados antes de borrar
        puestoRepo.deleteById(id);
    }

    /* ======================= EMPLEADOS ==================== */
    @Override
    @Transactional(readOnly = true)
    public Page<EmpleadoDto> listarEmpleados(EmpleadoFilter filter, Pageable pageable) {
        Page<EmpleadoEntity> page;
        if (filter == null) filter = new EmpleadoFilter(null, null, null, null);

        if (filter.departamentoId() != null) {
            page = empleadoRepo.findByPuesto_Departamento_Id(filter.departamentoId(), pageable);
        } else if (filter.puestoId() != null) {
            page = empleadoRepo.findByPuesto_Id(filter.puestoId(), pageable);
        } else if (filter.estado() != null && !filter.estado().isBlank()) {
            page = empleadoRepo.findByEstado(filter.estado(), pageable);
        } else if (filter.q() != null && !filter.q().isBlank()) {
            page = empleadoRepo.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
                    filter.q(), filter.q(), pageable);
        } else {
            page = empleadoRepo.findAll(pageable);
        }

        return page.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoDto obtenerEmpleado(Integer id) {
        var e = empleadoRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));
        return mapper.toDto(e);
    }

    @Override
    @Transactional
    public EmpleadoDto crearEmpleado(EmpleadoCreateRequest req) {
        var puesto = puestoRepo.findById(req.puestoId())
                .orElseThrow(() -> new NoSuchElementException("Puesto no encontrado"));

        EmpleadoEntity jefe = null;
        if (req.jefeInmediatoId() != null) {
            jefe = empleadoRepo.findById(req.jefeInmediatoId())
                    .orElseThrow(() -> new NoSuchElementException("Jefe inmediato no encontrado"));
        }

        var e = EmpleadoEntity.builder()
                .codigo(req.codigo())
                .nombres(req.nombres())
                .apellidos(req.apellidos())
                .dpi(req.dpi())
                .nit(req.nit())
                .telefono(req.telefono())
                .email(req.email())
                .direccion(req.direccion())
                .fechaNacimiento(req.fechaNacimiento())
                .fechaIngreso(req.fechaIngreso())
                .estado(req.estado() == null ? "A" : req.estado())
                .puesto(puesto)
                .jefeInmediato(jefe)
                .foto(req.foto())
                .fechaCreacion(OffsetDateTime.now())
                .build();

        return mapper.toDto(empleadoRepo.save(e));
    }

    @Override
    @Transactional
    public EmpleadoDto actualizarEmpleado(Integer id, EmpleadoUpdateRequest req) {
        var e = empleadoRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));

        var puesto = puestoRepo.findById(req.puestoId())
                .orElseThrow(() -> new NoSuchElementException("Puesto no encontrado"));

        EmpleadoEntity jefe = null;
        if (req.jefeInmediatoId() != null) {
            jefe = empleadoRepo.findById(req.jefeInmediatoId())
                    .orElseThrow(() -> new NoSuchElementException("Jefe inmediato no encontrado"));
        }

        e.setNombres(req.nombres());
        e.setApellidos(req.apellidos());
        e.setDpi(req.dpi());
        e.setNit(req.nit());
        e.setTelefono(req.telefono());
        e.setEmail(req.email());
        e.setDireccion(req.direccion());
        e.setFechaNacimiento(req.fechaNacimiento());
        e.setFechaIngreso(req.fechaIngreso());
        e.setFechaSalida(req.fechaSalida());
        e.setEstado(req.estado());
        e.setPuesto(puesto);
        e.setJefeInmediato(jefe);
        e.setFoto(req.foto());

        return mapper.toDto(empleadoRepo.save(e));
    }

    @Override
    @Transactional
    public void eliminarEmpleado(Integer id) {
        empleadoRepo.deleteById(id);
    }
}
