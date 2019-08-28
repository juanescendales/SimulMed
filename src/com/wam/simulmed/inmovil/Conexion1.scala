package com.wam.simulmed.inmovil
import org.neo4j.driver.v1._
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
  def guardarEstadoSemaforo(estadoSimulacion:EstadoSemaforo,tiempoSemaforoActual:Double){
    val (driver, session) = Conexion.getSession()
    
  }
}