package com.wam.simulmed.movil
import com.wam.simulmed.inmovil._
class Moto(p:Punto, v : Velocidad) extends Vehiculo()(p,v){
  this.placa = this.generarPlacaAleatoria()
  def generarPlacaAleatoria():String={
    val r = scala.util.Random
    var placa = ""
    do{
      placa = r.alphanumeric.filter(_.isLetter).take(3).mkString+(r.nextInt(10)).toString+(r.nextInt(10)).toString+r.alphanumeric.filter(_.isLetter).take(1).mkString
      placa.toUpperCase()
    }while(!Vehiculo.setPlacas.add(placa))
    placa
    
  }
}
