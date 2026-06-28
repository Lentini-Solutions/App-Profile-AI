# `stateIn` vs patrón `_uiState` / `uiState`

Guía de referencia para decidir cómo exponer estado desde un `ViewModel` en Android,
y entender las diferencias de rendimiento, recursos y casos de uso.

---

## TL;DR

| Fuente | Forma | Patrón recomendado |
|---|---|---|
| **DataStore** | `Flow` (reactivo) | `stateIn` |
| **Room** | `Flow` (reactivo) | `stateIn` |
| **Retrofit** | `suspend` (one-shot) | `_uiState` (loading/error/data a mano) |

> **El criterio NO es "local vs remoto" ni "qué librería".**
> Es: **¿la fuente te da un `Flow` reactivo, o un resultado de una sola vez (one-shot)?**
>
> - Fuente reactiva (`Flow`) → `stateIn`.
> - Operación puntual (`suspend`) con estados de carga/error → `_uiState`.

---

## Los dos patrones, lado a lado

Ambos producen un `StateFlow` listo para la UI. Hacen "lo mismo" en este ejemplo
(exponer un `user`), pero por dentro se comportan distinto.

### Patrón A — `stateIn`

```kotlin
val user: StateFlow<User> = repository.user.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = User()
)
```

### Patrón B — `_uiState` / `uiState`

```kotlin
private val _uiState = MutableStateFlow(User())
val uiState: StateFlow<User> = _uiState.asStateFlow()

init {
    viewModelScope.launch {
        repository.user.collect { _uiState.value = it }   // ← la diferencia clave
    }
}
```

---

## ¿Qué hace `stateIn` y qué significan sus parámetros?

`stateIn` toma un `Flow` **frío** (cold) y lo convierte en un `StateFlow` **caliente** (hot):

- **Cold flow:** no produce nada hasta que alguien lo colecta, y se ejecuta de cero
  por **cada** colector. Dos observadores = dos lecturas al DataStore. Además no tiene
  "valor actual".
- **Hot flow (`StateFlow`):** una sola ejecución compartida entre todos los colectores,
  y siempre tiene un `.value` disponible.

```kotlin
repository.user.stateIn(
    scope = viewModelScope,                          // ¿hasta cuándo lo mantengo vivo?
    started = SharingStarted.WhileSubscribed(5_000), // ¿cuándo lo enciendo/apago?
    initialValue = User()                            // ¿qué muestro hasta el primer dato?
)
```

### `scope = viewModelScope`
Define cuánto tiempo vive la colección de fondo. `viewModelScope` vive exactamente lo
que vive el `ViewModel`; al destruirse, se cancela y no hay leaks.

### `started = SharingStarted.WhileSubscribed(5_000)`
Define **cuándo arranca y para** la colección del upstream (`repository.user`).

| Estrategia | Cuándo colecta el upstream |
|---|---|
| `Eagerly` | Apenas se crea, y nunca para (hasta que muere el scope) |
| `Lazily` | Con el primer colector, y ya nunca para |
| `WhileSubscribed(...)` | **Solo mientras haya al menos un colector activo** |

`WhileSubscribed` es la recomendada para UI:
- **0 colectores → para de leer** el DataStore (ahorra batería/CPU).
- **Aparece un colector → arranca** (o re-arranca).

El `5_000` es `stopTimeoutMillis`: **tiempo de gracia** antes de parar cuando se va el
último colector. Sirve para sobrevivir a **cambios de configuración** (ej. rotar la
pantalla): si el colector vuelve dentro de 5 s, la colección nunca se cortó → no se
re-lee disco ni hay parpadeos. Si pasan los 5 s sin nadie, ahí sí para.

### `initialValue = User()`
Un `StateFlow` **siempre** debe tener un valor desde el primer instante. Como el
DataStore tarda unos ms en entregar el primer dato real (leer disco, deserializar),
este placeholder se muestra mientras tanto. Evita manejar `null` en la UI.

---

## Diferencia real de rendimiento

La **única** diferencia de rendimiento que importa está en **cuándo se colecta el
upstream** (la lectura del DataStore/Room → disco → deserialización).

### `stateIn` con `WhileSubscribed(5_000)`
La colección arranca y para según haya observadores (+ 5 s de gracia). App en background
> 5 s → **deja de leer**. Cero trabajo desperdiciado.

### `_uiState` con `init { collect }`
El `collect` corre durante **toda la vida del ViewModel**, haya o no alguien mirando.
App en background → **sigue colectando** y actualizando `_uiState.value` al pedo. Solo
para cuando el ViewModel muere.

👉 **Ahí está el ahorro:** `WhileSubscribed` libera el trabajo del upstream cuando la UI
no está; el `init { collect }` "ingenuo" lo mantiene prendido de gancho.

### En todo lo demás, empatan

| Aspecto | `stateIn` | `_uiState` |
|---|---|---|
| Memoria (1 StateFlow + 1 valor cacheado) | igual | igual |
| Lecturas compartidas entre N colectores | 1 sola | 1 sola |
| Tiene `.value` siempre disponible | Sí | Sí |
| Costo de recomposición de la UI | igual | igual |

> No elijas un patrón sobre el otro "por memoria": ahí son equivalentes. La diferencia
> es el ciclo de colección del upstream, y la **semántica**.

---

## Cuándo usar cada uno

### Usá `stateIn` cuando…
El estado es un *passthrough* o transformación directa de **una** fuente reactiva
(un `Flow` del repo). Es read-only, declarativo, y con `WhileSubscribed` te da la mejor
gestión de recursos **gratis**.

> Sirve igual para **DataStore y Room**: ambos emiten `Flow`.

### Usá `_uiState` cuando…
Necesitás **mutar el estado imperativamente desde varios eventos** que no vienen de un
solo flow: `isLoading`, mensajes de error, estado de un diálogo, validaciones de
formulario, o una llamada one-shot de Retrofit.

```kotlin
private val _uiState = MutableStateFlow(UiState())
val uiState = _uiState.asStateFlow()

fun load() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val user = api.fetchUser()           // Retrofit: suspend one-shot
            _uiState.update { it.copy(isLoading = false, user = user) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }
}
```

`stateIn` no sirve acá porque no hay un flow de origen para envolver: hay una llamada
puntual y varios estados que manejás a mano.

---

## El matiz importante: no son excluyentes

El patrón `_uiState` **no es intrínsecamente menos eficiente**: lo es solo si lo escribís
con `init { collect }`. Podés combinar lo mejor de ambos — armar el estado desde varios
flows y **seguir** usando `stateIn` para conservar el `WhileSubscribed`:

```kotlin
val uiState: StateFlow<UiState> = combine(
    repository.user,      // reactivo (DataStore/Room)
    repository.settings   // reactivo
) { user, settings ->
    UiState(user, settings)
}.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = UiState()
)
```

En apps reales casi siempre **convive todo**: Room te da datos cacheados (reactivo) y
Retrofit refresca desde la red (one-shot con loading/error). El patrón ganador es un
`UiState` propio que mezcla ambas naturalezas:

```kotlin
data class UiState(
    val users: List<User> = emptyList(),  // viene de Room (reactivo, vía stateIn)
    val isRefreshing: Boolean = false,    // viene de Retrofit (imperativo, a mano)
    val error: String? = null
)
```

---

## Reglas para recordar

1. **Fuente reactiva (`Flow`) → `stateIn`.** Da igual DataStore o Room.
2. **Operación one-shot (`suspend`) con carga/error → `_uiState`.** Típico de Retrofit.
3. **Room y DataStore están del mismo lado**: ambos emiten `Flow` → `stateIn`.
4. A igual funcionalidad, `stateIn + WhileSubscribed` **ahorra recursos** frente a
   `_uiState + init { collect }`, porque apaga la lectura cuando nadie observa.
5. La elección de fondo es **semántica**, no de rendimiento: estado **derivado de un
   flow** (`stateIn`) vs estado que **mutás a mano** desde varios eventos (`_uiState`).
6. Cuando convive lo reactivo y lo imperativo → `UiState` propio con
   `combine(...).stateIn(...)`.
