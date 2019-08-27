package com.wam.simulmed.inmovil
import com.wam.simulmed.ciudad._
import scala.collection.mutable.Queue

case class NodoSemaforo(interseccion:Interseccion)(semaforos:Queue[Semaforo]=Queue[Semaforo]()){
  def addSemaforo(semaforo:Semaforo)=semaforos.enqueue(semaforo)
  def getSemaforos()=semaforos.toArray
  def avanzarProximo():Semaforo={
    val semaforo=semaforos.dequeue()
    semaforo.estado=EstadoRojo
    semaforos.enqueue(semaforo)
    semaforo
  }
  def verActual():Semaforo=semaforos.front
  def actualizarNodo(){
    Simulacion.estadoSemaforo match{
      case EstadoVerde=>{
        this.avanzarProximo()
        this.verActual().estado=EstadoVerde
      }
      case EstadoAmarillo=>  this.verActual().estado=EstadoAmarillo
    }
  }
}