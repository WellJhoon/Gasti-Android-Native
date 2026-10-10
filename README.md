# Gasti-Android-Native 📱🌿

Aplicación nativa de Android para el sistema de control financiero **Gasty (Control Manager)**, desarrollada en **Kotlin** con **Jetpack Compose** y **Material 3**.

---

## 🎨 Características

- **Diseño Táctil Fiel a Gasty Web:** Paleta de colores idéntica a `tailwind.config.ts` (`#101714` verde noche, `#5E824A` oliva de balance/ingresos, `#D95D5D` coral de gastos).
- **Soporte Dinámico de Temas:**
  - 🌙 **Modo Oscuro (Gasty Night):** Contraste profundo y descansado para la vista.
  - ☀️ **Modo Claro (Gasty Soft):** Fondos cremas limpios `#F7F8F5` y tarjetas blancas `#FFFFFF` con bordes 3D.
- **Navegación Móvil de 4 Secciones:**
  - 🏠 **Inicio (Dashboard):** Dinero actual en cuenta, presupuesto diario sugerido, gastos e ingresos del mes, accesos rápidos.
  - 📋 **Movimientos:** Historial con buscador reactivo y filtros táctiles por gastos/ingresos.
  - 🏦 **Finanzas:** Gestión de metas de ahorro con barras de progreso y control de deudas con fechas de corte.
  - ⚙️ **Ajustes:** Perfil de usuario, conmutador de tema oscuro/claro y configuración de seguridad.
- **Registro Rápido de Movimientos:** Modal Bottom Sheet táctil para registrar gastos o ingresos al instante.

---

## 🏗️ Arquitectura & Tecnologías

- **Lenguaje:** Kotlin 2.x
- **UI Toolkit:** Jetpack Compose con Material 3
- **Arquitectura:** MVVM (Model-View-ViewModel) con `StateFlow` reactivo y Coroutines
- **Networking:** Retrofit 2 + OkHttp + Gson
- **Backend API:** Conecta con el API REST de Gasty en Express (`localhost:4000` / `10.0.2.2:4000` en emulador)

---

## 🚀 Requisitos & Ejecución

- **Android Studio:** Ladybug / Koala o superior
- **JDK:** 17+
- **Min SDK:** Android 8.0 (API 26)
- **Target SDK:** Android 14+ (API 34)

Para compilar y correr:
```bash
./gradlew installDebug
```
