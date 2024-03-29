package com.wam.simulmed.inmovil
import com.wam.simulmed.ciudad._
import com.wam.simulmed.movil.Vehiculo
import com.wam.simulmed.grafico.Grafico
import scala.collection.mutable.Queue
import scala.collection.mutable.ArrayBuffer

class Viaje(val vehiculo: Vehiculo, val recorrido: Queue[Via], val interseccionesRecorrido: Queue[Interseccion], val recorridoCompleto: Array[Via], val interseccionesCompletas: Array[Interseccion]) {
  Viaje.listaDeVehiculosSimulacion += this
  Viaje.listaDeVehiculosSimulacionParaCalculos += this

  private var _viaActual: Via = recorrido.dequeue()
  private var _interseccionDestino: Interseccion = interseccionesRecorrido.dequeue()

  def interseccionDestino = _interseccionDestino
  def interseccionDestino_=(interseccionDestino: Interseccion) = _interseccionDestino = interseccionDestino

  def viaActual = _viaActual
  def viaActual_=(viaActual: Via) = _viaActual = viaActual

  def mover(dt: Double): Unit = {
    if (!vehiculo.detenido) {
      vehiculo.avance(dt)
      val xViaFin = interseccionDestino.x
      val yViaFin = interseccionDestino.y
      val margenError = (vehiculo.velocidad.velocidadTotalMagnitud * dt) + 5
      val distanciaDelCarroALaInterseccion = Punto.distanciaEntre2Puntos(interseccionDestino, vehiculo.posicion)
      if (viaActual.camaraDeFotoDeteccion.isDefined) {
        val distanciaCarroCamara = Punto.distanciaEntre2Puntos(vehiculo.posicion, viaActual.camaraDeFotoDeteccion.get.posicion)
        if (distanciaCarroCamara <= margenError) {
          CamaraFotoDeteccion.verificarVelocidad(vehiculo, viaActual)
        }
      }

      val semaforo = semaforoActual(this)

      //hay que verificar
      if (semaforo.isDefined) {
        if (distanciaDelCarroALaInterseccion <= Simulacion.XSemaforoFrenar && !vehiculo.aceleracion.frenando && semaforo.get.estado == EstadoRojo) {
          vehiculo.aceleracion.frenando = true
          //el XSemaforoFrenar hay que mirarlo para cuadrar bien que distancia es, si la de amarillo o la de rojo(en la linea de abajo)
          vehiculo.aceleracion.magnitud_=(-1 * (1 / distanciaDelCarroALaInterseccion) * (math.pow(vehiculo.velocidad.magnitud, 2) / 2))

        } else if (distanciaDelCarroALaInterseccion > Simulacion.XSemaforoAmarilloContinuar && !vehiculo.aceleracion.frenando && semaforo.get.estado == EstadoAmarillo) {
          vehiculo.aceleracion.frenando = true
          vehiculo.aceleracion.magnitud_=(-1 * (1 / distanciaDelCarroALaInterseccion) * (math.pow(vehiculo.velocidad.magnitud, 2) / 2))
        } else if (vehiculo.aceleracion.frenando && semaforo.get.estado == EstadoVerde) {
          vehiculo.aceleracion.frenando = false
          vehiculo.aceleracion.magnitud = vehiculo.aceleracion.aceleracionArranque
        }

        if (vehiculo.posicion.x > xViaFin - margenError && vehiculo.posicion.x < xViaFin + margenError && vehiculo.posicion.y > yViaFin - margenError && vehiculo.posicion.y < yViaFin + margenError && semaforo.get.estado == EstadoRojo) {
          vehiculo.posicion.x = xViaFin
          vehiculo.posicion.y = yViaFin
          vehiculo.aceleracion.magnitud = 0
          vehiculo.velocidad.magnitud_=(0)
          if (interseccionesRecorrido.isEmpty) {
            vehiculo.detenido = true
          }

        } else if (vehiculo.posicion.x > xViaFin - margenError && vehiculo.posicion.x < xViaFin + margenError && vehiculo.posicion.y > yViaFin - margenError && vehiculo.posicion.y < yViaFin + margenError && semaforo.get.estado == EstadoVerde) {
          vehiculo.aceleracion.frenando = false
          vehiculo.posicion.x = xViaFin
          vehiculo.posicion.y = yViaFin
          vehiculo.aceleracion.magnitud = vehiculo.aceleracion.aceleracionArranque
          if (!interseccionesRecorrido.isEmpty) {
            this.viaActual = recorrido.dequeue()
            this.interseccionDestino = interseccionesRecorrido.dequeue()
            vehiculo.velocidad.sentidoEntreDosPuntos(vehiculo.posicion, this.interseccionDestino, viaActual.angulo)
          } else {
            vehiculo.detenido = true
          }

        } else if (vehiculo.posicion.x > xViaFin - margenError && vehiculo.posicion.x < xViaFin + margenError && vehiculo.posicion.y > yViaFin - margenError && vehiculo.posicion.y < yViaFin + margenError && semaforo.get.estado == EstadoAmarillo) {
          vehiculo.posicion.x = xViaFin
          vehiculo.posicion.y = yViaFin
          if (distanciaDelCarroALaInterseccion >= Simulacion.XSemaforoAmarilloContinuar) {
            vehiculo.aceleracion.magnitud = 0
            vehiculo.velocidad.magnitud = (0)
            if (interseccionesRecorrido.isEmpty) {
              vehiculo.detenido = true
            }
          } else {
            vehiculo.aceleracion.frenando = false
            vehiculo.posicion.x = xViaFin
            vehiculo.posicion.y = yViaFin
            vehiculo.aceleracion.magnitud = vehiculo.aceleracion.aceleracionArranque
            if (!interseccionesRecorrido.isEmpty) {
              this.viaActual = recorrido.dequeue()
              this.interseccionDestino = interseccionesRecorrido.dequeue()
              vehiculo.velocidad.sentidoEntreDosPuntos(vehiculo.posicion, this.interseccionDestino, viaActual.angulo)
            } else {
              vehiculo.detenido = true
            }
          }
        }
      } else {
        if (vehiculo.posicion.x > xViaFin - margenError && vehiculo.posicion.x < xViaFin + margenError && vehiculo.posicion.y > yViaFin - margenError && vehiculo.posicion.y < yViaFin + margenError) {
          vehiculo.posicion.x = xViaFin
          vehiculo.posicion.y = yViaFin
          vehiculo.aceleracion.magnitud = vehiculo.aceleracion.aceleracionArranque
          if (!interseccionesRecorrido.isEmpty) {
            this.viaActual = recorrido.dequeue()
            this.interseccionDestino = interseccionesRecorrido.dequeue()
            vehiculo.velocidad.sentidoEntreDosPuntos(vehiculo.posicion, this.interseccionDestino, viaActual.angulo)
          } else {
            vehiculo.detenido = true
          }
        }

      }
      //println(vehiculo.aceleracion.magnitud)

    }
  }

  def semaforoActual(vehiculo: Viaje): Option[Semaforo] = {
    var semaforo: Option[Semaforo] = None
    val semaforoInicio = vehiculo.viaActual.semaforoInicio
    val semaforoFin = vehiculo.viaActual.semaforoFin
    if (semaforoFin.isDefined && semaforoInicio.isDefined) {
      if (semaforoInicio.get.interseccion == vehiculo.interseccionDestino) {
        semaforo = semaforoInicio;
      } else if (semaforoFin.get.interseccion == vehiculo.interseccionDestino) {
        semaforo = semaforoFin;
      }

    } else if (semaforoFin.isDefined) {
      if (semaforoFin.get.interseccion == vehiculo.interseccionDestino) {
        semaforo = semaforoFin;
      }

    } else if (semaforoInicio.isDefined) {
      if (semaforoInicio.get.interseccion == vehiculo.interseccionDestino) {
        semaforo = semaforoInicio;
      }

    }
    semaforo

  }
  def pararVehiculo(vehiculo: Viaje) {
    Viaje.listaDeVehiculosSimulacion.-=(vehiculo)
  }

}

object Viaje {
  val listaDeVehiculosSimulacionParaCalculos = new ArrayBuffer[Viaje]
  val listaDeVehiculosSimulacion = new ArrayBuffer[Viaje]
  val listaDeVehiculosSimulacionDetenidos = new ArrayBuffer[Viaje]

  def apply(): Viaje = {
    val r = new scala.util.Random
    val origen: Interseccion = GrafoVias.listaDeNodos(r.nextInt(GrafoVias.listaDeNodos.length))
    var destino: Interseccion = GrafoVias.listaDeNodos(r.nextInt(GrafoVias.listaDeNodos.length))
    while (origen == destino) {
      destino = GrafoVias.listaDeNodos(r.nextInt(GrafoVias.listaDeNodos.length))
    }

    val camino = (GrafoVias.n(origen)).shortestPathTo(GrafoVias.n(destino))

    val interseccionesRecorridoCompleto = camino.get.nodes.map(_.value).toArray
    val viasRecorridoCompleto = camino.get.edges.map(_.label.asInstanceOf[Via]).toArray

    val interseccionesRecorrido = Viaje.toQueue(interseccionesRecorridoCompleto)
    val viasRecorrido = Viaje.toQueue(viasRecorridoCompleto)
    val viaInicial = viasRecorrido.head
    val magnitudVelocidadAleatoria = Velocidad.conversorKmHorAMetroSeg((r.nextDouble() * (Simulacion.maxVelocidad - Simulacion.minVelocidad)) + Simulacion.minVelocidad)
    val magnitudAceleracionAleatoria = (r.nextDouble() * (Simulacion.maxAceleracion - Simulacion.minAceleracion)) + Simulacion.minAceleracion
    interseccionesRecorrido.dequeue()
    val interseccionInicial = interseccionesRecorrido.head
    val puntoOrigen = new Punto(origen.x, origen.y)
    val vehiculoDeSimulacion = new Viaje(Vehiculo(puntoOrigen, Velocidad(magnitudVelocidadAleatoria)(Angulo(0)), Aceleracion(magnitudAceleracionAleatoria)), viasRecorrido, interseccionesRecorrido, viasRecorridoCompleto, interseccionesRecorridoCompleto)
    vehiculoDeSimulacion.vehiculo.velocidad.sentidoEntreDosPuntos(origen, interseccionInicial, viaInicial.angulo)
    val grafico = Grafico
    grafico.cargarVehiculo(vehiculoDeSimulacion)
    return vehiculoDeSimulacion
  }

  def toQueue[T](L: Array[T]): Queue[T] = {
    val Q = new Queue[T]();
    L.foreach(f => Q += f)
    return Q
  }

}