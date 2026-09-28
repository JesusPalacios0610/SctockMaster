import { useState } from 'react';
import { Plus, Trash2, CheckCircle2 } from 'lucide-react';
import { PageProps } from '../types';
import { productService, inventoryService, money, useStore } from '../services/store';
import { Btn, Field, inp, Panel, Confirm, Empty, toast } from '../components/ui';

interface Item {
  pid: string;
  qty: number;
  cost: number;
}

export default function Movement({ mode, go, ctx }: PageProps & { mode: 'Entrada' | 'Salida' }) {
  useStore();
  const isEntry = mode === 'Entrada';
  const activeProducts = productService.list().filter((p) => p.active);
  const movementOptions = isEntry ? ['Factura', 'Guía de remisión', 'Boleta'] : ['Venta', 'Traslado', 'Merma/Deterioro'];

  // Estados locales del formulario
  const [kind, setKind] = useState(movementOptions[0]);
  const [reference, setReference] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [productId, setProductId] = useState(ctx.productId || '');
  const [quantity, setQuantity] = useState('');
  const [cost, setCost] = useState('');
  const [items, setItems] = useState<Item[]>([]);
  const [errorMsg, setErrorMsg] = useState('');
  const [askConfirm, setAskConfirm] = useState(false);
  const [isBusy, setIsBusy] = useState(false);
  const [isDone, setIsDone] = useState(false);

  const currentProduct = productService.get(productId);
  const parsedQty = +quantity;
  const alreadyInListQty = items.filter((i) => i.pid === productId).reduce((acc, i) => acc + i.qty, 0);
  const isStockExceeded = !isEntry && !!currentProduct && parsedQty + alreadyInListQty > currentProduct.stock;
  const totalAmount = items.reduce((acc, i) => acc + i.qty * i.cost, 0);
  const shownProducts = activeProducts.filter((x) => (x.sku + x.name).toLowerCase().includes(searchTerm.toLowerCase()));

  // Función para agregar ítems a la lista temporal
  const addItem = () => {
    setErrorMsg('');
    if (!currentProduct) return setErrorMsg('Selecciona un producto.');
    if (!(parsedQty > 0 && Number.isInteger(parsedQty))) return setErrorMsg('Ingresa una cantidad entera mayor a 0.');
    if (isEntry && !(+cost > 0)) return setErrorMsg('Ingresa el costo unitario.');
    if (isStockExceeded) return;

    setItems([...items, { pid: currentProduct.id, qty: parsedQty, cost: +cost || currentProduct.price }]);
    setQuantity('');
    setCost('');
    setProductId('');
  };

  // Confirmar el registro final en el servidor/servicio
  const confirmMovement = async () => {
    setIsBusy(true);
    try {
      await inventoryService.register(
        mode,
        kind,
        reference.trim(),
        items.map((i) => ({ pid: i.pid, qty: i.qty }))
      );
      setAskConfirm(false);
      setIsDone(true);
      toast(`${mode} registrada`);
    } catch (e: any) {
      setAskConfirm(false);
      setErrorMsg(e.message);
    } finally {
      setIsBusy(false);
    }
  };

  if (isDone) {
    return (
      <Panel>
        <div className="flex flex-col items-center py-10 text-center">
          <CheckCircle2 size={44} className="text-emerald-600" />
          <p className="mt-3 text-lg font-semibold text-slate-800">{isEntry ? 'Entrada registrada' : 'Salida registrada'}</p>
          <p className="text-sm text-slate-500">Stock y Kardex actualizados.</p>
          <div className="mt-5 flex gap-2">
            <Btn v="s" onClick={() => { setIsDone(false); setItems([]); setReference(''); }}>
              Nueva operación
            </Btn>
            <Btn onClick={() => go('kardex', { productId: items[0]?.pid })}>Ver Kardex</Btn>
          </div>
        </div>
      </Panel>
    );
  }

  return (
    <div className="grid gap-5 lg:grid-cols-5">
      {/* Formulario de Captura */}
      <Panel title={isEntry ? 'Datos de la entrada' : 'Datos de la salida'} className="lg:col-span-2">
        <div className="space-y-3">
          <Field label={isEntry ? 'Tipo de documento' : 'Motivo'}>
            <select className={inp} value={kind} onChange={(e) => setKind(e.target.value)}>
              {movementOptions.map((o) => (
                <option key={o}>{o}</option>
              ))}
            </select>
          </Field>

          <Field label={isEntry ? 'N.º de guía o factura' : 'Documento de referencia'}>
            <input
              className={inp}
              value={reference}
              onChange={(e) => setReference(e.target.value)}
              placeholder={isEntry ? 'F001-00234' : 'B001-00045'}
            />
          </Field>

          <Field label="Buscar producto">
            <input
              className={inp}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="SKU o nombre"
            />
          </Field>

          <Field label="Producto">
            <select className={inp} value={productId} onChange={(e) => setProductId(e.target.value)}>
              <option value="">Selecciona…</option>
              {shownProducts.map((x) => (
                <option key={x.id} value={x.id}>
                  {x.sku} · {x.name}
                </option>
              ))}
            </select>
          </Field>

          {currentProduct && (
            <p className="text-sm text-slate-600">
              Stock disponible: <b>{currentProduct.stock}</b>
              {alreadyInListQty > 0 && <span className="text-slate-500"> ({alreadyInListQty} ya en la lista)</span>}
            </p>
          )}

          <div className="grid grid-cols-2 gap-3">
            <Field label="Cantidad">
              <input
                className={inp}
                type="number"
                min="1"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
              />
            </Field>
            {isEntry && (
              <Field label="Costo unitario (S/)">
                <input
                  className={inp}
                  type="number"
                  step="any"
                  value={cost}
                  onChange={(e) => setCost(e.target.value)}
                />
              </Field>
            )}
          </div>

          {isStockExceeded && (
            <p role="alert" className="rounded-lg bg-red-50 p-3 text-sm text-red-700">
              No hay stock suficiente para realizar esta operación.
            </p>
          )}
          {errorMsg && <p role="alert" className="text-sm text-red-600">{errorMsg}</p>}

          <Btn v="s" className="w-full" onClick={addItem} disabled={isStockExceeded}>
            <Plus size={16} />
            Agregar a la lista
          </Btn>
        </div>
      </Panel>

      {/* Tabla de Productos Agregados */}
      <Panel title="Productos agregados" className="lg:col-span-3">
        {!items.length ? (
          <Empty title="Aún no agregaste productos" text="Selecciona un producto y una cantidad." />
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="text-left text-xs text-slate-500 bg-slate-50">
                  <tr>
                    <th className="px-3 py-2">Producto</th>
                    <th className="px-3 py-2">Cant.</th>
                    {isEntry && (
                      <>
                        <th className="px-3 py-2">Costo</th>
                        <th className="px-3 py-2">Subtotal</th>
                      </>
                    )}
                    <th className="w-10" />
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {items.map((i, index) => (
                    <tr key={index}>
                      <td className="px-3 py-2 font-medium">{productService.get(i.pid)?.name}</td>
                      <td className="px-3 py-2">{i.qty}</td>
                      {isEntry && (
                        <>
                          <td className="px-3 py-2">{money(i.cost)}</td>
                          <td className="px-3 py-2">{money(i.qty * i.cost)}</td>
                        </>
                      )}
                      <td className="px-3 py-2">
                        <button
                          aria-label="Quitar"
                          className="p-2 text-slate-500 hover:text-red-600"
                          onClick={() => setItems(items.filter((_, j) => j !== index))}
                        >
                          <Trash2 size={16} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="mt-4 flex items-center justify-between border-t border-slate-100 pt-3">
              {isEntry ? <p className="text-sm">Total: <b>{money(totalAmount)}</b></p> : <span />}
              <Btn onClick={() => setAskConfirm(true)}>Revisar y confirmar</Btn>
            </div>
          </>
        )}
      </Panel>

      {askConfirm && (
        <Confirm
          title={isEntry ? '¿Deseas registrar esta entrada de stock?' : '¿Deseas registrar esta salida de stock?'}
          text={`${items.length} producto(s), ${items.reduce((a, i) => a + i.qty, 0)} unidades. ${isEntry ? 'El stock aumentará' : 'El stock disminuirá'} y se generará el registro en el Kardex.`}
          busy={isBusy}
          onOk={confirmMovement}
          onCancel={() => setAskConfirm(false)}
        />
      )}
    </div>
  );
}