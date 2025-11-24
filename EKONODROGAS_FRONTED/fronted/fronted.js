
// FRONTEND PRINCIPAL - EKONODROGAS

document.addEventListener('DOMContentLoaded', function() {
    inicializarMenuHamburguesa();
    inicializarCambioProductos();
});


// MENÚ HAMBURGUESA
function inicializarMenuHamburguesa() {
    const menuToggle = document.getElementById('menu-toggle');
    const menu = document.querySelector('.menu');
    
    if (!menuToggle || !menu) {
        console.warn('Menú hamburguesa: elementos no encontrados');
        return;
    }

    // Toggle del menú
    menuToggle.addEventListener('click', function(e) {
        e.stopPropagation();
        menu.classList.toggle('active');
        console.log('Menú toggled:', menu.classList.contains('active'));
    });

    // Cerrar menú al hacer clic fuera
    document.addEventListener('click', function(e) {
        if (!menu.contains(e.target) && !menuToggle.contains(e.target)) {
            menu.classList.remove('active');
        }
    });

    // Cerrar menú al hacer clic en un enlace
    const menuLinks = menu.querySelectorAll('a');
    menuLinks.forEach(link => {
        link.addEventListener('click', function() {
            menu.classList.remove('active');
        });
    });

    console.log('✓ Menú hamburguesa inicializado');
}


// CAMBIO DE PRODUCTOS (destacados/recientes/vendidos)

function inicializarCambioProductos() {
    const botones = document.querySelectorAll('.container-options span');
    const secciones = document.querySelectorAll('.top-products .container-products');

    if (botones.length === 0 || secciones.length === 0) {
        console.warn('Sistema de cambio de productos: elementos no encontrados');
        return;
    }

    botones.forEach(boton => {
        boton.addEventListener('click', function() {
            // Quitar active de todos los botones
            botones.forEach(b => b.classList.remove('active'));
            boton.classList.add('active');

            // Ocultar todas las secciones
            secciones.forEach(s => {
                s.classList.remove('active');
                s.style.display = 'none';
            });

            // Mostrar la sección correspondiente
            const target = boton.getAttribute('data-target');
            const targetSection = document.getElementById(target);
            
            if (targetSection) {
                targetSection.classList.add('active');
                targetSection.style.display = 'grid';
            }
        });
    });

    console.log('✓ Sistema de cambio de productos inicializado');
}

