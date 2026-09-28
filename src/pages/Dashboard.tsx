import { useEffect, useState } from 'react';
import { Package, Boxes, AlertTriangle, ArrowDownToLine, ArrowUpFromLine, Wallet } from 'lucide-react';
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, PieChart, Pie, Cell, Legend } from 'recharts';
import { PageProps } from '../types';
import { productService, inventoryService, status, money, useStore } from '../services/store';
import { daysAgo } from '../data/mock';
import { Panel, Loading } from '../components/ui';

export default function Dashboard({ go }: PageProps) {
  useStore();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => setIsLoading(false), 400);
    return () => clearTimeout(timer);
  }, []);

  const ps = productService.list();
  const mv = inventoryService.movements();
  const al = productService.alerts();

  const sum = (t: string, from: string) => 
    mv.filter(m => m.type === t && m.date >= from).reduce((a, m) => a + m.qty, 0);

  const kpi: [string, string | number, any, string][] = [
    ['Productos', ps.length, Package, 'text-blue-600'],
    ['Stock total', ps.reduce((a, p) => a + p.stock, 0), Boxes, 'text-blue-600'],
    ['Stock bajo', al.length, AlertTriangle, 'text-amber-600'],
    ['Entradas (30 d)', sum('Entrada', daysAgo(30)), ArrowDownToLine, 'text-emerald-600'],
    ['Salidas (30 d)', sum('Salida', daysAgo(30)), ArrowUpFromLine, 'text-orange-600'],
    ['Valor inventario', money(ps.reduce((a, p) => a + p.stock * p.price, 0)), Wallet, 'text-blue-600']
  ];

  const days = Array.from({ length: 14 }, (_, i) => daysAgo(13 - i)).map(d => ({
    d: d.slice(5),
    Entradas: mv.filter(m => m.date === d && m.type === 'Entrada').reduce((a, m) => a + m.qty, 0),
    Salidas: mv.filter(m => m.date === d && m.type === 'Salida').reduce((a, m) => a + m.qty, 0)
  }));

  const st = ['Normal', 'Bajo', 'Crítico'].map(n => ({
    n,
    v: ps.filter(p => status(p) === n).length
  }));
  const col = ['#059669', '#d97706', '#dc2626'];

  const top = ps.map(p => ({
    n: p.name.slice(0, 16),
    v: mv.filter(m => m.productId === p.id).length
  })).sort((a, b) => b.v - a.v).slice(0, 5);

  if (isLoading) return <Loading rows={8} />;

  return (
    <div className="space-y-5">
      {/* Tarjetas de Métricas / KPIs */}
      <div className="grid grid-cols-2 gap-3 lg:grid-cols-6">
        {kpi.map(([l, v, I, c]) => (
          <div key={l} className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
            <I size={18} className={c} />
            <p className="mt-2 truncate text-xl font-bold text-slate-800">{v}</p>
            <p className="text-xs text-slate-500">{l}</p>
          </div>
        ))}
      </div>

      {/* Cuadrícula de Gráficos y Paneles */}
      <div className="grid gap-5 lg:grid-cols-3">
        {/* Gráfico de Movimientos */}
        <Panel title="Movimientos, últimos 14 días" className="lg:col-span-2">
          <div className="h-64" role="img" aria-label="Entradas y salidas por día">
            <ResponsiveContainer>
              <BarChart data={days}>
                <XAxis dataKey="d" fontSize={11} />
                <YAxis fontSize={11} width={30} />
                <Tooltip />
                <Legend />
                <Bar dataKey="Entradas" fill="#2563eb" radius={[3, 3, 0, 0]} />
                <Bar dataKey="Salidas" fill="#94a3b8" radius={[3, 3, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Panel>

        {/* Panel de Alertas */}
        <Panel 
          title="Alertas de stock" 
          action={<button className="text-sm text-blue-600 hover:underline" onClick={() => go('alerts')}>Ver todas</button>}
        >
          <ul className="space-y-3">
            {al.slice(0, 4).map(p => (
              <li key={p.id} className="text-sm border-b border-slate-100 pb-2">
                <p className="font-medium text-slate-700">{p.name}</p>
                <p className="text-slate-500 text-xs">{p.stock} disponibles · mínimo {p.min}</p>
                <div className="mt-1 flex gap-3 text-xs">
                  <button className="text-blue-600 hover:underline" onClick={() => go('products', { open: p.id })}>Ver producto</button>
                  <button className="text-blue-600 hover:underline" onClick={() => go('entradas', { productId: p.id })}>Registrar reposición</button>
                </div>
              </li>
            ))}
            {!al.length && <li className="text-sm text-emerald-700 font-medium">✓ Sin novedades de stock</li>}
          </ul>
        </Panel>

        {/* Gráfico de Pastel */}
        <Panel title="Estado del inventario">
          <div className="h-52">
            <ResponsiveContainer>
              <PieChart>
                <Pie data={st} dataKey="v" nameKey="n" innerRadius={45} outerRadius={75}>
                  {st.map((_, i) => <Cell key={i} fill={col[i]} />)}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </Panel>

        {/* Gráfico de Barras Vertical */}
        <Panel title="Productos con más movimientos" className="lg:col-span-2">
          <div className="h-52">
            <ResponsiveContainer>
              <BarChart data={top} layout="vertical">
                <XAxis type="number" fontSize={11} />
                <YAxis type="category" dataKey="n" width={110} fontSize={11} />
                <Tooltip />
                <Bar dataKey="v" name="Movimientos" fill="#0f1b2d" radius={[0, 3, 3, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Panel>
      </div>
    </div>
  );
}