// ========================================
// MOSTRAR ESTADO DE AUTENTICACIÓN EN HEADER
// ========================================

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    // Esperar un poco para que authManager se inicialice
    setTimeout(() => {
        actualizarHeaderAutenticacion();
    }, 200);
});

/**
 * Actualiza el header para mostrar el estado de autenticación
 */
function actualizarHeaderAutenticacion() {
    const userIcon = document.querySelector('.container-user .bi-person-fill');
    
    if (!userIcon) {
        console.warn('No se encontró el icono de usuario');
        return;
    }

    // Verificar si el usuario está autenticado
    if (typeof authManager !== 'undefined' && authManager.estaAutenticado()) {
        const usuario = authManager.obtenerUsuario();
        mostrarUsuarioAutenticado(usuario);
    } else {
        mostrarUsuarioNoAutenticado();
    }
}

/*
 * Muestra el menú cuando el usuario está autenticado
 */
function mostrarUsuarioAutenticado(usuario) {
    const containerUser = document.querySelector('.container-user');
    const userLink = containerUser.querySelector('a[href*="login"]');
    
    if (!userLink) return;

    // Reemplazar el enlace simple por un dropdown
    const nombreCorto = usuario.primerNombre || 'Usuario';
    
    const dropdownHTML = `
        <div class="user-dropdown">
            <div class="user-info" id="user-info-toggle">
                <i class="bi bi-person-fill-check"></i>
                <span class="user-name">${nombreCorto}</span>
                <i class="bi bi-chevron-down dropdown-arrow"></i>
            </div>
            <div class="user-dropdown-menu" id="user-dropdown-menu">
                <div class="dropdown-header">
                    <p class="user-full-name">${authManager.obtenerNombreCompleto()}</p>
                    <p class="user-email">${usuario.correo}</p>
                </div>
                <a href="#" class="dropdown-item logout" onclick="event.preventDefault(); cerrarSesionConfirmacion();">
                    <i class="bi bi-box-arrow-right"></i>
                    <span>Cerrar Sesión</span>
                </a>
            </div>
        </div>
    `;

    // Reemplazar el enlace con el dropdown
    userLink.outerHTML = dropdownHTML;

    // Inicializar eventos del dropdown
    inicializarDropdownUsuario();
}

/*
 * Muestra el enlace de login cuando NO está autenticado
 */
function mostrarUsuarioNoAutenticado() {
    // Ya está por defecto en el HTML
    console.log('Usuario no autenticado');
}

/**
 * Inicializa los eventos del dropdown de usuario
 */
function inicializarDropdownUsuario() {
    const toggle = document.getElementById('user-info-toggle');
    const menu = document.getElementById('user-dropdown-menu');

    if (!toggle || !menu) return;

    // Toggle del menú
    toggle.addEventListener('click', function(e) {
        e.stopPropagation();
        menu.classList.toggle('active');
    });

    // Cerrar al hacer clic fuera
    document.addEventListener('click', function(e) {
        if (!toggle.contains(e.target) && !menu.contains(e.target)) {
            menu.classList.remove('active');
        }
    });
}

/* Cierra sesión con confirmación */
async function cerrarSesionConfirmacion() {
    if (typeof Swal !== 'undefined') {
        const result = await Swal.fire({
            title: '¿Cerrar sesión?',
            text: '¿Estás seguro que deseas cerrar sesión?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#C7A17A',
            cancelButtonColor: '#6c757d',
            confirmButtonText: 'Sí, cerrar sesión',
            cancelButtonText: 'Cancelar'
        });

        if (result.isConfirmed) {
            await Swal.fire({
                title: 'Cerrando sesión...',
                text: 'Hasta pronto',
                icon: 'success',
                timer: 1500,
                showConfirmButton: false
            });
            
            authManager.cerrarSesion();
        }
    } else {
        if (confirm('¿Cerrar sesión?')) {
            authManager.cerrarSesion();
        }
    }
}

// Exportar funciones globales
window.actualizarHeaderAutenticacion = actualizarHeaderAutenticacion;
window.cerrarSesionConfirmacion = cerrarSesionConfirmacion;
