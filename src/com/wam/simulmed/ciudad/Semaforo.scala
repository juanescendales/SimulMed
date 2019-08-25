package com.wam.simulmed.ciudad
import com.wam.simulmed.inmovil._

case class Semaforo(xi: Double, yi: Double,val estado: EstadoSemaforo, val via: Via) extends Punto(xi, yi)
