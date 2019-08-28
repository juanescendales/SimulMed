package com.wam.simulmed.inmovil
import com.wam.simulmed.ciudad._
import scala.collection.mutable.ArrayBuffer
import com.wam.simulmed.movil._
import com.wam.simulmed.json.Json
import com.wam.simulmed.entradaSimulacion._
import com.wam.simulmed.salidaSimulacion._
import com.wam.simulmed.grafico.Grafico

object Simulacion extends Runnable {
  val jsonAdmin = new Json[Salida]
  val ruta = System.getProperty("user.dir") + "\\src\\com\\wam\\simulmed\\"
  val parametros = jsonAdmin.leerDatosIniciales(ruta + "parametros.json")
  var running = false
  val grafo = GrafoVias
  var t: Double = 0
  var dt: Double = parametros.pametrosSimulacion.dt
  val tRefresh = parametros.pametrosSimulacion.tRefresh
  val minVehiculos = parametros.pametrosSimulacion.vehiculos.minimo
  val maxVehiculos = parametros.pametrosSimulacion.vehiculos.maximo
  val minVelocidad: Double = parametros.pametrosSimulacion.velocidad.minimo
  val maxVelocidad: Double = parametros.pametrosSimulacion.velocidad.maximo
  val minAceleracion: Double = parametros.pametrosSimulacion.aceleración.mínimo
  val maxAceleracion: Double = parametros.pametrosSimulacion.aceleración.máximo
  val XSemaforoFrenar: Double = parametros.pametrosSimulacion.distanciasFrenadoVehiculos.XSemaforoFrenar
  val XSemaforoAmarilloContinuar: Double = parametros.pametrosSimulacion.distanciasFrenadoVehiculos.XSemaforoAmarilloContinuar
  val minTiempoVerde: Double = parametros.pametrosSimulacion.semaforos.minTiempoVerde
  val maxTiempoVerde: Double = parametros.pametrosSimulacion.semaforos.maxTiempoVerde
  val tiempoAmarillo: Double = parametros.pametrosSimulacion.semaforos.tiempoAmarillo

  val totalVehiculos = (((new scala.util.Random).nextDouble() * (Simulacion.maxVehiculos - Simulacion.minVehiculos)) + Simulacion.minVehiculos).toInt

  var estadoSemaforo: EstadoSemaforo = EstadoVerde
  var listaVias = ArrayBuffer.empty[Via]
  var nodosSemaforos = ArrayBuffer.empty[NodoSemaforo]
  var hilo: Thread = _ //new Thread(Simulacion)

  def cargar() {

    Conexion.getIntersecciones
    Conexion.getVias()
    Conexion.getCamarasFotoDeteccion()

    val nodosSemaforos = ArrayBuffer[NodoSemaforo]()
    var numeroSemaforos = 0
    def cargarSemaforosVia(via: Via) = {
      val nodoInicio = NodoSemaforo(via.origen)()
      val nodoFin = NodoSemaforo(via.fin)()
      via.sentido match {
        case Sentido("Un sentido") => {
          val indice = nodosSemaforos.indexOf(nodoFin)
          if (indice == -1) {
            val semaforoFin = Semaforo(numeroSemaforos.toString(), EstadoVerde, via, via.fin)
            numeroSemaforos += 1
            nodoFin.addSemaforo(semaforoFin)
            nodosSemaforos += nodoFin
          } else {
            val semaforoFin = Semaforo(numeroSemaforos.toString(), EstadoRojo, via, via.fin)
            numeroSemaforos += 1
            nodosSemaforos(indice).addSemaforo(semaforoFin)
          }
        }
        case Sentido("Doble via") => {
          val indiceFin = nodosSemaforos.indexOf(nodoFin)
          val indiceInicio = nodosSemaforos.indexOf(nodoInicio)
          if (indiceFin == -1) {
            val semaforoFin = Semaforo(numeroSemaforos.toString(), EstadoVerde, via, via.fin)
            via.semaforoFin = Some(semaforoFin)
            numeroSemaforos += 1
            nodoFin.addSemaforo(semaforoFin)
            nodosSemaforos += nodoFin
          } else {
            val semaforoFin = Semaforo(numeroSemaforos.toString(), EstadoRojo, via, via.fin)
            via.semaforoFin = Some(semaforoFin)
            numeroSemaforos += 1
            nodosSemaforos(indiceFin).addSemaforo(semaforoFin)
          }
          if (indiceInicio == -1) {
            val semaforoInicio = Semaforo(numeroSemaforos.toString(), EstadoVerde, via, via.origen, true)
            via.semaforoInicio = Some(semaforoInicio)
            numeroSemaforos += 1
            nodoInicio.addSemaforo(semaforoInicio)
            nodosSemaforos += nodoInicio
          } else {
            val semaforoInicio = Semaforo(numeroSemaforos.toString(), EstadoRojo, via, via.origen, true)
            via.semaforoInicio = Some(semaforoInicio)
            numeroSemaforos += 1
            nodosSemaforos(indiceInicio).addSemaforo(semaforoInicio)
          }
        }
      }
    }
    listaVias.foreach(cargarSemaforosVia)
    this.nodosSemaforos = nodosSemaforos
    grafo.construir(listaVias)
    val grafico = Grafico
    grafico.iniciarGrafico(listaVias, nodosSemaforos.map(_.getSemaforos()).flatten)

  }
  def start() {
    if (Simulacion.running) {
      Simulacion.stop()
    }

    hilo = new Thread(Simulacion)
    hilo.start()
  }

  def stop() {
    Simulacion.running = false
    hilo.join()
    Vehiculo.setPlacas.clear()

    Grafico.borrarVehiculos(Viaje.listaDeVehiculosSimulacion ++ Viaje.listaDeVehiculosSimulacionDetenidos)
    Viaje.listaDeVehiculosSimulacion.clear()
    Viaje.listaDeVehiculosSimulacionDetenidos.clear()
    Comparendo.listaComparendos.clear()
    Simulacion.t = 0
  }

  def run() {
    for (i <- 0 until Simulacion.totalVehiculos) {
      Viaje.apply()
    }
    Simulacion.running = true

    while (!Viaje.listaDeVehiculosSimulacion.isEmpty && Simulacion.running) {
      val grafico = Grafico
      this.nodosSemaforos.foreach(nodo=>{
        nodo.ts+=this.dt
        nodo.verificarNodo()
      })
      Viaje.listaDeVehiculosSimulacion.foreach(vehiculo => {
        vehiculo.mover(Simulacion.dt)
        grafico.actualizarVehiculo(vehiculo)
        if (vehiculo.vehiculo.detenido) {
          Viaje.listaDeVehiculosSimulacionDetenidos.+=(vehiculo)
        }
      })
      Viaje.listaDeVehiculosSimulacionDetenidos.foreach(vehiculo => vehiculo.pararVehiculo(vehiculo))
      Simulacion.t += Simulacion.dt
      Thread.sleep(tRefresh)
    }

    //Calculos Finales
    if (Simulacion.running) {
      //calculoDeResultados(Viaje.listaDeVehiculosSimulacionParaCalculos, Comparendo.listaComparendos)   DESCOMENTAR LUEGO, SON LOS RESULTADOS
    }

    //Fin Calculos Finales

  }
  def calculoDeResultados(listaDeVehiculosSimulacionParaCalculos: ArrayBuffer[Viaje], listaComparendos: ArrayBuffer[Comparendo]): Unit = {

    val totalVehiculos = listaDeVehiculosSimulacionParaCalculos.length
    val totalCarros = listaDeVehiculosSimulacionParaCalculos.filter(_.vehiculo.isInstanceOf[Carro]).length
    val totalMotos = listaDeVehiculosSimulacionParaCalculos.filter(_.vehiculo.isInstanceOf[Moto]).length
    val totalBuses = listaDeVehiculosSimulacionParaCalculos.filter(_.vehiculo.isInstanceOf[Bus]).length
    val totalCamiones = listaDeVehiculosSimulacionParaCalculos.filter(_.vehiculo.isInstanceOf[Camion]).length
    val totalMototaxis = listaDeVehiculosSimulacionParaCalculos.filter(_.vehiculo.isInstanceOf[MotoTaxi]).length

    val vias = listaDeVehiculosSimulacionParaCalculos.flatMap(_.recorridoCompleto).distinct.length
    val intersecciones = listaDeVehiculosSimulacionParaCalculos.flatMap(_.interseccionesCompletas).distinct.length
    val viasUnSentido = listaDeVehiculosSimulacionParaCalculos.flatMap(_.recorridoCompleto).filter(v => v.sentido.nombre == "Un sentido").distinct.length
    val viasDobleSentido = listaDeVehiculosSimulacionParaCalculos.flatMap(_.recorridoCompleto).filter(v => v.sentido.nombre == "Doble via").distinct.length
    val velMaximaVias = listaDeVehiculosSimulacionParaCalculos.flatMap(_.recorridoCompleto).distinct.map(_.velMaxima).max
    val velMinimaVias = listaDeVehiculosSimulacionParaCalculos.flatMap(_.recorridoCompleto).distinct.map(_.velMaxima).min
    val longitudPromedio = (listaDeVehiculosSimulacionParaCalculos.flatMap(_.recorridoCompleto).distinct.map(_.distancia).sum) / vias

    val mapPromedioOrigen = contar(listaDeVehiculosSimulacionParaCalculos.map(_.interseccionesCompletas(0)))

    val promedioOrigen = totalVehiculos.toDouble / (mapPromedioOrigen.size.toDouble) //Revisar

    val mapPromedioDestino = contar(listaDeVehiculosSimulacionParaCalculos.map(_.interseccionesCompletas.last))

    val promedioDestino = totalVehiculos.toDouble / (mapPromedioDestino.size.toDouble) //Revisar

    val sinOrigen = GrafoVias.listaDeNodos.length - mapPromedioOrigen.size
    val sinDestino = GrafoVias.listaDeNodos.length - mapPromedioDestino.size
    val arrayDistancias = listaDeVehiculosSimulacionParaCalculos.map(_.recorridoCompleto.map(_.distancia).reduce(_ + _))
    val distanciaMinima = arrayDistancias.min
    val distanciaMaxima = arrayDistancias.max
    val distanciaPromedio = (arrayDistancias.sum) / (arrayDistancias.length)

    val velocidadesVehiculos = ArrayBuffer[Double]()
    listaDeVehiculosSimulacionParaCalculos.foreach(x => { velocidadesVehiculos += x.vehiculo.velocidad.magnitud })
    val velVehiMax = Velocidad.conversorMetroSegAKmHor(velocidadesVehiculos.max)
    val velVehiMin = Velocidad.conversorMetroSegAKmHor(velocidadesVehiculos.min)
    val velVehiProm = Velocidad.conversorMetroSegAKmHor((velocidadesVehiculos.sum) / (velocidadesVehiculos.length))
    val tiempoRealidad = t
    val tiempoSimulacion: Double = tiempoRealidad * (tRefresh.toDouble / 1000)
    val cantidadComparendos: Int = listaComparendos.length
    var promedioExcesoComparendos: Double = 0
    if (cantidadComparendos > 0) {
      promedioExcesoComparendos = listaComparendos.map(_.porcentajeExcedido).reduce((x, y) => (x + y)) / listaComparendos.length
    }
    //creacion de objetos para la creacion de los resultados
    val salidaVehiculos = new SalidaVehiculos(totalVehiculos, totalCarros, totalMotos, totalBuses, totalCamiones, totalMototaxis)
    val vehiculosEnInterseccion = new VehiculosEnInterseccion(promedioOrigen, promedioDestino, sinOrigen, sinDestino)
    val mallaVial = new MallaVial(vias, intersecciones, viasUnSentido, viasDobleSentido, velMinimaVias, velMaximaVias, longitudPromedio, vehiculosEnInterseccion)
    val tiempos = new Tiempos(tiempoSimulacion, tiempoRealidad)
    val salidaVelocidades = new SalidaVelocidades(velVehiMin, velVehiMax, velVehiProm)
    val distancias = new Distancias(distanciaMinima, distanciaMaxima, distanciaPromedio)
    val comparendos = new ComparendosJson(cantidadComparendos, promedioExcesoComparendos)

    val resultados = new ResultadosSimulacion(salidaVehiculos, mallaVial, tiempos, salidaVelocidades, distancias, comparendos)
    val salida = new Salida(resultados)
    jsonAdmin.escribirArchivo(ruta + "resultados.json", salida)
  }

  //funcion para contar la cantidad de vehiculos en cada interseccion
  def contar(recorrido: ArrayBuffer[Interseccion]): scala.collection.mutable.Map[Interseccion, Int] = {
    var r = scala.collection.mutable.Map[Interseccion, Int]()
    recorrido.foreach(f => {
      if (!(r.contains(f))) {
        r += (f -> 1)
      } else {
        var aux = r.get(f).get.toInt
        r.remove(f)
        r += (f -> (1 + aux))
      }
    })
    r
  }

}