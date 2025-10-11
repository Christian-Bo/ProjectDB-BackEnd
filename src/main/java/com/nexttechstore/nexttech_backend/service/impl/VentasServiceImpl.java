package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.VentaDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import com.nexttechstore.nexttech_backend.dto.VentaResumenDto;
import com.nexttechstore.nexttech_backend.repository.sql.VentasCommandRepository;
import com.nexttechstore.nexttech_backend.repository.sql.VentasSqlRepository;
import com.nexttechstore.nexttech_backend.service.api.VentasService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VentasServiceImpl implements VentasService {

    private final VentasCommandRepository commandRepo;
    private final VentasSqlRepository queryRepo;

    public VentasServiceImpl(VentasCommandRepository commandRepo,
                             VentasSqlRepository queryRepo) {
        this.commandRepo = commandRepo;
        this.queryRepo = queryRepo;
    }

    @Override
    @Transactional
    public int registrar(VentaRequestDto req) {
        return commandRepo.registrarVenta(req);
    }

    @Override
    public VentaDto obtenerVentaPorId(int id) {
        var dto = queryRepo.findVentaById(id);
        if (dto == null) throw new NoSuchElementException("Venta no encontrada: id=" + id);
        dto.setItems(queryRepo.findItemsByVentaId(id));
        return dto;
    }

    @Override
    public List<VentaResumenDto> listarVentas(LocalDate desde,
                                              LocalDate hasta,
                                              Integer clienteId,
                                              String numeroVenta,
                                              Integer page,
                                              Integer size) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 200) ? 50 : size;
        int offset = p * s;
        return queryRepo.buscarVentas(desde, hasta, clienteId, numeroVenta, s, offset);
    }
}
