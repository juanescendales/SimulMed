package com.wam.simulmed.inmovil
import org.neo4j.driver.v1._
import com.wam.simulmed.ciudad._
import scala.collection.mutable.ArrayBuffer

object Conexion1 {
  val url = "bolt://localhost/7687"
  val user = "neo4j" 
  val pass = "1234" 

  def getSession(): (Driver, Session) = {
    val driver = GraphDatabase.driver(url, AuthTokens.basic(user, pass))
    val session = driver.session
    (driver, session)
  }
  
  def closeSession(driver:Driver, session:Session){
    session.close()
    driver.close()
  }
}

object ControladorSemaforos{
  
  def guardarNodosSemaforos(nodos:ArrayBuffer[NodoSemaforo]){
    //Se simulan las intersecciones como nodos semaforos
    val (driver, session) = Conexion.getSession()
    nodos.foreach(nodo=>{
      val script=s"""
        MATCH (i:Interseccion{ nombre:'${nodo.interseccion.nombre.get}'}) SET i.ts=${nodo.verTiempoActual()},i.semaforoActual='${nodo.verActual().id}',i.estadoSemaforo='${nodo.verActual().estado}'
        """
      val result = session.run(script)
    })
    Conexion1.closeSession(driver, session)
  }
  
  def cargarNodosSemaforos(){
    val (driver, session) = Conexion.getSession()
    val script = s"match(i:Interseccion) return(i)"
    val result = session.run(script)
    while(result.hasNext()) {
        val nodo = result.next().values().get(0)
        val interseccion=Interseccion(nodo.get("xi").asInt, nodo.get("yi").asInt, Some(nodo.get("nombre").asString))
        val nodoSemaforo=Simulacion.nodosSemaforos.find(inte=>inte==NodoSemaforo(interseccion)())
      println(nodoSemaforo.get.getSemaforos())
      
      } 
    session.close()
    driver.close()
  }
  
}