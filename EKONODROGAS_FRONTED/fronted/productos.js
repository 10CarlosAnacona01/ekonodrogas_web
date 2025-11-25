// productos.js - Conexión Frontend-Backend para EKONODROGAS

// Configuración de la API
const API_BASE_URL = 'http://localhost:8080/api'; // Ajusta el puerto según tu configuración

// Función para formatear precios en formato colombiano
function formatearPrecio(precio) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    }).format(precio);
}

// Función para obtener productos por categoría
async function obtenerProductosPorCategoria(categoriaId) {
    try {
        const response = await fetch(`${API_BASE_URL}/productos`);
        
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        
        const productos = await response.json();
        
        // Filtrar productos por categoría y muestra los que tengan stock disponieble
        const productosFiltrados = productos.filter(
            producto => producto.idCategoria === categoriaId && producto.disponible
        );
        
        return productosFiltrados;
    } catch (error) {
        console.error('Error al obtener productos:', error);
        return [];
    }
}

// Función para obtener ofertas
async function obtenerOfertas() {
    try {
        const response = await fetch(`${API_BASE_URL}/ofertas`);
        
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        
        const ofertas = await response.json();
        return ofertas;
    } catch (error) {
        console.error('Error al obtener ofertas:', error);
        return [];
    }
}

// Función para crear la tarjeta HTML de un producto
function crearTarjetaProducto(producto, oferta = null) {
    const cardProduct = document.createElement('div');
    cardProduct.className = 'card-product';
    cardProduct.dataset.productoId = producto.idProducto;
    
    // Construir HTML de la tarjeta
    let precioHTML;
    let badgeOferta = '';
    
    if (oferta) {
        // Producto con oferta
        badgeOferta = `<span class="badge-oferta">-${oferta.descuentoPorcentaje}%</span>`;
        precioHTML = `
            <p class="price-old">${formatearPrecio(oferta.precioAnterior)}</p>
            <p class="price">${formatearPrecio(oferta.precioNuevo)}</p>
        `;
    } else {
        // Producto normal
        precioHTML = `<p class="price">${formatearPrecio(producto.precio)}</p>`;
    }
    
    cardProduct.innerHTML = `
        <div class="container-img">
            ${badgeOferta}
            <img src="/EKONODROGAS_FRONTED/imagenes/${producto.imagen}" 
                alt="${producto.nombreProducto}"
                onerror="this.src='/EKONODROGAS_FRONTED/imagenes/default.png'">
        </div>
        <div class="content-card-product">
            <h3>${producto.nombreProducto}</h3>
            <span class="add-cart" data-producto='${JSON.stringify({
                id: producto.idProducto,
                nombre: producto.nombreProducto,
                precio: oferta ? oferta.precioNuevo : producto.precio,
                imagen: producto.imagen
            })}'>
                <i class="bi bi-cart4"></i>
            </span>
            ${precioHTML}
            ${producto.stock < 5 ? '<p class="stock-bajo">¡Pocas unidades!</p>' : ''}
        </div>
    `;
    
    return cardProduct;
}

// Función para renderizar productos en el contenedor
async function renderizarProductos() {
    const container = document.getElementById('productos-container');
    
    if (!container) {
        console.error('No se encontró el contenedor de productos');
        return;
    }
    
    // Obtener ID de categoría del atributo data
    const categoriaId = parseInt(container.dataset.categoriaId);
    
    // Mostrar mensaje de carga
    container.innerHTML = '<div class="loading-message"><p>Cargando productos...</p></div>';
    
    try {
        // Obtener productos y ofertas en paralelo
        const [productos, ofertas] = await Promise.all([
            obtenerProductosPorCategoria(categoriaId),
            obtenerOfertas()
        ]);
        
        // Limpiar contenedor
        container.innerHTML = '';
        
        if (productos.length === 0) {
            container.innerHTML = '<div class="no-productos"><p>No hay productos disponibles en esta categoría</p></div>';
            return;
        }
        
        // Crear un mapa de ofertas por ID de producto para búsqueda rápida
        const ofertasMap = new Map();
        ofertas.forEach(oferta => {
            ofertasMap.set(oferta.idProducto, oferta);
        });
        
        // Renderizar cada producto
        productos.forEach(producto => {
            const oferta = ofertasMap.get(producto.idProducto);
            const tarjeta = crearTarjetaProducto(producto, oferta);
            container.appendChild(tarjeta);
        });
        
        // Agregar event listeners para los botones de carrito
        agregarEventListenersCarrito();
        
    } catch (error) {
        console.error('Error al renderizar productos:', error);
        container.innerHTML = '<div class="error-message"><p>Error al cargar los productos. Por favor, intenta de nuevo.</p></div>';
    }
}

// Función para agregar event listeners a los botones de carrito
function agregarEventListenersCarrito() {
    const botonesCarrito = document.querySelectorAll('.add-cart');
    
    console.log(`Inicializando ${botonesCarrito.length} botones de carrito`);
    
    botonesCarrito.forEach(boton => {
        boton.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const productoData = JSON.parse(this.dataset.producto);
            
            console.log('=== CLICK EN AGREGAR AL CARRITO ===');
            console.log('Producto:', productoData.nombre);
            console.log('AuthManager disponible:', typeof authManager !== 'undefined');
            console.log('Token en localStorage:', localStorage.getItem('token') ? 'presente' : 'ausente');
            console.log('Usuario autenticado:', typeof authManager !== 'undefined' ? authManager.estaAutenticado() : false);
            console.log('===================================');
            
            // VERIFICACIÓN 1: AuthManager existe
            if (typeof authManager === 'undefined') {
                console.error('❌ AuthManager no está disponible');
                alert('Error: Sistema de autenticación no disponible. Recarga la página.');
                return;
            }
            
            // VERIFICACIÓN 2: Usuario autenticado
            if (!authManager.estaAutenticado()) {
                console.log('⚠ Usuario NO autenticado, mostrando modal de login');
                authManager.requiereAutenticacion(true);
                return;
            }
            
            // Usuario autenticado, proceder
            console.log('✓ Usuario autenticado, agregando producto al carrito...');
            
            // Animación visual
            this.style.transform = 'scale(1.2)';
            setTimeout(() => {
                this.style.transform = 'scale(1)';
            }, 200);
            
            // Llamar a la función de carrito
            if (typeof agregarAlCarrito === 'function') {
                agregarAlCarrito(productoData.id, 1);
            } else {
                console.error('❌ Función agregarAlCarrito no disponible');
                alert('Error: No se puede agregar al carrito. Recarga la página.');
            }
        });
    });
}

// Función para actualizar stock de producto (opcional, para el admin)
async function actualizarStockProducto(productoId, nuevoStock) {
    try {
        const response = await fetch(`${API_BASE_URL}/productos/${productoId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ stock: nuevoStock })
        });
        
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('Error al actualizar stock:', error);
        throw error;
    }
}

// FUNCIONES ESPECÍFICAS PARA OFERTAS

// Función para obtener productos con ofertas (información combinada)
async function obtenerProductosConOfertas() {
    try {
        const [productos, ofertas] = await Promise.all([
            fetch(`${API_BASE_URL}/productos`).then(res => res.json()),
            fetch(`${API_BASE_URL}/ofertas`).then(res => res.json())
        ]);
        
        // Combinar información de productos y ofertas
        const productosConOfertas = ofertas.map(oferta => {
            const producto = productos.find(p => p.idProducto === oferta.idProducto);
            
            if (!producto) return null;
            
            return {
                idProducto: producto.idProducto,
                nombreProducto: producto.nombreProducto,
                imagen: producto.imagen,
                precioAnterior: oferta.precioAnterior,
                precioNuevo: oferta.precioNuevo,
                descuentoPorcentaje: oferta.descuentoPorcentaje,
                stock: producto.stock,
                disponible: producto.disponible,
                idOferta: oferta.idOferta
            };
        }).filter(item => item !== null && item.disponible);
        
        return productosConOfertas;
    } catch (error) {
        console.error('Error al obtener productos con ofertas:', error);
        return [];
    }
}

// Función para crear tarjeta de oferta
function crearTarjetaOferta(productoOferta) {
    const cardProduct = document.createElement('div');
    cardProduct.className = 'card-product';
    cardProduct.dataset.productoId = productoOferta.idProducto;
    cardProduct.dataset.ofertaId = productoOferta.idOferta;
    
    cardProduct.innerHTML = `
        <div class="container-img">
            <img src="/EKONODROGAS_FRONTED/imagenes/${productoOferta.imagen}" 
                alt="${productoOferta.nombreProducto}"
                onerror="this.src='/EKONODROGAS_FRONTED/imagenes/default.png'">
            <span class="discount">-${productoOferta.descuentoPorcentaje}%</span>
        </div>
        <div class="content-card-product">
            <h3>${productoOferta.nombreProducto}</h3>
            <span class="add-cart" data-producto='${JSON.stringify({
                id: productoOferta.idProducto,
                nombre: productoOferta.nombreProducto,
                precio: productoOferta.precioNuevo,
                imagen: productoOferta.imagen
            })}'>
                <i class="bi bi-cart4"></i>
            </span>
            <p class="price">${formatearPrecio(productoOferta.precioNuevo)} 
                <span>${formatearPrecio(productoOferta.precioAnterior)}</span>
            </p>
            ${productoOferta.stock < 5 ? '<p class="stock-bajo">¡Pocas unidades!</p>' : ''}
        </div>
    `;
    
    return cardProduct;
}

// Función para renderizar solo ofertas
async function renderizarOfertas() {
    const container = document.getElementById('productos-container');
    
    if (!container) {
        console.error('No se encontró el contenedor de productos');
        return;
    }
    
    // Mostrar mensaje de carga
    container.innerHTML = '<div class="loading-message"><p>Cargando ofertas...</p></div>';
    
    try {
        const productosConOfertas = await obtenerProductosConOfertas();
        
        // Limpiar contenedor
        container.innerHTML = '';
        
        if (productosConOfertas.length === 0) {
            container.innerHTML = '<div class="no-productos"><p>No hay ofertas disponibles en este momento</p></div>';
            return;
        }
        
        // Renderizar cada producto con oferta
        productosConOfertas.forEach(productoOferta => {
            const tarjeta = crearTarjetaOferta(productoOferta);
            container.appendChild(tarjeta);
        });
        
        // Agregar event listeners para los botones de carrito
        agregarEventListenersCarrito();
        
    } catch (error) {
        console.error('Error al renderizar ofertas:', error);
        container.innerHTML = '<div class="error-message"><p>Error al cargar las ofertas. Por favor, intenta de nuevo.</p></div>';
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    const container = document.getElementById('productos-container');
    
    if (!container) return;
    
    // Detectar si es la página de ofertas
    const isOfertasPage = container.dataset.categoriaId === 'ofertas';
    
    if (isOfertasPage) {
        renderizarOfertas();
        // Recargar ofertas cada 5 minutos
        setInterval(renderizarOfertas, 5 * 60 * 1000);
    } else {
        renderizarProductos();
        // Recargar productos cada 5 minutos
        setInterval(renderizarProductos, 5 * 60 * 1000);
    }
});

// Exportar funciones para uso global si es necesario
window.productosAPI = {
    obtenerProductosPorCategoria,
    obtenerOfertas,
    obtenerProductosConOfertas,
    renderizarProductos,
    renderizarOfertas,
    actualizarStockProducto
};