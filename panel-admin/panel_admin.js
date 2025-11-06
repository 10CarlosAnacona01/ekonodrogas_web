
// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    renderStock();
});

// Inicializar todos los event listeners
function initializeEventListeners() {
    // Event listeners para navegación
    const navItems = document.querySelectorAll('.nav-item[data-section]');
    navItems.forEach(item => {
        item.addEventListener('click', function() {
            const sectionId = this.getAttribute('data-section');
            showSection(sectionId, this);
        });
    });

    // Event listener para botón agregar producto
    const addProductBtn = document.getElementById('addProductBtn');
    if (addProductBtn) {
        addProductBtn.addEventListener('click', function() {
            openProductModal();
        });
    }

    // Event listener para cerrar modal
    const closeModalBtn = document.getElementById('closeModalBtn');
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', function() {
            closeProductModal();
        });
    }

    // Event listener para formulario de producto
    const productForm = document.getElementById('productForm');
    if (productForm) {
        productForm.addEventListener('submit', function(event) {
            saveProduct(event);
        });
    }

    // Cerrar modal al hacer clic fuera de él
    const modal = document.getElementById('productModal');
    if (modal) {
        modal.addEventListener('click', function(event) {
            if (event.target === modal) {
                closeProductModal();
            }
        });
    }
}

// Función para mostrar secciones
function showSection(sectionId, element) {
    // Oculta todas las secciones
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    
    // Quita el color de los items del menu
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    
    // Muestra la seccion seleccionada
    document.getElementById(sectionId).classList.add('active');
    
    // Da color al item seleccionado
    element.classList.add('active');
}

// Función para renderizar productos
function renderProducts() {
    const tbody = document.getElementById('productsTable');
    if (!tbody) return;
    
    tbody.innerHTML = products.map(p => `
        <tr>
            <td>${p.id}</td>
            <td>${p.category}</td>
            <td>${p.name}</td>
            <td>$${p.price.toLocaleString()}</td>
            <td>${p.stock}</td>
            <td>
                <span class="badge ${p.stock <= p.minStock ? 'badge-danger' : 'badge-success'}">
                    ${p.stock <= p.minStock ? 'Stock Bajo' : 'Disponible'}
                </span>
            </td>
            <td>
                <button class="btn btn-primary" style="padding: 6px 12px; margin-right: 5px;" data-edit-product="${p.id}">Editar</button>
                <button class="btn btn-danger" style="padding: 6px 12px;" data-delete-product="${p.id}">Eliminar</button>
            </td>
        </tr>
    `).join('');

    // Agregar event listeners a los botones de editar y eliminar
    tbody.querySelectorAll('[data-edit-product]').forEach(btn => {
        btn.addEventListener('click', function() {
            const productId = parseInt(this.getAttribute('data-edit-product'));
            editProduct(productId);
        });
    });

    tbody.querySelectorAll('[data-delete-product]').forEach(btn => {
        btn.addEventListener('click', function() {
            const productId = parseInt(this.getAttribute('data-delete-product'));
            deleteProduct(productId);
        });
    });
}

// Función para renderizar ventas
function renderSales() {
    const tbody = document.getElementById('salesTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = sales.map(s => `
        <tr>
            <td>#${s.id}</td>
            <td>${s.productName}</td>
            <td>${s.customer}</td>
            <td>${s.quantity}</td>
            <td>$${s.total.toLocaleString()}</td>
            <td>${s.date}</td>
        </tr>
    `).join('');
}

// Función para renderizar stock
function renderStock() {
    const tbody = document.querySelector('#stockTable tbody');
    if (!tbody) return;
    
    tbody.innerHTML = products.map(p => `
        <tr>
            <td>${p.name}</td>
            <td>${p.stock}</td>
            <td>${p.minStock}</td>
            <td>
                <span class="badge ${p.stock <= p.minStock ? 'badge-danger' : p.stock <= p.minStock * 2 ? 'badge-warning' : 'badge-success'}">
                    ${p.stock <= p.minStock ? 'Crítico' : p.stock <= p.minStock * 2 ? 'Bajo' : 'Normal'}
                </span>
            </td>
            <td>
                <button class="btn btn-success" style="padding: 6px 12px;" data-stock-product="${p.id}">+ Agregar Stock</button>
            </td>
        </tr>
    `).join('');

    // Agregar event listeners a los botones de agregar stock
    tbody.querySelectorAll('[data-stock-product]').forEach(btn => {
        btn.addEventListener('click', function() {
            const productId = parseInt(this.getAttribute('data-stock-product'));
            openStockModal(productId);
        });
    });
}

// Función para abrir modal de producto
function openProductModal(id = null) {
    currentEditingProductId = id;
    const modal = document.getElementById('productModal');
    modal.classList.add('active');
    
    if (id) {
        const product = products.find(p => p.id === id);
        document.getElementById('modalTitle').textContent = 'Editar Producto';
        document.getElementById('productId').value = product.id;
        document.getElementById('productName').value = product.name;
        document.getElementById('productCategory').value = product.category;
        document.getElementById('productPrice').value = product.price;
        document.getElementById('productStock').value = product.stock;
    } else {
        document.getElementById('modalTitle').textContent = 'Agregar Producto';
        document.getElementById('productForm').reset();
    }
}

// Función para cerrar modal de producto
function closeProductModal() {
    const modal = document.getElementById('productModal');
    modal.classList.remove('active');
    currentEditingProductId = null;
}

// Función para abrir modal de stock
function openStockModal(id) {
    const product = products.find(p => p.id === id);
    const stockModal = document.getElementById('stockModal');
    if (!stockModal) return;
    
    stockModal.classList.add('active');
    document.getElementById('stockProductId').value = product.id;
    document.getElementById('stockProductName').textContent = product.name;
    document.getElementById('currentStock').textContent = product.stock;
    document.getElementById('stockAmount').value = '';
}

// Función para guardar producto
function saveProduct(event) {
    event.preventDefault();
    
    // Aquí irá la lógica para guardar el producto cuando se implemente el backend
    console.log('Guardando producto...');
    
    closeProductModal();
}

// Función para editar producto
function editProduct(id) {
    openProductModal(id);
}

// Función para eliminar producto
function deleteProduct(id) {
    if (confirm('¿Está seguro de que desea eliminar este producto?')) {
        // Aquí irá la lógica para eliminar el producto cuando se implemente el backend
        console.log('Eliminando producto con ID:', id);
    }
}

