import { Product, Movement, Supplier } from '../types';


export const today = () => new Date().toISOString().slice(0, 10);


export const daysAgo = (n: number) =>
  new Date(Date.now() - n * 864e5).toISOString().slice(0, 10);


const createProduct = (
  index: number,
  sku: string,
  name: string,
  category: string,
  brand: string,
  price: number,
  location: string,
  stock: number,
  min: number
): Product => ({
  id: 'p' + index,
  sku,
  name,
  category,
  brand,
  price,
  location,
  stock,
  min,
  active: true,
});

// Listado inicial de productos 
export const products0: Product[] = [
  createProduct(1, 'CEL-001', 'iPhone 15 128GB', 'Celulares', 'Apple', 3899, 'A-01', 18, 8),
  createProduct(2, 'CEL-002', 'Samsung Galaxy A54', 'Celulares', 'Samsung', 1499, 'A-02', 7, 10),
  createProduct(3, 'CEL-003', 'Xiaomi Redmi Note 13', 'Celulares', 'Xiaomi', 899, 'A-03', 26, 10),
  createProduct(4, 'AUD-001', 'Audífonos Bluetooth Soundcore', 'Audio', 'Anker', 159, 'B-01', 34, 12),
  createProduct(5, 'AUD-002', 'Parlante Bluetooth Flip 6', 'Audio', 'JBL', 449, 'B-02', 4, 8),
  createProduct(6, 'CAB-001', 'Cable USB-C 1m', 'Accesorios', 'Baseus', 19, 'C-01', 2, 10),
  createProduct(7, 'CAR-001', 'Cargador rápido 65W', 'Accesorios', 'Anker', 119, 'C-02', 21, 10),
  createProduct(8, 'MEM-001', 'Memoria USB 64GB', 'Almacenamiento', 'Kingston', 35, 'D-01', 40, 15),
  createProduct(9, 'MEM-002', 'MicroSD 128GB', 'Almacenamiento', 'SanDisk', 59, 'D-02', 9, 15),
  createProduct(10, 'PER-001', 'Teclado mecánico K552', 'Periféricos', 'Redragon', 169, 'E-01', 15, 6),
  createProduct(11, 'PER-002', 'Mouse Logitech M185', 'Periféricos', 'Logitech', 49, 'E-02', 3, 10),
  createProduct(12, 'WEA-001', 'Smartwatch Amazfit Bip 5', 'Wearables', 'Amazfit', 299, 'F-01', 12, 5),
];

// Listado inicial de proveedores 
export const suppliers0: Supplier[] = [
  {
    ruc: '20512345678',
    name: 'Distribuidora Tecnoperú S.A.C.',
    contact: 'Marcos Rivas',
    phone: '987654321',
    email: 'ventas@tecnoperu.pe',
    lead: 3,
    active: true,
  },
  {
    ruc: '20498765432',
    name: 'Importaciones Digitales E.I.R.L.',
    contact: 'Lucía Paredes',
    phone: '956123456',
    email: 'lparedes@impdigital.pe',
    lead: 7,
    active: true,
  },
  {
    ruc: '20601234567',
    name: 'Global Mobile Perú S.A.',
    contact: 'Andrés Quispe',
    phone: '944332211',
    email: 'aquispe@globalmobile.pe',
    lead: 5,
    active: true,
  },
  {
    ruc: '20555667788',
    name: 'Accesorios Lima Norte S.R.L.',
    contact: 'Rosa Medina',
    phone: '933221100',
    email: 'rmedina@aln.pe',
    lead: 2,
    active: false,
  },
];

// Generador automático de movimientos simulados 
export function buildMovements(productsList: Product[]): Movement[] {
  const operations: ['Salida' | 'Entrada', string][] = [
    ['Salida', 'Venta'],
    ['Entrada', 'Compra'],
    ['Salida', 'Venta'],
    ['Salida', 'Traslado'],
    ['Entrada', 'Compra'],
    ['Salida', 'Venta'],
    ['Salida', 'Merma/Deterioro'],
    ['Entrada', 'Compra'],
  ];

  const generatedMovements: Movement[] = [];

  productsList.forEach((product, productIndex) => {
    let currentStock = product.stock;
    const productMovements: Movement[] = [];

    operations.forEach(([type, reason], i) => {
      const quantity = Math.max(
        1,
        Math.min(type === 'Entrada' ? currentStock || 3 : 99, 3 + ((productIndex * 7 + i * 5) % 11))
      );

      productMovements.push({
        id: 0,
        date: daysAgo(1 + i * 3 + (productIndex % 3)),
        doc: (type === 'Entrada' ? 'FAC-' : 'GRE-') + (1000 + productIndex * 10 + i),
        type,
        reason,
        productId: product.id,
        qty: quantity,
        balance: currentStock,
        user: ['Carla Soto', 'Jorge Ramos'][(productIndex + i) % 2],
      });

      currentStock += type === 'Entrada' ? -quantity : quantity;
    });

    generatedMovements.push(...productMovements.reverse());
  });

  return generatedMovements
    .sort((a, b) => a.date.localeCompare(b.date))
    .map((movement, index) => ({ ...movement, id: index + 1 }));
}

export const movements0 = buildMovements(products0);