package com.wam.simulmed.ciudad
import com.wam.simulmed.movil._
import scala.collection.mutable.ArrayBuffer
case class Comparendo(val vehiculo: Vehiculo, val via: Via)(val velocidadVia: Double) {
  if (!Comparendo.listaComparendos.contains(this)) {
    Comparendo.listaComparendos += this
  }
  val velocidadVehiculo: Double = vehiculo.velocidad.magnitud
  def porcentajeExcedido: Double = {
    ((velocidadVehiculo / velocidadVia) - 1) * 100
  }
}
object Comparendo {
  val listaComparendos = new ArrayBuffer[Comparendo]
}