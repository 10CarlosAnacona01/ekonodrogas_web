package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.CarritoDTO;
import com.ekonodrogas.ekonodrogas.dto.ItemCarritoDTO;
import com.ekonodrogas.ekonodrogas.persistence.ProductosEntity;
import com.ekonodrogas.ekonodrogas.repository.ProductosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final ProductosRepository productosRepository;

    // Almacenamiento temporal del carrito en memoria (por sesión/usuario)
    // Cada usuario tiene su propio carrito identificado por idUsuario
    private final Map<Long, CarritoDTO> carritos = new ConcurrentHashMap<>();

    /*
     * Obtiene el carrito de un usuario específico
     * Si no existe, retorna un carrito vacío
     */
    @Transactional(readOnly = true)
    public CarritoDTO obtenerCarrito(Long idUsuario) {
        return carritos.getOrDefault(idUsuario, CarritoDTO.builder()
                .idUsuario(idUsuario)
                .items(new ArrayList<>())
                .total(0)
                .cantidadTotal(0)
                .build());
    }

    /*
     * Agrega un producto al carrito del usuario
     * Si el producto ya existe, incrementa la cantidad
     * Valida disponibilidad y stock antes de agregar
     */
    @Transactional
    public CarritoDTO agregarItem(Long idUsuario, ItemCarritoDTO itemDTO) {
        // Obtener el producto para validar stock y disponibilidad
        ProductosEntity producto = productosRepository.findById(itemDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + itemDTO.getIdProducto()));

        // Validar que el producto esté disponible
        if (!producto.getDisponible()) {
            throw new RuntimeException("El producto no está disponible");
        }

        // Validar que haya stock suficiente
        if (producto.getStock() < itemDTO.getCantidad()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        // Obtener o crear el carrito del usuario
        CarritoDTO carrito = obtenerCarrito(idUsuario);

        // Buscar si el producto ya existe en el carrito
        ItemCarritoDTO itemExistente = carrito.getItems().stream()
                .filter(item -> item.getIdProducto().equals(itemDTO.getIdProducto()))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            // Si el producto ya existe, actualizar la cantidad
            int nuevaCantidad = itemExistente.getCantidad() + itemDTO.getCantidad();

            // Validar que el stock sea suficiente para la nueva cantidad
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
            }

            // Actualizar cantidad y subtotal
            itemExistente.setCantidad(nuevaCantidad);
            itemExistente.setSubtotal(nuevaCantidad * itemExistente.getPrecioUnitario());
        } else {
            // Si es un producto nuevo, agregarlo al carrito
            ItemCarritoDTO nuevoItem = ItemCarritoDTO.builder()
                    .idProducto(producto.getIdProducto())
                    .nombreProducto(producto.getNombreProducto())
                    .imagen(producto.getImagen())
                    .cantidad(itemDTO.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(itemDTO.getCantidad() * producto.getPrecio())
                    .stockDisponible(producto.getStock())
                    .build();

            carrito.getItems().add(nuevoItem);
        }

        // Recalcular los totales del carrito
        recalcularTotales(carrito);

        // Guardar el carrito actualizado
        carritos.put(idUsuario, carrito);

        return carrito;
    }

    /*
     * Actualiza la cantidad de un producto específico en el carrito
     * Valida que haya stock suficiente antes de actualizar
     */
    @Transactional
    public CarritoDTO actualizarCantidad(Long idUsuario, Long idProducto, Integer nuevaCantidad) {
        CarritoDTO carrito = obtenerCarrito(idUsuario);

        // Validar que el carrito no esté vacío
        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Buscar el item en el carrito
        ItemCarritoDTO item = carrito.getItems().stream()
                .filter(i -> i.getIdProducto().equals(idProducto))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        // Validar stock disponible
        ProductosEntity producto = productosRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getStock() < nuevaCantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        // Actualizar cantidad y subtotal
        item.setCantidad(nuevaCantidad);
        item.setSubtotal(nuevaCantidad * item.getPrecioUnitario());

        // Recalcular totales del carrito
        recalcularTotales(carrito);

        // Guardar carrito actualizado
        carritos.put(idUsuario, carrito);

        return carrito;
    }

    /*
     * Elimina un producto específico del carrito
     */
    @Transactional
    public CarritoDTO eliminarItem(Long idUsuario, Long idProducto) {
        CarritoDTO carrito = obtenerCarrito(idUsuario);

        // Remover el item del carrito
        carrito.getItems().removeIf(item -> item.getIdProducto().equals(idProducto));

        // Recalcular totales
        recalcularTotales(carrito);

        // Guardar carrito actualizado
        carritos.put(idUsuario, carrito);

        return carrito;
    }

    /*
     * Vacía completamente el carrito del usuario
     */
    @Transactional
    public CarritoDTO vaciarCarrito(Long idUsuario) {
        // Crear un carrito vacío
        CarritoDTO carrito = CarritoDTO.builder()
                .idUsuario(idUsuario)
                .items(new ArrayList<>())
                .total(0)
                .cantidadTotal(0)
                .build();

        // Guardar el carrito vacío
        carritos.put(idUsuario, carrito);

        return carrito;
    }

    /*
     * Valida que todos los productos del carrito tengan stock disponible
     * Se debe llamar antes de procesar una compra
     */
    @Transactional
    public void validarStockCarrito(Long idUsuario) {
        CarritoDTO carrito = obtenerCarrito(idUsuario);

        // Validar cada item del carrito
        for (ItemCarritoDTO item : carrito.getItems()) {
            ProductosEntity producto = productosRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getNombreProducto()));

            // Validar disponibilidad
            if (!producto.getDisponible()) {
                throw new RuntimeException("El producto " + item.getNombreProducto() + " ya no está disponible");
            }

            // Validar stock
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + item.getNombreProducto() +
                        ". Disponible: " + producto.getStock());
            }
        }
    }

    /*
     * Método privado para recalcular el total y cantidad total del carrito
     * Se ejecuta cada vez que se modifica el carrito
     */
    private void recalcularTotales(CarritoDTO carrito) {
        int total = 0;
        int cantidadTotal = 0;

        // Sumar todos los subtotales y cantidades
        for (ItemCarritoDTO item : carrito.getItems()) {
            total += item.getSubtotal();
            cantidadTotal += item.getCantidad();
        }

        // Actualizar los totales del carrito
        carrito.setTotal(total);
        carrito.setCantidadTotal(cantidadTotal);
    }
}