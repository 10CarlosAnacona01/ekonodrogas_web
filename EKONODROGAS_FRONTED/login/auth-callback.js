// ========================================
// AUTH CALLBACK - MANEJO DE OAUTH2
// ========================================

console.log('auth-callback.js cargado');

// Ejecutar inmediatamente al cargar
(function() {
    // Obtener parámetros de la URL
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    const error = urlParams.get('error');

    console.log('Callback recibido:', { 
        token: token ? 'presente' : 'ausente', 
        tokenLength: token ? token.length : 0,
        error: error || 'ninguno',
        fullURL: window.location.href
    });

    const container = document.getElementById('loader-container');

    if (token) {
        // PASO 1: Guardar token INMEDIATAMENTE
        localStorage.setItem('token', token);
        console.log('✓ Token guardado en localStorage');
        
        // PASO 2: Verificar que se guardó
        const tokenVerificacion = localStorage.getItem('token');
        if (!tokenVerificacion) {
            console.error('❌ Error: Token no se guardó correctamente');
            mostrarError(container, 'Error al guardar sesión');
            return;
        }
        console.log('✓ Verificación de guardado exitosa');
        
        // PASO 3: Obtener info del usuario
        obtenerInfoUsuario(token)
            .then(usuario => {
                if (usuario) {
                    console.log('✓ Usuario autenticado:', usuario.correo);
                }
                
                // PASO 4: Actualizar UI
                if (container) {
                    container.innerHTML = `
                        <img src="/EKONODROGAS_FRONTED/imagenes/solo_logo.jpg" alt="Logo" class="logo">
                        <div class="success-icon">✓</div>
                        <h2>¡Autenticación Exitosa!</h2>
                        <p>Redirigiendo a la página principal...</p>
                    `;
                }
                
                // PASO 5: Redirigir después de 1.5 segundos
                setTimeout(() => {
                    window.location.href = '/EKONODROGAS_FRONTED/fronted/fronted.html';
                }, 1500);
            })
            .catch(err => {
                console.error('Error al obtener usuario:', err);
                // Continuar de todos modos si hay token
                setTimeout(() => {
                    window.location.href = '/EKONODROGAS_FRONTED/fronted/fronted.html';
                }, 1500);
            });
        
    } else if (error) {
        // Mostrar el error
        const errorMessage = decodeURIComponent(error).replace(/_/g, ' ');
        mostrarError(container, errorMessage);
        
        console.error('Error de autenticación:', errorMessage);
        
        // Redirigir a login después de 3 segundos
        setTimeout(() => {
            window.location.href = '/EKONODROGAS_FRONTED/login/login.html';
        }, 3000);
        
    } else {
        // Sin token ni error, redirigir al login
        console.warn('No se recibió token ni error');
        window.location.href = '/EKONODROGAS_FRONTED/login/login.html';
    }
})();

// Función para obtener información del usuario
async function obtenerInfoUsuario(token) {
    try {
        const response = await fetch('http://localhost:8080/api/auth/me', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const usuario = await response.json();
        
        // Guardar información del usuario
        localStorage.setItem('usuario', JSON.stringify(usuario));
        console.log('✓ Usuario guardado en localStorage');
        
        return usuario;
    } catch (error) {
        console.error('Error al obtener info del usuario:', error);
        throw error;
    }
}

// Función para mostrar errores
function mostrarError(container, mensaje) {
    if (container) {
        container.innerHTML = `
            <img src="/EKONODROGAS_FRONTED/imagenes/solo_logo.jpg" alt="Logo" class="logo">
            <div class="error-icon">❌</div>
            <h2>Error de Autenticación</h2>
            <p>${mensaje}</p>
            <p>Redirigiendo al inicio de sesión...</p>
        `;
    }
}