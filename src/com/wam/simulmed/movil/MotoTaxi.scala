package com.wam.simulmed.movil
import com.wam.simulmed.inmovil._
class MotoTaxi(p: Punto, v: Velocidad, a: Aceleracion) extends Vehiculo()(p, v, a) {
  this.placa = this.generarPlacaAleatoria()
  def generarPlacaAleatoria(): String = {
    val r = scala.util.Random
    var placa = ""
    do {
      placa = (r.nextInt(10)).toString + (r.nextInt(10)).toString + (r.nextInt(10)).toString + r.alphanumeric.filter(_.isLetter).take(3).mkString
      placa.toUpperCase()
    } while (!Vehiculo.setPlacas.add(placa))
    placa
  }
}
