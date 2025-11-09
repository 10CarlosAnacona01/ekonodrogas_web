package com.ekonodrogas.ekonodrogas.controller;

import com.ekonodrogas.ekonodrogas.dto.OfertasDTO;
import com.ekonodrogas.ekonodrogas.service.OfertasService;
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
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
@Tag(name = "Ofertas", description = "API para gestión de ofertas de productos")
public class OfertasController {

    private final OfertasService ofertasService;

    @GetMapping
    @Operation(summary = "Obtener todas las ofertas", description = "Retorna una lista con todas las ofertas activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ofertas obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<OfertasDTO>> obtenerTodas() {
        return ResponseEntity.ok(ofertasService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener oferta por ID", description = "Retorna una oferta específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Oferta encontrada"),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<OfertasDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ofertasService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva oferta", description = "Crea una nueva oferta para un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Oferta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<OfertasDTO> crear(@RequestBody OfertasDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ofertasService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar oferta", description = "Actualiza los datos de una oferta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Oferta actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<OfertasDTO> actualizar(@PathVariable Long id, @RequestBody OfertasDTO dto) {
        return ResponseEntity.ok(ofertasService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar oferta", description = "Elimina una oferta del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Oferta eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ofertasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
