    // Configuración de la API
    const API_URL = "http://localhost:8080/api";

    // Variables globales
    let cartTotal = 0;
    let userId = 1; // Cambiar según el usuario logueado

    // Cargar el total del carrito al iniciar
    async function loadCartTotal() {
    try {
        const response = await fetch(`${API_URL}/carrito/${userId}`);
        const cart = await response.json();
        cartTotal = cart.total || 0;
        document.getElementById(
        "totalAmount"
        ).textContent = `$${cartTotal.toLocaleString("es-CO")}`;
    } catch (error) {
        console.error("Error al cargar el carrito:", error);
        showModal(false, "Error", "No se pudo cargar el carrito");
    }
    }

    // Formatear número de tarjeta con espacios
    document.getElementById("cardNumber").addEventListener("input", function (e) {
    let value = e.target.value.replace(/\s/g, "");
    let formattedValue = value.match(/.{1,4}/g)?.join(" ") || value;
    e.target.value = formattedValue;

    // Actualizar vista previa
    document.getElementById("cardNumberDisplay").textContent =
        formattedValue || "•••• •••• •••• ••••";
    });

    // Formatear fecha de vencimiento
    document.getElementById("cardExpiry").addEventListener("input", function (e) {
    let value = e.target.value.replace(/\D/g, "");
    if (value.length >= 2) {
        value = value.slice(0, 2) + "/" + value.slice(2, 4);
    }
    e.target.value = value;
    document.getElementById("cardExpiryDisplay").textContent = value || "MM/AA";
    });

    // Solo números en CVV
    document.getElementById("cardCvv").addEventListener("input", function (e) {
    e.target.value = e.target.value.replace(/\D/g, "");
    });

    // Actualizar nombre del titular
    document.getElementById("cardHolder").addEventListener("input", function (e) {
    document.getElementById("cardHolderDisplay").textContent =
        e.target.value.toUpperCase() || "NOMBRE COMPLETO";
    });

    // Validar formulario
    function validateForm() {
    let isValid = true;

    // Validar nombre
    const holder = document.getElementById("cardHolder");
    if (holder.value.trim().length < 3) {
        showError("errorHolder", holder);
        isValid = false;
    } else {
        hideError("errorHolder", holder);
    }

    // Validar número de tarjeta
    const cardNumber = document
        .getElementById("cardNumber")
        .value.replace(/\s/g, "");
    const numberInput = document.getElementById("cardNumber");
    if (!/^\d{16}$/.test(cardNumber)) {
        showError("errorNumber", numberInput);
        isValid = false;
    } else {
        hideError("errorNumber", numberInput);
    }

    // Validar fecha
    const expiry = document.getElementById("cardExpiry");
    if (!/^\d{2}\/\d{2}$/.test(expiry.value)) {
        showError("errorExpiry", expiry);
        isValid = false;
    } else {
        hideError("errorExpiry", expiry);
    }

    // Validar CVV
    const cvv = document.getElementById("cardCvv");
    if (!/^\d{3,4}$/.test(cvv.value)) {
        showError("errorCvv", cvv);
        isValid = false;
    } else {
        hideError("errorCvv", cvv);
    }

    return isValid;
    }

    function showError(errorId, input) {
    document.getElementById(errorId).classList.add("show");
    input.classList.add("error");
    }

    function hideError(errorId, input) {
    document.getElementById(errorId).classList.remove("show");
    input.classList.remove("error");
    }

    // Procesar pago
    document
    .getElementById("paymentForm")
    .addEventListener("submit", async function (e) {
        e.preventDefault();

        if (!validateForm()) {
        return;
        }

        const btnPay = document.getElementById("btnPay");
        const loading = document.getElementById("loading");

        btnPay.disabled = true;
        loading.classList.add("show");

        const paymentData = {
        idUsuario: userId,
        nombreTitular: document.getElementById("cardHolder").value,
        numeroTarjeta: document
            .getElementById("cardNumber")
            .value.replace(/\s/g, ""),
        fechaVencimiento: document.getElementById("cardExpiry").value,
        cvv: document.getElementById("cardCvv").value,
        montoTotal: cartTotal,
        };

        try {
        const response = await fetch(`${API_URL}/pagos/procesar`, {
            method: "POST",
            headers: {
            "Content-Type": "application/json",
            },
            body: JSON.stringify(paymentData),
        });

        const data = await response.json();

        if (response.ok) {
            showModal(
            true,
            "¡Pago Exitoso!",
            `Tu pago de $${data.montoTotal.toLocaleString(
                "es-CO"
            )} ha sido procesado correctamente.
                            <br><br><strong>Autorización:</strong> ${
                            data.numeroAutorizacion
                            }`,
            data.comprobanteBase64
            );
        } else {
            showModal(
            false,
            "Pago Rechazado",
            data.error || "No se pudo procesar el pago. Verifica tus datos."
            );
        }
        } catch (error) {
        console.error("Error:", error);
        showModal(
            false,
            "Error de Conexión",
            "No se pudo conectar con el servidor. Intenta nuevamente."
        );
        } finally {
        btnPay.disabled = false;
        loading.classList.remove("show");
        }
    });

    // Mostrar modal de resultado
    function showModal(success, title, message, pdfBase64 = null) {
    const modal = document.getElementById("resultModal");
    const iconContainer = document.getElementById("iconContainer");
    const modalTitle = document.getElementById("modalTitle");
    const modalMessage = document.getElementById("modalMessage");
    const modalButtons = document.getElementById("modalButtons");

    iconContainer.innerHTML = success
        ? '<div class="success-icon">✓</div>'
        : '<div class="error-icon">✗</div>';

    modalTitle.textContent = title;
    modalMessage.innerHTML = message;

    if (success && pdfBase64) {
        modalButtons.innerHTML = `
                        <button class="btn-download" onclick="downloadPDF('${pdfBase64}')">
                            📄 Descargar Comprobante
                        </button>
                        <button class="btn-close" onclick="closeModal()">Cerrar</button>
                    `;
    } else {
        modalButtons.innerHTML = `
                        <button class="btn-close" onclick="closeModal()">Cerrar</button>
                    `;
    }

    modal.classList.add("show");
    }

    // Descargar PDF
    function downloadPDF(base64) {
    const linkSource = `data:application/pdf;base64,${base64}`;
    const downloadLink = document.createElement("a");
    const fileName = `Comprobante_${Date.now()}.pdf`;

    downloadLink.href = linkSource;
    downloadLink.download = fileName;
    downloadLink.click();
    }

    // Cerrar modal
    function closeModal() {
    document.getElementById("resultModal").classList.remove("show");
    window.location.href = '/EKONODROGAS_FRONTED/fronted/fronted.html';
    }

    // Cargar total al iniciar
    loadCartTotal();
