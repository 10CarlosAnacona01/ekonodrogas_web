package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.*;
import com.ekonodrogas.ekonodrogas.persistence.*;
import com.ekonodrogas.ekonodrogas.repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final CarritoService carritoService;
    private final VentasRepository ventasRepository;
    private final DetalleVentasRepository detalleVentasRepository;
    private final ProductosRepository productosRepository;
    private final UsuariosRepository usuariosRepository;

    @Transactional
    public PagoRespuestaDTO procesarPago(PagoDTO pagoDTO) {
        try {
            // Validar que el usuario exista
            UsuariosEntity usuario = usuariosRepository.findById(pagoDTO.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener el carrito del usuario
            CarritoDTO carrito = carritoService.obtenerCarrito(pagoDTO.getIdUsuario());

            if (carrito.getItems().isEmpty()) {
                throw new RuntimeException("El carrito está vacío");
            }

            // Validar stock antes de procesar
            carritoService.validarStockCarrito(pagoDTO.getIdUsuario());

            // Validar que el monto coincida
            if (!carrito.getTotal().equals(pagoDTO.getMontoTotal())) {
                throw new RuntimeException("El monto no coincide con el total del carrito");
            }

            // Simular validación de tarjeta (ficticia)
            validarTarjetaFicticia(pagoDTO);

            // Crear la venta
            LocalDateTime fechaVenta = LocalDateTime.now();
            VentasEntity venta = VentasEntity.builder()
                    .usuario(usuario)
                    .fechaVenta(fechaVenta)
                    .totalVenta(carrito.getTotal())
                    .estadoVenta(VentasEntity.EstadoVenta.completada)
                    .build();

            VentasEntity ventaGuardada = ventasRepository.save(venta);

            // Guardar detalles y actualizar stock
            for (ItemCarritoDTO item : carrito.getItems()) {
                // Crear detalle de venta
                ProductosEntity producto = productosRepository.findById(item.getIdProducto())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getIdProducto()));

                DetalleVentasEntity detalle = DetalleVentasEntity.builder()
                        .venta(ventaGuardada)
                        .producto(producto)
                        .cantidad(item.getCantidad())
                        .precioUnitario(item.getPrecioUnitario())
                        .subtotal(item.getSubtotal())
                        .build();

                detalleVentasRepository.save(detalle);

                // Actualizar stock
                int nuevoStock = producto.getStock() - item.getCantidad();
                producto.setStock(nuevoStock);

                // Marcar como no disponible si se agota
                if (nuevoStock <= 0) {
                    producto.setDisponible(false);
                }

                productosRepository.save(producto);
            }

            // Generar número de autorización ficticio
            String numeroAutorizacion = "AUTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Generar PDF del comprobante
            String pdfBase64 = generarComprobantePDF(ventaGuardada, carrito, pagoDTO, numeroAutorizacion);

            // Vaciar el carrito después del pago exitoso
            carritoService.vaciarCarrito(pagoDTO.getIdUsuario());

            // Construir respuesta
            return PagoRespuestaDTO.builder()
                    .idVenta(ventaGuardada.getIdVenta())
                    .estadoPago("completada")
                    .mensaje("Pago procesado exitosamente")
                    .fechaPago(fechaVenta)
                    .montoTotal(carrito.getTotal())
                    .numeroAutorizacion(numeroAutorizacion)
                    .comprobanteBase64(pdfBase64)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }

    // Validación ficticia de tarjeta
    private void validarTarjetaFicticia(PagoDTO pagoDTO) {
        // Simular rechazo de tarjetas que terminen en 0000
        if (pagoDTO.getNumeroTarjeta().endsWith("0000")) {
            throw new RuntimeException("Tarjeta rechazada. Por favor, intente con otra tarjeta.");
        }

        // Simular CVV inválido
        if ("000".equals(pagoDTO.getCvv())) {
            throw new RuntimeException("CVV inválido");
        }

        // Todo está OK (simulado)
    }

    // Generar PDF del comprobante
    private String generarComprobantePDF(VentasEntity venta, CarritoDTO carrito,
                                         PagoDTO pagoDTO, String numeroAutorizacion) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Título
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph titulo = new Paragraph("COMPROBANTE DE PAGO", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // Información de la empresa (ficticia)
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

            Paragraph empresa = new Paragraph();
            empresa.add(new Chunk("EKONODROGAS\n", boldFont));
            empresa.add(new Chunk("NIT: 123.456.789-0\n", normalFont));
            empresa.add(new Chunk("Cra 22 7-05, Neiva\n", normalFont));
            empresa.add(new Chunk("Tel: 313-333-4455\n", normalFont));
            empresa.setAlignment(Element.ALIGN_CENTER);
            empresa.setSpacingAfter(20);
            document.add(empresa);

            // Línea separadora
            document.add(new Paragraph("_________________________________________________"));
            document.add(Chunk.NEWLINE);

            // Información de la venta
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            Paragraph infoVenta = new Paragraph();
            infoVenta.add(new Chunk("Número de Venta: ", boldFont));
            infoVenta.add(new Chunk(venta.getIdVenta().toString() + "\n", normalFont));
            infoVenta.add(new Chunk("Fecha: ", boldFont));
            infoVenta.add(new Chunk(venta.getFechaVenta().format(formatter) + "\n", normalFont));
            infoVenta.add(new Chunk("Número de Autorización: ", boldFont));
            infoVenta.add(new Chunk(numeroAutorizacion + "\n", normalFont));
            infoVenta.add(new Chunk("Cliente: ", boldFont));
            infoVenta.add(new Chunk(pagoDTO.getNombreTitular() + "\n", normalFont));
            infoVenta.setSpacingAfter(15);
            document.add(infoVenta);

            // Tabla de productos
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            // Encabezados de la tabla
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setPadding(5);

            headerCell.setPhrase(new Phrase("Producto", boldFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Cantidad", boldFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Precio Unit.", boldFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Subtotal", boldFont));
            table.addCell(headerCell);

            // Agregar items
            for (ItemCarritoDTO item : carrito.getItems()) {
                table.addCell(new PdfPCell(new Phrase(item.getNombreProducto(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(item.getCantidad().toString(), normalFont)));
                table.addCell(new PdfPCell(new Phrase("$" + String.format("%,d", item.getPrecioUnitario()), normalFont)));
                table.addCell(new PdfPCell(new Phrase("$" + String.format("%,d", item.getSubtotal()), normalFont)));
            }

            document.add(table);

            // Total
            Paragraph total = new Paragraph();
            total.setAlignment(Element.ALIGN_RIGHT);
            total.add(new Chunk("TOTAL: ", boldFont));
            total.add(new Chunk("$" + String.format("%,d", venta.getTotalVenta()),
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
            total.setSpacingBefore(10);
            document.add(total);

            // Método de pago
            document.add(Chunk.NEWLINE);
            Paragraph metodoPago = new Paragraph();
            metodoPago.add(new Chunk("Método de Pago: ", boldFont));
            metodoPago.add(new Chunk("Tarjeta de Crédito ****" +
                    pagoDTO.getNumeroTarjeta().substring(12) + "\n", normalFont));
            document.add(metodoPago);

            // Pie de página
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("_________________________________________________"));
            Paragraph piePagina = new Paragraph();
            piePagina.add(new Chunk("\n¡Gracias por su compra!\n", boldFont));
            piePagina.add(new Chunk("Este es un comprobante de pago válido\n", normalFont));
            piePagina.setAlignment(Element.ALIGN_CENTER);
            document.add(piePagina);

            document.close();

            // Convertir a Base64
            byte[] pdfBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(pdfBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage());
        }
    }
}
