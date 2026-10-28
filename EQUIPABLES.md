# Equipables

36 objetos: un arma y una armadura para cada una de las 6 clases, en 3 rarezas.
La armadura es siempre un set completo, no se divide en partes.

Rarezas: **RARO, EPICO, LEGENDARIO**. Se elimina COMUN de los equipables.


## Precios

| Rareza | Arma | Armadura | Equipar a un personaje |
|---|---|---|---|
| RARO | 60 | 70 | 130 |
| EPICO | 200 | 220 | 420 |
| LEGENDARIO | 430 | 470 | 900 |

Los precios no dependen de la clase: un báculo del Mago cuesta lo mismo que una
espada del Guerrero. Cobrar distinto por clase castigaría elegir cierta party sin
ningún motivo de diseño.

Los objetos son **exclusivos de su clase**: el Mandoble del Juramento solo lo puede
equipar un Guerrero.


## De dónde sale el oro

Esto no estaba definido y los precios no se pueden fijar sin ello. Propuesta: **cada
etapa paga en oro lo mismo que paga en experiencia**, es decir `25 + 10 x nº etapa`,
el doble en el minijefe y en el jefe final. Una sola fórmula para las dos
recompensas, una cosa menos que recordar y que explicar en la defensa.

| | Etapas | Oro del tramo | Acumulado al llegar, sin gastar |
|---|---|---|---|
| Tienda 1 (etapa 4) | 1, 2, 3 | 135 | **135** |
| Tienda 2 (etapa 9) | 5, 6, 7, 8 | 360 | 495 |
| Tienda 3 (etapa 14) | 10, 11, 12, 13 | 685 | 1180 |
| Tienda 4 (etapa 19) | 15, 16, 17, 18 | 760 | **1940** |

El oro de la etapa 20 no sirve para nada: cae después de la última tienda.

**Verificación de tu restricción.** En la tienda 1 llegás con 135 de oro. Equipar a un
solo personaje con RARO completo cuesta 130. Equipar a los tres cuesta 390, casi el
triple de lo que tenés. Ni comprando únicamente armas (180) te alcanza. La primera
tienda es una elección real: un personaje entero, o dos armas, o un arma y pociones.

Y el total de la partida son 1940 de oro contra 2700 que cuesta vestir a la party
entera de LEGENDARIO. O sea que nunca se llega al equipo completo máximo, que es lo
que mantiene la decisión viva hasta la última tienda.

Un recorrido típico: tienda 1 equipás a uno en RARO (130), tienda 2 comprás un arma
EPICA y una armadura RARA (270), tienda 3 dos armas EPICAS y una armadura EPICA
(620), tienda 4 un arma LEGENDARIA y una armadura EPICA (650). Quedan unos 270 para
consumibles en toda la partida, que es como un 14% del oro total.


## Qué rareza aparece en cada tienda

| Tienda | RARO | EPICO | LEGENDARIO |
|---|---|---|---|
| 1 (etapa 4) | 100% | — | — |
| 2 (etapa 9) | 60% | 40% | — |
| 3 (etapa 14) | 15% | 60% | **25%** |
| 4 (etapa 19) | — | 50% | **50%** |

LEGENDARIO se desbloquea en la tienda 3, así se usa desde la etapa 15 y no solo en
la pelea final. En la tienda 4 se saca RARO del sorteo: a esa altura es peor que lo
que ya tenés puesto y solo gastaría un slot.


## El reroll

Los 3 slots **ya no se refrescan al comprar**. En su lugar hay un botón de reroll que
vuelve a sortear los tres a la vez, y se paga.

El precio sube dentro de la misma visita y se reinicia al entrar a la tienda
siguiente:

```
precio del reroll = 20 x nº de tienda x nº de reroll en esta visita
```

| Tienda | 1º | 2º | 3º | 4º | 5º | 6º |
|---|---|---|---|---|---|---|
| 1 (etapa 4) | 20 | 40 | 60 | 80 | 100 | 120 |
| 2 (etapa 9) | 40 | 80 | 120 | 160 | 200 | 240 |
| 3 (etapa 14) | 60 | 120 | 180 | 240 | 300 | 360 |
| 4 (etapa 19) | 80 | 160 | 240 | 320 | 400 | 480 |

Máximo 6 rerolls por visita.

La escalada es lo que resuelve tu preocupación original. En la tienda 1 llegás con
135 de oro: el primer reroll te cuesta 20 y es barato, pero diez rerolls costarían
1100, ocho veces lo que tenés. Nunca se puede farmear la tienda hasta que salga lo
que querés; cada reroll de más es un objeto menos.


## Las armas

| Clase | RARO | EPICO | LEGENDARIO |
|---|---|---|---|
| Guerrero | Espada de Acero Templado | Mandoble del Juramento | Filo del Primer Amanecer |
| Mago | Báculo de Roble Rúnico | Cetro de Cuarzo Ardiente | Vara del Eclipse |
| Arquero | Arco Corto de Tejo | Arco Largo del Vendaval | Arco de la Luna Menguante |
| Curador | Cetro de Plata Bendecida | Báculo del Alba | Cetro de la Luz Primera |
| Asesino | Daga de Hoja Negra | Colmillo Gemelo | Estilete de la Última Sombra |
| Tanque | Maza de Hierro Macizo | Martillo del Yunque | Quebrantamontañas |

Bonificaciones:

| Clase | RARO | EPICO | LEGENDARIO |
|---|---|---|---|
| Guerrero | AtqF +5 | AtqF +12 | AtqF +24 |
| Mago | AtqM +6 | AtqM +14 | AtqM +28 |
| Arquero | AtqF +4, Vel +2 | AtqF +10, Vel +4 | AtqF +20, Vel +8 |
| Curador | AtqM +5, DefM +2 | AtqM +12, DefM +4 | AtqM +24, DefM +8 |
| Asesino | AtqF +5, Suerte +3 | AtqF +11, Suerte +6 | AtqF +22, Suerte +10 |
| Tanque | AtqF +4, DefF +3 | AtqF +9, DefF +7 | AtqF +18, DefF +14 |

Cada arma lleva además su `tipoDañoQueMejora`, que ya está en el código: FISICO para
Guerrero, Arquero, Asesino y Tanque; MAGICO para Mago y Curador.

Una consecuencia buena que conviene tener presente: **el arma del Curador sube su
curación**. Curación cura `20 + ataque mágico` y Luz Restauradora `15 + ataque
mágico`, así que el Cetro de la Luz Primera le suma 24 a cada curación de cada
objetivo. Es, de lejos, el objeto que más cambia la supervivencia de la party.


## Las armaduras

Set completo, una pieza por personaje.

| Clase | RARO | EPICO | LEGENDARIO |
|---|---|---|---|
| Guerrero | Cota de Malla Reforzada | Armadura de Placas del Bastión | Égida del Rey Caído |
| Mago | Túnica de Hilo Rúnico | Manto del Cónclave | Vestiduras del Vacío |
| Arquero | Jubón de Cuero Endurecido | Atuendo del Montaraz | Vestimenta del Cazador Eterno |
| Curador | Hábito de Lino Sagrado | Vestiduras del Santuario | Manto de la Aurora |
| Asesino | Traje de Contrabandista | Sayo del Gremio Silencioso | Sudario del Sin Nombre |
| Tanque | Armadura de Escamas Pesada | Coraza del Muro Inquebrantable | Baluarte de Obsidiana |

Bonificaciones:

| Clase | RARO | EPICO | LEGENDARIO |
|---|---|---|---|
| Guerrero | Vida +20, DefF +4 | Vida +50, DefF +10, DefM +4 | Vida +100, DefF +20, DefM +8 |
| Mago | Vida +14, DefM +6, Maná +10 | Vida +35, DefM +13, Maná +25 | Vida +70, DefM +26, Maná +50 |
| Arquero | Vida +16, DefF +4, Vel +2 | Vida +40, DefF +9, Vel +4 | Vida +80, DefF +18, Vel +8 |
| Curador | Vida +16, DefM +5, Maná +12 | Vida +40, DefM +11, Maná +28 | Vida +80, DefM +22, Maná +55 |
| Asesino | Vida +14, DefF +4, Suerte +2 | Vida +35, DefF +9, Suerte +4 | Vida +70, DefF +18, Suerte +8 |
| Tanque | Vida +26, DefF +6 | Vida +65, DefF +14, DefM +5 | Vida +130, DefF +28, DefM +10 |

La armadura da vida además de defensa. Es lo que hace que comprarla valga la pena
aunque la defensa tenga rendimiento decreciente por la curva del
`CalculadorDeDaño`: 20 de vida son 20 de vida siempre, mientras que 4 de defensa
valen cada vez menos.

Como `Combatiente.subirVidaMaxima()` sube el techo y la vida actual a la vez,
equiparse una armadura **cura** en el acto. Eso es intencional y hace que comprar en
la tienda sea también una forma de recuperarse.


## Decisiones tomadas

**COMUN se queda, pero solo para consumibles.** El enum `Rareza` no se toca: los
equipables usan RARO, EPICO y LEGENDARIO, y COMUN queda reservada para la poción
básica. No hay que modificar el diagrama 3 ni `PersonajeTest`.

**Los objetos son exclusivos de su clase**, y `GeneradorDeStock` sortea únicamente
entre las clases que están en la party. El pool efectivo por tienda pasa de 36
objetos a 6, y todo lo que se ve es comprable por alguien.

**Al equipar algo nuevo, la pantalla avisa qué pieza se va a descartar** antes de
confirmar la compra.

**LEGENDARIO desde la tienda 3.**


## Verificación por simulación

Volví a simular las 20 etapas, pero esta vez con la tienda de verdad: los 36 objetos
de este documento con sus precios y sus bonificaciones, el sorteo de 3 slots
filtrado por las clases de la party, los rerolls con su escalada, y un comprador que
solo compra mejoras. Nada de porcentajes inventados.

Party de referencia Guerrero + Mago + Curador, 2000 partidas:

| Etapa | Rondas | Vida de la party al terminar | Derrotas |
|---|---|---|---|
| 1 a 3 | 1.9 – 2.3 | 90% → 79% | 0 |
| 5 a 8 | 3.4 – 4.9 | 86% → 62% | 32 |
| 10 (minijefe) | 7.6 | 73% | 0 |
| 11 a 13 | 4.6 – 7.8 | 57% → 60% | 370 |
| 15 a 18 | 3.4 – 4.4 | 80% → 63% | 52 |
| 20 (jefe final) | 5.5 | 53% | 0 |

**454 derrotas sobre 2000: 23%.** Y la economía cierra: la party gasta en promedio
**1354 de los 1940 de oro** de la partida en equipamiento, dejando unos 590 para
rerolls y consumibles. Ni sobra oro sin gastar ni falta.


## Lo que esto cambia del documento de enemigos

Dos cosas que solo se vieron al simular con la tienda real, y que hay que corregir en
`BALANCE_ENEMIGOS.md`:

**1. El escalado correcto es `0.14` de aguante y `0.10` de ataque.** No el `0.09 /
0.05` que había propuesto midiendo sin tienda, ni el `0.15` parejo del diseño
original. Con la tienda real, el `0.15 / 0.15` da 72% de derrotas y el `0.09 / 0.05`
da 1%: el punto está en el medio y es bastante angosto.

**2. El 73% que medí primero no era dificultad, era una bola de nieve.** Con los
enemigos muy fuertes la party muere temprano, junta menos oro, compra menos equipo y
muere todavía antes: en esas corridas gastaba 776 de oro en equipo contra los 1354 de
la configuración buena. El oro gastado en la tienda es el mejor indicador de si el
balance está bien, mejor incluso que el porcentaje de derrotas.

Y queda un pendiente: **los dos jefes siguen siendo gratis**. El Verdugo y el Señor de
las Sombras dan cero muertes porque la party llega a los dos recién salida de una
tienda, curada y con el mejor equipo que pudo comprar. Toda la dificultad real está
en las etapas 11 a 13. Hay que subirlos, pero recién cuando estén los consumibles,
para no volver a calibrar contra una party a medio armar.
