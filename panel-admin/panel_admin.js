
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
