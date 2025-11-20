package com.ekonodrogas.ekonodrogas.controller;

import com.ekonodrogas.ekonodrogas.dto.VentaCompletaDTO;
import com.ekonodrogas.ekonodrogas.dto.VentasDTO;
import com.ekonodrogas.ekonodrogas.service.VentasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "API para gestión de ventas")
public class VentasController {

    private final VentasService ventasService;

    // ========================================
    // NUEVOS ENDPOINTS - VENTAS COMPLETAS
    // ========================================

    @GetMapping("/completas")
    @Operation(
            summary = "Obtener todas las ventas con detalles completos",
            description = "Retorna una lista completa de ventas incluyendo todos los productos de cada venta. " +
                    "Ideal para el panel de administración."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ventas completas obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<VentaCompletaDTO>> obtenerTodasCompletas() {
        return ResponseEntity.ok(ventasService.obtenerTodasCompletas());
    }

    @GetMapping("/completas/{id}")
    @Operation(
            summary = "Obtener venta completa por ID",
            description = "Retorna una venta específica con todos sus detalles de productos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta completa encontrada"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<VentaCompletaDTO> obtenerCompletaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventasService.obtenerCompletaPorId(id));
    }

    @GetMapping("/completas/usuario/{idUsuario}")
    @Operation(
            summary = "Obtener ventas completas de un usuario",
            description = "Retorna todas las ventas de un usuario específico con detalles completos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas del usuario obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<VentaCompletaDTO>> obtenerVentasCompletasPorUsuario(
            @PathVariable Long idUsuario) {
        return ResponseEntity.ok(ventasService.obtenerVentasCompletasPorUsuario(idUsuario));
    }

    // ========================================
    // ENDPOINTS ORIGINALES (mantener compatibilidad)
    // ========================================

    @GetMapping
    @Operation(
            summary = "Obtener todas las ventas (resumidas)",
            description = "Retorna una lista resumida de ventas sin detalles de productos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<VentasDTO>> obtenerTodas() {
        return ResponseEntity.ok(ventasService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener venta por ID (resumida)",
            description = "Retorna una venta específica sin detalles de productos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<VentasDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventasService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Crear nueva venta",
            description = "Registra una nueva venta en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<VentasDTO> crear(@RequestBody VentasDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar venta",
            description = "Actualiza los datos de una venta existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<VentasDTO> actualizar(@PathVariable Long id, @RequestBody VentasDTO dto) {
        return ResponseEntity.ok(ventasService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar venta",
            description = "Elimina una venta del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Venta eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ventasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}