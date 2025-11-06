// ========== CARRITO DE COMPRAS ==========

// Carrito almacenado en memoria (se puede cambiar a localStorage después)
let cart = [];

// Inicializar carrito cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    initializeCart();
});

// Inicializar funcionalidades del carrito
function initializeCart() {
    // Cargar carrito desde localStorage si existe
    loadCartFromStorage();
    
    // Renderizar carrito inicial
    renderCart();
    
    // Event listeners
    setupCartEventListeners();
}

// Configurar event listeners
function setupCartEventListeners() {
    const cartIcon = document.getElementById('cart-icon');
    const cartBox = document.getElementById('cart-box');
    const emptyCartBtn = document.getElementById('empty-cart');
    const checkoutBtn = document.getElementById('checkout-btn');

    // Toggle carrito al hacer clic en el ícono
    if (cartIcon) {
        cartIcon.addEventListener('click', function(e) {
            e.stopPropagation();
            toggleCart();
        });
    }

    // Cerrar carrito al hacer clic fuera
    document.addEventListener('click', function(e) {
        const cartContainer = document.querySelector('.cart-container');
        if (cartBox && cartContainer && !cartContainer.contains(e.target)) {
            if (cartBox.classList.contains('active')) {
                cartBox.classList.remove('active');
            }
        }
    });

    // Vaciar carrito
    if (emptyCartBtn) {
        emptyCartBtn.addEventListener('click', emptyCart);
    }

    // Proceso de pago
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', checkout);
    }
}

// Toggle visibilidad del carrito
function toggleCart() {
    const cartBox = document.getElementById('cart-box');
    if (cartBox) {
        cartBox.classList.toggle('active');
    }
}

// Agregar producto al carrito
function addToCart(productId, productName, productPrice, productStock = 999) {
    const cartItem = cart.find(item => item.id === productId);

    if (cartItem) {
        // Verificar stock
        if (cartItem.quantity < productStock) {
            cartItem.quantity++;
        } else {
            alert('No hay suficiente stock disponible');
            return;
        }
    } else {
        cart.push({
            id: productId,
            name: productName,
            price: productPrice,
            quantity: 1,
            maxStock: productStock
        });
    }

    saveCartToStorage();
    renderCart();
    
    // Animación del ícono
    animateCartIcon();
    
    // Mostrar notificación (opcional)
    showNotification('Producto agregado al carrito');
}

// Renderizar carrito
function renderCart() {
    const cartItems = document.getElementById('cart-items');
    const cartCount = document.getElementById('cart-count');
    const cartCountText = document.getElementById('cart-count-text');
    const cartTotal = document.getElementById('cart-total');

    if (!cartItems) return;

    // Actualizar contador
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    
    if (cartCount) {
        cartCount.textContent = totalItems;
        if (totalItems > 0) {
            cartCount.classList.add('show');
        } else {
            cartCount.classList.remove('show');
        }
    }
    
    if (cartCountText) {
        cartCountText.textContent = `(${totalItems})`;
    }

    // Calcular total
    const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    if (cartTotal) {
        cartTotal.textContent = `$${total.toLocaleString('es-CO')}`;
    }

    // Renderizar items
    if (cart.length === 0) {
        cartItems.innerHTML = `
            <div class="empty-cart-message">
                <i class="bi bi-cart-x"></i>
                <p>Tu carrito está vacío</p>
            </div>
        `;
    } else {
        cartItems.innerHTML = cart.map(item => `
            <div class="cart-item" data-product-id="${item.id}">
                <div class="cart-item-image">💊</div>
                <div class="cart-item-details">
                    <div class="cart-item-name">${item.name}</div>
                    <div class="cart-item-price">$${item.price.toLocaleString('es-CO')}</div>
                    <div class="cart-item-quantity">
                        <button class="qty-btn" data-action="decrease" data-product-id="${item.id}">-</button>
                        <span>${item.quantity}</span>
                        <button class="qty-btn" data-action="increase" data-product-id="${item.id}">+</button>
                    </div>
                </div>
                <i class="bi bi-trash remove-btn" data-remove-id="${item.id}"></i>
            </div>
        `).join('');

        // Agregar event listeners a los botones dinámicos
        attachCartItemListeners();
    }
}

// Agregar event listeners a botones de items del carrito
function attachCartItemListeners() {
    // Botones de cantidad
    const qtyButtons = document.querySelectorAll('.qty-btn');
    qtyButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const productId = parseInt(this.getAttribute('data-product-id'));
            const action = this.getAttribute('data-action');
            const change = action === 'increase' ? 1 : -1;
            updateQuantity(productId, change);
        });
    });

    // Botones de eliminar
    const removeButtons = document.querySelectorAll('.remove-btn');
    removeButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const productId = parseInt(this.getAttribute('data-remove-id'));
            removeFromCart(productId);
        });
    });
}

// Actualizar cantidad de un producto
function updateQuantity(productId, change) {
    const cartItem = cart.find(item => item.id === productId);
    
    if (!cartItem) return;

    cartItem.quantity += change;

    if (cartItem.quantity <= 0) {
        removeFromCart(productId);
    } else if (cartItem.quantity > cartItem.maxStock) {
        alert('No hay suficiente stock disponible');
        cartItem.quantity = cartItem.maxStock;
    }

    saveCartToStorage();
    renderCart();
}

// Remover producto del carrito
function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    saveCartToStorage();
    renderCart();
    showNotification('Producto eliminado del carrito');
}

// Vaciar carrito completo
function emptyCart() {
    if (cart.length === 0) {
        alert('El carrito ya está vacío');
        return;
    }
    
    if (confirm('¿Estás seguro de vaciar el carrito?')) {
        cart = [];
        saveCartToStorage();
        renderCart();
        showNotification('Carrito vaciado');
    }
}

// Proceso de pago (checkout)
function checkout() {
    if (cart.length === 0) {
        alert('El carrito está vacío');
        return;
    }

    // Calcular total
    const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

    // Aquí es donde enviarías los datos al backend
    const orderData = {
        items: cart,
        total: total,
        date: new Date().toISOString()
    };

    console.log('Datos de la orden:', orderData);
    
    // Simulación de compra exitosa
    alert(`¡Compra realizada con éxito!\nTotal: $${total.toLocaleString('es-CO')}`);
    
    // Limpiar carrito
    cart = [];
    saveCartToStorage();
    renderCart();
    
    // Cerrar carrito
    const cartBox = document.getElementById('cart-box');
    if (cartBox) {
        cartBox.classList.remove('active');
    }
}

// Guardar carrito en localStorage
function saveCartToStorage() {
    try {
        // localStorage.setItem('ekonoCart', JSON.stringify(cart));
        // Por ahora solo en memoria, descomentar la línea de arriba para usar localStorage
    } catch (error) {
        console.error('Error al guardar el carrito:', error);
    }
}

// Cargar carrito desde localStorage
function loadCartFromStorage() {
    try {
        // const savedCart = localStorage.getItem('ekonoCart');
        // if (savedCart) {
        //     cart = JSON.parse(savedCart);
        // }
        // Por ahora el carrito está vacío al inicio, descomentar para usar localStorage
    } catch (error) {
        console.error('Error al cargar el carrito:', error);
        cart = [];
    }
}

// Animación del ícono del carrito
function animateCartIcon() {
    const cartIcon = document.getElementById('cart-icon');
    if (cartIcon) {
        cartIcon.style.transform = 'scale(1.3)';
        setTimeout(() => {
            cartIcon.style.transform = 'scale(1)';
        }, 300);
    }
}

// Mostrar notificación (opcional)
function showNotification(message) {
    // Puedes implementar una notificación toast aquí
    console.log('Notificación:', message);
}

// Obtener carrito actual (útil para integraciones)
function getCart() {
    return cart;
}

// Obtener total del carrito
function getCartTotal() {
    return cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
}

// Obtener cantidad total de items
function getCartItemCount() {
    return cart.reduce((sum, item) => sum + item.quantity, 0);
}
