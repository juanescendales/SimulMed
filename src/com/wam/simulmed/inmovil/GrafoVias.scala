package com.wam.simulmed.inmovil

import com.wam.simulmed.ciudad.Interseccion
import com.wam.simulmed.ciudad.Via
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._
import scalax.collection.edge.WLDiEdge
import scala.collection.mutable.ArrayBuffer
import scalax.collection.mutable.Graph

object GrafoVias {
  val g = Graph[Interseccion, WLDiEdge]()
  val listaDeNodos = new ArrayBuffer[Interseccion]()
  
  def n(outer: Interseccion):g.NodeT = g.get(outer)

  def construir(vias: ArrayBuffer[Via]):Unit = {
    vias.foreach(x=> {
      if((x.sentido.nombre).equalsIgnoreCase("Un Sentido")){
        (g += WLDiEdge(x.origen, x.fin)(x.distancia,x))
      }else{
        (g += WLDiEdge(x.origen, x.fin)(x.distancia,x))
        (g += WLDiEdge(x.fin, x.origen)(x.distancia,x))
      }
      
    })
    
    g.nodes.foreach(x=> listaDeNodos+= x.value)
    
  }
    
}