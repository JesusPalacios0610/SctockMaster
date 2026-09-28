import { ReactNode, useState } from 'react';
import {
  LayoutDashboard,
  Package,
  ArrowDownToLine,
  ArrowUpFromLine,
  BookOpen,
  Truck,
  FileBarChart,
  Settings,
  Bell,
  Menu,
  LogOut,
  Boxes,
} from 'lucide-react';
import { Page } from '../types';
import { productService, useStore } from '../services/store';

// Estructura de navegación agrupada por categorías con sus respectivas rutas e iconos
const navigationGroups: [string, [Page, string, any][]][] = [
  ['Principal', [['dashboard', 'Dashboard', LayoutDashboard]]],
  ['Inventario', [['products', 'Productos', Package], ['entradas', 'Entradas', ArrowDownToLine], ['salidas', 'Salidas', ArrowUpFromLine], ['kardex', 'Kardex', BookOpen]]],
  ['Gestión', [['suppliers', 'Proveedores', Truck], ['alerts', 'Alertas de stock', Bell]]],
  ['Reportes', [['reports', 'Reportes', FileBarChart]]],
  ['Sistema', [['settings', 'Configuración', Settings]]],
];

// Diccionario para los títulos de las páginas según la ruta actual
const pageTitles: Record<string, [string, string]> = {
  dashboard: ['Principal', 'Dashboard'],
  products: ['Inventario', 'Productos'],
  entradas: ['Inventario', 'Entradas'],
  salidas: ['Inventario', 'Salidas'],
  kardex: ['Inventario', 'Kardex'],
  suppliers: ['Gestión', 'Proveedores'],
  alerts: ['Gestión', 'Alertas de stock'],
  reports: ['Reportes', 'Reportes'],
  settings: ['Sistema', 'Configuración'],
};

export default function AppLayout({
  page,
  go,
  logout,
  children,
}: {
  page: Page;
  go: (p: Page) => void;
  logout: () => void;
  children: ReactNode;
}) {
  useStore();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const activeAlertsCount = productService.alerts().length;

  // Componente reutilizable del menú lateral (Sidebar)
  const sidebarContent = (
    <nav aria-label="Principal" className="flex h-full flex-col bg-[#0f1b2d] p-4 text-slate-300">
      {/* Logotipo y Marca */}
      <div className="mb-6 flex items-center gap-2 px-2 text-white">
        <span className="grid h-9 w-9 place-items-center rounded-lg bg-blue-600">
          <Boxes size={20} />
        </span>
        <div className="leading-tight">
          <b>ElectroMarket</b>
          <div className="text-xs text-slate-400">Inventario</div>
        </div>
      </div>

      {/* Enlaces de Navegación por Grupos */}
      <div className="flex-1 space-y-5 overflow-auto">
        {navigationGroups.map(([groupName, items]) => (
          <div key={groupName}>
            <p className="mb-1 px-3 text-xs text-slate-500 uppercase tracking-wider">{groupName}</p>
            {items.map(([id, label, IconComponent]) => {
              const isActive = page === id;
              return (
                <button
                  key={id}
                  onClick={() => {
                    go(id);
                    setIsMobileMenuOpen(false);
                  }}
                  aria-current={isActive ? 'page' : undefined}
                  className={`flex h-11 w-full items-center gap-3 rounded-lg px-3 text-sm outline-none transition focus-visible:ring-2 focus-visible:ring-blue-400 ${
                    isActive ? 'bg-blue-600 text-white font-medium' : 'hover:bg-white/10 text-slate-300'
                  }`}
                >
                  <IconComponent size={18} />
                  {label}
                  {id === 'alerts' && activeAlertsCount > 0 && (
                    <span className="ml-auto rounded-full bg-red-500 px-2 py-0.5 text-xs text-white font-semibold">
                      {activeAlertsCount}
                    </span>
                  )}
                </button>
              );
            })}
          </div>
        ))}
      </div>

      {/* Botón de Cerrar Sesión */}
      <button
        onClick={logout}
        className="flex h-11 items-center gap-3 rounded-lg px-3 text-sm hover:bg-white/10 text-slate-300 transition"
      >
        <LogOut size={18} />
        Cerrar sesión
      </button>
    </nav>
  );

  return (
    <div className="min-h-screen lg:pl-64 bg-slate-50">
      {/* Sidebar para pantallas grandes (Desktop) */}
      <aside className="fixed inset-y-0 left-0 hidden w-64 lg:block shadow-sm">{sidebarContent}</aside>

      {/* Menú desplegable para móviles */}
      {isMobileMenuOpen && (
        <div className="fixed inset-0 z-40 lg:hidden">
          <div className="absolute inset-0 bg-slate-900/50 backdrop-blur-xs" onClick={() => setIsMobileMenuOpen(false)} />
          <aside className="pop absolute inset-y-0 left-0 w-72 shadow-xl">{sidebarContent}</aside>
        </div>
      )}

      {/* Barra superior (Header) */}
      <header className="sticky top-0 z-30 flex h-16 items-center gap-3 border-b border-slate-200 bg-white/95 px-4 backdrop-blur sm:px-6 shadow-xs">
        <button
          aria-label="Abrir menú"
          className="grid h-10 w-10 place-items-center rounded-lg hover:bg-slate-100 lg:hidden text-slate-700"
          onClick={() => setIsMobileMenuOpen(true)}
        >
          <Menu size={20} />
        </button>

        <div className="min-w-0">
          <p className="hidden text-xs text-slate-500 sm:block">
            {pageTitles[page]?.[0]} / {pageTitles[page]?.[1]}
          </p>
          <h1 className="truncate font-semibold text-slate-800">{pageTitles[page]?.[1]}</h1>
        </div>

        <div className="ml-auto flex items-center gap-3">
          {/* Botón de Alertas rápido */}
          <button
            aria-label={`Alertas de stock: ${activeAlertsCount}`}
            onClick={() => go('alerts')}
            className="relative grid h-10 w-10 place-items-center rounded-lg hover:bg-slate-100 text-slate-600 transition"
          >
            <Bell size={20} />
            {activeAlertsCount > 0 && <span className="absolute right-2 top-2 h-2.5 w-2.5 rounded-full bg-red-500 animate-pulse" />}
          </button>

          {/* Perfil del usuario administrador */}
          <div className="flex items-center gap-2 pl-2 border-l border-slate-200">
            <span className="grid h-9 w-9 place-items-center rounded-full bg-blue-100 text-sm font-semibold text-blue-700">
              AD
            </span>
            <div className="hidden text-sm leading-tight sm:block">
              <b className="text-slate-800">Administrador</b>
              <div className="text-xs text-slate-500">admin@electromarket.pe</div>
            </div>
          </div>
        </div>
      </header>

      {/* Contenido principal de la página actual */}
      <main className="mx-auto max-w-7xl p-4 sm:p-6">{children}</main>
    </div>
  );
}