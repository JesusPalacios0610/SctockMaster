# Base de datos ElectroMarket (PostgreSQL)

Base: `electromarket`, esquema `public`.

## Archivos

| Archivo | Para qué sirve |
|---|---|
| `script_electromarket.sql` | Crea todas las tablas (versión 3). Solo para una base **nueva y vacía**. |
| `migracion_usuario_actualizacion.sql` | Pasa una base de la versión 1 a la 2 (agrega `usuario_actualizacion`). |
| `migracion_v3_activo_contacto_documentos.sql` | Pasa una base de la versión 2 a la 3 (agrega `activo`, `telefono`, `correo`, `tipo_documento`, `documento`). |

Cada script se ejecuta **una sola vez**, en un Query Tool de pgAdmin conectado a `electromarket`.

## ¿Qué hago yo?

1. **No tengo la base creada:** crea la base con `CREATE DATABASE electromarket;`, conéctate a ella y ejecuta `script_electromarket.sql`. Listo.
2. **Ya tengo la base:** averigua tu versión con esta consulta:

```sql
SELECT table_name, column_name
FROM information_schema.columns
WHERE table_schema = 'public'
  AND column_name IN ('usuario_actualizacion', 'activo')
  AND table_name = 'producto';
```

- No devuelve filas: tienes la **versión 1**. Ejecuta `migracion_usuario_actualizacion.sql` y luego `migracion_v3_activo_contacto_documentos.sql`.
- Devuelve solo `usuario_actualizacion`: tienes la **versión 2**. Ejecuta solo `migracion_v3_activo_contacto_documentos.sql`.
- Devuelve `usuario_actualizacion` y `activo`: ya tienes la **versión 3**. No hagas nada.

## Importante

El backend usa `spring.jpa.hibernate.ddl-auto=validate`: Hibernate **no crea ni modifica tablas**. Si tu base no está en la versión 3, el backend no arranca y marca error por columnas que faltan.

La contraseña de PostgreSQL **nunca** se sube al repositorio.
