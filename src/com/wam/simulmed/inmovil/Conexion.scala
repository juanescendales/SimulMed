package com.wam.simulmed.inmovil
import org.neo4j.driver.v1._
import com.wam.simulmed.ciudad._
import com.wam.simulmed.movil._
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
    while (result.hasNext()) {
      val nodo = result.next().values().get(0)
      val id = nodo.get("id").asInt
      val via = Via.listaVias.get(id).get
      new CamaraFotoDeteccion(via, id)
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
      if (NodoV.get("Sentido").asString() == "unaVia") {
        var sentido = Sentido.unaVia
      }

      new Via(NodoV.get("id").asInt(), interseccionInicio, interseccionFinal, NodoV.get("velMaxima").asDouble(), new TipoVia(NodoV.get("TipoVia").asString()), sentido, NodoV.get("numero").asString(), Some(NodoV.get("nombre").asString()))

    }
    session.close()
    driver.close()
  }
  def guardarDatosViaje() {
    println("Aguarda mientras se ejecuta")
    val (driver, session) = getSession()
    val lista = Viaje.listaDeVehiculosSimulacionParaCalculos
    lista.foreach(f => {
      var script = s"""
        create(viaje:Viaje)
        create(v:Vehiculo{placa:'${f.vehiculo.placa}',tipoVehiculo: '${f.vehiculo.getClass.getSimpleName}'})
        create(p:Punto{x:${f.vehiculo.posicion.x}, y:${f.vehiculo.posicion.y}})
        create(a:Aceleracion{magnitud:${f.vehiculo.aceleracion.magnitud}, frenando:${f.vehiculo.aceleracion.frenando}, aceleracionArranque:${f.vehiculo.aceleracion.aceleracionArranque}})
        create(vel:Velocidad{magnitud:${f.vehiculo.velocidad.magnitud}, velocidadTotalMagnitud:${f.vehiculo.velocidad.velocidadTotalMagnitud}, angulo: ${f.vehiculo.velocidad.direccion.valor}, sentidoX: ${f.vehiculo.velocidad.sentidoX}, sentidoY: ${f.vehiculo.velocidad.sentidoY}})
        create (v)-[:UBICADO_EN]->(p)
        create(v)-[:ACELERA_POR]->(a)
        create(v)-[:VA_A_UNA]->(vel)
        create(viaje)-[:TIENE_UN]->(v)
        create(rec:Recorrido)
        create(interRec: InterseccionesRecorrido)
        create(recComp: RecorridoCompleto)
        create(interComp: InterseccionesCompleto)
                    """
      val recorrido = f.recorrido
      val interseccionesRecorrido = f.interseccionesRecorrido
      val recorridoCompleto = f.recorridoCompleto
      val interseccionesCompletas = f.interseccionesCompletas
      recorrido.foreach(r => {
        val idVia = r.id
        var count = 0
        val aux = s"""
          create(rec)-[:CONTIENE1{posicion: ${count}}]->(:Via{id: ${idVia}})
          """
        count += 1
        script = script + aux
      })
      interseccionesRecorrido.foreach(r => {
        val nombreInterseccion = r.nombre
        var count = 0
        val aux = s"""
          create(interRec)-[:CONTIENE2{posicion: ${count}}]->(:Interseccion{nombre: '${nombreInterseccion.get}'})
          """
        count += 1
        script = script + aux
      })
      recorridoCompleto.foreach(r => {
        val idVia = r.id
        var count = 0
        val aux = s"""
          create(recComp)-[:CONTIENE3{posicion: ${count}}]->(:Via{id: ${idVia}})
          """
        count += 1
        script = script + aux
      })
      interseccionesCompletas.foreach(r => {
        val nombreInterseccion = r.nombre
        var count = 0
        val aux = s"""
          create(interComp)-[:CONTIENE4{posicion: ${count}}]->(:Interseccion{nombre: '${nombreInterseccion.get}'})
          """
        count += 1
        script = script + aux
      })
      val aux = """
        create(viaje)-[:RECORRE1]->(rec)
        create(viaje)-[:RECORRE2]->(interRec)
        create(viaje)-[:RECORRE3]->(recComp)
        create(viaje)-[:RECORRE4]->(interComp)
        """
      script = script + aux
      val result = session.run(script)
    })
    session.close()
    driver.close()
  }

  def eliminarViajes {
    val (driver, session) = getSession()
    val script = s"""
      match(n:Viaje)
      match(vehi:Vehiculo)
      match(p:Punto)
      match(a:Aceleracion)
      match(vel: Velocidad)
      match(rec:Recorrido)-[:CONTIENE1]->(via:Via)
      match(interRec: InterseccionesRecorrido)-[:CONTIENE2]->(via2:Via)
      match(recComp: RecorridoCompleto)-[:CONTIENE3]->(via3:Via)
      match(interComp: InterseccionesCompleto)-[:CONTIENE4]->(via4:Via)
      detach delete n,vehi,p,a,vel,rec,via, interRec,via2, recComp, via3, interComp, via4
      """
    val result = session.run(script)
    session.close()
    driver.close()
  }

  def obtenerViajes: ArrayBuffer[Viaje] = {
    val array = ArrayBuffer[Viaje]()
    val (driver, session) = getSession()
    val script = s"""
      match(viaje:Viaje)-[:TIENE_UN]->(vehiculo:Vehiculo)
      match(vehiculo)-[:UBICADO_EN]->(punto:Punto)
      match(vehiculo)-[:VA_A_UNA]->(vel:Velocidad)
      match(vehiculo)-[:ACELERA_POR]->(acel:Aceleracion)
      return vehiculo, punto, vel, acel
      """
    val result = session.run(script)
    while (result.hasNext()) {
      val values = result.next().values()
      val vehiculo = values.get(0)
      val punto = values.get(1)
      val vel = values.get(2)
      val acel = values.get(3)
      val velocidad = new Velocidad(vel.get("magnitud").asDouble())(new Angulo(vel.get("angulo").asDouble()))
      velocidad.velocidadTotalMagnitud = vel.get("velocidadTotalMagnitud").asDouble()
      velocidad.sentidoX = vel.get("sentidoX").asInt()
      velocidad.sentidoY = vel.get("sentidoY").asInt()
      val aceleracion = new Aceleracion(acel.get("magnitud").asDouble())
      aceleracion.aceleracionArranque = acel.get("aceleracionArranque").asDouble()
      aceleracion.frenando = acel.get("frenando").asBoolean()
      //val vehiculoF = new Vehiculo(vehiculo.get("placa").asString())(new Punto(punto.get("x").asInt(), punto.get("y").asInt()), velocidad, aceleracion)
      val cosa: String = vehiculo.get("tipoVehiculo").asString()
      /*val x = match {
        case cosa == "Carro" =>
      }*/

    }
    session.close()
    driver.close()
    array
  }
  
  def guardarNodosSemaforos(nodos:ArrayBuffer[NodoSemaforo]){
    //Se simulan las intersecciones como nodos semaforos
    val (driver, session) = Conexion.getSession()
    nodos.foreach(nodo=>{
      val script=s"""
        MATCH (i:Interseccion{ nombre:'${nodo.interseccion.nombre.get}'}) SET i.ts=${nodo.verTiempoActual()},i.semaforoActual='${nodo.verActual().id}',i.estadoSemaforo='${nodo.verActual().estado}'
        """
      val result = session.run(script)
    })
    session.close()
    driver.close()
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