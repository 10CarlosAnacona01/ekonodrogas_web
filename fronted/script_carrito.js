document.addEventListener('DOMContentLoaded', () => {
    const cartIcon = document.querySelector('.bi-cart4');
    const cartBox = document.querySelector('.buy-card');

    if (!cartIcon || !cartBox) return;

    // Mostrar / ocultar carrito al hacer clic en el ícono
    cartIcon.addEventListener('click', (e) => {
        e.stopPropagation(); // Evita que el clic cierre inmediatamente
        cartBox.classList.toggle('active');
    });

    // Cerrar carrito al hacer clic fuera
    document.addEventListener('click', (e) => {
        if (!cartBox.contains(e.target) && !cartIcon.contains(e.target)) {
            cartBox.classList.remove('active');
        }
    });
});
