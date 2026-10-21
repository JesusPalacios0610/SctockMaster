import { PageProps } from '../types';
import { productService, status, useStore } from '../services/store';
import { Btn, Badge, Panel } from '../components/ui';

export default function Alerts({ go }: PageProps) {
  useStore();
  const alertsList = productService.alerts();

  // Si no hay alertas de stock, muestra un mensaje de éxito
  if (!alertsList.length) {
    return (
      <Panel>
        <p className="py-10 text-center font-medium text-emerald-700">✓ Sin novedades de stock</p>
      </Panel>
    );
  }

  return (
    <div className="grid gap-3 md:grid-cols-2">
      {alertsList.map((product) => (
        <div key={product.id} className="pop rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
          {/* Cabecera de la tarjeta con nombre y estado */}
          <div className="flex items-start justify-between gap-2">
            <div>
              <p className="font-semibold text-slate-800">{product.name}</p>
              <p className="text-xs text-slate-500">
                {product.sku} · Ubicación {product.location}
              </p>
            </div>
            <Badge t={status(product)} />
          </div>

          {/* Detalles numéricos del stock y déficit */}
          <dl className="mt-3 grid grid-cols-3 text-sm border-t border-b border-slate-100 py-2">
            <div>
              <dt className="text-xs text-slate-500">Stock actual</dt>
              <dd className="font-bold text-slate-700">{product.stock}</dd>
            </div>
            <div>
              <dt className="text-xs text-slate-500">Mínimo</dt>
              <dd className="font-bold text-slate-700">{product.min}</dd>
            </div>
            <div>
              <dt className="text-xs text-slate-500">Déficit</dt>
              <dd className="font-bold text-red-600">{product.min - product.stock}</dd>
            </div>
          </dl>

          {/* Botones de acción rápida */}
          <div className="mt-4 flex gap-2">
            <Btn v="s" onClick={() => go('products', { open: product.id })}>
              Ver producto
            </Btn>
            <Btn onClick={() => go('entradas', { productId: product.id })}>
              Registrar reposición
            </Btn>
          </div>
        </div>
      ))}
    </div>
  );
}