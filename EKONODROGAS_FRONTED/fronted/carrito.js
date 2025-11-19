// Configuración de la API
const API_URL = 'http://localhost:8080/api';
const ID_USUARIO = 1; // Este ID debería venir de la sesión/autenticación

// Estado del carrito
let carritoActual = {
    idUsuario: ID_USUARIO,
    items: [],
    total: 0,
    cantidadTotal: 0
};

// Función para formatear precios
function formatearPrecio(precio) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(precio);
}

// Cargar carrito al iniciar
document.addEventListener('DOMContentLoaded', async () => {
    await cargarCarrito();
    inicializarEventos();
});

// Inicializar eventos del carrito
function inicializarEventos() {
    const cartIcon = document.getElementById('cart-icon');
    const cartBox = document.getElementById('cart-box');
    const btnVaciar = document.querySelector('.btn-empty');
    const btnCheckout = document.querySelector('.btn-checkout');

    if (cartIcon && cartBox) {
        cartIcon.addEventListener('click', (e) => {
            e.stopPropagation();
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

// Cargar carrito desde el backend
async function cargarCarrito() {
    try {
        const response = await fetch(`${API_URL}/carrito/${ID_USUARIO}`);
        if (!response.ok) throw new Error('Error al cargar el carrito');
        carritoActual = await response.json();
        actualizarInterfazCarrito();
    } catch (error) {
        console.error('Error al cargar carrito:', error);
        mostrarNotificacion('Error al cargar el carrito', 'error');
    }
}

// Agregar producto al carrito
async function agregarAlCarrito(idProducto, cantidad = 1) {
    try {
        const response = await fetch(`${API_URL}/carrito/${ID_USUARIO}/items`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idProducto, cantidad })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Error al agregar al carrito');
        }

        carritoActual = await response.json();
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
    if (nuevaCantidad < 1) {
        await eliminarDelCarrito(idProducto);
        return;
    }

    try {
        const response = await fetch(`${API_URL}/carrito/${ID_USUARIO}/items/${idProducto}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ cantidad: nuevaCantidad })
        });

        if (!response.ok) throw new Error('Error al actualizar cantidad');
        carritoActual = await response.json();
        actualizarInterfazCarrito();
    } catch (error) {
        console.error('Error al actualizar cantidad:', error);
        mostrarNotificacion('Error al actualizar cantidad', 'error');
    }
}

// Eliminar producto del carrito
async function eliminarDelCarrito(idProducto) {
    try {
        const response = await fetch(`${API_URL}/carrito/${ID_USUARIO}/items/${idProducto}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Error al eliminar del carrito');
        carritoActual = await response.json();
        actualizarInterfazCarrito();
        mostrarNotificacion('Producto eliminado del carrito', 'success');
    } catch (error) {
        console.error('Error al eliminar del carrito:', error);
        mostrarNotificacion('Error al eliminar del carrito', 'error');
    }
}

// Vaciar carrito con confirmación
async function vaciarCarrito() {
    const result = await Swal.fire({
        title: '¿Vaciar carrito?',
        text: 'Esta acción eliminará todos los productos del carrito',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Sí, vaciar carrito',
        cancelButtonText: 'Cancelar',
        backdrop: true,
        customClass: { popup: 'swal-vaciar-carrito' }
    });

    if (!result.isConfirmed) return;

    try {
        const response = await fetch(`${API_URL}/carrito/${ID_USUARIO}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Error al vaciar el carrito');
        carritoActual = await response.json();
        actualizarInterfazCarrito();

        await Swal.fire({
            title: 'Carrito vaciado',
            text: 'Tu carrito ha sido vaciado con éxito',
            icon: 'success',
            confirmButtonColor: '#28a745',
            timer: 2000,
            showConfirmButton: false
        });

    } catch (error) {
        console.error('Error al vaciar carrito:', error);
        Swal.fire({ title: 'Error', text: error.message || 'No se pudo vaciar el carrito', icon: 'error', confirmButtonColor: '#d33' });
    }
}

// Actualizar interfaz del carrito
function actualizarInterfazCarrito() {
    const cartCountText = document.getElementById('cart-count-text');
    if (cartCountText) cartCountText.textContent = `(${carritoActual.cantidadTotal || 0})`;

    const cartTotalAmount = document.querySelector('.cart-total-amount');
    if (cartTotalAmount) cartTotalAmount.textContent = formatearPrecio(carritoActual.total || 0);

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
    itemDiv.innerHTML = `
        <div class="cart-item-col cart-item-name">
            <div class="cart-item-info">
                <img src="/EKONODROGAS_FRONTED/imagenes/${item.imagen || 'default.png'}" alt="${item.nombreProducto}">
                <div class="cart-item-text">
                    <h4>${item.nombreProducto}</h4>
                    <p>${formatearPrecio(item.precioUnitario)}</p>
                </div>
            </div>
        </div>

        <div class="cart-item-col cart-item-quantity">
            <button class="btn-quantity" onclick="actualizarCantidad(${item.idProducto}, ${item.cantidad - 1})"><i class="bi bi-dash"></i></button>
            <span class="quantity-value">${item.cantidad}</span>
            <button class="btn-quantity" onclick="actualizarCantidad(${item.idProducto}, ${item.cantidad + 1})"><i class="bi bi-plus"></i></button>
        </div>

        <div class="cart-item-col cart-item-price">
            <span>${formatearPrecio(item.subtotal)}</span>
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

// Procesar compra (redirige a pago.html)
async function procesarCompra() {
    if (!carritoActual.items || carritoActual.items.length === 0) {
        mostrarNotificacion('El carrito está vacío', 'warning');
        return;
    }

    try {
        const validacionResponse = await fetch(`${API_URL}/carrito/${ID_USUARIO}/validar`);
        if (!validacionResponse.ok) {
            const error = await validacionResponse.json();
            throw new Error(error.error || 'Error al validar stock');
        }

        window.location.href = `/EKONODROGAS_FRONTED/simulacion-pago/pago.html?usuario=${ID_USUARIO}`;
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
    document.body.appendChild(notificacion);
    setTimeout(() => notificacion.classList.add('show'), 10);
    setTimeout(() => { notificacion.classList.remove('show'); setTimeout(()=>document.body.removeChild(notificacion),300); }, 3000);
}

// Agregar botones de productos
function agregarBotonesCarrito() {
    const productos = document.querySelectorAll('.producto-card');
    productos.forEach(producto => {
        const btnAgregar = producto.querySelector('.btn-agregar-carrito');
        if (btnAgregar) {
            const idProducto = btnAgregar.dataset.idProducto;
            btnAgregar.addEventListener('click', () => agregarAlCarrito(parseInt(idProducto)));
        }
    });
}

// Exportar funciones
window.agregarAlCarrito = agregarAlCarrito;
window.actualizarCantidad = actualizarCantidad;
window.eliminarDelCarrito = eliminarDelCarrito;
window.vaciarCarrito = vaciarCarrito;
window.procesarCompra = procesarCompra;

