# ElectroMarket · Sistema de inventario (frontend)
React + TypeScript + Vite + Tailwind v4 + Recharts + Lucide. Datos mock en memoria.
```
npm install
npm run dev
```
Acceso demo: admin@electromarket.pe / admin123

- `src/services/store.ts`: capa de servicios (auth, product, inventory, supplier, report). Para pasar a una API REST, reemplaza el cuerpo de cada método por `fetch()`.
- `src/pages`: una pantalla por módulo. `src/components/ui.tsx`: Button, Badge, Modal, Confirm, Empty, Loading, Toast.
