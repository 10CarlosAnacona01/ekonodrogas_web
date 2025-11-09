package com.ekonodrogas.ekonodrogas.controller;

import com.ekonodrogas.ekonodrogas.dto.DetalleVentasDTO;
import com.ekonodrogas.ekonodrogas.service.DetalleVentasService;
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
@RequestMapping("/api/detalle-ventas")
@RequiredArgsConstructor
@Tag(name = "Detalle de Ventas", description = "API para gestión del detalle de ventas")
public class DetalleVentasController {

    private final DetalleVentasService detalleVentasService;

    @GetMapping
    @Operation(summary = "Obtener todos los detalles", description = "Retorna una lista con todos los detalles de ventas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de detalles obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<DetalleVentasDTO>> obtenerTodos() {
        return ResponseEntity.ok(detalleVentasService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle por ID", description = "Retorna un detalle de venta específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle encontrado"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DetalleVentasDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detalleVentasService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo detalle de venta", description = "Registra un nuevo detalle de venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DetalleVentasDTO> crear(@RequestBody DetalleVentasDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleVentasService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar detalle de venta", description = "Actualiza los datos de un detalle de venta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DetalleVentasDTO> actualizar(@PathVariable Long id, @RequestBody DetalleVentasDTO dto) {
        return ResponseEntity.ok(detalleVentasService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar detalle de venta", description = "Elimina un detalle de venta del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleVentasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
