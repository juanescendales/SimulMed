package com.wam.simulmed.ciudad
import com.wam.simulmed.movil._
import scala.collection.mutable.ArrayBuffer
class Comparendo(val vehiculo: Vehiculo, val velocidadVia: Double) {
  
  val velocidadVehiculo: Double = vehiculo.velocidad.velocidadTotalMagnitud

  def porcentajeExcedido: Double = {
    ((velocidadVehiculo/velocidadVia)-1)*100
  }
}
object Comparendo{
  val listaComparendos = new ArrayBuffer[Comparendo]
}