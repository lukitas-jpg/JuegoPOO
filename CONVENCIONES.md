# Convenciones del grupo

Acuerdos para que el código de los cuatro se vea igual y coincida con los diagramas. Esto importa para la entrega: la consigna exige que los diagramas sean consistentes con la implementación.

---

## 1. Java y codificación

- **Java 17** como mínimo (`maven.compiler.release = 17` en el `pom.xml`). Si alguien tiene JDK 21 o más nuevo también funciona; lo único que no se puede es usar funciones posteriores a Java 17.
- **El proyecto compila sin un solo warning**, con `-Xlint:all,-serial`. Si tu cambio
  agrega uno, arreglalo antes de subirlo. El único lint apagado es `serial`, y el
  motivo está escrito en el `pom.xml`: pediría un `serialVersionUID` en unas 40
  subclases y además se queja de que los campos sean `List` o `Map`. El
  `serialVersionUID` sí está en las siete raíces del grafo que se guarda
  (`Combatiente`, `Movimiento`, `EfectoEstado`, `Item`, `Inventario`, `Party` y
  `Partida`); las subclases lo heredan.
- **Todos los archivos en UTF-8.** Ya está configurado en tres lugares (`pom.xml`, `.editorconfig` y `.gitattributes`). Si alguien ve `TipoDaÃ±o` en vez de `TipoDaño`, su IDE no está respetando el `.editorconfig`:
  - **IntelliJ:** *Settings → Editor → File Encodings*, poner UTF-8 en los tres campos.
  - **Eclipse:** *Window → Preferences → General → Workspace → Text file encoding*.
  - **VS Code:** lo toma del `.editorconfig` sin tocar nada.

---

## 2. Nombres

| Qué | Formato | Ejemplo |
|---|---|---|
| Clases, interfaces, enums | `PascalCase` | `CalculadorDeDaño` |
| Métodos y atributos | `camelCase` | `getVelocidadEfectiva()` |
| Constantes y valores de enum | `MAYUSCULAS_CON_GUION` | `OBJETIVO_UNICO` |
| Paquetes | minúscula, una palabra | `combatientes` |

### La regla de la ñ

Va ñ en todo el código: nombres de tipo incluidos.

| Dónde | Con ñ | Ejemplo |
|---|---|---|
| Nombres de tipo (clases, interfaces, enums) | sí | `TipoDaño`, `CalculadorDeDaño`, `Emponzoñador` |
| Métodos | sí | `recibirDaño()`, `getDañoTotal()` |
| Atributos | sí | `dañoPorTurno`, `tipoDañoQueMejora` |
| Parámetros y variables locales | sí | `recibirDaño(int daño, TipoDaño tipo)` |
| Textos que ve el jugador | sí | `"¡Daño crítico!"` |
| Valores de enum | no | `FISICO`, `MAGICO`, `OBJETIVO_UNICO` |

Los valores de enum son la única excepción, y no es por la codificación: la convención de Java es mayúsculas con guión bajo, y en mayúsculas la ñ no aporta nada.

**Lo que había que verificar, y por qué.** Cada clase, interfaz o enum genera un archivo `.class` con ese mismo nombre, y el nombre de ese archivo lo escribe el compilador usando la codificación **del sistema operativo**, no la del proyecto. Eso no se configura desde el `pom.xml`. En una máquina con el sistema en una codificación que no sea UTF-8, un tipo llamado `TipoDaño` hace fallar la compilación entera con un error del tipo `bad filename RelativeFile[TipoDa?o.class]`. Por eso los cinco tipos afectados estuvieron en ASCII hasta tenerlo probado.

**Ya está probado en las cuatro máquinas**, así que los cinco pasaron a ñ: `TipoDaño`, `CalculadorDeDaño`, `ResultadoDaño`, `Emponzoñador` y `SeñorDeLasSombras`.

**Si se suma una máquina nueva al grupo**, que corra esto antes de tocar nada. Guardarlo como `Prueba.java` y compilarlo con `javac -encoding UTF-8 Prueba.java`:

```java
public class Prueba {
    enum TipoDaño { FISICO }
    public static void main(String[] args) { System.out.println(TipoDaño.FISICO); }
}
```

Si da `bad filename`, esa máquina tiene el sistema en una codificación que no es UTF-8 y hay que arreglarlo ahí (en Windows, *Configuración regional → Administrativo → Cambiar configuración regional del sistema → Usar UTF-8*), no sacarle la ñ al proyecto.

- Booleanos con prefijo de pregunta: `estaVivo()`, `tieneManaSuficiente()`, `puedeActuar()`, `esDeCombate()`.
- Getters con `get`, salvo los booleanos: `getNombre()`, pero `fueVictoria()`.
- Los parámetros se llaman igual que en el diagrama: si el diagrama dice `elegirAccion(party : Party, aliados : List<Enemigo>)`, el método se escribe con esos dos nombres.
- **Exactamente los mismos nombres que en los diagramas.** Si hay que renombrar algo, se cambia también en el diagrama y se avisa al grupo.

---

## 3. Dónde va cada cosa

```
modelo/
├── combatientes/           Combatiente, Personaje, Enemigo y sus subclases
│   └── objetivos/          las 4 interfaces de selección de objetivo
├── movimientos/            Accion, Movimiento, Ataque, Habilidad
├── efectos/                EfectoEstado y sus 8 subclases
├── items/                  Item, Consumible, Equipable, Inventario y los catálogos
├── tienda/                 Tienda, GeneradorDeStock, MaquinaDeSlots, ResultadoTirada
├── combate/                Combate, GestorDeTurnos, CalculadorDeDaño
└── partida/                Juego, Partida, Party, Etapa, GeneradorDeEtapas

persistencia/               RepositorioPartidas y RepositorioPartidasArchivo
```

`items/` tiene las definiciones de objetos; `tienda/` tiene la lógica de venderlos.
Están separados porque son dos cosas distintas: un `Arma` existe aunque no haya
ninguna tienda abierta.

Reglas:

- **Una clase por archivo.** Nada de clases internas salvo que sean privadas y de dos líneas.
- **Las interfaces chicas y relacionadas entre sí van juntas en su propio paquete.** Por eso las cuatro interfaces de apuntado (`ObjetivoMasDebil`, `ObjetivoMasPeligroso`, `ObjetivoAleatorio`, `ObjetivoSinEfecto`) están en `combatientes/objetivos/` y no sueltas entre los once enemigos.
- **Las clases abstractas van en el paquete de su jerarquía**, junto a sus subclases: `Combatiente` con los combatientes, `Movimiento` con los movimientos.
- Cada paquete tiene un `package-info.java` que lista qué clases le corresponden. **Si creás una clase, fijate ahí que vaya en el paquete correcto.**

---

## 4. SOLID aplicado a este proyecto

No son cinco reglas abstractas: cada una se traduce en algo concreto del modelo. Si al escribir una clase dudás, estos son los criterios.

**S — Responsabilidad única.** Una clase, un motivo para cambiar. Por eso `Combate` no calcula daño ni ordena turnos: delega en `CalculadorDeDaño` y `GestorDeTurnos`. Si una clase te queda con más de 15 métodos o el nombre te sale con un "y" en el medio, está haciendo dos cosas.

**O — Abierto/cerrado.** Agregar un enemigo nuevo tiene que ser crear una subclase de `Enemigo`, sin tocar `Combate` ni `GestorDeTurnos`. Si para sumar contenido hay que modificar el motor, algo está mal.

**L — Sustitución de Liskov.** Donde se espera un `Combatiente`, cualquier subclase tiene que funcionar. En la práctica: una subclase no puede lanzar una excepción en un método que hereda ni devolver `null` donde el padre devuelve una lista; que devuelva la lista vacía.

**I — Segregación de interfaces.** Interfaces chicas y específicas en lugar de una grande. Es la razón por la que tenemos cuatro interfaces de apuntado de un método cada una y no una sola interfaz con todo adentro: el `Hechicero` no implementa ninguna porque ataca en área, y el `SeñorDeLasSombras` implementa dos.

**D — Inversión de dependencias.** Depender de abstracciones, no de implementaciones. `Juego` tiene un atributo de tipo `RepositorioPartidas` (la interfaz), nunca de `RepositorioPartidasArchivo`. Lo mismo con `EfectoConsumible`.

---

## 5. GRASP

Los que ya están decididos en el diseño y hay que respetar al programar:

- **Controller:** `Juego` recibe los eventos del sistema y delega. Los controladores de la vista hablan con `Juego`, no con `Combate` ni `Party`.
- **Information Expert:** quien tiene el dato hace el cálculo. Nadie fuera de `Combatiente` suma equipamiento y buffs: para eso están los getters "efectivos".
- **Creator:** `EtapaDeCombate` crea el `Combate` porque tiene los enemigos. `Combate` crea la `Accion` porque tiene los datos.
- **Polymorphism:** nada de `instanceof` ni de `switch` por tipo. Si aparece uno, falta un método abstracto.
- **Pure Fabrication:** `GestorDeTurnos`, `CalculadorDeDaño`, `GeneradorDeEtapas` y `GeneradorDeStock` no existen en el dominio real; existen para que las otras clases no se vuelvan gigantes.

---

## 6. MVC

1. **El modelo no conoce a la vista.** Ningún archivo de `modelo/` puede importar `javax.swing`, `java.awt`, `vista` ni `controlador`. El modelo tiene que poder probarse por consola.
2. **La vista no tiene lógica del juego.** Muestra datos y le avisa al controlador cuando el jugador hace algo.
3. **Los controladores hablan con `Juego`.**
4. **`App` es la única clase que conoce las tres capas:** crea el modelo, las vistas y los controladores, y los conecta.

---

## 7. Encapsulamiento

- Atributos siempre `private` o `protected`, como en el diagrama. Nunca `public`.
- `protected` solo cuando las subclases realmente lo necesitan (es el caso de los atributos de `Combatiente`). Si ninguna subclase lo toca, va `private`.
- No devolver listas internas para que las modifiquen desde afuera: devolver `List.copyOf(...)` o una copia.
- Nada de setters "por las dudas". Si nadie necesita cambiar un atributo desde afuera, no lleva setter.

---

## 8. Tests

El proyecto tiene JUnit 5 configurado. Los tests van en `src/test/java/` y se corren
solos con `mvn test` o `mvn package`.

Hoy hay **128 tests en seis archivos**:

| Archivo | Qué cubre |
|---|---|
| `PersonajeTest` | estadísticas base, daño, curación, niveles y equipo |
| `EfectosTest` | los 8 efectos de estado, el tope del veneno, los buffos que no se multiplican |
| `MovimientosTest` | los 24 movimientos, la fórmula de daño, el tope del crítico |
| `EnemigosTest` | los 11 enemigos, las 4 interfaces de apuntado y el escalado por etapa |
| `TiendaTest` | catálogos, inventario, reroll y el retorno exacto de la máquina |
| `MotorTest` | turnos, combate, etapas, partida, persistencia y **una run entera** |

Reglas para los que se agreguen:

- **Un test prueba una cosa y el nombre la dice.** `elVenenoNuncaPegaMasDelTope` se
  entiende sin abrirlo; `testVeneno2` no.
- **Nada de azar sin semilla.** `CalculadorDeDaño`, `HabilidadDeEstado`, `Enemigo`,
  `GeneradorDeStock` y `MaquinaDeSlots` tienen todos un constructor que recibe el
  `Random`. Usalo con semilla fija: un test que falla una vez cada veinte corridas es
  peor que no tenerlo.
- **Donde se pueda, calculá en vez de estimar.** El test del retorno de la máquina
  recorre los 216 resultados posibles y verifica que la suma dé 205, en vez de tirar
  muchas veces y mirar el promedio. Si alguien toca un premio, el test falla con el
  número viejo al lado del nuevo.
- **Si un test falla, primero leé si el que está mal es el test.** Nos pasó tres
  veces: el código tenía razón y la aserción estaba mal escrita.

---

## 9. Git

- **No trabajar directo en `main`.** Una rama por tarea:
  ```
  git checkout -b combate/gestor-de-turnos
  ```
- Antes de empezar: `git pull` en `main`.
- Cuando la tarea está lista: subir la rama y abrir un **Pull Request**. Otro integrante lo revisa antes de mergear, así todos conocen el código de los demás. Esto sirve directamente para la defensa individual, donde preguntan por cualquier parte del proyecto.
- Commits chicos y en español, diciendo qué se hizo: `Agrega CalculadorDeDaño con golpe critico`.
- No subir archivos del IDE ni `target/`; el `.gitignore` ya los excluye.
- GitHub compila y corre los tests en cada push (pestaña *Actions*). Si aparece una X roja, hay que arreglarlo antes de mergear.
