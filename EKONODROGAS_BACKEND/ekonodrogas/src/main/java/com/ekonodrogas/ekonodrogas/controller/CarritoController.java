package com.ekonodrogas.ekonodrogas.controller;

import com.ekonodrogas.ekonodrogas.dto.CarritoDTO;
import com.ekonodrogas.ekonodrogas.dto.ItemCarritoDTO;
import com.ekonodrogas.ekonodrogas.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/*
 * Controlador REST para gestionar el carrito de compras
 * Proporciona endpoints para agregar, actualizar, eliminar y consultar items del carrito
 */

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "API para gestión del carrito de compras")
public class CarritoController {

    private final CarritoService carritoService;

    /*
     * Obtiene el carrito completo de un usuario
     * GET /api/carrito/{idUsuario}
     */
    @GetMapping("/{idUsuario}")
    @Operation(summary = "Obtener carrito", description = "Obtiene el carrito de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CarritoDTO> obtenerCarrito(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(idUsuario));
    }

    /*
     * Agrega un nuevo item al carrito o incrementa la cantidad si ya existe
     * POST /api/carrito/{idUsuario}/items
     * Body: { "idProducto": 1, "cantidad": 2 }
     */
    @PostMapping("/{idUsuario}/items")
    @Operation(summary = "Agregar item al carrito", description = "Agrega un producto al carrito del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CarritoDTO> agregarItem(
            @PathVariable Long idUsuario,
            @RequestBody ItemCarritoDTO itemDTO) {
        try {
            CarritoDTO carrito = carritoService.agregarItem(idUsuario, itemDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(carrito);
        } catch (RuntimeException e) {
            // Si hay error (stock insuficiente, producto no disponible, etc.)
            return ResponseEntity.badRequest().build();
        }
    }

    /*
     * Actualiza la cantidad de un producto específico en el carrito
     * PUT /api/carrito/{idUsuario}/items/{idProducto}
     * Body: { "cantidad": 5 }
     */
    @PutMapping("/{idUsuario}/items/{idProducto}")
    @Operation(summary = "Actualizar cantidad", description = "Actualiza la cantidad de un producto en el carrito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @PathVariable Long idUsuario,
            @PathVariable Long idProducto,
            @RequestBody Map<String, Integer> payload) {
        try {
            // Extraer la cantidad del JSON recibido
            Integer cantidad = payload.get("cantidad");
            CarritoDTO carrito = carritoService.actualizarCantidad(idUsuario, idProducto, cantidad);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /*
     * Elimina un producto específico del carrito
     * DELETE /api/carrito/{idUsuario}/items/{idProducto}
     */
    @DeleteMapping("/{idUsuario}/items/{idProducto}")
    @Operation(summary = "Eliminar item", description = "Elimina un producto del carrito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CarritoDTO> eliminarItem(
            @PathVariable Long idUsuario,
            @PathVariable Long idProducto) {
        return ResponseEntity.ok(carritoService.eliminarItem(idUsuario, idProducto));
    }

    /*
     * Vacía completamente el carrito del usuario (elimina todos los items)
     * DELETE /api/carrito/{idUsuario}
     */
    @DeleteMapping("/{idUsuario}")
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los items del carrito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito vaciado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CarritoDTO> vaciarCarrito(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(carritoService.vaciarCarrito(idUsuario));
    }

    /*
     * Valida que todos los productos del carrito tengan stock disponible
     * Se debe llamar antes de procesar una compra
     * GET /api/carrito/{idUsuario}/validar
     */
    @GetMapping("/{idUsuario}/validar")
    @Operation(summary = "Validar stock", description = "Valida que todos los productos del carrito tengan stock disponible")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock validado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente para algunos productos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, String>> validarStock(@PathVariable Long idUsuario) {
        try {
            // Intenta validar el stock de todos los items
            carritoService.validarStockCarrito(idUsuario);
            return ResponseEntity.ok(Map.of("mensaje", "Stock validado correctamente"));
        } catch (RuntimeException e) {
            // Si algún producto no tiene stock suficiente, retorna error
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}