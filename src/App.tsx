import { useState } from 'react';
import { Page } from './types';
import AppLayout from './layouts/AppLayout';
import { Toasts } from './components/ui';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Products from './pages/Products';
import Movement from './pages/Movement';
import Kardex from './pages/Kardex';
import Alerts from './pages/Alerts';
import Suppliers from './pages/Suppliers';
import Reports from './pages/Reports';

export default function App() {
  // Estados principales de la aplicación
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [currentPage, setCurrentPage] = useState<Page>('dashboard');
  const [pageContext, setPageContext] = useState<any>({});

  // Función centralizada para cambiar de vista y resetear el scroll
  const navigateTo = (page: Page, context: any = {}) => {
    setCurrentPage(page);
    setPageContext(context);
    window.scrollTo(0, 0);
  };

  if (!isAuthenticated) {
    return (
      <>
        <Login onLogin={() => setIsAuthenticated(true)} />
        <Toasts />
      </>
    );
  }

  const sharedProps = {
    go: navigateTo,
    ctx: pageContext,
    key: currentPage + JSON.stringify(pageContext),
  };

  return (
    <AppLayout page={currentPage} go={navigateTo} logout={() => setIsAuthenticated(false)}>
      {/* Enrutamiento dinámico de pantallas */}
      {currentPage === 'dashboard' && <Dashboard {...sharedProps} />}
      {currentPage === 'products' && <Products {...sharedProps} />}
      {currentPage === 'entradas' && <Movement mode="Entrada" {...sharedProps} />}
      {currentPage === 'salidas' && <Movement mode="Salida" {...sharedProps} />}
      {currentPage === 'kardex' && <Kardex {...sharedProps} />}
      {currentPage === 'alerts' && <Alerts {...sharedProps} />}
      {currentPage === 'suppliers' && <Suppliers {...sharedProps} />}
      {currentPage === 'reports' && <Reports {...sharedProps} />}
      
      {currentPage === 'settings' && (
        <p className="text-sm text-slate-500">Configuración: pendiente de definir.</p>
      )}
      
      <Toasts />
    </AppLayout>
  );
}