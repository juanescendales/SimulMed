package com.wam.simulmed.inmovil

case class Aceleracion(private var _magnitud: Double) {
  def magnitud = _magnitud
  def magnitud_=(magnitud: Double) = _magnitud = magnitud
}