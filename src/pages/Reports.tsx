import { useState } from 'react';
import { FileDown, FileSpreadsheet } from 'lucide-react';
import { PageProps } from '../types';
import { productService, reportService } from '../services/store';
import { Btn, Field, inp, Panel, Empty, Loading, toast } from '../components/ui';

const reportKinds = ['Reporte de inventario', 'Reporte Kardex', 'Movimientos', 'Productos con stock bajo'];

export default function Reports(_: PageProps) {
  const [kind, setKind] = useState(reportKinds[0]);
  const [category, setCategory] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [isBusy, setIsBusy] = useState(false);
  const [reportResult, setReportResult] = useState<{ head: string[]; rows: (string | number)[][] } | null>(null);

  const categories = [...new Set(productService.list().map((p) => p.category))];
  const isDateError = !!fromDate && !!toDate && fromDate > toDate;

  // Generar reporte
  const generateReport = async () => {
    if (isDateError) return;
    setIsBusy(true);
    setReportResult(await reportService.generate(kind, { category, from: fromDate, to: toDate }));
    setIsBusy(false);
    toast('Reporte generado');
  };

  // Exportar a CSV / Excel
  const exportToCSV = () => {
    if (!reportResult) return;
    const csvContent = [reportResult.head, ...reportResult.rows]
      .map((r) => r.map((c) => `"${c}"`).join(','))
      .join('\n');
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = kind.replace(/\s/g, '_') + '.csv';
    link.click();
    toast('Excel exportado');
  };

  return (
    <div className="space-y-4">
      {/* Panel de Filtros para Reportes */}
      <Panel>
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          <Field label="Tipo de reporte">
            <select className={inp} value={kind} onChange={(e) => setKind(e.target.value)}>
              {reportKinds.map((k) => (
                <option key={k}>{k}</option>
              ))}
            </select>
          </Field>

          <Field label="Categoría">
            <select className={inp} value={category} onChange={(e) => setCategory(e.target.value)}>
              <option value="">Todas</option>
              {categories.map((c) => (
                <option key={c}>{c}</option>
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

        <div className="mt-4 flex flex-wrap gap-2 pt-3 border-t border-slate-100">
          <Btn onClick={generateReport} disabled={isBusy || isDateError}>
            {isBusy ? 'Generando…' : 'Generar reporte'}
          </Btn>
          <Btn v="s" disabled={!reportResult} onClick={() => window.print()}>
            <FileDown size={16} />
            Exportar PDF
          </Btn>
          <Btn v="s" disabled={!reportResult} onClick={exportToCSV}>
            <FileSpreadsheet size={16} />
            Exportar Excel
          </Btn>
        </div>
      </Panel>

      {/* Visualización de Resultados */}
      <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        {isBusy ? (
          <div className="p-4"><Loading /></div>
        ) : !reportResult ? (
          <Empty title="Aún no generaste un reporte" text="Elige los filtros y pulsa Generar reporte." />
        ) : !reportResult.rows.length ? (
          <Empty title="Sin datos para estos filtros" />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[600px] text-sm">
              <thead className="bg-slate-50 text-left text-xs text-slate-500">
                <tr>
                  {reportResult.head.map((h) => (
                    <th key={h} className="px-4 py-3 font-medium">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {reportResult.rows.map((row, i) => (
                  <tr key={i} className="hover:bg-slate-50">
                    {row.map((cell, j) => (
                      <td key={j} className="px-4 py-3 text-slate-700">{cell}</td>
                    ))}
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