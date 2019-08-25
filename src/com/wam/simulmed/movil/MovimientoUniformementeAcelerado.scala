package com.wam.simulmed.movil
import com.wam.simulmed.inmovil._
trait MovimientoUniformementeAcelerado {
  val posicion: Punto
  val velocidad: Velocidad
  val aceleracion: Aceleracion
  def avance(dt: Double): Unit = {
    
    
    if(velocidad.magnitud >= velocidad.velocidadTotalMagnitud && !aceleracion.frenando){
      velocidad.magnitud_=(velocidad.velocidadTotalMagnitud)
      aceleracion.magnitud_=(0)
    }else{
      val v = velocidad.magnitud + (aceleracion.magnitud * dt)
      velocidad.magnitud_=(v)
    }
    
    
    posicion.x = posicion.x + velocidad.sentidoX * dt * (velocidad.magnitud * Math.cos(this.velocidad.direccion.valor.toRadians))
    posicion.y = posicion.y + velocidad.sentidoY * dt * (velocidad.magnitud * Math.sin(this.velocidad.direccion.valor.toRadians))
  }

}