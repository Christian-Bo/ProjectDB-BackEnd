package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.VentaDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import com.nexttechstore.nexttech_backend.dto.VentaResumenDto;
import com.nexttechstore.nexttech_backend.repository.sp.VentasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.VentasService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class VentasServiceImpl implements VentasService {

    private final VentasSpRepository spRepo;

    public VentasServiceImpl(VentasSpRepository spRepo) {
        this.spRepo = spRepo;
    }

    @Override
    @Transactional
    public int registrar(VentaRequestDto req) {
        try {
            return spRepo.crearVenta(req);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    // Estos dos siguen usando tus repos de consulta SI EXISTEN en otro equipo.
    // Como acordamos mover todo a SP, por ahora devuelvo stubs para que compile
    // (o déjame los repos de consulta y los conecto).
    @Override
    public VentaDto obtenerVentaPorId(int id) {
        throw new UnsupportedOperationException("Consulta de venta por id: definir SP o repos de lectura.");
    }

    @Override
    public List<VentaResumenDto> listarVentas(LocalDate desde, LocalDate hasta, Integer clienteId, String numeroVenta, Integer page, Integer size) {
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public void anular(int ventaId, String motivo) {
        try {
            spRepo.anularVenta(ventaId, motivo);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
