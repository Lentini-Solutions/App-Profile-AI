# Profile AI

Aplicación Android nativa de **tarjeta de perfil personal** que utiliza la **cámara inteligente** del dispositivo para escanear códigos (QR, Data Matrix, Aztec, Codabar) mediante **Machine Learning on-device** y **autocompletar** automáticamente los campos del perfil (correo, sitio web, teléfono, ubicación geográfica), evitando la captura manual de datos.

> ⚠️ **Proyecto exclusivamente educativo.** Su uso comercial está prohibido. Ver la sección [Licencia](#-licencia).

---

## 📱 ¿Qué hace?

Profile AI gestiona la información de una persona (nombre, email, sitio web, teléfono e imagen, además de coordenadas geográficas) en una pantalla de perfil, y permite editarla de dos maneras:

1. **Manualmente**, escribiendo los datos en la pantalla de edición.
2. **Automáticamente**, abriendo la *Cámara Inteligente* que, en tiempo real, detecta códigos de barras / QR y rellena el campo correspondiente según el tipo de dato detectado.

Desde el perfil, los datos se vuelven accionables: tocar el teléfono abre el marcador, el email abre la app de correo, el sitio web abre el navegador y la ubicación abre Google Maps.

### Funcionalidades principales

- **Escaneo con ML Kit + CameraX**: reconocimiento de códigos `QR_CODE`, `DATA_MATRIX`, `AZTEC` y `CODABAR` directamente en el dispositivo (sin enviar datos a la nube).
- **Autocompletado inteligente por tipo**: el valor escaneado se direcciona al campo correcto según su tipo (`EMAIL`, `URL`, `PHONE`, `GEO`, o texto plano como nombre).
- **Persistencia local** mediante SQLite (operaciones CRUD) y/o DataStore Preferences.
- **Acciones contextuales** mediante *Intents*: marcar teléfono, enviar correo, abrir web y ver ubicación en el mapa.
- **Pantalla de configuración**: habilitar/deshabilitar clicks, ajustar el tamaño de la imagen del perfil (chica / mediana / grande) y restaurar / borrar datos.
- **Gestión de permisos de cámara** en tiempo de ejecución con Accompanist Permissions.

---

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura limpia por capas (Clean Architecture)** con patrón **MVVM**, escrito 100% en **Kotlin** con **Jetpack Compose**.

```
com.example.profileai
│
├── App.kt                      # Application: inicializa Koin (DI)
├── MainActivity.kt             # Punto de entrada de la UI Compose
│
├── di/                         # Inyección de dependencias (Koin)
│   └── MainModule.kt
│
├── navigation/                 # Navegación (Navigation Compose)
│   ├── Screens.kt              # Rutas: Profile, Settings, Edit
│   └── NavGraph.kt
│
├── view/                       # Capa de presentación (Composables)
│   ├── ProfileScreen.kt
│   ├── EditScreen.kt
│   ├── SettingsScreen.kt
│   └── ScannerScreen.kt        # Cámara inteligente (CameraX + ML Kit)
│
├── view_model/                 # MVVM: expone estado vía StateFlow
│   └── MainViewModel.kt
│
├── domain/                     # Capa de dominio (independiente del framework)
│   ├── model/User.kt           # Modelo de datos
│   └── repository/MainRepository.kt   # Contrato (interfaz)
│
├── data/                       # Capa de datos (implementaciones)
│   ├── repository/
│   │   ├── MainRepositorySQLiteDSImpl.kt
│   │   └── MainRepositoryDSImpl.kt
│   └── source/
│       ├── DatabaseHelper.kt   # SQLiteOpenHelper (CRUD)
│       ├── DataStore.kt        # DataStore Preferences
│       └── FakeDatabase.kt
│
├── utils/
│   └── BarCodeAnalizer.kt      # ImageAnalysis.Analyzer con ML Kit
│
├── components/                 # Componentes UI reutilizables (Splash, etc.)
├── constants/                  # Constantes (nombres de BD, claves, etc.)
└── ui/theme/                   # Tema, colores, tipografía, dimensiones
```

### Flujo de datos

```
ScannerScreen (CameraX) → BarCodeAnalyzer (ML Kit)
        │
        ▼
   Barcode detectado → se direcciona por tipo → EditScreen autocompletado
        │
        ▼
MainViewModel.saveUser() → MainRepository → DatabaseHelper / DataStore
        │
        ▼
   Flow<User> → StateFlow → la UI Compose se recompone reactivamente
```

- La **capa de dominio** define el contrato `MainRepository` y el modelo `User`, sin depender de Android ni de frameworks.
- La **capa de datos** ofrece dos implementaciones intercambiables del repositorio (SQLite y DataStore), seleccionables desde el módulo de Koin.
- El **ViewModel** expone el estado mediante `StateFlow` usando `stateIn(SharingStarted.WhileSubscribed)`, y la UI lo observa de forma reactiva.

### Stack tecnológico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2 |
| UI | Jetpack Compose + Material 3 |
| Arquitectura | Clean Architecture + MVVM |
| Inyección de dependencias | Koin |
| Navegación | Navigation Compose |
| Cámara | CameraX |
| Machine Learning | Google ML Kit – Barcode Scanning (on-device) |
| Persistencia | SQLite (`SQLiteOpenHelper`) + DataStore Preferences |
| Carga de imágenes | Coil 3 |
| Asincronía | Kotlin Coroutines + Flow |
| Permisos | Accompanist Permissions |

---

## 🚀 Uso

### Requisitos

- Android Studio (versión reciente compatible con AGP 9.x)
- JDK 11
- SDK mínimo: **Android 8.0 (API 26)** · SDK objetivo: **API 37**
- Un dispositivo o emulador con **cámara** (necesaria para el escaneo)

### Compilar y ejecutar

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd ProfileAI

# Compilar
./gradlew assembleDebug

# Instalar en un dispositivo/emulador conectado
./gradlew installDebug
```

También puedes abrir el proyecto directamente en **Android Studio** y ejecutarlo con el botón *Run*.

### Cómo se usa la app

1. Al abrir, se muestra la **pantalla de perfil** con los datos actuales del usuario.
2. Pulsa **Editar** para modificar los datos manualmente, o abre la **Cámara Inteligente**.
3. Apunta la cámara a un **código QR / código de barras**: la app detecta el tipo de dato y muestra el valor reconocido.
4. Pulsa **Añadir** para autocompletar el campo correspondiente del perfil.
5. Guarda los cambios. Desde el perfil, toca cualquier dato para ejecutar su acción (llamar, escribir, navegar, ver mapa).
6. En **Configuración** puedes habilitar/deshabilitar clicks, cambiar el tamaño de imagen y borrar/restaurar datos.

> La app requiere permiso de **cámara**, que se solicita en tiempo de ejecución. El reconocimiento de códigos ocurre **completamente en el dispositivo**; no se envían datos a servidores externos.

---

## 📄 Licencia

Este proyecto se distribuye **únicamente con fines educativos y de aprendizaje**.

- ✅ Permitido: estudiar, modificar y usar el código con fines **educativos, académicos y personales**.
- ❌ **Prohibido todo uso comercial** del proyecto o de cualquiera de sus partes.

Consulta el archivo [LICENSES](./LICENSES) para conocer los términos completos.
