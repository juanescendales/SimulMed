package com.wam.simulmed.salidaSimulacion
import net.liftweb.json._
import net.liftweb.json.JsonAST.JValue
class ResultadosSimulacion(val vehiculos: SalidaVehiculos, val mallaVial: MallaVial, val tiempos: Tiempos, val velocidades: SalidaVelocidades, val distancias: Distancias, val comparendos: ComparendosJson) {

}