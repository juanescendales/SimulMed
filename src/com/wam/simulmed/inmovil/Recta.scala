package com.wam.simulmed.inmovil

trait Recta {
  type T <: Punto
  val origen: T
  val fin: T
  val angulo = math.atan(math.abs(fin.y - origen.y) / math.abs(fin.x - origen.x)).toDegrees
  val anguloPolar =  math.atan2(fin.y - origen.y,fin.x - origen.x).toDegrees 
}
object Recta {
  // Metodo que retorna el x y y de la parametrizacion del segmento segun un t definido entre 0 y 1 (x , y)
  def parametrizacionDeDosPuntos(origen: Punto, fin: Punto, t: Double): (Double, Double) = {
    val posX = fin.x * (t) + origen.x * (1-t)
    val posY = fin.y * (t) + origen.y * (1-t)
    (posX, posY)
  }
}