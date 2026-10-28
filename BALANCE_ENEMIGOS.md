# Balance de los enemigos

Estadísticas base, movimientos y escalado de los 11 enemigos. **Estos son los
valores que están efectivamente en el código**, extraídos de las clases del paquete
`modelo.combatientes`.

Todo lo que está acá salió de simular partidas completas con el modelo real: las seis
clases jugables con sus 24 movimientos, el `CalculadorDeDaño`, los efectos de estado,
y —desde la última revisión— la `Tienda`, el `CatalogoDeEquipables`, los consumibles
y la `MaquinaDeSlots` del proyecto. No es un modelo aparte: la simulación instancia
las mismas clases que se entregan. El simulador en sí es descartable y no está en el
repo.


## Resultado, 2000 partidas completas

Party de referencia Guerrero + Mago + Curador, con tienda, pociones y tres tiradas de
slots por máquina:

| Etapa | Tipo | Rondas | Vida de la party al terminar | Caídos | Derrotas |
|---|---|---|---|---|---|
| 1 | combate | 1.9 | 90% | 0.00 | 0 |
| 2 | combate | 2.0 | 83% | 0.00 | 0 |
| 3 | combate | 2.3 | 79% | 0.00 | 0 |
| 5 | combate | 3.6 | 86% | 0.00 | 0 |
| 6 | combate | 4.2 | 73% | 0.01 | 0 |
| 7 | combate | 4.9 | 65% | 0.04 | 9 |
| 8 | combate | 4.9 | 62% | 0.08 | 28 |
| 10 | **minijefe** | 7.7 | 77% | 0.08 | 0 |
| 11 | combate | 7.8 | 63% | 0.24 | 102 |
| 12 | combate | 4.7 | 62% | 0.19 | 74 |
| 13 | combate | 5.1 | 60% | 0.25 | 99 |
| 15 | combate | 3.7 | 79% | 0.02 | 1 |
| 16 | combate | 4.1 | 70% | 0.05 | 4 |
| 17 | combate | 4.9 | 65% | 0.10 | 22 |
| 18 | combate | 5.0 | 64% | 0.13 | 32 |
| 20 | **jefe final** | 5.6 | 53% | 0.01 | 0 |

**371 derrotas sobre 2000: 19%.** La party gasta en promedio 1126 de oro en
equipamiento, 454 en pociones y 179 en la máquina.

La dificultad se concentra en las etapas 11 a 13, que se llevan el 74% de las
partidas perdidas. Tiene sentido: la party viene de pelear al minijefe, el pool de
enemigos se abre con el Acólito y el Berserker, y la tienda siguiente recién está en
la etapa 14.

Por composición de party, 600 partidas cada una:

| Party | Derrotas | Oro en equipo | Oro en pociones |
|---|---|---|---|
| Guerrero + Arquero + Curador | **11%** | 1191 | 499 |
| Guerrero + Mago + Curador | **17%** | 1164 | 459 |
| Tanque + Guerrero + Arquero | 54% | 700 | 391 |
| Tanque + Mago + Curador | 60% | 690 | 387 |
| Guerrero + Mago + Tanque | 63% | 594 | 329 |
| Guerrero + Mago + Asesino | 70% | 542 | 303 |
| Guerrero + Arquero + Asesino | 73% | 504 | 311 |

Las ocho composiciones terminan el juego. Con Curador se gana cuatro de cada cinco
veces, sin Curador una de cada tres.


## Estadísticas base

Son los valores a **etapa 1**, tal como están escritos en el constructor de cada
clase. De ahí en adelante escalan.

| Enemigo | Vida | Maná | AtqF | DefF | AtqM | DefM | Vel | Suerte | Resist. |
|---|---|---|---|---|---|---|---|---|---|
| Esqueleto | 30 | — | 11 | 5 | — | 4 | 12 | 5 | 0 |
| Bruto | 70 | — | 18 | 8 | — | 5 | 9 | 8 | 0 |
| Guardián | 95 | — | 10 | 22 | — | 15 | 5 | 4 | 0 |
| Acechador | 55 | — | 13 | 7 | — | 6 | 20 | 15 | 0 |
| Hechicero | 50 | 60 | 4 | 6 | 17 | 12 | 11 | 8 | 0 |
| Emponzoñador | 60 | — | 12 | 8 | — | 7 | 10 | 8 | 0 |
| Acólito | 55 | 50 | 5 | 7 | 12 | 12 | 10 | 6 | 0 |
| Berserker | 80 | — | 15 | 6 | — | 5 | 12 | 10 | 0 |
| Nigromante | 45 | — | 4 | 5 | 14 | 10 | 8 | 6 | 0 |
| **Verdugo** (minijefe) | 380 | — | 50 | 38 | — | 26 | 24 | 14 | **50** |
| **Señor de las Sombras** (jefe final) | 500 | — | 52 | 42 | 52 | 42 | 40 | 20 | **50** |

Los dos jefes **no escalan**: sobrescriben `escalarParaEtapa()` para no hacer nada,
porque esos ya son sus valores finales.

Para comparar: un personaje arranca entre 80 y 150 de vida y termina la partida entre
150 y 435.


## Movimientos

Los enemigos tienen lista libre: algunos uno solo, otros dos o tres. No gastan maná
salvo el Hechicero y el Acólito, y no tienen niveles de desbloqueo.

Atención con el nombre del alcance: `TODOS_LOS_ENEMIGOS` está escrito desde el punto
de vista del jugador, así que cuando lo usa un enemigo significa *toda la party*.

| Enemigo | Movimiento | Clase | Pot. | Alcance | Detalle |
|---|---|---|---|---|---|
| Esqueleto | Zarpazo | AtaqueFisico | 7 | objetivo único | |
| Bruto | Mazazo | AtaqueFisico | 12 | objetivo único | apunta al más débil |
| Guardián | Embestida | AtaqueFisico | 8 | objetivo único | |
| Guardián | *Cubrir* | *acción, no movimiento* | — | — | 35% de sus turnos: pasa y aplica `Proteccion` (−30%) a sus aliados |
| Acechador | Tajo Rápido | AtaqueFisico | 5 | objetivo único | **3 golpes por turno**, reelige objetivo en cada uno |
| Hechicero | Onda Oscura | AtaqueMagico | 9 | toda la party | 12 de maná |
| Hechicero | Chispa | AtaqueMagico | 9 | objetivo único | cuando se queda sin maná |
| Emponzoñador | Escupitajo Tóxico | HabilidadDeEstado | — | objetivo único | Veneno 6% × 3 turnos, sobre quien no esté envenenado |
| Emponzoñador | Mordisco | AtaqueFisico | 9 | objetivo único | si ya están todos envenenados |
| Acólito | Plegaria | HabilidadDeCuracion | 15 base | aliado | 10 de maná, al aliado más herido (por debajo del 70%) |
| Acólito | Golpe de Báculo | AtaqueMagico | 8 | objetivo único | si no hay a quién curar |
| Berserker | Embate | AtaqueFisico | 11 | objetivo único | apunta al más peligroso |
| Nigromante | Toque Marchito | AtaqueMagico | 9 | objetivo único | |
| **Verdugo** | Hachazo Brutal | AtaqueFisico | 15 | objetivo único | apunta al más peligroso |
| **Verdugo** | Mirada Petrificante | HabilidadDeEstado | — | objetivo único | Aturdimiento 1 turno, 50% de acierto, cooldown de 4 turnos |
| **Señor** fase 1 | Garra de Sombra | AtaqueFisico | 17 | objetivo único | apunta al más débil |
| **Señor** fase 2 | Marea de Sombras | AtaqueMagico | 12 | toda la party | apunta al más peligroso |
| **Señor** fase 2 | Llamas Sombrías | HabilidadDeEstado | — | toda la party | Quemadura 10% × 2 turnos, 40% de acierto por objetivo |

El Berserker sobrescribe `getAtaqueFisicoEfectivo()`: su ataque sube de forma lineal
a medida que baja su vida, hasta el doble cuando está por caer.

El Nigromante invoca 5 Esqueletos antes de la primera ronda y vuelve a completar el
tope cada 3 rondas. Los Esqueletos invocados **no escalan**: sobrescriben
`escalarParaEtapa()` igual que los jefes. Si escalaran, el Nigromante de la etapa 15
entraría con cinco enemigos de pleno nivel.

La Mirada Petrificante del Verdugo está marcada como definitiva. No lo es: es la
forma de quedarse con el cooldown de 4 turnos que ya trae `Movimiento`, sin inventar
un contador aparte.


## Escalado por etapa

Dos factores distintos, y esto es lo más importante del documento.

```
aguante (vida, maná, defensa física, defensa mágica) x (1 + 0.14 x (etapa - 1))
ataque  (ataque físico, ataque mágico)               x (1 + 0.10 x (etapa - 1))
velocidad y suerte                                    NO escalan
```

En el código son `Enemigo.CRECIMIENTO_AGUANTE = 14` y
`Enemigo.CRECIMIENTO_ATAQUE = 10`. En la etapa 20 eso da ×3.66 de aguante y ×2.90 de
ataque.

**Por qué dos factores y no uno.** La party no crece pareja: de nivel 1 a 11 el
ataque del Guerrero se multiplica por 2.67 y su vida solo por 2.0. Si los enemigos
escalaran todo con un único número, o bien se vuelven de papel sobre el final, o bien
pegan demasiado. Con el aguante creciendo al ritmo del ataque de la party y el ataque
creciendo al ritmo de su vida, las dos curvas quedan paralelas.

**Por qué velocidad y suerte no escalan.** Si escalaran, sobre el final los enemigos
actuarían siempre primero y criticarían en la mitad de los golpes. La velocidad es lo
que distingue al Acechador del Guardián, y esa diferencia tiene que seguir
significando lo mismo en la etapa 18 que en la 2.

**Por qué `(etapa - 1)` y no `etapa`.** Así las estadísticas base son las de la etapa
1 y se pueden leer tal cual en la tabla de arriba. Con `etapa`, hasta el enemigo de la
primera pelea aparecería multiplicado.


## Qué enemigo sale en qué etapa

El enemigo se elige al azar de un pool que se va abriendo. La cantidad también es al
azar dentro de un rango. Todo esto vive en `GeneradorDeEtapas.getPool()`.

| Etapas | Cantidad | Pool |
|---|---|---|
| 1 a 3 | 2 | Esqueleto, Bruto, Acechador |
| 5 a 8 | 2 o 3 | Bruto, Acechador, Guardián, Hechicero, Emponzoñador, Esqueleto |
| 10 | 1 | Verdugo (minijefe) |
| 11 a 13 | 3 o 4 | los de arriba sin Esqueleto, más Acólito y Berserker |
| 15 a 18 | 3 o 4 | ídem, más Nigromante |
| 20 | 1 | Señor de las Sombras (jefe final) |

Las etapas 4, 9, 14 y 19 son de tienda y no tienen combate.

El Esqueleto deja de aparecer suelto a partir de la 11 porque a esa altura es un
estorbo sin amenaza; desde ahí solo aparece invocado. El Nigromante entra recién en la
15 porque llega con 5 esqueletos y multiplica la cantidad de enemigos en pantalla:
antes de que la party tenga sus definitivas, es injusto.


## Historia de este balance, y qué aprendimos

Vale la pena dejarlo escrito porque es el tipo de cosa que se pregunta en la defensa.

**Primera medición, sin tienda: el escalado original parecía imposible.** El
`x (1 + 0.15 x etapa)` que teníamos anotado daba 100% de derrotas sobre 200 partidas,
y bajarlo a 0.12, 0.10, 0.08 o 0.06 daba 100% igual. La conclusión fue que había que
separar el escalado en dos factores, y se propuso 0.09 / 0.05.

**Esa conclusión estaba mal, porque el modelo no tenía tienda.** Al agregar
equipamiento y pociones, el mismo `0.15` parejo pasó a dar 28% de derrotas, y el
`0.09 / 0.05` pasó a dar **1%**: un juego que se gana solo. El margen de error de la
tienda era más grande que el del enemigo.

**El número correcto, medido contra la tienda real, es 0.14 / 0.10.** La forma de dos
factores sigue siendo la buena; lo que estaba mal eran los valores.

Y la lección que más sirvió: **el oro gastado en la tienda es mejor indicador del
balance que el porcentaje de derrotas**. Con enemigos demasiado fuertes la party muere
temprano, junta menos oro, compra menos equipo y muere todavía antes. En las corridas
rotas gastaba unos 500 de oro en equipo; en las buenas, 1126. Ese número avisa del
problema antes que el de derrotas.


## Dos cosas que quedan para decidir

**La party nunca llega cerca del nivel 20.** Con la experiencia actual pelea el jefe
final en nivel 11 y termina la partida en 13. Los niveles 14 a 20 de las tablas de
crecimiento no se usan nunca. O se sube la experiencia por etapa, o se baja el
`NIVEL_MAXIMO`, o se acepta que 20 es un techo teórico.

**La definitiva se desbloquea después del minijefe.** El cuarto movimiento entra en
nivel 8 y la party llega a nivel 8 recién al terminar la etapa 11, así que el minijefe
de la etapa 10 se pelea sin definitivas. Si no es a propósito, se arregla bajando el
desbloqueo a nivel 7.

Y una advertencia para cuando alguien toque estos números: **los jefes son muy
sensibles**. Subirle al Señor de las Sombras la vida de 500 a 540 y el ataque de 52 a
56 lleva la derrota del 16% al 53%. Hay que moverlos de a poco y volver a simular.
