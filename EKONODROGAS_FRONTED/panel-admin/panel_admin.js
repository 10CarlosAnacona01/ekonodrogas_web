// ========================================
// PANEL DE ADMINISTRACIÓN - VERSIÓN CORREGIDA
// ========================================

// VARIABLES GLOBALES
let currentEditingProductId = null;
let products = [];
let categorias = [];
let ventas = [];
let usuarios = [];

// INICIALIZACIÓN
document.addEventListener('DOMContentLoaded', async function() {
    // 1. VERIFICAR AUTENTICACIÓN Y PERMISOS
    if (!verificarAccesoAdmin()) {
        return; // Detener ejecución si no es admin
    }
    
    // 2. MOSTRAR INFORMACIÓN DEL ADMINISTRADOR
    mostrarInfoAdmin();
    
    // 3. INICIALIZAR INTERFAZ
    initializeEventListeners();
    addLoadingAnimations();
    
    // 4. CARGAR DATOS DESDE EL BACKEND
    await cargarDatosIniciales();
});

// ========================================
// VERIFICACIÓN DE ACCESO
// ========================================

function verificarAccesoAdmin() {
    // Verificar que el usuario esté autenticado
    if (!authManager.estaAutenticado()) {
        console.error('Usuario no autenticado');
        Swal.fire({
            title: 'Acceso Denegado',
            text: 'Debes iniciar sesión para acceder al panel de administración',
            icon: 'error',
            confirmButtonText: 'Ir al login',
            confirmButtonColor: '#C2AB2D'
        }).then(() => {
            window.location.href = '/EKONODROGAS_FRONTED/login/login.html';
        });
        return false;
    }
    
    // Verificar que sea administrador
    if (!authManager.esAdministrador()) {
        console.error('Usuario no es administrador');
        Swal.fire({
            title: 'Acceso Denegado',
            text: 'No tienes permisos para acceder al panel de administración',
            icon: 'error',
            confirmButtonText: 'Volver al inicio',
            confirmButtonColor: '#C2AB2D'
        }).then(() => {
            window.location.href = '/EKONODROGAS_FRONTED/fronted/index.html';
        });
        return false;
    }
    
    console.log('✓ Acceso autorizado como administrador');
    return true;
}

function mostrarInfoAdmin() {
    const nombreAdmin = authManager.obtenerNombreCompleto();
    const headerUser = document.querySelector('.header div:first-child');
    if (headerUser) {
        headerUser.innerHTML = `Administrador: <strong>${nombreAdmin}</strong>`;
    }
}

// ========================================
// CARGA DE DATOS INICIAL
// ========================================

async function cargarDatosIniciales() {
    try {
        // Mostrar indicador de carga
        mostrarCargando(true);
        
        // Cargar datos en orden
        console.log('1. Cargando categorías...');
        await loadCategorias();
        
        console.log('2. Cargando productos...');
        await loadProducts();
        
        console.log('3. Cargando usuarios...');
        await loadUsuarios();
        
        console.log('4. Cargando ventas...');
        await loadVentas();

        console.log('5. Renderizando productos...');
        renderProducts();
        
        console.log('6. Renderizando dashboard...');
        renderDashboard();
        
        console.log('Todos los datos cargados y renderizados exitosamente');
        console.log('Estado actual:');
        console.log('- Categorías:', categorias.length);
        console.log('- Productos:', products.length);
        console.log('- Usuarios:', usuarios.length);
        console.log('- Ventas:', ventas.length);
        
        if (ventas.length > 0) {
            console.log('Primera venta:', ventas[0]);
        }
        
    } catch (error) {
        console.error(' Error al cargar datos iniciales:', error);
        Swal.fire({
            title: 'Error de Carga',
            text: 'No se pudieron cargar los datos. Verifica tu conexión.',
            icon: 'error',
            confirmButtonColor: '#C2AB2D'
        });
    } finally {
        mostrarCargando(false);
    }
}

function mostrarCargando(mostrar) {

    if (mostrar) {
        console.log('Cargando datos...');
    }
}

// ========================================
// CARGA DE DATOS DESDE BACKEND (CON AUTH)
// ========================================

// Cargar categorías
async function loadCategorias() {
    try {
        const response = await authManager.fetchConAuth(
            `${window.APP_CONFIG.API_URL}/categorias`
        );
        
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        
        categorias = await response.json();
        console.log(' Categorías cargadas:', categorias.length);
        
    } catch (error) {
        console.error('Error al cargar categorías:', error);
        throw error;
    }
}

// Cargar usuarios
async function loadUsuarios() {
    try {
        const response = await authManager.fetchConAuth(
            `${window.APP_CONFIG.API_URL}/usuarios`
        );
        
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        
        usuarios = await response.json();
        console.log(' Usuarios cargados:', usuarios.length);
        
    } catch (error) {
        console.error('Error al cargar usuarios:', error);
        throw error;
    }
}

// Cargar productos
async function loadProducts() {
    try {
        const response = await authManager.fetchConAuth(
            `${window.APP_CONFIG.API_URL}/productos`
        );
        
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        
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
        
        console.log(' Productos cargados:', products.length);
        
    } catch (error) {
        console.error('Error al cargar productos:', error);
        throw error;
    }
}

// Cargar ventas (VERSIÓN COMPATIBLE CON AMBOS ENDPOINTS)
async function loadVentas() {
    try {
        console.log('Intentando cargar ventas...');
        
        // INTENTAR PRIMERO CON EL NUEVO ENDPOINT
        try {
            const responseCompletas = await authManager.fetchConAuth(
                `${window.APP_CONFIG.API_URL}/ventas/completas`
            );
            
            if (responseCompletas.ok) {
                const ventasCompletas = await responseCompletas.json();
                console.log(' Usando endpoint /ventas/completas');
                console.log('Datos recibidos:', ventasCompletas);

                // Transformar a formato del frontend
                ventas = ventasCompletas.map(venta => {
                    let productosDescripcion = '';
                    if (venta.detalles && venta.detalles.length > 0) {
                        if (venta.detalles.length === 1) {
                            productosDescripcion = venta.detalles[0].nombreProducto;
                        } else {
                            productosDescripcion = `${venta.detalles.length} productos diferentes`;
                        }
                    }

                    return {
                        id: venta.idVenta,
                        usuario: venta.nombreUsuario,
                        productosDescripcion: productosDescripcion,
                        detalles: venta.detalles || [],
                        cantidadProductos: venta.cantidadProductosDiferentes || 0,
                        cantidadUnidades: venta.cantidadTotalUnidades || 0,
                        total: venta.totalVenta,
                        date: venta.fechaVenta ? venta.fechaVenta.split('T')[0] : new Date().toISOString().split('T')[0],
                        estado: venta.estadoVenta
                    };
                });

                console.log(' Ventas completas cargadas:', ventas.length);
                return;
            }
        } catch (errorCompletas) {
            console.warn('Endpoint /ventas/completas no disponible, usando método alternativo');
        }

        // SI FALLA, USAR MÉTODO TRADICIONAL
        console.log('Usando método tradicional con dos peticiones...');
        
        const responseVentas = await authManager.fetchConAuth(
            `${window.APP_CONFIG.API_URL}/ventas`
        );
        
        if (!responseVentas.ok) {
            throw new Error(`Error HTTP ventas: ${responseVentas.status}`);
        }
        
        const ventasDTO = await responseVentas.json();
        console.log('Ventas básicas obtenidas:', ventasDTO.length);

        const responseDetalles = await authManager.fetchConAuth(
            `${window.APP_CONFIG.API_URL}/detalle-ventas`
        );
        
        if (!responseDetalles.ok) {
            throw new Error(`Error HTTP detalles: ${responseDetalles.status}`);
        }
        
        const todosDetalles = await responseDetalles.json();
        console.log('Detalles de ventas obtenidos:', todosDetalles.length);

        // Transformar DTO del backend a formato del frontend
        ventas = ventasDTO.map(venta => {
            const detallesVenta = todosDetalles.filter(d => d.idVenta === venta.idVenta);
            const usuario = usuarios.find(u => 
                u.id === venta.idUsuario || u.idUsuario === venta.idUsuario
            );

            // Construir lista completa de detalles
            const detallesCompletos = detallesVenta.map(detalle => {
                const producto = products.find(p => p.id === detalle.idProducto);
                return {
                    idProducto: detalle.idProducto,
                    nombreProducto: producto ? producto.name : 'Producto desconocido',
                    categoria: producto ? producto.categoryName : 'Sin categoría',
                    cantidad: detalle.cantidad,
                    precioUnitario: detalle.precioUnitario,
                    subtotal: detalle.subtotal
                };
            });

            // Calcular totales
            const cantidadTotalUnidades = detallesVenta.reduce((sum, d) => sum + d.cantidad, 0);

            // Descripción de productos
            let productosDescripcion = '';
            if (detallesCompletos.length === 1) {
                productosDescripcion = detallesCompletos[0].nombreProducto;
            } else if (detallesCompletos.length > 1) {
                productosDescripcion = `${detallesCompletos.length} productos diferentes`;
            } else {
                productosDescripcion = 'Sin productos';
            }

            return {
                id: venta.idVenta,
                usuario: usuario ? formatNombreUsuario(usuario) : 'Cliente desconocido',
                productosDescripcion: productosDescripcion,
                detalles: detallesCompletos,
                cantidadProductos: detallesCompletos.length,
                cantidadUnidades: cantidadTotalUnidades,
                total: venta.totalVenta ?? venta.total_venta ?? 0,
                date: venta.fechaVenta ? venta.fechaVenta.split('T')[0] : new Date().toISOString().split('T')[0],
                estado: venta.estadoVenta ?? venta.estado_venta ?? ''
            };
        });

        console.log(' Ventas cargadas con método tradicional:', ventas.length);
        
    } catch (error) {
        console.error('Error al cargar ventas:', error);
        throw error;
    }
}

// ========================================
// FUNCIONES AUXILIARES
// ========================================

function formatNombreUsuario(usuario) {
    const nombres = [
        usuario.primerNombre || usuario.primer_nombre || '',
        usuario.segundoNombre || usuario.segundo_nombre || '',
        usuario.primerApellido || usuario.primer_apellido || '',
        usuario.segundoApellido || usuario.segundo_apellido || ''
    ];
    
    return nombres.filter(n => n).join(' ');
}

function getCategoryNameById(idCategoria) {
    const categoria = categorias.find(c => c.idCategoria === idCategoria);
    return categoria ? categoria.nombreCategoria : 'Sin categoría';
}

function getCategoryIdByName(nombreCategoria) {
    const categoria = categorias.find(c => 
        c.nombreCategoria.toLowerCase() === nombreCategoria.toLowerCase()
    );
    return categoria ? categoria.idCategoria : null;
}

// ========================================
// EVENT LISTENERS
// ========================================

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

// ========================================
// NAVEGACIÓN
// ========================================

function showSection(sectionId, element) {
    const activeSections = document.querySelectorAll('.section.active');
    activeSections.forEach(s => {
        s.style.opacity = '0';
        s.style.transform = 'translateY(20px)';
    });

    setTimeout(() => {
        document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
        document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
        
        const targetSection = document.getElementById(sectionId);
        targetSection.classList.add('active');
        element.classList.add('active');

        setTimeout(() => {
            targetSection.style.opacity = '1';
            targetSection.style.transform = 'translateY(0)';
        }, 50);

        closeMobileMenu();
    }, 300);
}

// ========================================
// GESTIÓN DE PRODUCTOS
// ========================================

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
        row.setAttribute('data-id', product.id);
        
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
            <td>${window.formatearPrecio(product.price)}</td>
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

        setTimeout(() => {
            row.style.transition = 'all 0.3s ease';
            row.style.opacity = '1';
            row.style.transform = 'translateX(0)';
        }, index * 50);
    });
}

function openProductModal(id = null) {
    currentEditingProductId = id;
    const modal = document.getElementById('productModal');
    const form = document.getElementById('productForm');
    const selectCategoria = document.getElementById('productCategory');
    
    selectCategoria.innerHTML = '<option value="">Selecciona una categoría</option>';
    categorias.forEach(cat => {
        selectCategoria.innerHTML += `<option value="${cat.idCategoria}">${cat.nombreCategoria}</option>`;
    });
    
    modal.classList.add('active');
    
    if (id) {
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
        document.getElementById('modalTitle').textContent = 'Agregar Producto';
        form.reset();
        document.getElementById('productId').value = '';
    }

    const modalContent = modal.querySelector('.modal-content');
    modalContent.style.transform = 'scale(0.9)';
    modalContent.style.opacity = '0';
    setTimeout(() => {
        modalContent.style.transition = 'all 0.3s ease';
        modalContent.style.transform = 'scale(1)';
        modalContent.style.opacity = '1';
    }, 10);
}

function closeProductModal() {
    const modal = document.getElementById('productModal');
    const modalContent = modal.querySelector('.modal-content');
    
    modalContent.style.transform = 'scale(0.9)';
    modalContent.style.opacity = '0';
    
    setTimeout(() => {
        modal.classList.remove('active');
        currentEditingProductId = null;
    }, 300);
}

async function saveProduct(event) {
    event.preventDefault();
    
    const idCategoria = parseInt(document.getElementById('productCategory').value);
    const nombre = document.getElementById('productName').value.trim();
    const precio = parseInt(document.getElementById('productPrice').value);
    const stock = parseInt(document.getElementById('productStock').value);
    
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
    
    const productoDTO = {
        idCategoria: idCategoria,
        nombreProducto: nombre,
        imagen: null,
        precio: precio,
        stock: stock,
        disponible: stock > 0
    };
    
    try {
        let response;
        
        if (currentEditingProductId) {
            productoDTO.idProducto = currentEditingProductId;
            response = await authManager.fetchConAuth(
                `${window.APP_CONFIG.API_URL}/productos/${currentEditingProductId}`,
                {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(productoDTO)
                }
            );
            
            if (!response.ok) throw new Error('Error al actualizar producto');
            showNotification('Producto actualizado exitosamente', 'success');
        } else {
            response = await authManager.fetchConAuth(
                `${window.APP_CONFIG.API_URL}/productos`,
                {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(productoDTO)
                }
            );
            
            if (!response.ok) throw new Error('Error al crear producto');
            showNotification('Producto creado exitosamente', 'success');
        }
        
        await loadProducts();
        renderProducts();
        renderDashboard();
        closeProductModal();
        
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || 'Error al guardar producto', 'error');
    }
}

function editProduct(id) {
    openProductModal(id);
}

async function deleteProduct(id) {
    const product = products.find(p => p.id === id);
    
    if (!product) {
        showNotification('Producto no encontrado', 'error');
        return;
    }

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
        const response = await authManager.fetchConAuth(
            `${window.APP_CONFIG.API_URL}/productos/${id}`,
            {
                method: 'DELETE'
            }
        );
        
        if (!response.ok) throw new Error('Error al eliminar producto');
        
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

// ========================================
// DASHBOARD
// ========================================

function renderDashboard() {
    const totalSales = ventas.reduce((sum, venta) => sum + venta.total, 0);
    const totalProducts = products.length;
    const totalPurchases = ventas.length;
    const lowStock = products.filter(p => p.stock < 10).length;

    const statsCards = document.querySelectorAll('.stat-card .value');
    if (statsCards.length >= 4) {
        animateValue(statsCards[0], 0, totalSales, 1000, true);
        animateValue(statsCards[1], 0, totalProducts, 1000, false);
        animateValue(statsCards[2], 0, totalPurchases, 1000, false);
        animateValue(statsCards[3], 0, lowStock, 1000, false);
    }

    renderRecentSales();
}

function renderRecentSales() {
    const tbody = document.getElementById('dashboardSalesTable');
    if (!tbody) {
        console.error('No se encontró el elemento dashboardSalesTable');
        return;
    }

    tbody.innerHTML = '';

    if (ventas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 30px; color: #666;">No hay ventas registradas</td></tr>';
        return;
    }

    const recentSales = ventas.slice().reverse();
    console.log('Renderizando ventas:', recentSales.length);
    
    recentSales.forEach((venta, index) => {
        console.log(`Venta ${index}:`, venta); // Debug
        
        const row = document.createElement('tr');
        row.style.opacity = '0';
        row.style.cursor = 'pointer';
        row.title = 'Clic para ver detalles completos';
        
        // Validar y crear descripción de productos
        let productosInfo = 'Sin información';
        let cantidadMostrar = 0;
        
        if (venta.detalles && Array.isArray(venta.detalles) && venta.detalles.length > 0) {
            cantidadMostrar = venta.cantidadUnidades || 0;
            
            if (venta.detalles.length === 1) {
                const detalle = venta.detalles[0];
                productosInfo = `${detalle.nombreProducto || 'Producto'} (${detalle.cantidad || 0} unidades)`;
            } else {
                productosInfo = `${venta.cantidadProductos || venta.detalles.length} productos (${cantidadMostrar} unidades)`;
            }
        } else if (venta.productosDescripcion) {
            productosInfo = venta.productosDescripcion;
            cantidadMostrar = venta.cantidadUnidades || 0;
        }
        
        // Validar usuario
        const usuarioMostrar = venta.usuario || 'Usuario desconocido';
        
        // Validar total
        const totalMostrar = venta.total || 0;
        
        // Validar fecha
        const fechaMostrar = venta.date || new Date().toISOString().split('T')[0];
        
        row.innerHTML = `
            <td>${venta.id || '-'}</td>
            <td>${usuarioMostrar}</td>
            <td>${productosInfo}</td>
            <td>${cantidadMostrar}</td>
            <td><span style="color: #10b981; cursor: pointer; font-weight: 600;">Ver detalles</span></td>
            <td>${window.formatearPrecio(totalMostrar)}</td>
            <td>${formatDate(fechaMostrar)}</td>
        `;
        
        // Agregar evento click para mostrar detalles
        row.addEventListener('click', () => mostrarDetallesVenta(venta));
        
        tbody.appendChild(row);

        setTimeout(() => {
            row.style.transition = 'all 0.3s ease';
            row.style.opacity = '1';
        }, index * 50);
    });
    
    console.log(' Tabla de ventas renderizada con', recentSales.length, 'filas');
}

// Nueva función para mostrar detalles completos de una venta
function mostrarDetallesVenta(venta) {
    console.log('Mostrando detalles de venta:', venta);
    
    if (!venta.detalles || !Array.isArray(venta.detalles) || venta.detalles.length === 0) {
        Swal.fire({
            title: 'Sin detalles',
            text: 'Esta venta no tiene productos asociados',
            icon: 'info',
            confirmButtonColor: '#C2AB2D'
        });
        return;
    }
    
    // Crear tabla HTML con todos los productos
    let productosHTML = `
        <div style="max-height: 400px; overflow-y: auto;">
            <table style="width: 100%; text-align: left; border-collapse: collapse;">
                <thead style="position: sticky; top: 0; background: #f9fafb;">
                    <tr>
                        <th style="padding: 10px; border-bottom: 2px solid #e5e7eb;">Producto</th>
                        <th style="padding: 10px; border-bottom: 2px solid #e5e7eb;">Categoría</th>
                        <th style="padding: 10px; border-bottom: 2px solid #e5e7eb; text-align: center;">Cantidad</th>
                        <th style="padding: 10px; border-bottom: 2px solid #e5e7eb; text-align: right;">Precio Unit.</th>
                        <th style="padding: 10px; border-bottom: 2px solid #e5e7eb; text-align: right;">Subtotal</th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    venta.detalles.forEach(detalle => {
        const nombreProducto = detalle.nombreProducto || 'Producto sin nombre';
        const categoria = detalle.categoria || 'Sin categoría';
        const cantidad = detalle.cantidad || 0;
        const precioUnitario = detalle.precioUnitario || 0;
        const subtotal = detalle.subtotal || (cantidad * precioUnitario);
        
        productosHTML += `
            <tr style="border-bottom: 1px solid #f3f4f6;">
                <td style="padding: 10px;">${nombreProducto}</td>
                <td style="padding: 10px;">${categoria}</td>
                <td style="padding: 10px; text-align: center;">${cantidad}</td>
                <td style="padding: 10px; text-align: right;">${window.formatearPrecio(precioUnitario)}</td>
                <td style="padding: 10px; text-align: right; font-weight: 600;">${window.formatearPrecio(subtotal)}</td>
            </tr>
        `;
    });
    
    const totalVenta = venta.total || 0;
    
    productosHTML += `
                </tbody>
                <tfoot style="border-top: 2px solid #e5e7eb;">
                    <tr>
                        <td colspan="4" style="padding: 15px; text-align: right; font-weight: bold;">TOTAL:</td>
                        <td style="padding: 15px; text-align: right; font-weight: bold; color: #10b981; font-size: 18px;">
                            ${window.formatearPrecio(totalVenta)}
                        </td>
                    </tr>
                </tfoot>
            </table>
        </div>
    `;
    
    const fechaVenta = formatDate(venta.date || new Date().toISOString().split('T')[0]);
    const estadoVenta = venta.estado || 'completada';
    const usuarioVenta = venta.usuario || 'Usuario desconocido';
    const cantidadProductos = venta.cantidadProductos || venta.detalles.length;
    const cantidadUnidades = venta.cantidadUnidades || venta.detalles.reduce((sum, d) => sum + (d.cantidad || 0), 0);
    
    Swal.fire({
        title: `Detalle de Venta #${venta.id}`,
        html: `
            <div style="text-align: left; margin-bottom: 15px;">
                <p><strong>Cliente:</strong> ${usuarioVenta}</p>
                <p><strong>Fecha:</strong> ${fechaVenta}</p>
                <p><strong>Estado:</strong> <span style="color: #10b981;">${estadoVenta}</span></p>
                <p><strong>Total de productos:</strong> ${cantidadProductos} diferentes</p>
                <p><strong>Total de unidades:</strong> ${cantidadUnidades}</p>
            </div>
            ${productosHTML}
        `,
        width: '800px',
        confirmButtonText: 'Cerrar',
        confirmButtonColor: '#C2AB2D',
        customClass: {
            popup: 'swal-wide'
        }
    });
}

// ========================================
// ANIMACIONES Y EFECTOS
// ========================================

function animateValue(element, start, end, duration, isCurrency = false) {
    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        const value = Math.floor(progress * (end - start) + start);
        
        if (isCurrency) {
            element.textContent = window.formatearPrecio(value);
        } else {
            element.textContent = value;
        }
        
        if (progress < 1) {
            window.requestAnimationFrame(step);
        }
    };
    window.requestAnimationFrame(step);
}

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

function showNotification(message, type = 'success') {
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

// ========================================
// MENÚ MÓVIL
// ========================================

function createMobileMenu() {
    const sidebar = document.querySelector('.sidebar');
    if (!sidebar) return;

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

// ========================================
// UTILIDADES
// ========================================

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

// Mostrar fecha actual en header
document.getElementById('currentDate').textContent = new Date().toLocaleDateString('es-CO', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
});

// Exponer funciones globales necesarias
window.editProduct = editProduct;
window.deleteProduct = deleteProduct;