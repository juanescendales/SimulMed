package com.wam.simulmed.movil
import com.wam.simulmed.inmovil._
import scala.collection.mutable.Set

abstract case class Vehiculo(var placa: String = "")(pos: Punto, vel: Velocidad, acel: Aceleracion) extends Movil(pos, vel,acel) with MovimientoUniformementeAcelerado {

  private var _detenido = false
  private var _frenando = false
  def detenido = _detenido
  def detenido_=(detenido: Boolean) = _detenido = detenido
  
  def frenando = _frenando
  def frenando_=(frenando: Boolean) = _frenando = frenando

}

object Vehiculo {

  val setPlacas: Set[String] = Set()

  val pCarros: Double = Simulacion.parametros.pametrosSimulacion.proporciones.carros
  val pMotos: Double = Simulacion.parametros.pametrosSimulacion.proporciones.motos + pCarros
  val pBuses: Double = Simulacion.parametros.pametrosSimulacion.proporciones.buses + pMotos
  val pCamiones: Double = Simulacion.parametros.pametrosSimulacion.proporciones.camiones + pBuses
  val pMotoTaxis: Double = Simulacion.parametros.pametrosSimulacion.proporciones.motoTaxis + pCamiones

  def apply(pos: Punto, vel: Velocidad, acel: Aceleracion): Vehiculo = {
    val r = new scala.util.Random
    val numeroAleatorio = r.nextDouble()

    if (numeroAleatorio <= pCarros) {
      return new Carro(pos, vel, acel)

    } else if (numeroAleatorio <= pMotos) {
      return new Moto(pos, vel, acel)

    } else if (numeroAleatorio <= pBuses) {
      return new Bus(pos, vel, acel)

    } else if (numeroAleatorio <= pCamiones) {
      return new Camion(pos, vel, acel)

    } else {
      return new MotoTaxi(pos, vel, acel)
    }

  }

}
