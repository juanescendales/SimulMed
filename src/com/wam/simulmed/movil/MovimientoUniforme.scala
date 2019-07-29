package com.wam.simulmed.movil
import com.wam.simulmed.inmovil._
trait MovimientoUniforme {
  val posicion: Punto
  val velocidad: Velocidad 
  def avance(dt:Double):Unit ={
    posicion.x = posicion.x + velocidad.sentidoX*dt*(velocidad.magnitud*Math.cos(this.velocidad.direccion.valor.toRadians))
    posicion.y = posicion.y + velocidad.sentidoY*dt*(velocidad.magnitud*Math.sin(this.velocidad.direccion.valor.toRadians))
  }
}