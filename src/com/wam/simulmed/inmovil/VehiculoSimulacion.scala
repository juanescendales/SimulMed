package com.wam.simulmed.inmovil
import com.wam.simulmed.ciudad.Via
import com.wam.simulmed.ciudad.Interseccion
import com.wam.simulmed.movil.Vehiculo
import com.wam.simulmed.grafico.Grafico
import scala.collection.mutable.Queue
import scala.collection.mutable.ArrayBuffer

class VehiculoSimulacion(val vehiculo: Vehiculo, val recorrido: Queue[Via], val interseccionesRecorrido: Queue[Interseccion], val recorridoCompleto:Array[Via], val interseccionesCompletas:Array[Interseccion]) {
  VehiculoSimulacion.listaDeVehiculosSimulacion += this
  VehiculoSimulacion.listaDeVehiculosSimulacionParaCalculos += this

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
      val margenError = (vehiculo.velocidad.magnitud*dt) + 5

      if (vehiculo.posicion.x > xViaFin - margenError && vehiculo.posicion.x < xViaFin + margenError && vehiculo.posicion.y > yViaFin - margenError && vehiculo.posicion.y < yViaFin + margenError) {
        vehiculo.posicion.x = xViaFin
        vehiculo.posicion.y = yViaFin
        if (!interseccionesRecorrido.isEmpty) {
          this.viaActual = recorrido.dequeue()
          this.interseccionDestino = interseccionesRecorrido.dequeue()
          vehiculo.velocidad.sentidoEntreDosPuntos(vehiculo.posicion, this.interseccionDestino,viaActual.angulo)
        } else {
          vehiculo.detenido = true
        }
      }
    }
  }
  def pararVehiculo(vehiculo:VehiculoSimulacion){
       VehiculoSimulacion.listaDeVehiculosSimulacion.-=(vehiculo)
  }
  
}

object VehiculoSimulacion {
  val listaDeVehiculosSimulacionParaCalculos = new ArrayBuffer[VehiculoSimulacion]
  val listaDeVehiculosSimulacion = new ArrayBuffer[VehiculoSimulacion]
  val listaDeVehiculosSimulacionDetenidos=new ArrayBuffer[VehiculoSimulacion]

  def apply(): VehiculoSimulacion = {
    val r = new scala.util.Random
    val origen: Interseccion = GrafoVias.listaDeNodos(r.nextInt(GrafoVias.listaDeNodos.length))
    var destino: Interseccion = GrafoVias.listaDeNodos(r.nextInt(GrafoVias.listaDeNodos.length))
    while (origen == destino) {
      destino = GrafoVias.listaDeNodos(r.nextInt(GrafoVias.listaDeNodos.length))
    }

    val camino = (GrafoVias.n(origen)).shortestPathTo(GrafoVias.n(destino))
    
    val interseccionesRecorridoCompleto = camino.get.nodes.map(_.value).toArray
    val viasRecorridoCompleto = camino.get.edges.map(_.label.asInstanceOf[Via]).toArray

    val interseccionesRecorrido = VehiculoSimulacion.toQueue(interseccionesRecorridoCompleto)
    val viasRecorrido = VehiculoSimulacion.toQueue(viasRecorridoCompleto)
    val viaInicial = viasRecorrido.head
    val magnitudVelocidadAleatoria = Velocidad.conversorKmHorAMetroSeg((r.nextDouble() * (Simulacion.maxVelocidad - Simulacion.minVelocidad)) + Simulacion.minVelocidad)
    interseccionesRecorrido.dequeue()
    val interseccionInicial = interseccionesRecorrido.head
    val puntoOrigen = new Punto(origen.x, origen.y)
    val vehiculoDeSimulacion = new VehiculoSimulacion(Vehiculo(puntoOrigen, Velocidad(magnitudVelocidadAleatoria)(Angulo(0))), viasRecorrido, interseccionesRecorrido,viasRecorridoCompleto,interseccionesRecorridoCompleto)
    vehiculoDeSimulacion.vehiculo.velocidad.sentidoEntreDosPuntos(origen, interseccionInicial,viaInicial.angulo)
    val grafico=Grafico
    grafico.cargarVehiculo(vehiculoDeSimulacion)
    return vehiculoDeSimulacion
  }

  def toQueue[T](L: Array[T]): Queue[T] = {
    val Q = new Queue[T]();
    L.foreach(f => Q += f)
    return Q
  }

}