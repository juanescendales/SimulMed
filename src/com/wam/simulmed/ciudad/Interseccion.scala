package com.wam.simulmed.ciudad

import com.wam.simulmed.inmovil.Punto
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
case class Interseccion(xi: Int, yi: Int, val nombre: Option[String]) extends Punto(xi, yi) {
  Interseccion.listaIntersecciones += ((this.nombre.getOrElse("")-> this))
}
object Interseccion {
  val listaIntersecciones = collection.mutable.Map[String, Interseccion]()
}