import { useSyncExternalStore } from 'react';
import { Product, Movement, Supplier } from '../types';
import { products0, suppliers0, movements0, today } from '../data/mock';

/* 
  Capa de datos mock. 
  Para conectar un backend real, reemplaza el cuerpo de cada método por peticiones fetch() a tu API REST.
*/
let products = [...products0];
let movements = [...movements0];
let suppliers = [...suppliers0];
let storeVersion = 0;

const listeners = new Set<() => void>();
const notifyListeners = () => {
  storeVersion++;
  listeners.forEach((listener) => listener());
};

// Hook personalizado para sincronizar el estado global en toda la aplicación
export const useStore = () =>
  useSyncExternalStore(
    (listener) => {
      listeners.add(listener);
      return () => listeners.delete(listener);
    },
    () => storeVersion
  );

// Funciones auxiliares para simular tiempos de respuesta y errores de red
const wait = <T,>(value: T, ms = 500) =>
  new Promise<T>((resolve) => setTimeout(() => resolve(value), ms));

const fail = (message: string, ms = 500) =>
  new Promise<never>((_, reject) => setTimeout(() => reject(new Error(message)), ms));

// Utilidad para calcular el estado del stock de un producto
export const status = (p: Product) =>
  p.stock <= Math.floor(p.min / 2) ? 'Crítico' : p.stock <= p.min ? 'Bajo' : 'Normal';

// Utilidad para formatear montos en Soles Peruanos (S/)
export const money = (n: number) =>
  'S/ ' + n.toLocaleString('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

// Servicio de Autenticación
export const authService = {
  login: (email: string, pw: string) =>
    email.trim().toLowerCase() === 'admin@electromarket.pe' && pw === 'admin123'
      ? wait({ name: 'Administrador', role: 'Administrador' }, 700)
      : fail('Correo o contraseña incorrectos.', 700),
};

// Servicio de Productos
export const productService = {
  list: () => products,
  
  get: (id: string) => products.find((p) => p.id === id),
  
  alerts: () =>
    products
      .filter((p) => p.active && p.stock <= p.min)
      .sort((a, b) => b.min - b.stock - (a.min - a.stock)),
  
  async save(p: Partial<Product> & { sku: string }) {
    if (products.some((x) => x.sku.toLowerCase() === p.sku.toLowerCase() && x.id !== p.id)) {
      return fail('Ya existe un producto con ese SKU.');
    }
    
    if (p.id) {
      products = products.map((x) => (x.id === p.id ? { ...x, ...p } : x));
    } else {
      products = [...products, { stock: 0, active: true, ...p, id: 'p' + Date.now() } as Product];
    }
    
    notifyListeners();
    return wait(true, 300);
  },
  
  toggle(id: string) {
    products = products.map((x) => (x.id === id ? { ...x, active: !x.active } : x));
    notifyListeners();
  },
};

// Servicio de Movimientos e Inventario
export const inventoryService = {
  movements: (pid?: string) => movements.filter((m) => !pid || m.productId === pid),
  
  async register(
    type: 'Entrada' | 'Salida',
    reason: string,
    doc: string,
    items: { pid: string; qty: number }[]
  ) {
    for (const item of items) {
      const product = products.find((x) => x.id === item.pid)!;
      if (type === 'Salida' && item.qty > product.stock) {
        return fail('No hay stock suficiente para ' + product.name + '.');
      }
    }

    items.forEach((item) => {
      const product = products.find((x) => x.id === item.pid)!;
      const newStock = product.stock + (type === 'Entrada' ? item.qty : -item.qty);

      products = products.map((x) => (x.id === product.id ? { ...x, stock: newStock } : x));
      
      movements = [
        ...movements,
        {
          id: movements.length + 1,
          date: today(),
          doc,
          type,
          reason,
          productId: product.id,
          qty: item.qty,
          balance: newStock,
          user: 'Administrador',
        },
      ];
    });

    notifyListeners();
    return wait(true, 600);
  },
};

// Servicio de Proveedores
export const supplierService = {
  list: () => suppliers,
  
  async save(s: Supplier) {
    if (suppliers.some((x) => x.ruc === s.ruc)) {
      return fail('Ya existe un proveedor con ese RUC.');
    }
    suppliers = [...suppliers, s];
    notifyListeners();
    return wait(true, 300);
  },
};

// Servicio de Reportes
export const reportService = {
  async generate(kind: string, f: { category: string; from: string; to: string }) {
    const getProduct = (id: string) => products.find((p) => p.id === id)!;
    let head: string[];
    let rows: (string | number)[][];

    if (kind === 'Reporte de inventario' || kind === 'Productos con stock bajo') {
      const list = products.filter(
        (p) => (!f.category || p.category === f.category) && (kind === 'Reporte de inventario' || p.stock <= p.min)
      );
      head = ['SKU', 'Producto', 'Categoría', 'Stock', 'Mínimo', 'Valor'];
      rows = list.map((p) => [
        p.sku,
        p.name,
        p.category,
        p.stock,
        p.min,
        money(p.stock * p.price),
      ]);
    } else {
      const list = movements
        .filter(
          (m) =>
            (!f.category || getProduct(m.productId).category === f.category) &&
            (!f.from || m.date >= f.from) &&
            (!f.to || m.date <= f.to)
        )
        .reverse();
      head = ['Fecha', 'Documento', 'Tipo', 'Producto', 'Cantidad', 'Saldo'];
      rows = list.map((m) => [
        m.date,
        m.doc,
        m.type,
        getProduct(m.productId).name,
        m.qty,
        m.balance,
      ]);
    }

    return wait({ head, rows }, 900);
  },
};