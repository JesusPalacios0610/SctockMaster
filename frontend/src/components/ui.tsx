import { useEffect, useState, ReactNode, ButtonHTMLAttributes } from 'react';
import { X, Inbox } from 'lucide-react';

export const inp = 'w-full h-10 rounded-lg border border-slate-300 bg-white px-3 text-sm outline-none transition focus:border-blue-600 focus:ring-2 focus:ring-blue-600/25';


export const Btn = ({
  v = 'p',
  className = '',
  ...props
}: ButtonHTMLAttributes<HTMLButtonElement> & { v?: 'p' | 's' | 'd' }) => {
  const variantStyles = {
    p: 'bg-blue-600 text-white hover:bg-blue-700',
    s: 'bg-white border border-slate-300 text-slate-700 hover:bg-slate-50',
    d: 'bg-red-600 text-white hover:bg-red-700',
  }[v];

  return (
    <button
      {...props}
      className={`h-10 px-4 rounded-lg text-sm font-medium transition inline-flex items-center justify-center gap-2 disabled:opacity-50 outline-none focus-visible:ring-2 focus-visible:ring-blue-600 focus-visible:ring-offset-1 ${variantStyles} ${className}`}
    />
  );
};


const tone: Record<string, string> = {
  Normal: 'bg-emerald-50 text-emerald-700',
  Bajo: 'bg-amber-50 text-amber-700',
  Crítico: 'bg-red-50 text-red-700',
  Inactivo: 'bg-slate-100 text-slate-600',
  Activo: 'bg-emerald-50 text-emerald-700',
  Entrada: 'bg-emerald-50 text-emerald-700',
  Salida: 'bg-orange-50 text-orange-700',
};


export const Badge = ({ t }: { t: string }) => (
  <span className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-medium ${tone[t] || 'bg-slate-100 text-slate-700'}`}>
    <i className="h-1.5 w-1.5 rounded-full bg-current" />
    {t}
  </span>
);


export const Panel = ({
  title,
  children,
  action,
  className = '',
}: {
  title?: string;
  children: ReactNode;
  action?: ReactNode;
  className?: string;
}) => (
  <section className={`rounded-xl border border-slate-200 bg-white shadow-sm ${className}`}>
    {title && (
      <div className="flex items-center justify-between px-5 pt-4 border-b border-slate-100 pb-3">
        <h2 className="text-sm font-semibold text-slate-800">{title}</h2>
        {action}
      </div>
    )}
    <div className="p-5">{children}</div>
  </section>
);

// Componente de Campo de Formulario con Etiqueta y Errores
export const Field = ({
  label,
  error,
  children,
}: {
  label: string;
  error?: string;
  children: ReactNode;
}) => (
  <label className="block text-sm">
    <span className="mb-1 block font-medium text-slate-700">{label}</span>
    {children}
    {error && (
      <span role="alert" className="mt-1 block text-xs text-red-600">
        {error}
      </span>
    )}
  </label>
);

// Componente Modal General
export function Modal({
  title,
  onClose,
  children,
  wide,
}: {
  title: string;
  onClose: () => void;
  children: ReactNode;
  wide?: boolean;
}) {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => e.key === 'Escape' && onClose();
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [onClose]);

  return (
    <div
      className="fixed inset-0 z-50 flex items-end justify-center bg-slate-900/50 backdrop-blur-xs sm:items-center sm:p-4"
      onMouseDown={(e) => e.target === e.currentTarget && onClose()}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-label={title}
        className={`pop max-h-[92vh] w-full overflow-auto rounded-t-2xl bg-white shadow-xl sm:rounded-2xl ${
          wide ? 'sm:max-w-2xl' : 'sm:max-w-md'
        }`}
      >
        <div className="sticky top-0 z-10 flex items-center justify-between border-b border-slate-200 bg-white px-5 py-3">
          <h2 className="font-semibold text-slate-800">{title}</h2>
          <button
            aria-label="Cerrar"
            onClick={onClose}
            className="grid h-9 w-9 place-items-center rounded-lg text-slate-500 hover:bg-slate-100"
          >
            <X size={18} />
          </button>
        </div>
        <div className="p-5">{children}</div>
      </div>
    </div>
  );
}

// Componente de Confirmación para Acciones Críticas
export const Confirm = ({
  title,
  text,
  busy,
  onOk,
  onCancel,
}: {
  title: string;
  text: string;
  busy?: boolean;
  onOk: () => void;
  onCancel: () => void;
}) => (
  <Modal title={title} onClose={onCancel}>
    <p className="text-sm text-slate-600">{text}</p>
    <div className="mt-5 flex justify-end gap-2 pt-3 border-t border-slate-100">
      <Btn v="s" onClick={onCancel}>
        Cancelar
      </Btn>
      <Btn onClick={onOk} disabled={busy}>
        {busy ? 'Procesando…' : 'Confirmar'}
      </Btn>
    </div>
  </Modal>
);

// Componente para Estados Vacíos
export const Empty = ({
  title,
  text,
  action,
}: {
  title: string;
  text?: string;
  action?: ReactNode;
}) => (
  <div className="flex flex-col items-center py-12 text-center">
    <Inbox className="mb-3 text-slate-400" size={36} />
    <p className="font-medium text-slate-700">{title}</p>
    {text && <p className="mt-1 text-sm text-slate-500">{text}</p>}
    {action && <div className="mt-4">{action}</div>}
  </div>
);

// Componente de Carga Esquelética (Skeleton Loading)
export const Loading = ({ rows = 6 }: { rows?: number }) => (
  <div className="space-y-3" aria-busy="true">
    {Array.from({ length: rows }, (_, i) => (
      <div key={i} className="h-11 animate-pulse rounded-lg bg-slate-100" />
    ))}
  </div>
);

// Sistema de Notificaciones Toast
let pushToast: (message: string, isError?: boolean) => void = () => {};

export const toast = (message: string, isError = false) => pushToast(message, isError);

export function Toasts() {
  const [toastsList, setToastsList] = useState<{ id: number; m: string; e: boolean }[]>([]);

  useEffect(() => {
    pushToast = (m: string, e = false) => {
      const id = Math.random();
      setToastsList((prev) => [...prev, { id, m, e }]);
      setTimeout(() => {
        setToastsList((prev) => prev.filter((item) => item.id !== id));
      }, 3200);
    };
  }, []);

  return (
    <div role="status" className="fixed right-4 top-4 z-[60] space-y-2">
      {toastsList.map((toastItem) => (
        <div
          key={toastItem.id}
          className={`pop rounded-lg px-4 py-3 text-sm text-white shadow-lg ${
            toastItem.e ? 'bg-red-600' : 'bg-slate-900'
          }`}
        >
          {toastItem.m}
        </div>
      ))}
    </div>
  );
}