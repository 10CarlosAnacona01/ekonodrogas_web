

        // Initialize
        renderStock();

        function showSection(sectionId) {
            document.querySelectorAll('.section').forEach(s => s.classList.remove('active')); /*Oculta todas las secciones*/
            document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active')); /*Quita el color de los items del menu*/
            document.getElementById(sectionId).classList.add('active'); /*Muestra la seccion seleccionada*/
            event.target.classList.add('active'); /*Da color al item seleccionado*/
        }

        function renderProducts() {
            const tbody = document.querySelector('#productsTable tbody');
            tbody.innerHTML = products.map(p => `
                <tr>
                    <td>${p.id}</td>
                    <td>${p.name}</td>
                    <td>${p.category}</td>
                    <td>$${p.price.toLocaleString()}</td>
                    <td>${p.stock}</td>
                    <td>
                        <span class="badge ${p.stock <= p.minStock ? 'badge-danger' : 'badge-success'}">
                            ${p.stock <= p.minStock ? 'Stock Bajo' : 'Disponible'}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-primary" style="padding: 6px 12px; margin-right: 5px;" onclick="editProduct(${p.id})">Editar</button>
                        <button class="btn btn-danger" style="padding: 6px 12px;" onclick="deleteProduct(${p.id})">Eliminar</button>
                    </td>
                </tr>
            `).join('');
        }

        function renderSales() {
            const tbody = document.querySelector('#salesTable tbody');
            tbody.innerHTML = sales.map(s => `
                <tr>
                    <td>#${s.id}</td>
                    <td>${s.productName}</td>
                    <td>${s.customer}</td>
                    <td>${s.quantity}</td>
                    <td>$${s.total.toLocaleString()}</td>
                    <td>${s.date}</td>
                </tr>
            `).join('');
        }

        function renderStock() {
            const tbody = document.querySelector('#stockTable tbody');
            tbody.innerHTML = products.map(p => `
                <tr>
                    <td>${p.name}</td>
                    <td>${p.stock}</td>
                    <td>${p.minStock}</td>
                    <td>
                        <span class="badge ${p.stock <= p.minStock ? 'badge-danger' : p.stock <= p.minStock * 2 ? 'badge-warning' : 'badge-success'}">
                            ${p.stock <= p.minStock ? 'Crítico' : p.stock <= p.minStock * 2 ? 'Bajo' : 'Normal'}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-success" style="padding: 6px 12px;" onclick="openStockModal(${p.id})">+ Agregar Stock</button>
                    </td>
                </tr>
            `).join('');
        }

        function openProductModal(id = null) {
            currentEditingProductId = id;
            document.getElementById('productModal').classList.add('active');
            
            if (id) {
                const product = products.find(p => p.id === id);
                document.getElementById('modalTitle').textContent = 'Editar Producto';
                document.getElementById('productId').value = product.id;
                document.getElementById('productName').value = product.name;
                document.getElementById('productCategory').value = product.category;
                document.getElementById('productPrice').value = product.price;
                document.getElementById('productStock').value = product.stock;
                document.getElementById('productDescription').value = product.description || '';
            } else {
                document.getElementById('modalTitle').textContent = 'Agregar Producto';
                document.getElementById('productForm').reset();
            }
        }

        function closeProductModal() {
            document.getElementById('productModal').classList.remove('active');
            currentEditingProductId = null;
        }

        function openStockModal(id) {
            const product = products.find(p => p.id === id);
            document.getElementById('stockModal').classList.add('active');
            document.getElementById('stockProductId').value = product.id;
            document.getElementById('stockProductName').textContent = product.name;
            document.getElementById('currentStock').textContent = product.stock;
            document.getElementById('stockAmount').value = '';
        }

