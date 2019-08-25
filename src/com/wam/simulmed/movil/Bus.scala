
package com.wam.simulmed.movil
import com.wam.simulmed.inmovil._
class Bus(p:Punto, v : Velocidad, a:Aceleracion) extends Vehiculo()(p,v,a){
  this.placa = this.generarPlacaAleatoria()
  def generarPlacaAleatoria():String={
    val r = scala.util.Random
    var placa =""
    do{
    placa = r.alphanumeric.filter(_.isLetter).take(3).mkString+(r.nextInt(10)).toString+(r.nextInt(10)).toString+(r.nextInt(10)).toString
    placa.toUpperCase()
    }while(!Vehiculo.setPlacas.add(placa))
    placa
  }
}
