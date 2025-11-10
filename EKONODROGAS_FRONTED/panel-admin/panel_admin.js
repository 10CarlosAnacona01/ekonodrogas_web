
// CONFIGURACIÓN API

const API_BASE_URL = 'http://localhost:8080/api'; // Cambia el puerto si es necesario


// VARIABLES GLOBALES

let currentEditingProductId = null;
let products = [];
let categorias = [];
let ventas = [];
let usuarios = [];


// INICIALIZACIÓN

document.addEventListener('DOMContentLoaded', async function() {
    initializeEventListeners();
    addLoadingAnimations();
    
    // Cargar datos desde el backend
    await loadCategorias();
    await loadProducts();
    // Cargar usuarios antes de las ventas para que estén disponibles al procesar ventas y no aparezca "Cliente desconocido"
    await loadUsuarios();
    await loadVentas();

    // Muestra la información de la Base de Datos en pantalla
    renderProducts();
    // Muestra los datos resumidos en el dashboard
    renderDashboard();
});

// Mostrar fecha actual
document.getElementById('currentDate').textContent = new Date().toLocaleDateString('es-CO', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
});


// CARGA DE DATOS DESDE BACKEND


// Cargar categorías
async function loadCategorias() {
    try {
        const response = await fetch(`${API_BASE_URL}/categorias`);
        if (!response.ok) throw new Error('Error al cargar categorías');
        categorias = await response.json();
        console.log('Categorías cargadas:', categorias);
    } catch (error) {
        console.error('Error al cargar categorías:', error);
        showNotification('Error al cargar categorías', 'error');
    }
}

// Cargar usuarios
async function loadUsuarios() {
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios`);
        if (!response.ok) throw new Error('Error al cargar usuarios');
        usuarios = await response.json();
        console.log('Usuarios cargados:', usuarios);
    } catch (error) {
        console.error('Error al cargar usuarios:', error);
        showNotification('Error al cargar usuarios', 'error');
    }
}

// Cargar productos
async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE_URL}/productos`);
        if (!response.ok) throw new Error('Error al cargar productos');
        const productosDTO = await response.json();
        
        // Transformar DTO del backend a formato del frontend
        products = productosDTO.map(dto => ({
            id: dto.idProducto,
            idCategoria: dto.idCategoria,
            categoryName: getCategoryNameById(dto.idCategoria),
            name: dto.nombreProducto,
            price: dto.precio,
            stock: dto.stock,
            available: dto.disponible,
            imagen: dto.imagen
        }));
        
        console.log('Productos cargados:', products);
    } catch (error) {
        console.error('Error al cargar productos:', error);
        showNotification('Error al cargar productos', 'error');
    }
}

// Cargar ventas
async function loadVentas() {
    try {
        // Obtener ventas
        const response = await fetch(`${API_BASE_URL}/ventas`);
        if (!response.ok) throw new Error('Error al cargar ventas');
        const ventasDTO = await response.json();

        // Obtener todos los detalles de venta una sola vez
        const detallesResponse = await fetch(`${API_BASE_URL}/detalle-ventas`);
        if (!detallesResponse.ok) throw new Error('Error al cargar detalles de venta');
        const todosDetalles = await detallesResponse.json();

        // Transformar DTO del backend a formato del frontend
        ventas = ventasDTO.map(venta => {
            const detallesVenta = todosDetalles.filter(d => d.idVenta === venta.idVenta);

            // Buscar usuario (asegúrate que la variable `usuarios` exista y esté cargada)
            const usuario = usuarios && usuarios.find(u => u.id === venta.idUsuario || u.idUsuario === venta.idUsuario);

            // Obtener nombre del primer producto y cantidad total
            let productName = 'Varios productos';
            let quantity = 0;
            let precioUnitario = 0;

            if (detallesVenta.length > 0) {
                const primerDetalle = detallesVenta[0];
                const producto = products.find(p => p.id === primerDetalle.idProducto);
                productName = producto ? producto.name : 'Producto desconocido';
                quantity = detallesVenta.reduce((sum, d) => sum + (d.cantidad || 0), 0);
                // usar el campo que tenga tu detalle; aquí asumo 'precioUnitario'
                precioUnitario = primerDetalle.precioUnitario ?? primerDetalle.precio_unitario ?? 0;
            }

            return {
                id: venta.idVenta,
                usuario: usuario
                    ? `${usuario.primerNombre || usuario.primer_nombre || ''} ${usuario.segundoNombre || usuario.segundo_nombre || ''} ${usuario.primerApellido || usuario.primer_apellido || ''} ${usuario.segundoApellido || usuario.segundo_apellido || ''}`.replace(/\s+/g, ' ').trim()
                    : 'Cliente desconocido',
                product: productName,
                quantity: quantity,
                price: precioUnitario,
                total: venta.totalVenta ?? venta.total_venta ?? 0,
                date: venta.fechaVenta ? venta.fechaVenta.split('T')[0] : new Date().toISOString().split('T')[0],
                estado: venta.estadoVenta ?? venta.estado_venta ?? ''
            };
        });

        console.log('Ventas cargadas:', ventas);
    } catch (error) {
        console.error('Error al cargar ventas:', error);
        showNotification('Error al cargar ventas', 'error');
    }
}


// Obtener nombre de categoría por ID
function getCategoryNameById(idCategoria) {
    const categoria = categorias.find(c => c.idCategoria === idCategoria);
    return categoria ? categoria.nombreCategoria : 'Sin categoría';
}

// Obtener ID de categoría por nombre
function getCategoryIdByName(nombreCategoria) {
    const categoria = categorias.find(c => 
        c.nombreCategoria.toLowerCase() === nombreCategoria.toLowerCase()
    );
    return categoria ? categoria.idCategoria : null;
}
function initializeEventListeners() {
    // Navegación entre secciones
    const navItems = document.querySelectorAll('.nav-item[data-section]');
    navItems.forEach(item => {
        item.addEventListener('click', function() {
            const sectionId = this.getAttribute('data-section');
            showSection(sectionId, this);
        });
    });

    // Botón agregar producto
    const addProductBtn = document.getElementById('addProductBtn');
    if (addProductBtn) {
        addProductBtn.addEventListener('click', () => openProductModal());
    }

    // Cerrar modal
    const closeModalBtn = document.getElementById('closeModalBtn');
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', () => closeProductModal());
    }

    // Formulario de producto
    const productForm = document.getElementById('productForm');
    if (productForm) {
        productForm.addEventListener('submit', (e) => saveProduct(e));
    }

    // Cerrar modal al hacer clic fuera
    const modal = document.getElementById('productModal');
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) closeProductModal();
        });
    }

    // Cerrar modal con tecla ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') closeProductModal();
    });

    // Menú móvil
    createMobileMenu();
}

// NAVEGACIÓN

function showSection(sectionId, element) {
    // Animación de salida
    const activeSections = document.querySelectorAll('.section.active');
    activeSections.forEach(s => {
        s.style.opacity = '0';
        s.style.transform = 'translateY(20px)';
    });

    setTimeout(() => {
        // Ocultar todas las secciones
        document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
        
        // Quitar active de items de navegación
        document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
        
        // Mostrar sección seleccionada
        const targetSection = document.getElementById(sectionId);
        targetSection.classList.add('active');
        
        // Activar item de navegación
        element.classList.add('active');

        // Animación de entrada
        setTimeout(() => {
            targetSection.style.opacity = '1';
            targetSection.style.transform = 'translateY(0)';
        }, 50);

        // Cerrar menú móvil si está abierto
        closeMobileMenu();
    }, 300);
}

// EVENT LISTENERS

// GESTIÓN DE PRODUCTOS

// Renderizar tabla de productos
function renderProducts() {
    const tbody = document.getElementById('productsTable');
    if (!tbody) return;

    tbody.innerHTML = '';

    if (products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 30px; color: #666;">No hay productos registrados</td></tr>';
        return;
    }

    products.forEach((product, index) => {
        const row = document.createElement('tr');
        row.style.opacity = '0';
        row.style.transform = 'translateX(-20px)';
        
        // Determinar badge de stock
        let stockBadge = '';
        if (product.stock === 0) {
            stockBadge = '<span class="badge badge-danger">Sin stock</span>';
        } else if (product.stock < 10) {
            stockBadge = '<span class="badge badge-warning">Stock bajo</span>';
        } else {
            stockBadge = '<span class="badge badge-success">Stock normal</span>';
        }

        row.innerHTML = `
            <td>${product.id}</td>
            <td>${product.categoryName || 'Sin categoría'}</td>
            <td>${product.name}</td>
            <td>$${product.price.toLocaleString('es-CO')}</td>
            <td>${product.stock}</td>
            <td>${stockBadge}</td>
            <td>
                <button class="btn btn-success btn-sm" onclick="editProduct(${product.id})" title="Editar">
                    Editar
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteProduct(${product.id})" title="Eliminar">
                    Eliminar
                </button>
            </td>
        `;

        tbody.appendChild(row);

        // Animación de entrada
        setTimeout(() => {
            row.style.transition = 'all 0.3s ease';
            row.style.opacity = '1';
            row.style.transform = 'translateX(0)';
        }, index * 50);
    });
}

// Abrir modal de producto (nuevo o editar)
function openProductModal(id = null) {
    currentEditingProductId = id;
    const modal = document.getElementById('productModal');
    const form = document.getElementById('productForm');
    const selectCategoria = document.getElementById('productCategory');
    
    // Poblar select de categorías
    selectCategoria.innerHTML = '<option value="">Selecciona una categoría</option>';
    categorias.forEach(cat => {
        selectCategoria.innerHTML += `<option value="${cat.idCategoria}">${cat.nombreCategoria}</option>`;
    });
    
    modal.classList.add('active');
    
    if (id) {
        // Modo edición
        const product = products.find(p => p.id === id);
        if (!product) {
            showNotification('Producto no encontrado', 'error');
            return;
        }
        
        document.getElementById('modalTitle').textContent = 'Editar Producto';
        document.getElementById('productId').value = product.id;
        document.getElementById('productName').value = product.name;
        document.getElementById('productCategory').value = product.idCategoria;
        document.getElementById('productPrice').value = product.price;
        document.getElementById('productStock').value = product.stock;
    } else {
        // Modo creación
        document.getElementById('modalTitle').textContent = 'Agregar Producto';
        form.reset();
        document.getElementById('productId').value = '';
    }

    // Animación de entrada del modal
    const modalContent = modal.querySelector('.modal-content');
    modalContent.style.transform = 'scale(0.9)';
    modalContent.style.opacity = '0';
    setTimeout(() => {
        modalContent.style.transition = 'all 0.3s ease';
        modalContent.style.transform = 'scale(1)';
        modalContent.style.opacity = '1';
    }, 10);
}

// Cerrar modal de producto
function closeProductModal() {
    const modal = document.getElementById('productModal');
    const modalContent = modal.querySelector('.modal-content');
    
    // Animación de salida
    modalContent.style.transform = 'scale(0.9)';
    modalContent.style.opacity = '0';
    
    setTimeout(() => {
        modal.classList.remove('active');
        currentEditingProductId = null;
    }, 300);
}

// Guardar producto (crear o editar)
async function saveProduct(event) {
    event.preventDefault();
    
    const idCategoria = parseInt(document.getElementById('productCategory').value);
    const nombre = document.getElementById('productName').value.trim();
    const precio = parseInt(document.getElementById('productPrice').value);
    const stock = parseInt(document.getElementById('productStock').value);
    
    // Validaciones
    if (!idCategoria) {
        showNotification('Debes seleccionar una categoría', 'error');
        return;
    }
    
    if (!nombre) {
        showNotification('El nombre del producto es obligatorio', 'error');
        return;
    }
    
    if (precio <= 0) {
        showNotification('El precio debe ser mayor a 0', 'error');
        return;
    }
    
    if (stock < 0) {
        showNotification('El stock no puede ser negativo', 'error');
        return;
    }
    
    // Preparar DTO para el backend
    const productoDTO = {
        idCategoria: idCategoria,
        nombreProducto: nombre,
        imagen: null, // Se puede agregar después funcionalidad de carga de imágenes
        precio: precio,
        stock: stock,
        disponible: stock > 0 // Automático según stock
    };
    
    try {
        let response;
        
        if (currentEditingProductId) {
            // Actualizar producto existente
            productoDTO.idProducto = currentEditingProductId;
            response = await fetch(`${API_BASE_URL}/productos/${currentEditingProductId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(productoDTO)
            });
            
            if (!response.ok) {
                throw new Error('Error al actualizar producto');
            }
            
            showNotification('Producto actualizado exitosamente', 'success');
        } else {
            // Crear nuevo producto
            response = await fetch(`${API_BASE_URL}/productos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(productoDTO)
            });
            
            if (!response.ok) {
                throw new Error('Error al crear producto');
            }
            
            showNotification('Producto creado exitosamente', 'success');
        }
        
        // Recargar productos y actualizar interfaz
        await loadProducts();
        renderProducts();
        renderDashboard();
        closeProductModal();
        
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || 'Error al guardar producto', 'error');
    }
}

// Editar producto
function editProduct(id) {
    openProductModal(id);
}

// Eliminar producto
async function deleteProduct(id) {
    const product = products.find(p => p.id === id);
    
    if (!product) {
        showNotification('Producto no encontrado', 'error');
        return;
    }

    // Confirmación más bonita con SweetAlert2 para eliminar
    const result = await Swal.fire({
        title: `¿Eliminar "${product.name}"?`,
        text: "Esta acción no se puede deshacer",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
    });

    if (!result.isConfirmed) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/productos/${id}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error('Error al eliminar producto');
        }
        
        // Animación de salida
        const row = document.querySelector(`tr[data-id="${id}"]`);
        if (row) {
            row.style.transition = 'all 0.3s ease';
            row.style.opacity = '0';
            row.style.transform = 'translateX(-50px)';
        }
        
        setTimeout(async () => {
            await loadProducts();
            renderProducts();
            renderDashboard();
            showNotification('Producto eliminado exitosamente', 'success');
        }, 300);
        
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || 'Error al eliminar producto', 'error');
    }
}

// DASHBOARD

function renderDashboard() {
    // Calcular estadísticas
    const totalSales = ventas.reduce((sum, venta) => sum + venta.total, 0);
    const totalProducts = products.length;
    const totalPurchases = ventas.length;
    const lowStock = products.filter(p => p.stock < 10).length;

    // Actualizar cards de estadísticas
    const statsCards = document.querySelectorAll('.stat-card .value');
    if (statsCards.length >= 4) {
        animateValue(statsCards[0], 0, totalSales, 1000, true);
        animateValue(statsCards[1], 0, totalProducts, 1000, false);
        animateValue(statsCards[2], 0, totalPurchases, 1000, false);
        animateValue(statsCards[3], 0, lowStock, 1000, false);
    }

    // Renderizar últimas ventas
    renderRecentSales();
}

function renderRecentSales() {
    const tbody = document.getElementById('dashboardSalesTable');
    if (!tbody) return;

    tbody.innerHTML = '';

    // Mostrar todas las ventas siendo las más recientes primero
    const recentSales = ventas.slice().reverse();
    
    recentSales.forEach((venta, index) => {
        const row = document.createElement('tr');
        row.style.opacity = '0';
        row.innerHTML = `
            <td>${venta.id}</td>
            <td>${venta.usuario}</td>
            <td>${venta.product}</td>
            <td>${venta.quantity}</td>
            <td>$${venta.price.toLocaleString('es-CO')}</td>
            <td>$${venta.total.toLocaleString('es-CO')}</td>
            <td>${formatDate(venta.date)}</td>
        `;
        tbody.appendChild(row);

        setTimeout(() => {
            row.style.transition = 'all 0.3s ease';
            row.style.opacity = '1';
        }, index * 100);
    });
}

// ANIMACIONES Y EFECTOS

// Animación de contador numérico
function animateValue(element, start, end, duration, isCurrency = false) {
    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        const value = Math.floor(progress * (end - start) + start);
        
        if (isCurrency) {
            element.textContent = '$' + value.toLocaleString('es-CO');
        } else {
            element.textContent = value;
        }
        
        if (progress < 1) {
            window.requestAnimationFrame(step);
        }
    };
    window.requestAnimationFrame(step);
}

// Animaciones de carga inicial
function addLoadingAnimations() {
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => {
        section.style.transition = 'all 0.3s ease';
    });

    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        setTimeout(() => {
            card.style.transition = 'all 0.5s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
}

// Notificaciones
function showNotification(message, type = 'success') {
    // Remover notificaciones anteriores
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(n => n.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 25px;
        background: ${type === 'success' ? '#10b981' : '#ef4444'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 10000;
        opacity: 0;
        transform: translateX(100px);
        transition: all 0.3s ease;
        max-width: 400px;
        word-wrap: break-word;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateX(0)';
    }, 10);

    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(100px)';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// MENÚ MÓVIL

function createMobileMenu() {
    const sidebar = document.querySelector('.sidebar');
    if (!sidebar) return;

    // Crear botón hamburguesa
    const menuBtn = document.createElement('button');
    menuBtn.className = 'mobile-menu-btn';
    menuBtn.innerHTML = '☰';
    menuBtn.style.cssText = `
        display: none;
        position: fixed;
        top: 20px;
        left: 20px;
        z-index: 1001;
        background: var(--primary-color);
        color: white;
        border: none;
        border-radius: 8px;
        width: 45px;
        height: 45px;
        font-size: 24px;
        cursor: pointer;
        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
    `;

    document.body.appendChild(menuBtn);

    menuBtn.addEventListener('click', toggleMobileMenu);

    // Agregar overlay
    const overlay = document.createElement('div');
    overlay.className = 'mobile-overlay';
    overlay.style.cssText = `
        display: none;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0,0,0,0.5);
        z-index: 999;
    `;
    document.body.appendChild(overlay);

    overlay.addEventListener('click', closeMobileMenu);
}

function toggleMobileMenu() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.mobile-overlay');
    
    sidebar.classList.toggle('mobile-active');
    overlay.style.display = sidebar.classList.contains('mobile-active') ? 'block' : 'none';
}

function closeMobileMenu() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.mobile-overlay');
    
    if (sidebar) sidebar.classList.remove('mobile-active');
    if (overlay) overlay.style.display = 'none';
}

// UTILIDADES

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}