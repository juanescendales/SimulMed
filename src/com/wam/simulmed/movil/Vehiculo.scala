package com.wam.simulmed.movil
import com.wam.simulmed.inmovil._

case class Vehiculo(var placa:String = "")(pos: Punto, vel: Velocidad) extends Movil(pos, vel) with MovimientoUniforme {

  private var _detenido = false

  def detenido = _detenido
  def detenido_=(detenido: Boolean) = _detenido = detenido
  

}

object Vehiculo {

  def apply(pos: Punto, vel: Velocidad): Vehiculo = {
      return new Vehiculo()(pos,vel) //Se debe cambiar por los respectivos tipos de carros
  }


}
