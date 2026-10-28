# Consumibles

Catálogo fijo: los cinco están siempre disponibles en las cuatro tiendas, no rotan y
no se sortean. Todos son de rareza **COMUN**, que es la rareza que queda reservada
para ellos ahora que los equipables usan solo RARO, EPICO y LEGENDARIO.

| Objeto | Efecto | Precio | Tope |
|---|---|---|---|
| Poción de Vida | Cura el **55% de la vida máxima** del objetivo | 25 | 12 |
| Elixir de Maná | Restaura el 50% del maná máximo | 20 | 8 |
| Antídoto | Quita Veneno y Quemadura | 15 | 6 |
| Polvo de Renacer | Revive a un caído con el 50% de su vida | 120 | 3 |
| Piedra de Afilar | +30% de ataque durante 3 turnos | 40 | 6 |

El tope es cuántos se pueden llevar a la vez en el inventario, no cuántos se pueden
comprar en total. Al gastarlos se puede volver a comprar en la tienda siguiente.

Usar un consumible **gasta el turno**, igual que atacar. Es una `AccionUsarConsumible`
y entra por el mismo lugar que las demás acciones.


## La poción contra la Curación del Curador

Esto es lo que pediste medir y es la decisión central del documento.

| | Poción de Vida | Curación (Curador) |
|---|---|---|
| Cuánto cura | 55% de la vida máxima | 20 + ataque mágico |
| Qué cuesta | 25 de oro, una vez | 10 de maná, recargable |
| Cuántas veces | 12 como mucho, y hay que recomprarlas | ~9 por combate a nivel 1, más después |
| Quién la puede usar | cualquiera | solo el Curador |
| Fuera de combate | sí | no |

En números concretos, sobre un Guerrero:

| Nivel | Vida del Guerrero | Cura la poción | Cura el Curador |
|---|---|---|---|
| 1 | 120 | 66 | 34 |
| 6 | 180 | 99 | 44 |
| 11 | 240 | 132 | 78 (con báculo legendario) |

**La poción cura casi el doble por uso, y aun así el Curador es mejor.** Esa es la
relación que buscábamos: la poción pega más fuerte, el Curador aguanta más tiempo. El
maná se llena entero al empezar cada combate, así que el Curador puede curar nueve
veces por pelea sin gastar un peso, mientras que doce pociones tienen que alcanzar
para varias etapas y cuestan oro que si no iba a equipamiento.

Que la poción se calcule como **porcentaje de la vida máxima** y no como un número
fijo es lo que la mantiene vigente toda la partida. Con un valor fijo de 60 curaría
la mitad de la vida de un Guerrero de nivel 1 y una cuarta parte de la de nivel 11.


## Por qué la poción terminó siendo tan barata

Simulando partidas por composición, **sin pociones el juego es invencible si no
llevás Curador**:

| Party | Derrotas sin pociones |
|---|---|
| Guerrero + Mago + Curador | 41% |
| Tanque + Mago + Curador | 88% |
| Guerrero + Mago + Asesino | **100%** |
| Guerrero + Arquero + Asesino | **100%** |

Las dos composiciones sin Curador pierden el 100% de las partidas. No el 95: el 100.
La Poción de Vida no es un extra, es lo único que puede reemplazar al Curador, y por
eso tiene que ser barata y curar mucho.

Probé el grid completo de precio contra curación. Con la poción a 50 de oro y curando
el 40%, las partys sin Curador seguían en 97-100%. Recién con la poción **a 25 de oro
curando el 55%** todas las composiciones terminan el juego:

| Party | Derrotas | Oro en equipo | Oro en pociones |
|---|---|---|---|
| Guerrero + Arquero + Curador | **11%** | 1191 | 499 |
| Guerrero + Mago + Curador | **17%** | 1164 | 459 |
| Tanque + Guerrero + Arquero | 54% | 700 | 391 |
| Tanque + Mago + Curador | 60% | 690 | 387 |
| Guerrero + Mago + Tanque | 63% | 594 | 329 |
| Guerrero + Mago + Asesino | 70% | 542 | 303 |
| Guerrero + Arquero + Asesino | 73% | 504 | 311 |

Todas se ganan. Con Curador se gana cuatro de cada cinco veces, sin Curador una de
cada tres. El Curador sigue siendo de lejos la mejor elección, que es como tiene que
ser, pero ya no es obligatorio.

Estos números son de la última medición, hecha con las clases reales del proyecto:
la `Tienda` y el `CatalogoDeEquipables` que se entregan (y la máquina de slots, que
después se sacó del juego), no un modelo aparte. Coinciden con los de `BALANCE_ENEMIGOS.md`.

Un efecto secundario que conviene saber: las partys sin Curador gastan la mitad en
equipamiento (unos 500 contra 1100) porque el oro se les va en pociones. Es un
intercambio real y bastante elegante: o curás con una clase, o curás con oro.


## Los otros cuatro

**Elixir de Maná (20).** Barato porque el maná se llena solo al empezar cada combate:
solo sirve dentro de una pelea larga. Lo usa el Curador cuando se queda seco a mitad
del jefe, y el Mago para tirar un Meteoro de más.

**Antídoto (15).** El Veneno y la Quemadura son los únicos dos efectos que se
arrastran de una etapa a la siguiente, así que sin antídoto entrás envenenado a la
pelea que viene. Importa contra el Emponzoñador y contra la fase 2 del Señor de las
Sombras.

**Polvo de Renacer (120).** Caro a propósito: es la única forma de deshacer una
muerte, y la muerte es permanente. A 120 cuesta casi lo mismo que equipar a un
personaje entero en RARO, así que perder a alguien duele aunque lo puedas revivir.

**Piedra de Afilar (40).** +30% de ataque por 3 turnos, físico o mágico según a quién
se lo des. Reutiliza `BuffEstadistica`, que ya está codeado.


## Lo que no pude medir

El simulador revive gratis a los caídos al entrar a cada tienda. En el juego real eso
tiene que costar un Polvo de Renacer, así que **el precio de 120 está propuesto por
razonamiento, no verificado**. Es el único número de este documento que no pasó por
la simulación.

Tampoco medí el Elixir de Maná, el Antídoto ni la Piedra de Afilar: los tres son
situacionales y el simulador no modela la decisión de cuándo usarlos. Sus precios
están puestos en relación a la Poción de Vida, que sí está medida.

La máquina de slots que había en las etapas 9 y 19 se sacó del juego en octubre para
simplificarlo. Como en promedio se quedaba con oro, sin ella la party llega con un poco
más a cada tienda.
