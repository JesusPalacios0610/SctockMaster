import { useState } from 'react';
import { PageProps } from '../types';
import { productService, inventoryService, useStore } from '../services/store';
import { Field, inp, Panel, Badge, Empty } from '../components/ui';

export default function Kardex({ ctx }: PageProps) {
  useStore();
  const productsList = productService.list();

  // Estados locales para filtros del Kardex
  const [selectedProductId, setSelectedProductId] = useState(ctx.productId || productsList[0]?.id);
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');

  // Validación de fechas lógicas
  const isDateError = !!fromDate && !!toDate && fromDate > toDate;

  // Filtrado de movimientos según producto y rango de fechas
  const allMovements = inventoryService.movements(selectedProductId);
  const filteredRows = isDateError
    ? []
    : allMovements.filter(
        (m) => (!fromDate || m.date >= fromDate) && (!toDate || m.date <= toDate)
      );

  // Cálculo de totales y stock inicial/final
  const initialStock = filteredRows.length
    ? filteredRows[0].balance - (filteredRows[0].type === 'Entrada' ? filteredRows[0].qty : -filteredRows[0].qty)
    : productService.get(selectedProductId)?.stock || 0;

  const totalEntries = filteredRows.filter((m) => m.type === 'Entrada').reduce((acc, m) => acc + m.qty, 0);
  const totalOutputs = filteredRows.filter((m) => m.type === 'Salida').reduce((acc, m) => acc + m.qty, 0);

  return (
    <div className="space-y-4">
      {/* Panel de Filtros */}
      <Panel>
        <div className="grid gap-3 sm:grid-cols-3">
          <Field label="Producto">
            <select
              className={inp}
              value={selectedProductId}
              onChange={(e) => setSelectedProductId(e.target.value)}
            >
              {productsList.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.sku} · {p.name}
                </option>
              ))}
            </select>
          </Field>

          <Field label="Fecha inicial">
            <input
              type="date"
              className={inp}
              value={fromDate}
              onChange={(e) => setFromDate(e.target.value)}
            />
          </Field>

          <Field
            label="Fecha final"
            error={isDateError ? 'La fecha inicial no puede ser posterior a la final.' : undefined}
          >
            <input
              type="date"
              className={inp}
              value={toDate}
              onChange={(e) => setToDate(e.target.value)}
            />
          </Field>
        </div>
      </Panel>

      {/* Tarjetas de Resumen del Kardex */}
      <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
        {[
          ['Stock inicial', initialStock],
          ['Total entradas', totalEntries],
          ['Total salidas', totalOutputs],
          ['Stock final', initialStock + totalEntries - totalOutputs],
        ].map(([label, value]) => (
          <div key={label} className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
            <p className="text-xs text-slate-500">{label}</p>
            <p className="text-xl font-bold text-slate-800">{value}</p>
          </div>
        ))}
      </div>

      {/* Tabla de Movimientos */}
      <div className="rounded-xl border border-slate-200 bg-white overflow-hidden">
        {!filteredRows.length ? (
          <Empty title="Sin movimientos" text="No hay movimientos en el rango elegido." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[640px] text-sm">
              <thead className="bg-slate-50 text-left text-xs text-slate-500">
                <tr>
                  {['Fecha', 'Documento', 'Tipo', 'Entrada', 'Salida', 'Stock', 'Usuario'].map((h) => (
                    <th key={h} className="px-4 py-3 font-medium">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredRows.map((movement) => (
                  <tr key={movement.id} className="hover:bg-slate-50">
                    <td className="px-4 py-3">{movement.date}</td>
                    <td className="px-4 py-3">{movement.doc}</td>
                    <td className="px-4 py-3">
                      <Badge t={movement.type} />
                    </td>
                    <td className="px-4 py-3">{movement.type === 'Entrada' ? movement.qty : ''}</td>
                    <td className="px-4 py-3">{movement.type === 'Salida' ? movement.qty : ''}</td>
                    <td className="px-4 py-3 font-semibold">{movement.balance}</td>
                    <td className="px-4 py-3 text-slate-500">{movement.user}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}