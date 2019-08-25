package com.wam.simulmed.inmovil

class Punto(private var _x: Double, private var _y: Double) {
  def x = _x
  def x_=(x: Double) = _x = x

  def y = _y
  def y_=(y: Double) = _y = y

}
object Punto {
  def distanciaEntre2Puntos(punto1: Punto, punto2: Punto): Double = {
    Math.sqrt(Math.pow(punto2.x - punto1.x, 2) + Math.pow(punto2.y - punto1.y, 2))
  }
}