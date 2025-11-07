// Esperar a que cargue el DOM
document.addEventListener('DOMContentLoaded', () => {
    const cartIcon = document.getElementById('cart-icon');
    const cartBox = document.getElementById('cart-box');

    if (cartIcon && cartBox) {
        // Mostrar/ocultar carrito al hacer clic en el ícono
        cartIcon.addEventListener('click', (e) => {
            e.stopPropagation();
            cartBox.classList.toggle('active');
        });

        // Cerrar carrito al hacer clic fuera de él
        document.addEventListener('click', (e) => {
            if (!cartBox.contains(e.target) && e.target !== cartIcon) {
                cartBox.classList.remove('active');
            }
        });
    }
});

