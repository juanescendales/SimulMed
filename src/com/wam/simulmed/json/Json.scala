package com.wam.simulmed.json
import scala.collection.mutable._
import net.liftweb.json._
import net.liftweb.json.JsonAST.JValue
import java.io._
import com.wam.simulmed.entradaSimulacion._
import scala.io.Source
import net.liftweb.json.Serialization.write
class Json[T] {
  implicit val formats = DefaultFormats
  def leerDatosIniciales(ruta: String): Entrada = {
    val cadena = Source.fromFile(ruta).getLines.mkString
    val json = parse(cadena)
    val datos = json.extract[Entrada]
    datos
  }
  def escribirArchivo(ruta: String, datos: T) {
    val pw = new PrintWriter(new File(ruta))
    pw.write(write(datos))
    pw.close
  }
}