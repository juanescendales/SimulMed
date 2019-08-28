package com.wam.simulmed.inmovil

case class Aceleracion(private var _magnitud: Double) {
  private var _frenando = false
  def frenando = _frenando
  def frenando_=(frenando: Boolean) = _frenando = frenando
  var aceleracionArranque: Double = magnitud
  
  def magnitud = _magnitud

  def magnitud_=(magnitud: Double) = _magnitud = magnitud
}