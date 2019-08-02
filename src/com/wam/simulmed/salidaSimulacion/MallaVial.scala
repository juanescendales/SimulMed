package com.wam.simulmed.salidaSimulacion
import net.liftweb.json._
import net.liftweb.json.JsonAST.JValue
class MallaVial(val vias: Int, val intersecciones: Int, val viasUnSentido: Int,
                     val viasDobleSentido: Int, val velocidadMinima: Double,
                     val velocidadMaxima: Double, val longitudPromedio: Double, val vehiculosEnInterseccion: VehiculosEnInterseccion) {
}
