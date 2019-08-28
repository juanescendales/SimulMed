package com.wam.simulmed.inmovil
import org.neo4j.driver.v1._
import com.wam.simulmed.ciudad._
object Conexion {
  val url = "bolt://localhost/7687"
  val user = "neo4j"
  val pass = "1234"

  //RECORDAR QUE HAY QUE METER LAS SIGUIENTES 2 LINEAS PARA CERRAR CONEXION Y ESO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  //session.close()
  //driver.close()

  def getSession(): (Driver, Session) = {
    val driver = GraphDatabase.driver(url, AuthTokens.basic(user, pass))
    val session = driver.session
    (driver, session)
  }
  def getInterseccion(nombre: String): Option[Interseccion] = {
    val (driver, session) = getSession()
    val script = s"match(n:Interseccion{nombre:'$nombre'})return(n)"
    val result = session.run(script)
    val interseccion =
      if (result.hasNext()) {
        val nodo = result.next().values().get(0)
        Some(new Interseccion(nodo.get("xi").asInt(), nodo.get("yi").asInt(), Some(nodo.get("nombre").asString())))
      } else {
        None
      }
    interseccion
  }

  def getVias: Array[Option[Via]] = {
    //match(io:Interseccion)-[:ES_ORIGEN]->(v:Via)<-[:ES_FIN]-(if:Interseccion)
    val (driver, session) = getSession()
    val script = s"match(io:Interseccion)-[:ES_ORIGEN]->(v:Via)<-[:ES_FIN]-(if:Interseccion)"
    val result = session.run(script)
    val interseccion =
      if (result.hasNext()) {
        val nodo = result.next().values().get(0)
        Some(new Interseccion(nodo.get("xi").asInt(), nodo.get("yi").asInt(), Some(nodo.get("nombre").asString())))
      } else {
        None
      }
    interseccion
  }
}