// ========================================
// GESTOR CENTRAL DE AUTENTICACIÓN
// ========================================

class AuthManager {
    constructor() {
        this.usuario = null;
        this.token = null;
        this.inicializar();
    }

    inicializar() {
        const token = localStorage.getItem('token');
        const usuarioStr = localStorage.getItem('usuario');

        if (token && usuarioStr) {
            this.token = token;
            this.usuario = JSON.parse(usuarioStr);
            this.validarTokenAutomaticamente();
        }
    }

    estaAutenticado() {
        return this.token !== null && this.usuario !== null;
    }

    obtenerUsuario() {
        return this.usuario;
    }

    obtenerIdUsuario() {
        return this.usuario ? this.usuario.idUsuario : null;
    }

    obtenerToken() {
        return this.token;
    }

    obtenerRol() {
        return this.usuario ? this.usuario.idRol : null;
    }

    esAdministrador() {
        return this.usuario && this.usuario.idRol === 1;
    }

    esUsuario() {
        return this.usuario && this.usuario.idRol === 2;
    }

    obtenerNombreCompleto() {
        if (!this.usuario) return '';
        
        const { primerNombre, segundoNombre, primerApellido, segundoApellido } = this.usuario;
        let nombre = primerNombre;
        if (segundoNombre) nombre += ' ' + segundoNombre;
        nombre += ' ' + primerApellido;
        if (segundoApellido) nombre += ' ' + segundoApellido;
        
        return nombre;
    }

    guardarSesion(token, usuario) {
        this.token = token;
        this.usuario = usuario;
        localStorage.setItem('token', token);
        localStorage.setItem('usuario', JSON.stringify(usuario));
        console.log('Sesión guardada:', { usuario: usuario.correo, id: usuario.idUsuario });
    }

    cerrarSesion() {
        this.token = null;
        this.usuario = null;
        localStorage.removeItem('token');
        localStorage.removeItem('usuario');
        
        if (typeof limpiarCarritoLocal === 'function') {
            limpiarCarritoLocal();
        }
        
        console.log('Sesión cerrada');
        window.location.href = '/EKONODROGAS_FRONTED/login/login.html';
    }

    async validarToken() {
        if (!this.token) return false;

        try {
            const response = await fetch(`${window.APP_CONFIG.API_URL}/auth/validar`, {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${this.token}` }
            });

            if (!response.ok) {
                this.cerrarSesion();
                return false;
            }

            const data = await response.json();
            return data.valido === true;
        } catch (error) {
            console.error('Error al validar token:', error);
            this.cerrarSesion();
            return false;
        }
    }

    async validarTokenAutomaticamente() {
        const esValido = await this.validarToken();
        if (!esValido) {
            console.log('Token inválido, cerrando sesión...');
        }
    }

    requiereAutenticacion(mostrarMensaje = true) {
        if (!this.estaAutenticado()) {
            if (mostrarMensaje) {
                this.mostrarMensajeLogin();
            }
            return false;
        }
        return true;
    }

    mostrarMensajeLogin() {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                title: 'Inicia sesión',
                text: 'Debes iniciar sesión para realizar esta acción',
                icon: 'info',
                showCancelButton: true,
                confirmButtonText: 'Ir al login',
                cancelButtonText: 'Cancelar',
                confirmButtonColor: '#C7A17A'
            }).then((result) => {
                if (result.isConfirmed) {
                    sessionStorage.setItem('redirectAfterLogin', window.location.href);
                    window.location.href = '/EKONODROGAS_FRONTED/login/login.html';
                }
            });
        } else {
            alert('Debes iniciar sesión para realizar esta acción');
            sessionStorage.setItem('redirectAfterLogin', window.location.href);
            window.location.href = '/EKONODROGAS_FRONTED/login/login.html';
        }
    }

    redirigirDespuesDeLogin() {
        const redirectUrl = sessionStorage.getItem('redirectAfterLogin');
        sessionStorage.removeItem('redirectAfterLogin');
        
        if (redirectUrl) {
            window.location.href = redirectUrl;
        } else {
            window.location.href = '/EKONODROGAS_FRONTED/fronted/fronted.html';
        }
    }

    async fetchConAuth(url, options = {}) {
        if (!this.token) {
            throw new Error('No hay token de autenticación');
        }

        if (!options.headers) {
            options.headers = {};
        }

        options.headers['Authorization'] = `Bearer ${this.token}`;

        try {
            const response = await fetch(url, options);

            if (response.status === 401) {
                console.error('Token inválido, cerrando sesión');
                this.cerrarSesion();
                throw new Error('Sesión expirada');
            }

            return response;
        } catch (error) {
            console.error('Error en petición autenticada:', error);
            throw error;
        }
    }
}

// Instancia global
const authManager = new AuthManager();

// Funciones globales exportadas
window.authManager = authManager;
window.estaAutenticado = () => authManager.estaAutenticado();
window.obtenerUsuarioActual = () => authManager.obtenerUsuario();
window.obtenerIdUsuarioActual = () => authManager.obtenerIdUsuario();
window.requiereAutenticacion = (mostrarMensaje = true) => authManager.requiereAutenticacion(mostrarMensaje);
window.cerrarSesion = () => authManager.cerrarSesion();
window.fetchConAuth = (url, options) => authManager.fetchConAuth(url, options);

console.log('AuthManager inicializado');