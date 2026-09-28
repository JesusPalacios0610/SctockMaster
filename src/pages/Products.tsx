import { useEffect, useState } from 'react';
import { Plus, Search, Pencil, Eye, X } from 'lucide-react';
import { PageProps, Product } from '../types';
import { productService, inventoryService, status, money, useStore } from '../services/store';
import { Btn, Badge, Modal, Field, inp, Empty, Loading, toast } from '../components/ui';

const getProductStatus = (p: Product) => (p.active ? status(p) : 'Inactivo');

export default function Products({ go, ctx }: PageProps) {
  useStore();
  const allProducts = productService.list();

  // Estados de filtros y modales
  const [query, setQuery] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [sortOption, setSortOption] = useState('name');
  const [isLoading, setIsLoading] = useState(true);
  const [formProduct, setFormProduct] = useState<Product | null | undefined>(undefined);
  const [viewProduct, setViewProduct] = useState<Product | null>(
    ctx.open ? productService.get(ctx.open) || null : null
  );

  useEffect(() => {
    const timer = setTimeout(() => setIsLoading(false), 450);
    return () => clearTimeout(timer);
  }, []);

  const categories = [...new Set(allProducts.map((p) => p.category))];

  // Filtrado y ordenamiento de la lista
  const filteredProducts = allProducts
    .filter(
      (p) =>
        (!categoryFilter || p.category === categoryFilter) &&
        (!statusFilter || getProductStatus(p) === statusFilter) &&
        [p.sku, p.name, p.brand, p.category].join(' ').toLowerCase().includes(query.toLowerCase())
    )
    .sort((a, b) =>
      sortOption === 'stock'
        ? a.stock - b.stock
        : sortOption === 'price'
        ? b.price - a.price
        : a.name.localeCompare(b.name)
    );

  const currentView = viewProduct && productService.get(viewProduct.id);

  return (
    <div className="space-y-4">
      {/* Barra de Filtros y Botones */}
      <div className="flex flex-wrap gap-2">
        <div className="relative min-w-[200px] flex-1">
          <Search size={16} className="absolute left-3 top-3 text-slate-400" />
          <input
            aria-label="Buscar"
            placeholder="Buscar por SKU, nombre, marca o categoría"
            className={inp + ' pl-9'}
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>

        <select
          aria-label="Categoría"
          className={inp + ' w-auto'}
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
        >
          <option value="">Categoría</option>
          {categories.map((c) => (
            <option key={c}>{c}</option>
          ))}
        </select>

        <select
          aria-label="Estado"
          className={inp + ' w-auto'}
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
        >
          <option value="">Estado</option>
          {['Normal', 'Bajo', 'Crítico', 'Inactivo'].map((c) => (
            <option key={c}>{c}</option>
          ))}
        </select>

        <select
          aria-label="Ordenar"
          className={inp + ' w-auto'}
          value={sortOption}
          onChange={(e) => setSortOption(e.target.value)}
        >
          <option value="name">Nombre</option>
          <option value="stock">Menor stock</option>
          <option value="price">Mayor precio</option>
        </select>

        {(query || categoryFilter || statusFilter) && (
          <Btn v="s" onClick={() => { setQuery(''); setCategoryFilter(''); setStatusFilter(''); }}>
            <X size={16} />
            Limpiar filtros
          </Btn>
        )}

        <Btn onClick={() => setFormProduct(null)}>
          <Plus size={16} />
          Nuevo producto
        </Btn>
      </div>

      {/* Contenedor de la Tabla / Listado */}
      <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        {isLoading ? (
          <div className="p-4"><Loading /></div>
        ) : !filteredProducts.length ? (
          <Empty
            title="Sin resultados"
            text="Prueba con otros filtros."
            action={
              <Btn v="s" onClick={() => { setQuery(''); setCategoryFilter(''); setStatusFilter(''); }}>
                Limpiar filtros
              </Btn>
            }
          />
        ) : (
          <>
            {/* Vista de Tabla para Escritorio */}
            <div className="hidden overflow-x-auto md:block">
              <table className="w-full text-sm">
                <thead className="bg-slate-50 text-left text-xs text-slate-500">
                  <tr>
                    {['SKU', 'Producto', 'Categoría', 'Marca', 'Precio', 'Ubic.', 'Stock', 'Mín.', 'Estado', ''].map((h) => (
                      <th key={h} className="px-4 py-3 font-medium">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {filteredProducts.map((p) => (
                    <tr key={p.id} className="hover:bg-slate-50">
                      <td className="px-4 py-3 text-slate-500">{p.sku}</td>
                      <td className="px-4 py-3 font-medium text-slate-800">{p.name}</td>
                      <td className="px-4 py-3">{p.category}</td>
                      <td className="px-4 py-3">{p.brand}</td>
                      <td className="px-4 py-3">{money(p.price)}</td>
                      <td className="px-4 py-3">{p.location}</td>
                      <td className="px-4 py-3 font-semibold">{p.stock}</td>
                      <td className="px-4 py-3">{p.min}</td>
                      <td className="px-4 py-3"><Badge t={getProductStatus(p)} /></td>
                      <td className="whitespace-nowrap px-4 py-3 text-right">
                        <button
                          aria-label={'Ver ' + p.name}
                          className="p-2 text-slate-500 hover:text-blue-600"
                          onClick={() => setViewProduct(p)}
                        >
                          <Eye size={16} />
                        </button>
                        <button
                          aria-label={'Editar ' + p.name}
                          className="p-2 text-slate-500 hover:text-blue-600"
                          onClick={() => setFormProduct(p)}
                        >
                          <Pencil size={16} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Vista de Lista para Móviles */}
            <ul className="divide-y divide-slate-100 md:hidden">
              {filteredProducts.map((p) => (
                <li key={p.id}>
                  <button className="w-full p-4 text-left hover:bg-slate-50" onClick={() => setViewProduct(p)}>
                    <div className="flex items-start justify-between gap-2">
                      <div>
                        <p className="font-medium text-slate-800">{p.name}</p>
                        <p className="text-xs text-slate-500">
                          {p.sku} · {p.brand} · {p.location}
                        </p>
                      </div>
                      <Badge t={getProductStatus(p)} />
                    </div>
                    <p className="mt-2 text-sm text-slate-600">
                      Stock <b>{p.stock}</b> / mín. {p.min} · {money(p.price)}
                    </p>
                  </button>
                </li>
              ))}
            </ul>
          </>
        )}
      </div>

      {/* Modal de Formulario (Crear / Editar) */}
      {formProduct !== undefined && (
        <ProductForm p={formProduct} onClose={() => setFormProduct(undefined)} />
      )}

      {/* Modal de Detalle de Producto */}
      {currentView && (
        <Modal wide title={currentView.name} onClose={() => setViewProduct(null)}>
          <div className="flex flex-wrap items-center gap-3">
            <Badge t={getProductStatus(currentView)} />
            <span className="text-sm text-slate-500">{currentView.sku}</span>
          </div>

          <dl className="mt-4 grid grid-cols-2 gap-3 text-sm sm:grid-cols-3 border-t border-b border-slate-100 py-3">
            {(
              [
                ['Stock actual', currentView.stock],
                ['Stock mínimo', currentView.min],
                ['Ubicación', currentView.location],
                ['Precio', money(currentView.price)],
                ['Categoría', currentView.category],
                ['Marca', currentView.brand],
              ] as [string, any][]
            ).map(([k, v]) => (
              <div key={k}>
                <dt className="text-xs text-slate-500">{k}</dt>
                <dd className="font-semibold text-slate-700">{v}</dd>
              </div>
            ))}
          </dl>

          <div className="mt-4 h-2 overflow-hidden rounded bg-slate-100">
            <div
              className={`h-full ${currentView.stock <= currentView.min ? 'bg-amber-500' : 'bg-emerald-500'}`}
              style={{ width: Math.min(100, (currentView.stock / (currentView.min * 3)) * 100) + '%' }}
            />
          </div>

          <h3 className="mb-2 mt-5 text-sm font-semibold text-slate-800">Historial reciente</h3>
          <ul className="divide-y divide-slate-100 text-sm">
            {inventoryService.movements(currentView.id).slice(-5).reverse().map((m) => (
              <li key={m.id} className="flex items-center justify-between py-2">
                <span className="text-slate-600">
                  {m.date} · {m.doc}
                </span>
                <span className="flex items-center gap-2">
                  <Badge t={m.type} />
                  <b>{m.type === 'Entrada' ? '+' : '−'}{m.qty}</b>
                </span>
              </li>
            ))}
          </ul>

          <div className="mt-5 flex flex-wrap gap-2 pt-3 border-t border-slate-100">
            <Btn v="s" onClick={() => { setViewProduct(null); setFormProduct(currentView); }}>Editar</Btn>
            <Btn v="s" onClick={() => go('entradas', { productId: currentView.id })}>Registrar entrada</Btn>
            <Btn v="s" onClick={() => go('salidas', { productId: currentView.id })}>Registrar salida</Btn>
            <Btn onClick={() => go('kardex', { productId: currentView.id })}>Ver Kardex</Btn>
            <Btn
              v="d"
              className="sm:ml-auto"
              onClick={() => {
                productService.toggle(currentView.id);
                toast(currentView.active ? 'Producto inactivado' : 'Producto activado');
              }}
            >
              {currentView.active ? 'Inactivar' : 'Activar'}
            </Btn>
          </div>
        </Modal>
      )}
    </div>
  );
}

// Componente secundario para el formulario de producto
function ProductForm({ p, onClose }: { p: Product | null; onClose: () => void }) {
  const [formData, setFormData] = useState({
    sku: p?.sku || '',
    name: p?.name || '',
    category: p?.category || '',
    brand: p?.brand || '',
    price: String(p?.price ?? ''),
    location: p?.location || '',
    min: String(p?.min ?? ''),
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isBusy, setIsBusy] = useState(false);

  const handleInputChange = (field: string) => (e: any) =>
    setFormData({ ...formData, [field]: e.target.value });

  const saveProduct = async (e: React.FormEvent) => {
    e.preventDefault();
    const validationErrors: Record<string, string> = {};

    (['sku', 'name', 'category', 'brand', 'location'] as const).forEach((key) => {
      if (!formData[key].trim()) validationErrors[key] = 'Campo obligatorio.';
    });
    if (!(+formData.price > 0)) validationErrors.price = 'Ingresa un precio mayor a 0.';
    if (!(formData.min !== '' && +formData.min >= 0)) validationErrors.min = 'Ingresa un mínimo válido.';

    setErrors(validationErrors);
    if (Object.keys(validationErrors).length) return;

    setIsBusy(true);
    try {
      await productService.save({
        ...(p ? { id: p.id } : {}),
        sku: formData.sku.trim(),
        name: formData.name.trim(),
        category: formData.category.trim(),
        brand: formData.brand.trim(),
        location: formData.location.trim(),
        price: +formData.price,
        min: +formData.min,
      });
      toast(p ? 'Producto actualizado' : 'Producto creado');
      onClose();
    } catch (err: any) {
      setErrors({ sku: err.message });
    } finally {
      setIsBusy(false);
    }
  };

  const renderField = (fieldKey: keyof typeof formData, labelText: string, inputType = 'text') => (
    <Field label={labelText + ' *'} error={errors[fieldKey]}>
      <input
        className={inp}
        type={inputType}
        step="any"
        value={formData[fieldKey]}
        onChange={handleInputChange(fieldKey)}
      />
    </Field>
  );

  return (
    <Modal wide title={p ? 'Editar producto' : 'Nuevo producto'} onClose={onClose}>
      <form onSubmit={saveProduct} noValidate className="space-y-5">
        <fieldset className="grid gap-3 sm:grid-cols-2">
          <legend className="mb-2 text-sm font-semibold text-slate-800">Identificación</legend>
          {renderField('sku', 'SKU')}
          {renderField('name', 'Nombre')}
          {renderField('category', 'Categoría')}
          {renderField('brand', 'Marca')}
        </fieldset>

        <fieldset className="grid gap-3 sm:grid-cols-3">
          <legend className="mb-2 text-sm font-semibold text-slate-800">Precio y almacén</legend>
          {renderField('price', 'Precio (S/)', 'number')}
          {renderField('location', 'Ubicación')}
          {renderField('min', 'Stock mínimo', 'number')}
        </fieldset>

        <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
          <Btn type="button" v="s" onClick={onClose}>
            Cancelar
          </Btn>
          <Btn type="submit" disabled={isBusy}>
            {isBusy ? 'Guardando…' : 'Guardar'}
          </Btn>
        </div>
      </form>
    </Modal>
  );
}