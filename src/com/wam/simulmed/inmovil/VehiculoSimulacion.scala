package com.wam.simulmed.inmovil

package inmovil
import com.wam.simulmed.ciudad.Via
import com.wam.simulmed.ciudad.Interseccion
import com.wam.simulmed.movil.Vehiculo
import scala.collection.mutable.Queue
import scala.collection.mutable.ArrayBuffer

class VehiculoSimulacion(val vehiculo: Vehiculo, val recorrido: Queue[Via], val interseccionesRecorrido: Queue[Interseccion]) {
  VehiculoSimulacion.listaDeVehiculosSimulacion += this

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
      val margenError = vehiculo.velocidad.magnitud + 5

      if (vehiculo.posicion.x > xViaFin - margenError && vehiculo.posicion.x < xViaFin + margenError && vehiculo.posicion.y > yViaFin - margenError && vehiculo.posicion.y < yViaFin + margenError) {
        vehiculo.posicion.x = xViaFin
        vehiculo.posicion.y = yViaFin
        if (!interseccionesRecorrido.isEmpty) {
          this.viaActual = recorrido.dequeue()
          this.interseccionDestino = interseccionesRecorrido.dequeue()
          if (vehiculo.velocidad.magnitud > viaActual.velMaxima) {
            vehiculo.velocidad.magnitud = viaActual.velMaxima
          }
          vehiculo.velocidad.anguloYSentidoEntreDosPuntos(vehiculo.posicion, this.interseccionDestino)
        } else {
          vehiculo.detenido = true
          VehiculoSimulacion.listaDeVehiculosSimulacion.-=(this)
        }
      }
    }
  }
}

object VehiculoSimulacion {

  val listaDeVehiculosSimulacion = new ArrayBuffer[VehiculoSimulacion]

  def apply(): VehiculoSimulacion = {
    val r = new scala.util.Random
    val origen: Interseccion = GrafoVias.listaDeNodos(r.nextInt(GrafoVias.listaDeNodos.length))
    var destino: Interseccion = GrafoVias.listaDeNodos(r.nextInt(GrafoVias.listaDeNodos.length))
    while (origen == destino) {
      destino = GrafoVias.listaDeNodos(r.nextInt(GrafoVias.listaDeNodos.length))
    }

    val camino = (GrafoVias.n(origen)).shortestPathTo(GrafoVias.n(destino))

    val interseccionesRecorrido = VehiculoSimulacion.toQueue(camino.get.nodes.map(_.value).toList)
    val viasRecorrido = VehiculoSimulacion.toQueue(camino.get.edges.map(_.label.asInstanceOf[Via]).toList)
    val viaInicial = viasRecorrido.head
    var magnitudVelocidadAleatoria = (r.nextDouble() * (Simulacion.maxVelocidad - Simulacion.minVelocidad)) + Simulacion.minVelocidad
    if (magnitudVelocidadAleatoria > viaInicial.velMaxima) {
      magnitudVelocidadAleatoria = viaInicial.velMaxima
    }
    magnitudVelocidadAleatoria = Velocidad.conversorKmHorAMetroSeg(magnitudVelocidadAleatoria)
    interseccionesRecorrido.dequeue()
    val interseccionInicial = interseccionesRecorrido.head
    val puntoOrigen = new Punto(origen.x, origen.y)
    val vehiculoDeSimulacion = new VehiculoSimulacion(Vehiculo(puntoOrigen, Velocidad(magnitudVelocidadAleatoria)(Angulo(0))), viasRecorrido, interseccionesRecorrido)
    vehiculoDeSimulacion.vehiculo.velocidad.anguloYSentidoEntreDosPuntos(origen, interseccionInicial)
    return vehiculoDeSimulacion
  }

  def toQueue[T](L: List[T]): Queue[T] = {
    val Q = new Queue[T]();
    L.foreach(f => Q += f)
    return Q
  }

}