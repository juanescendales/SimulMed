package com.wam.simulmed.movil

import com.wam.simulmed.inmovil._
abstract class Movil(val posicion: Punto, val velocidad:Velocidad, val aceleracion: Aceleracion) {
  def anguloActual(): Double={
    velocidad.direccion.valor
  }
  def avance(dt:Double):Unit
}
