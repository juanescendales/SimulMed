package com.wam.simulmed.ciudad

import com.wam.simulmed.inmovil.Punto
import scala.collection.mutable.ArrayBuffer
case class Interseccion(xi: Int, yi: Int, val nombre: Option[String]) extends Punto(xi, yi)