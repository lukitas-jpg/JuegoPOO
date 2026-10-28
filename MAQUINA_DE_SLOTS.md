# Máquina de slots

Aparece únicamente en las etapas 9 y 19, antes de la tienda. El jugador elige
apostar o pasar; si pasa, no puede volver.

**3 rodillos, 6 símbolos, sorteo uniforme e independiente en cada rodillo.** Son 216
resultados posibles. La estadística SUERTE no interviene: es azar puro, como dice el
diseño.

Símbolos: Moneda, Cereza, Trébol, Rubí, Diamante y Limón (los mismos que las imágenes).

**Apuesta fija: 50 de oro. La primera tirada de cada máquina es gratis.**


## Tabla de premios

| Resultado | Paga | Probabilidad | Con apuesta de 50 |
|---|---|---|---|
| 3 Diamantes | x15 | 1 de 216 (0.46%) | **750** |
| 3 iguales (Moneda, Cereza, Trébol o Rubí) | x10 | 4 de 216 (1.85%) | 500 |
| 3 Limones | nada | 1 de 216 | 0 |
| 2 Diamantes | x4 | 15 de 216 (6.94%) | 200 |
| 2 Monedas o 2 Rubíes | x2 | 30 de 216 (13.89%) | 100 |
| 2 Cerezas o 2 Tréboles | x1 | 30 de 216 (13.89%) | 50 (devuelve la apuesta) |
| cualquier otra cosa | nada | 136 de 216 | 0 |

Los tres Limones son el chiste de la máquina, como en las tragamonedas de verdad: el único trío que no paga nada.

Hasta octubre los símbolos eran Moneda, Espada, Escudo, Poción, Corona y Calavera. Se
renombraron para que coincidan con las imágenes; los premios no cambiaron (Corona pasó a
Diamante, Calavera a Limón, Poción a Rubí, Espada a Cereza y Escudo a Trébol).

**Retorno: 94.9%.** No es una estimación: se calcula recorriendo los 216 resultados y
sumando lo que paga cada uno. Son 205/216 exactos.

**Paga algo 80 de las 216 veces: 37%, más de una de cada tres tiradas.**


## Por qué estos números y no otros

La primera versión de este documento tenía un retorno del 81% y pagaba el 23% de las
tiradas. Era una máquina correcta pero inútil: cualquiera que la ignorara jugaba
mejor. Se probaron tres tablas distintas, 1200 partidas completas por combinación:

| Tabla | Retorno | Paga | No apuesta | 3 tiradas | 6 tiradas | 12 tiradas | Apuesta todo |
|---|---|---|---|---|---|---|---|
| A | 81.0% | 23% | 18% | 20% | 26% | 32% | 49% |
| B | 91.7% | 23% | 18% | 20% | 24% | 30% | 51% |
| C | 94.9% | 37% | 18% | 21% | 22% | 28% | 49% |

(porcentaje de partidas perdidas)

Subir el retorno ayuda, pero no alcanza: con cualquiera de las tres, apostar seguía
siendo peor que no apostar. Lo que lo resuelve es **la primera tirada gratis**, que
cambia la pregunta: ya no es "¿apuesto o no?", es "¿paro después de la gratis?".


## Verificación, 5000 partidas por fila

Tabla C con la primera tirada gratis:

| Cómo juega | Derrotas | Oro en equipo |
|---|---|---|
| No pasa por la máquina | 18% | 1067 |
| Solo la tirada gratis | 19% | **1122** |
| Gratis + 2 pagas | 19% | 1116 |
| Gratis + 5 pagas | 19% | 1100 |
| Gratis + 11 pagas | 24% | 1045 |
| Apuesta hasta quedarse seco | **49%** | 288 |

**Tirar hasta seis veces ya no cuesta nada medible**: 19% contra el 18% de no
apostar, que es ruido estadístico incluso con 5000 corridas. Y la tirada gratis suma
55 de oro al presupuesto de equipamiento, así que pasar por la máquina es
directamente mejor que saltearla.

Comparado con la tabla A, donde seis tiradas llevaban la derrota del 18% al 26%, la
diferencia es enorme.


## Y sin embargo sigue siendo peligrosa

Fundirse el oro apostando arruina **el 49% de las partidas**, y el gasto en
equipamiento se derrumba de 1122 a 288.

Eso es lo más interesante del resultado: pasa **con un retorno del 94.9%**. La casa
se queda apenas con el 5%, y aun así apostar sin freno pierde la mitad de las
partidas. No es la ventaja de la casa lo que te funde, es la varianza: alcanza con
una mala racha antes de la tienda 4 para llegar al jefe final sin equipo.

La máquina no necesita robarte para ser peligrosa. Solo necesita que no sepas cuándo
parar.


## Para implementar

- `MaquinaDeSlots` tiene su propio `Random`. No consulta la suerte de nadie.
- Dos constantes: `APUESTA = 50` y un booleano de tirada gratis por máquina, que se
  consume en la primera tirada y no se repone.
- `tirar()` sortea 3 enteros de 0 a 5 y devuelve un `ResultadoTirada` con los tres
  símbolos y el oro pagado, para que la vista muestre los rodillos y el premio.
- El multiplicador sale de una sola función: si los tres son iguales mira qué símbolo
  es; si no, busca si hay exactamente dos iguales. No hace falta ninguna tabla en
  memoria.
- La máquina aparece antes que la tienda y se abandona al pasar. Como no se puede
  volver, el estado "abandonada" vive en la `EtapaDeTienda`, no en la máquina.
