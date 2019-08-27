package com.wam.simulmed.ciudad
import com.wam.simulmed.inmovil._

class Semaforo(val id: String, val xi: Double, val yi: Double,var estado: EstadoSemaforo, val via: Via, val interseccion: Interseccion) extends Punto(xi, yi)

object Semaforo{
  def apply(id:String,estado: EstadoSemaforo,via: Via,interseccion: Interseccion,inicio:Boolean=false):Semaforo={
    var x: Double = 0
    var y: Double = 0
    if(inicio){
      val (xi,yi)=Recta.parametrizacionDeDosPuntos(via.origen, via.fin, 0.2)
      x=xi
      y=yi
    }else{
      val (xi,yi)=Recta.parametrizacionDeDosPuntos(via.origen, via.fin, 0.8)
      x=xi
      y=yi
    }
    new Semaforo("semaforo"+id,x,y,estado,via,interseccion)
  }
}