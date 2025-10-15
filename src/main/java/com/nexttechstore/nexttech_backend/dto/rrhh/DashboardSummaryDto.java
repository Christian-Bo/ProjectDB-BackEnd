package com.nexttechstore.nexttech_backend.dto.rrhh;

import java.util.Map;

public record DashboardSummaryDto(
        long totalDepartamentos,
        long totalPuestos,
        long totalEmpleados,
        long activos,
        long inactivos,
        long suspendidos,
        Map<String, Long> empleadosPorDepartamento, // nombreDepto -> count
        Map<String, Long> empleadosPorPuesto        // nombrePuesto -> count
) { }
