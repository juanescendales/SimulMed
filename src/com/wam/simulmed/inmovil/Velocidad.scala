package com.wam.simulmed.inmovil

case class Velocidad(private var _magnitud:Double )( val direccion: Angulo = new Angulo(0.0)) {
  private var _sentidoX:Int = 1
  private var _sentidoY:Int = 1
  def magnitud = _magnitud
  def magnitud_=(magnitud: Double) = _magnitud = magnitud
  
  def sentidoX = _sentidoX
  def sentidoX_=(sentidoX: Int) = _sentidoX = sentidoX
  
  def sentidoY = _sentidoY
  def sentidoY_=(sentidoY: Int) = _sentidoY = sentidoY
  
  
  def sentidoEntreDosPuntos(p1: Punto, p2: Punto, magnitudAngular:Double): Unit = {
    val dy = p2.y - p1.y
    val dx = p2.x - p1.x
    var sentidoy = 1
    var sentidox = 1
    if (dy < 0) {
      sentidoy = -1
    }

    if (dx < 0) {
      sentidox = -1
    }
    this.direccion.valor = magnitudAngular
    this.sentidoX = sentidox
    this.sentidoY = sentidoy
  }
}

object Velocidad {
  def conversorMetroSegAKmHor(metroSeg: Double): Double = {
    val KmHor: Double = metroSeg * (3.6)
    KmHor
  }

  def conversorKmHorAMetroSeg(kmHor: Double): Double = {
    val metroSeg: Double = kmHor * (1 / 3.6)
    metroSeg
  }

}
