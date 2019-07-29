package com.wam.simulmed.ciudad

case class Sentido private (val nombre:String){

}

object Sentido{
  
  private val unSentidoObject = new Sentido("Un sentido")
  private val unDobleSentidoObject=new Sentido("Doble via")
  def unaVia:Sentido = {
    unSentidoObject
  }
  def dobleVia:Sentido = {
    unDobleSentidoObject
  }
}
