const express = require('express');
const path = require('path');

const app = express();

const PORT = process.env.PORT || 5501;
const PROJECT_ROOT = __dirname;
const FRONT_PAGE = path.join(PROJECT_ROOT, 'fronted', 'fronted.html');

// Serve everything under /EKONODROGAS_FRONTED exactly like the file structure on disk
app.use(
  '/EKONODROGAS_FRONTED',
  express.static(PROJECT_ROOT, {
    extensions: ['html'],
  })
);

// Helpful aliases so visiting /fronted/fronted.html also works without the prefix
app.use(
  express.static(PROJECT_ROOT, {
    extensions: ['html'],
  })
);

// Default route -> main storefront
app.get('/', (req, res) => {
  res.sendFile(FRONT_PAGE);
});

// Fallback when file not found
app.use((req, res) => {
  res.status(404).send('Recurso no encontrado. Verifica la ruta solicitada.');
});

app.listen(PORT, () => {
  console.log(`Ekonodrogas frontend listo en http://localhost:${PORT}`);
});

