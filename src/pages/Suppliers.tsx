import { useState } from 'react';
import { Plus, Search } from 'lucide-react';
import { PageProps, Supplier } from '../types';
import { supplierService, useStore } from '../services/store';
import { Btn, Badge, Modal, Field, inp, Empty, toast } from '../components/ui';

export default function Suppliers(_: PageProps) {
  useStore();
  const [query, setQuery] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);

  const suppliersList = supplierService
    .list()
    .filter((s) => (s.ruc + s.name + s.contact).toLowerCase().includes(query.toLowerCase()));

  return (
    <div className="space-y-4">
      {/* Barra de Búsqueda y Botón */}
      <div className="flex gap-2">
        <div className="relative flex-1">
          <Search size={16} className="absolute left-3 top-3 text-slate-400" />
          <input
            aria-label="Buscar proveedor"
            className={inp + ' pl-9'}
            placeholder="Buscar por RUC, razón social o contacto"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>
        <Btn onClick={() => setIsModalOpen(true)}>
          <Plus size={16} />
          Nuevo proveedor
        </Btn>
      </div>

      {/* Tabla de Proveedores */}
      <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        {!suppliersList.length ? (
          <Empty title="Sin proveedores" />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[760px] text-sm">
              <thead className="bg-slate-50 text-left text-xs text-slate-500">
                <tr>
                  {['RUC', 'Razón social', 'Contacto', 'Teléfono', 'Correo', 'Entrega', 'Estado'].map((h) => (
                    <th key={h} className="px-4 py-3 font-medium">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {suppliersList.map((s) => (
                  <tr key={s.ruc} className="hover:bg-slate-50">
                    <td className="px-4 py-3 text-slate-500">{s.ruc}</td>
                    <td className="px-4 py-3 font-medium text-slate-800">{s.name}</td>
                    <td className="px-4 py-3">{s.contact}</td>
                    <td className="px-4 py-3">{s.phone}</td>
                    <td className="px-4 py-3">{s.email}</td>
                    <td className="px-4 py-3">{s.lead} días</td>
                    <td className="px-4 py-3">
                      <Badge t={s.active ? 'Activo' : 'Inactivo'} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {isModalOpen && <SupForm onClose={() => setIsModalOpen(false)} />}
    </div>
  );
}

// Componente secundario para el formulario de proveedor
function SupForm({ onClose }: { onClose: () => void }) {
  const [formData, setFormData] = useState({
    ruc: '',
    name: '',
    contact: '',
    phone: '',
    email: '',
    lead: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isBusy, setIsBusy] = useState(false);

  const saveSupplier = async (e: React.FormEvent) => {
    e.preventDefault();
    const validationErrors: Record<string, string> = {};

    if (!/^\d{11}$/.test(formData.ruc)) validationErrors.ruc = 'El RUC debe tener 11 dígitos.';
    if (!formData.name.trim()) validationErrors.name = 'Campo obligatorio.';
    if (!formData.contact.trim()) validationErrors.contact = 'Campo obligatorio.';
    if (!/^\d{9}$/.test(formData.phone)) validationErrors.phone = 'Ingresa 9 dígitos.';
    if (!/\S+@\S+\.\S+/.test(formData.email)) validationErrors.email = 'Correo no válido.';
    if (!(+formData.lead > 0)) validationErrors.lead = 'Ingresa los días.';

    setErrors(validationErrors);
    if (Object.keys(validationErrors).length) return;

    setIsBusy(true);
    try {
      await supplierService.save({ ...formData, lead: +formData.lead, active: true } as Supplier);
      toast('Proveedor creado');
      onClose();
    } catch (err: any) {
      setErrors({ ruc: err.message });
    } finally {
      setIsBusy(false);
    }
  };

  const renderField = (fieldKey: keyof typeof formData, labelText: string, inputType = 'text') => (
    <Field label={labelText + ' *'} error={errors[fieldKey]}>
      <input
        className={inp}
        type={inputType}
        value={formData[fieldKey]}
        onChange={(e) => setFormData({ ...formData, [fieldKey]: e.target.value })}
      />
    </Field>
  );

  return (
    <Modal wide title="Nuevo proveedor" onClose={onClose}>
      <form onSubmit={saveSupplier} noValidate className="space-y-3">
        <div className="grid gap-3 sm:grid-cols-2">
          {renderField('ruc', 'RUC')}
          {renderField('name', 'Razón social')}
          {renderField('contact', 'Contacto')}
          {renderField('phone', 'Teléfono', 'tel')}
          {renderField('email', 'Correo', 'email')}
          {renderField('lead', 'Tiempo de entrega (días)', 'number')}
        </div>
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