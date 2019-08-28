package com.wam.simulmed.inmovil
import org.neo4j.driver.v1._
import com.wam.simulmed.ciudad._
import scala.collection.mutable.ArrayBuffer
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
  def getIntersecciones {
    val (driver, session) = getSession()
    val script = "match(n:Interseccion)return(n)"
    val result = session.run(script)
    while (result.hasNext()) {
      val node = result.next().values().get(0)
      getInterseccion(node.get("nombre").asString())
    }
    session.close()
    driver.close()
  }

  
  def getCamarasFotoDeteccion(): Unit = {
    val (driver, session) = getSession()
    val script = s"match(n:CamaraFotoDeteccion) return(n)"
    val result = session.run(script)
    while(result.hasNext()) {
        val nodo = result.next().values().get(0)
        val id = nodo.get("id").asInt
        val via = Via.listaVias.get(id).get
        new CamaraFotoDeteccion(via,id)
      } 
    
    session.close()
    driver.close()

  }

  def getVias(): Unit = {
    val (driver, session) = getSession()
    val script = s"match(io:Interseccion)-[:ES_ORIGEN]->(v:Via)<-[:ES_FIN]-(if:Interseccion) return io,v,if"
    val result = session.run(script)
    while (result.hasNext()) {
      val values = result.next().values()
      val NodoIo = values.get(0)
      val NodoV = values.get(1)
      val NodoIf = values.get(2)
      val interseccionInicio = Interseccion.listaIntersecciones.get(NodoIo.get("nombre").asString()).get
      val interseccionFinal = Interseccion.listaIntersecciones.get(NodoIf.get("nombre").asString()).get
      var sentido = Sentido.dobleVia
      if( NodoV.get("Sentido").asString() == "unaVia"){
        var sentido = Sentido.unaVia
      }
      
      new Via(NodoV.get("id").asInt(),interseccionInicio, interseccionFinal, NodoV.get("velMaxima").asDouble(), new TipoVia(NodoV.get("TipoVia").asString()),sentido,NodoV.get("numero").asString(),Some(NodoV.get("nombre").asString()))

    }
    session.close()
    driver.close()
  }
  

}