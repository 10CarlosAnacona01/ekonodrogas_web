    // Se ejecuta una sola vez al cargar la página
    
    document.addEventListener('DOMContentLoaded', function() {
    
    // Menú hamburguesa
    const menuToggle = document.getElementById('menu-toggle');
    const menu = document.querySelector('.menu');

    if (menuToggle) {
        menuToggle.addEventListener('click', () => {
        menu.classList.toggle('active');
        });
    }
    
    // Cambio de productos 
    const botones = document.querySelectorAll('.container-options span');
    const secciones = document.querySelectorAll('.container-products');

    botones.forEach(boton => {
        boton.addEventListener('click', () => {
        // Quita la clase activa de todos los botones
        botones.forEach(b => b.classList.remove('active'));
        boton.classList.add('active');

        // Oculta todas las secciones
        secciones.forEach(s => s.style.display = 'none');

        // Muestra la sección correspondiente
        const target = boton.getAttribute('data-target');
        document.getElementById(target).style.display = 'grid';
        });
    });
    });

