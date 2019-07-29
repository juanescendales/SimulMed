package com.wam.simulmed.inmovil

case class Velocidad(private var _magnitud:Double )( val direccion: Angulo = new Angulo(0.0)) {
  def magnitud = _magnitud
  def magnitud_=(magnitud: Double) = _magnitud = magnitud
    
  
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
