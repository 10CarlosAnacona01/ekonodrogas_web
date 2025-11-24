
// CONFIGURACIÓN
const API_URL = 'http://localhost:8080/api';


// ELEMENTOS DEL DOM
const btnSignIn = document.getElementById('btn-sign-in');
const btnSignUp = document.getElementById('btn-sign-up');
const forms = document.getElementById('forms');
const sidebar = document.getElementById('sidebar');
const signInForm = document.getElementById('sign-in');
const signUpForm = document.getElementById('sign-up');
const container = document.getElementById('container');
const linkSignIn = document.getElementById('link-sign-in');
const linkSignUp = document.getElementById('link-sign-up');


// EVENT LISTENERS PARA CAMBIAR FORMULARIOS

linkSignUp.addEventListener('click', (e) => {
    e.preventDefault();
    changeSignIn();
});

linkSignIn.addEventListener('click', (e) => {
    e.preventDefault();
    changeSignUp();
});

btnSignIn.addEventListener('click', () => {
    changeSignIn();
});

btnSignUp.addEventListener('click', () => {
    changeSignUp();
});


// FUNCIONES DE ANIMACIÓN

// Cambiar formulario Inicio de sesión 
function changeSignIn() {
    forms.classList.remove('active');
    sidebar.classList.remove('active');
    container.style.animation = 'none';
    container.style.animation = 'brounce-up 1s ease';
    transition(signInForm);
}

// Cambiar formulario de registro
function changeSignUp() {
    forms.classList.add('active');
    sidebar.classList.add('active');
    container.style.animation = 'none';
    container.style.animation = 'brounce-down 1s ease';
    transition(signUpForm);
}

// Recibe un elemento y obtiene los hijos de ese elemento
function transition(parent) {
    const children = parent.children;

    Array.from(children).forEach((child) => {
        child.style.opacity = '0';
        child.style.animation = 'none';
    });

    setTimeout(() => {
        Array.from(children).forEach((child, index) => {
            child.style.animation = 'slideIn 0.35s ease forwards';
            let delay = (index * 0.05) + 's';
            child.style.animationDelay = delay;
        });
    }, 300);
}


// FUNCIONES DE AUTENTICACIÓN

/**
 * Muestra un mensaje de alerta temporal
 */
function mostrarMensaje(mensaje, tipo, formulario) {
    // Buscar si ya existe un mensaje
    let alertaExistente = formulario.querySelector('.mensaje-alerta');
    if (alertaExistente) {
        alertaExistente.remove();
    }

    // Crear el elemento de alerta
    const alerta = document.createElement('div');
    alerta.className = `mensaje-alerta ${tipo}`;
    alerta.textContent = mensaje;
    
    // Estilos inline para la alerta
    alerta.style.cssText = `
        padding: 12px;
        margin: 15px 0;
        border-radius: 8px;
        font-size: 14px;
        text-align: center;
        animation: slideIn 0.3s ease;
    `;

    if (tipo === 'error') {
        alerta.style.backgroundColor = '#fee';
        alerta.style.color = '#c33';
        alerta.style.border = '1px solid #fcc';
    } else if (tipo === 'exito') {
        alerta.style.backgroundColor = '#efe';
        alerta.style.color = '#3c3';
        alerta.style.border = '1px solid #cfc';
    }

    // Insertar la alerta después del título h2
    const titulo = formulario.querySelector('h2');
    titulo.insertAdjacentElement('afterend', alerta);

    // Eliminar la alerta después de 5 segundos
    setTimeout(() => {
        if (alerta.parentNode) {
            alerta.remove();
        }
    }, 5000);
}

// Deshabilita un botón durante una operación
function deshabilitarBoton(boton, texto = 'Procesando...') {
    boton.disabled = true;
    boton.dataset.textoOriginal = boton.textContent;
    boton.textContent = texto;
    boton.style.opacity = '0.6';
    boton.style.cursor = 'not-allowed';
}

// Habilita un botón después de una operación
function habilitarBoton(boton) {
    boton.disabled = false;
    boton.textContent = boton.dataset.textoOriginal || 'Enviar';
    boton.style.opacity = '1';
    boton.style.cursor = 'pointer';
}

// Valida el formato del email
function validarEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

// Valida la longitud de la contraseña
function validarContrasena(contrasena) {
    return contrasena.length >= 8;
}

// Maneja el inicio de sesión
async function handleLogin(event) {
    event.preventDefault();

    const emailInput = signInForm.querySelector('#email-login');
    const passwordInput = signInForm.querySelector('#password-login');
    const boton = signInForm.querySelector('button[type="submit"], button:not([type])');

    const correo = emailInput.value.trim();
    const contrasena = passwordInput.value;

    // Validaciones
    if (!correo || !contrasena) {
        mostrarMensaje('Por favor, completa todos los campos', 'error', signInForm);
        return;
    }

    if (!validarEmail(correo)) {
        mostrarMensaje('Por favor, ingresa un correo válido', 'error', signInForm);
        return;
    }

    deshabilitarBoton(boton, 'Iniciando sesión...');

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ correo, contrasena })
        });

        const data = await response.json();

        if (response.ok) {
            // Guardar token y datos del usuario
            localStorage.setItem('token', data.token);
            localStorage.setItem('usuario', JSON.stringify(data.usuario));

            mostrarMensaje('¡Inicio de sesión exitoso! Redirigiendo...', 'exito', signInForm);

            // Redirigir después de 1.5 segundos
            setTimeout(() => {
                window.location.href = '/EKONODROGAS_FRONTED/fronted/index.html';
            }, 1500);
        } else {
            mostrarMensaje(data.mensaje || 'Credenciales incorrectas', 'error', signInForm);
            habilitarBoton(boton);
        }
    } catch (error) {
        console.error('Error al iniciar sesión:', error);
        mostrarMensaje('Error de conexión con el servidor', 'error', signInForm);
        habilitarBoton(boton);
    }
}

// Maneja el registro de un nuevo usuario
async function handleRegistro(event) {
    event.preventDefault();

    const nameInput = signUpForm.querySelector('#name');
    const emailInput = signUpForm.querySelector('#email-register');
    const passwordInput = signUpForm.querySelector('#password-register');
    const boton = signUpForm.querySelector('.btn-register');

    const nombreCompleto = nameInput.value.trim();
    const correo = emailInput.value.trim();
    const contrasena = passwordInput.value;

    // Validaciones
    if (!nombreCompleto || !correo || !contrasena) {
        mostrarMensaje('Por favor, completa todos los campos', 'error', signUpForm);
        return;
    }

    if (!validarEmail(correo)) {
        mostrarMensaje('Por favor, ingresa un correo válido', 'error', signUpForm);
        return;
    }

    if (!validarContrasena(contrasena)) {
        mostrarMensaje('La contraseña debe tener al menos 8 caracteres', 'error', signUpForm);
        return;
    }

    // Dividir el nombre completo en partes
    const partesNombre = nombreCompleto.split(' ').filter(parte => parte.length > 0);
    
    let primerNombre = '';
    let segundoNombre = '';
    let primerApellido = '';
    let segundoApellido = '';

    if (partesNombre.length === 1) {
        primerNombre = partesNombre[0];
    } else if (partesNombre.length === 2) {
        primerNombre = partesNombre[0];
        primerApellido = partesNombre[1];
    } else if (partesNombre.length === 3) {
        primerNombre = partesNombre[0];
        primerApellido = partesNombre[1];
        segundoApellido = partesNombre[2];
    } else if (partesNombre.length >= 4) {
        primerNombre = partesNombre[0];
        segundoNombre = partesNombre[1];
        primerApellido = partesNombre[2];
        segundoApellido = partesNombre.slice(3).join(' ');
    }

    const registro = {
        primerNombre,
        segundoNombre,
        primerApellido,
        segundoApellido,
        correo,
        contrasena
    };

    deshabilitarBoton(boton, 'Registrando...');

    try {
        const response = await fetch(`${API_URL}/auth/registro`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(registro)
        });

        const data = await response.json();

        if (response.ok) {
            // Guardar token y datos del usuario
            localStorage.setItem('token', data.token);
            localStorage.setItem('usuario', JSON.stringify(data.usuario));

            mostrarMensaje('¡Registro exitoso! Redirigiendo...', 'exito', signUpForm);

            // Redirigir después de 1.5 segundos
            setTimeout(() => {
                window.location.href = '/EKONODROGAS_FRONTED/fronted/index.html';
            }, 1500);
        } else {
            mostrarMensaje(data.mensaje || 'Error al registrar usuario', 'error', signUpForm);
            habilitarBoton(boton);
        }
    } catch (error) {
        console.error('Error al registrar:', error);
        mostrarMensaje('Error de conexión con el servidor', 'error', signUpForm);
        habilitarBoton(boton);
    }
}

// Maneja el login con Google
function loginConGoogle(event) {
    event.preventDefault();

    console.log('Iniciando login con Google...');

    // Redirigir al endpoint de OAuth2 de Google
    window.location.href = `${API_URL.replace('/api', '')}/oauth2/authorization/google`;
}


// ASIGNAR EVENT LISTENERS A LOS FORMULARIOS

// Formulario de Login
signInForm.addEventListener('submit', handleLogin);

// Formulario de Registro
signUpForm.addEventListener('submit', handleRegistro);

// Botón de Google en Login
const googleLoginBtn = signInForm.querySelector('.google');
if (googleLoginBtn) {
    googleLoginBtn.addEventListener('click', loginConGoogle);
}


// VERIFICAR ERRORES EN LA URL AL CARGAR
window.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');

    // Mostrar error si viene de un callback fallido
    if (error) {
        const errorMsg = decodeURIComponent(error).replace(/_/g, ' ');
        mostrarMensaje('Error de autenticación: ' + errorMsg, 'error', signInForm);
        
        // Limpiar la URL
        window.history.replaceState({}, document.title, window.location.pathname);
    }

    // Verificar si el usuario ya está autenticado
    const tokenGuardado = localStorage.getItem('token');
    if (tokenGuardado) {
        validarToken(tokenGuardado);
    }
});


// VERIFICAR SI HAY TOKEN EN LA URL (OAuth2 Callback)
window.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    const error = urlParams.get('error');

    if (token) {
        localStorage.setItem('token', token);
        mostrarMensaje('¡Login con Google exitoso! Redirigiendo...', 'exito', signInForm);

        setTimeout(() => {
            window.location.href = '/EKONODROGAS_FRONTED/fronted/index.html';
        }, 1500);
    } else if (error) {
        mostrarMensaje('Error al iniciar sesión con Google: ' + error, 'error', signInForm);
    }

    // Verificar si el usuario ya está autenticado
    const tokenGuardado = localStorage.getItem('token');
    if (tokenGuardado) {
        // Opcional: Validar el token con el backend
        validarToken(tokenGuardado);
    }
});

// Valida el token guardado
async function validarToken(token) {
    try {
        const response = await fetch(`${API_URL}/auth/validar`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            // Token válido, podría redirigir automáticamente
            console.log('Token válido, usuario autenticado');
        } else {
            // Token inválido, limpiar localStorage
            localStorage.removeItem('token');
            localStorage.removeItem('usuario');
        }
    } catch (error) {
        console.error('Error al validar token:', error);
    }
}


// FUNCIÓN AUXILIAR PARA PETICIONES AUTENTICADAS

// Realiza una petición fetch con el token de autenticación
async function fetchConToken(url, options = {}) {
    const token = localStorage.getItem('token');

    if (!options.headers) {
        options.headers = {};
    }

    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const response = await fetch(url, options);

        // Si el token es inválido (401), redirigir al login
        if (response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('usuario');
            window.location.href = '/EKONODROGAS_FRONTED/login/login.html';
        }

        return response;
    } catch (error) {
        console.error('Error en la petición:', error);
        throw error;
    }
}

// Exportar la función para usarla en otros archivos
window.fetchConToken = fetchConToken;