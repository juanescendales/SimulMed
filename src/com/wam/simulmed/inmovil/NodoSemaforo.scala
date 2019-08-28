package com.wam.simulmed.inmovil
import com.wam.simulmed.ciudad._
import scala.collection.mutable.Queue
import com.wam.simulmed.grafico.Grafico

case class NodoSemaforo(interseccion:Interseccion)(semaforos:Queue[Semaforo]=Queue[Semaforo]()){
  var ts:Double=0
  def addSemaforo(semaforo:Semaforo)=semaforos.enqueue(semaforo)
  def getSemaforos()=semaforos.toArray
  def avanzarProximo():Semaforo={
    val semaforo=semaforos.dequeue()
    semaforo.estado=EstadoRojo
    semaforos.enqueue(semaforo)
    semaforo
  }
  def verActual():Semaforo=semaforos.front
  def verTiempoActual():Double={
    val semaforo=this.verActual()
    semaforo.estado match{
      case EstadoVerde=>{
        semaforo.tiempoVerde
      }
      case EstadoAmarillo=>  EstadoAmarillo.getTiempo
    }
  }
  def actualizarNodo(estado:EstadoSemaforo){
    estado match{
      case EstadoVerde=>{
        this.avanzarProximo()
        this.verActual().estado=EstadoVerde
      }
      case EstadoAmarillo=>  this.verActual().estado=EstadoAmarillo
    }
  }
  def verificarNodo(){
    val tiempoActual=this.verTiempoActual()
    while (this.ts >= tiempoActual) {
        this.ts -= tiempoActual
        var estado:EstadoSemaforo=this.verActual().estado.avanzarEstado(this.verActual().estado)
        if (estado == EstadoRojo) estado = EstadoVerde
        this.actualizarNodo(estado)
        Grafico.actualizarSemaforos(this)
      }
  }
}