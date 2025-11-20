        // Obtener parámetros de la URL
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');
        const error = urlParams.get('error');

        if (token) {
            // Guardar el token en localStorage
            localStorage.setItem('token', token);
            
            // Redirigir a la página principal
            setTimeout(() => {
                window.location.href = '/EKONODROGAS_FRONTED/fronted/fronted.html';
            }, 15000);
        } else if (error) {
            // Si hay un error, redirigir a la página de login con el mensaje de error
            setTimeout(() => {
                window.location.href = `/EKONODROGAS_FRONTED/login/login.html?error=${encodeURIComponent(error)}`;
            }, 15000);
        } else {
            // Si no hay token ni error, redirigir al login
            setTimeout(() => {
                window.location.href = '/EKONODROGAS_FRONTED/login/login.html';
            }, 1500);
        }