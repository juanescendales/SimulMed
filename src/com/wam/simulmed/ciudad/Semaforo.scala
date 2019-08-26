package com.wam.simulmed.ciudad
import com.wam.simulmed.inmovil._

class Semaforo(val id: String, val xi: Double, val yi: Double,val estado: EstadoSemaforo, val via: Via) extends Punto(xi, yi)

object Semaforo{
  def apply(id:String,estado: EstadoSemaforo,via: Via,inicio:Boolean=false):Semaforo={
    var x: Double = 0
    var y: Double = 0
    if(inicio){
      via.anguloPolar match {
        case 90.0  => { x = via.origen.x; y = via.origen.y + 10 }
        case -90.0 => { x = via.origen.x; y = via.origen.y - 10 }
        case ang   => if (ang > 90 || ang < -90) { x = via.origen.x-10*math.cos(via.angulo.toRadians); y = via.origen.y-10*math.sin(via.angulo.toRadians) } else { x = via.origen.x+10*math.cos(via.angulo.toRadians); y = via.origen.y+10*math.sin(via.angulo.toRadians) }
      }
    }else{
      via.anguloPolar match {
        case 90.0  => { x = via.fin.x; y = via.fin.y - 10 }
        case -90.0 => { x = via.fin.x; y = via.fin.y + 10 }
        case ang   => if (ang > 90 || ang < -90) { x = via.fin.x+10*math.cos(via.angulo.toRadians); y = via.fin.y+10*math.sin(via.angulo.toRadians) } else { x = via.fin.x-10*math.cos(via.angulo.toRadians); y = via.fin.y-10*math.sin(via.angulo.toRadians) }
      }
    }
    new Semaforo("semaforo"+id,x,y,estado,via)
  }
}