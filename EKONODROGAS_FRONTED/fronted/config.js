
// CONFIGURACIÓN CENTRALIZADA DE LA APLICACIÓN

// Configuración de la API
window.APP_CONFIG = {
    API_URL: 'http://localhost:8080/api',
    API_TIMEOUT: 10000,
    CURRENCY: 'COP',
    LOCALE: 'es-CO'
};

// Función para formatear precios (uso global)
window.formatearPrecio = function(precio) {
    return new Intl.NumberFormat(window.APP_CONFIG.LOCALE, {
        style: 'currency',
        currency: window.APP_CONFIG.CURRENCY,
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    }).format(precio);
};

console.log('Configuración de la aplicación cargada');