import { useState } from 'react';
import { Boxes, Eye, EyeOff, PackageCheck } from 'lucide-react';
import { authService } from '../services/store';
import { Btn, Field, inp } from '../components/ui';

export default function Login({ onLogin }: { onLogin: () => void }) {
  const [email, setEmail] = useState('admin@electromarket.pe');
  const [password, setPassword] = useState('admin123');
  const [showPassword, setShowPassword] = useState(false);
  const [isBusy, setIsBusy] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [validationErrors, setValidationErrors] = useState<{ e?: string; p?: string }>({});

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage('');

    // Validación básica de campos
    const errors = {
      e: /\S+@\S+\.\S+/.test(email) ? undefined : 'Ingresa un correo válido.',
      p: password ? undefined : 'Ingresa tu contraseña.',
    };
    setValidationErrors(errors);

    if (errors.e || errors.p) return;

    setIsBusy(true);
    try {
      await authService.login(email, password);
      onLogin();
    } catch (err: any) {
      setErrorMessage(err.message);
    } finally {
      setIsBusy(false);
    }
  };

  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      {/* Formulario de Acceso */}
      <div className="flex items-center justify-center bg-white p-6">
        <form onSubmit={handleSubmit} noValidate className="w-full max-w-sm space-y-4">
          <div className="mb-6 flex items-center gap-2">
            <span className="grid h-10 w-10 place-items-center rounded-lg bg-blue-600 text-white">
              <Boxes />
            </span>
            <div className="leading-tight">
              <b>ElectroMarket</b>
              <div className="text-xs text-slate-500">Sistema de inventario</div>
            </div>
          </div>

          <h1 className="text-2xl font-bold text-slate-800">Inicia sesión</h1>

          {errorMessage && (
            <div role="alert" className="rounded-lg bg-red-50 p-3 text-sm text-red-700">
              {errorMessage}
            </div>
          )}

          <Field label="Correo" error={validationErrors.e}>
            <input
              className={inp}
              type="email"
              autoComplete="username"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </Field>

          <Field label="Contraseña" error={validationErrors.p}>
            <div className="relative">
              <input
                className={inp + ' pr-11'}
                type={showPassword ? 'text' : 'password'}
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <button
                type="button"
                aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-1 top-0 grid h-10 w-10 place-items-center text-slate-500 hover:text-slate-700"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </Field>

          <a href="#" onClick={(e) => e.preventDefault()} className="block text-sm text-blue-600 hover:underline">
            ¿Olvidaste tu contraseña?
          </a>

          <Btn type="submit" disabled={isBusy} className="w-full h-11">
            {isBusy ? 'Ingresando…' : 'Iniciar sesión'}
          </Btn>

          <p className="text-xs text-slate-500">Demo: admin@electromarket.pe / admin123</p>
        </form>
      </div>

      {/* Banner lateral decorativo */}
      <div className="hidden flex-col justify-end bg-[#0f1b2d] p-12 text-white lg:flex">
        <PackageCheck size={40} className="mb-6 text-blue-400" />
        <p className="max-w-md text-3xl font-semibold leading-snug">
          Sabe qué hay en el almacén, qué falta y qué se movió, sin abrir una hoja de cálculo.
        </p>
      </div>
    </div>
  );
}