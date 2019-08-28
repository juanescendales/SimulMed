package com.wam.simulmed.ciudad
import com.wam.simulmed.inmovil._

class Semaforo(val id: String, val xi: Double, val yi: Double,var estado: EstadoSemaforo,val tiempoVerde:Double, val via: Via, val interseccion: Interseccion) extends Punto(xi, yi){
  var serieId:Int=0
}

object Semaforo{
  def apply(id:String,estado: EstadoSemaforo,via: Via,interseccion: Interseccion,inicio:Boolean=false):Semaforo={
    var x: Double = 0
    var y: Double = 0
    if(inicio){
      val (xi,yi)=Recta.parametrizacionDeDosPuntos(via.origen, via.fin, (estado.distanciaFrenado)/via.distancia)
      x=xi
      y=yi
    }else{
      val (xi,yi)=Recta.parametrizacionDeDosPuntos(via.origen, via.fin, 1-(estado.distanciaFrenado)/via.distancia)
      x=xi
      y=yi
    }
    val tiempoVerde = (((new scala.util.Random).nextDouble() * (EstadoVerde.maxTiempoVerde - EstadoVerde.minTiempoVerde)) + EstadoVerde.minTiempoVerde).toDouble
    new Semaforo("semaforo"+id,x,y,estado,tiempoVerde,via,interseccion)
  }
}