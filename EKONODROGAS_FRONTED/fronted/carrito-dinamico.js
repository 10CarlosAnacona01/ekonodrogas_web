
// CARRITO DE COMPRAS 

// IMPORTANTE: Este archivo NO debe declarar API_URL ni API_BASE_URL
// Usa la variable ya declarada en productos.js

// Estado del carrito (ÚNICA declaración)
if (typeof carritoActual === 'undefined') {
    var carritoActual = {
        idUsuario: null,
        items: [],
        total: 0,
        cantidadTotal: 0
    };
}

// Usar API_BASE_URL de productos.js o del config
const CARRITO_API_URL = window.APP_CONFIG?.API_URL || (typeof API_BASE_URL !== 'undefined' ? API_BASE_URL : 'http://localhost:8080/api');

// Cargar carrito al iniciar
document.addEventListener('DOMContentLoaded', async () => {
    setTimeout(async () => {
        if (typeof authManager !== 'undefined' && authManager.estaAutenticado()) {
            await cargarCarrito();
        } else {
            mostrarCarritoVacio();
        }
        inicializarEventosCarrito();
    }, 100);
});

// Obtener ID del usuario actual
function obtenerIdUsuario() {
    if (typeof authManager === 'undefined' || !authManager.estaAutenticado()) {
        return null;
    }
    return authManager.obtenerIdUsuario();
}

// Inicializar eventos del carrito
function inicializarEventosCarrito() {
    const cartIcon = document.getElementById('cart-icon');
    const cartBox = document.getElementById('cart-box');
    const btnVaciar = document.querySelector('.btn-empty');
    const btnCheckout = document.querySelector('.btn-checkout');

    if (cartIcon && cartBox) {
        cartIcon.addEventListener('click', (e) => {
            e.stopPropagation();
            
            if (typeof authManager === 'undefined' || !authManager.requiereAutenticacion()) {
                return;
            }
            
            cartBox.classList.toggle('active');
        });

        document.addEventListener('click', (e) => {
            if (!cartBox.contains(e.target) && e.target !== cartIcon) {
                cartBox.classList.remove('active');
            }
        });
    }

    if (btnVaciar) btnVaciar.addEventListener('click', vaciarCarrito);
    if (btnCheckout) btnCheckout.addEventListener('click', procesarCompra);
}

// Mostrar carrito vacío
function mostrarCarritoVacio() {
    const cartCountText = document.getElementById('cart-count-text');
    if (cartCountText) cartCountText.textContent = '(0)';

    const cartTotalAmount = document.querySelector('.cart-total-amount');
    if (cartTotalAmount) {
        const precio = window.formatearPrecio ? window.formatearPrecio(0) : '$0';
        cartTotalAmount.textContent = precio;
    }

    actualizarListaItems();
    actualizarEstadoBotones();
}

// Cargar carrito desde el backend
async function cargarCarrito() {
    const idUsuario = obtenerIdUsuario();
    
    if (!idUsuario) {
        mostrarCarritoVacio();
        return;
    }

    try {
        const response = await fetchConAuth(`${CARRITO_API_URL}/carrito/${idUsuario}`);
        if (!response.ok) throw new Error('Error al cargar el carrito');
        carritoActual = await response.json();
        carritoActual.idUsuario = idUsuario;
        actualizarInterfazCarrito();
    } catch (error) {
        console.error('Error al cargar carrito:', error);
        mostrarCarritoVacio();
    }
}

// Agregar producto al carrito
async function agregarAlCarrito(idProducto, cantidad = 1) {
    if (typeof authManager === 'undefined' || !authManager.requiereAutenticacion()) {
        return;
    }

    const idUsuario = obtenerIdUsuario();
    
    try {
        const response = await fetchConAuth(`${CARRITO_API_URL}/carrito/${idUsuario}/items`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idProducto, cantidad })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Error al agregar al carrito');
        }

        carritoActual = await response.json();
        carritoActual.idUsuario = idUsuario;
        actualizarInterfazCarrito();
        mostrarNotificacion('Producto agregado al carrito', 'success');
        animarIconoCarrito();
    } catch (error) {
        console.error('Error al agregar al carrito:', error);
        mostrarNotificacion(error.message || 'Error al agregar al carrito', 'error');
    }
}

// Actualizar cantidad de un producto
async function actualizarCantidad(idProducto, nuevaCantidad) {
    if (typeof authManager === 'undefined' || !authManager.estaAutenticado()) return;

    const idUsuario = obtenerIdUsuario();

    if (nuevaCantidad < 1) {
        await eliminarDelCarrito(idProducto);
        return;
    }

    try {
        const response = await fetchConAuth(`${CARRITO_API_URL}/carrito/${idUsuario}/items/${idProducto}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ cantidad: nuevaCantidad })
        });

        if (!response.ok) throw new Error('Error al actualizar cantidad');
        carritoActual = await response.json();
        carritoActual.idUsuario = idUsuario;
        actualizarInterfazCarrito();
    } catch (error) {
        console.error('Error al actualizar cantidad:', error);
        mostrarNotificacion('Error al actualizar cantidad', 'error');
    }
}

// Eliminar producto del carrito
async function eliminarDelCarrito(idProducto) {
    if (typeof authManager === 'undefined' || !authManager.estaAutenticado()) return;

    const idUsuario = obtenerIdUsuario();

    try {
        const response = await fetchConAuth(`${CARRITO_API_URL}/carrito/${idUsuario}/items/${idProducto}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Error al eliminar del carrito');
        carritoActual = await response.json();
        carritoActual.idUsuario = idUsuario;
        actualizarInterfazCarrito();
        mostrarNotificacion('Producto eliminado del carrito', 'success');
    } catch (error) {
        console.error('Error al eliminar del carrito:', error);
        mostrarNotificacion('Error al eliminar del carrito', 'error');
    }
}

// Vaciar carrito con confirmación
async function vaciarCarrito() {
    if (typeof authManager === 'undefined' || !authManager.estaAutenticado()) return;

    const idUsuario = obtenerIdUsuario();

    if (typeof Swal !== 'undefined') {
        const result = await Swal.fire({
            title: '¿Vaciar carrito?',
            text: 'Esta acción eliminará todos los productos del carrito',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, vaciar carrito',
            cancelButtonText: 'Cancelar'
        });

        if (!result.isConfirmed) return;
    } else {
        if (!confirm('¿Vaciar el carrito?')) return;
    }
    
    try {
        const response = await fetchConAuth(`${CARRITO_API_URL}/carrito/${idUsuario}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Error al vaciar el carrito');
        carritoActual = await response.json();
        carritoActual.idUsuario = idUsuario;
        actualizarInterfazCarrito();

        if (typeof Swal !== 'undefined') {
            await Swal.fire({
                title: 'Carrito vaciado',
                text: 'Tu carrito ha sido vaciado con éxito',
                icon: 'success',
                confirmButtonColor: '#28a745',
                timer: 2000,
                showConfirmButton: false
            });
        }
    } catch (error) {
        console.error('Error al vaciar carrito:', error);
    }
}

// Limpiar carrito local
function limpiarCarritoLocal() {
    carritoActual = {
        idUsuario: null,
        items: [],
        total: 0,
        cantidadTotal: 0
    };
    mostrarCarritoVacio();
}

// Actualizar interfaz del carrito
function actualizarInterfazCarrito() {
    const cartCountText = document.getElementById('cart-count-text');
    if (cartCountText) cartCountText.textContent = `(${carritoActual.cantidadTotal || 0})`;

    const cartTotalAmount = document.querySelector('.cart-total-amount');
    if (cartTotalAmount) {
        const precio = window.formatearPrecio ? window.formatearPrecio(carritoActual.total || 0) : `$${(carritoActual.total || 0).toLocaleString('es-CO')}`;
        cartTotalAmount.textContent = precio;
    }

    actualizarListaItems();
    actualizarEstadoBotones();
}

// Actualizar lista de items
function actualizarListaItems() {
    const cartBox = document.getElementById('cart-box');
    if (!cartBox) return;

    let itemsContainer = cartBox.querySelector('.cart-items');
    if (!itemsContainer) {
        itemsContainer = document.createElement('div');
        itemsContainer.className = 'cart-items';
        const header = cartBox.querySelector('.card');
        if (header && header.parentNode) header.parentNode.insertBefore(itemsContainer, header.nextSibling);
    }

    itemsContainer.innerHTML = '';

    if (!carritoActual.items || carritoActual.items.length === 0) {
        itemsContainer.innerHTML = `
        <div class="cart-empty">
            <div class="cart-empty-icon"><i class="bi bi-cart-x"></i></div>
            <p class="cart-empty-text">Tu carrito está vacío</p>
        </div>`;
        return;
    }

    carritoActual.items.forEach(item => {
        const itemElement = crearElementoItem(item);
        itemsContainer.appendChild(itemElement);
    });
}

// Crear elemento del carrito
function crearElementoItem(item) {
    const itemDiv = document.createElement('div');
    itemDiv.className = 'cart-item';
    
    const formatPrecio = window.formatearPrecio || ((p) => `$${p.toLocaleString('es-CO')}`);
    
    itemDiv.innerHTML = `
        <div class="cart-item-col cart-item-name">
            <div class="cart-item-info">
                <img src="/EKONODROGAS_FRONTED/imagenes/${item.imagen || 'default.png'}" alt="${item.nombreProducto}">
                <div class="cart-item-text">
                    <h4>${item.nombreProducto}</h4>
                    <p>${formatPrecio(item.precioUnitario)}</p>
                </div>
            </div>
        </div>

        <div class="cart-item-col cart-item-quantity">
            <button class="btn-quantity" onclick="actualizarCantidad(${item.idProducto}, ${item.cantidad - 1})"><i class="bi bi-dash"></i></button>
            <span class="quantity-value">${item.cantidad}</span>
            <button class="btn-quantity" onclick="actualizarCantidad(${item.idProducto}, ${item.cantidad + 1})"><i class="bi bi-plus"></i></button>
        </div>

        <div class="cart-item-col cart-item-price">
            <span>${formatPrecio(item.subtotal)}</span>
            <button class="btn-remove" onclick="eliminarDelCarrito(${item.idProducto})"><i class="bi bi-trash"></i></button>
        </div>`;
    return itemDiv;
}

// Actualizar estado de los botones
function actualizarEstadoBotones() {
    const btnVaciar = document.querySelector('.btn-empty');
    const btnCheckout = document.querySelector('.btn-checkout');
    const hayItems = carritoActual.items && carritoActual.items.length > 0;
    if (btnVaciar) btnVaciar.disabled = !hayItems;
    if (btnCheckout) btnCheckout.disabled = !hayItems;
}

// Procesar compra
async function procesarCompra() {
    if (typeof authManager === 'undefined' || !authManager.requiereAutenticacion()) {
        return;
    }

    if (!carritoActual.items || carritoActual.items.length === 0) {
        mostrarNotificacion('El carrito está vacío', 'warning');
        return;
    }

    const idUsuario = obtenerIdUsuario();

    try {
        const validacionResponse = await fetchConAuth(`${CARRITO_API_URL}/carrito/${idUsuario}/validar`);
        if (!validacionResponse.ok) {
            const error = await validacionResponse.json();
            throw new Error(error.error || 'Error al validar stock');
        }

        window.location.href = `/EKONODROGAS_FRONTED/simulacion-pago/pago.html?usuario=${idUsuario}`;
    } catch (error) {
        console.error('Error al procesar compra:', error);
        mostrarNotificacion(error.message || 'Error al procesar la compra', 'error');
    }
}

// Animación carrito
function animarIconoCarrito() {
    const cartIcon = document.getElementById('cart-icon');
    if (cartIcon) {
        cartIcon.classList.add('cart-bounce');
        setTimeout(() => { cartIcon.classList.remove('cart-bounce'); }, 500);
    }
}

// Notificaciones
function mostrarNotificacion(mensaje, tipo = 'info') {
    const notificacion = document.createElement('div');
    notificacion.className = `notificacion notificacion-${tipo}`;
    notificacion.innerHTML = `<i class="bi bi-${tipo==='success'?'check-circle':tipo==='error'?'x-circle':'info-circle'}"></i><span>${mensaje}</span>`;
    notificacion.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${tipo === 'success' ? '#d4edda' : tipo === 'error' ? '#f8d7da' : '#d1ecf1'};
        color: ${tipo === 'success' ? '#155724' : tipo === 'error' ? '#721c24' : '#0c5460'};
        border: 1px solid ${tipo === 'success' ? '#c3e6cb' : tipo === 'error' ? '#f5c6cb' : '#bee5eb'};
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 14px;
        z-index: 10000;
        opacity: 0;
        transform: translateX(100px);
        transition: all 0.3s ease;
    `;
    
    document.body.appendChild(notificacion);
    
    setTimeout(() => {
        notificacion.style.opacity = '1';
        notificacion.style.transform = 'translateX(0)';
    }, 10);
    
    setTimeout(() => {
        notificacion.style.opacity = '0';
        notificacion.style.transform = 'translateX(100px)';
        setTimeout(() => {
            if (notificacion.parentNode) {
                document.body.removeChild(notificacion);
            }
        }, 300);
    }, 3000);
}

// Exportar funciones
window.agregarAlCarrito = agregarAlCarrito;
window.actualizarCantidad = actualizarCantidad;
window.eliminarDelCarrito = eliminarDelCarrito;
window.vaciarCarrito = vaciarCarrito;
window.procesarCompra = procesarCompra;
window.limpiarCarritoLocal = limpiarCarritoLocal;   