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
  //aspectos de aceleracion que hay que meter en el json
  val minAceleracion: Double = 5.0
  val maxAceleracion: Double = 20.0
  val XSemaforoFrenar: Double = 500
  //FIN
  val totalVehiculos = (((new scala.util.Random).nextDouble() * (Simulacion.maxVehiculos - Simulacion.minVehiculos)) + Simulacion.minVehiculos).toInt

  var listaVias = ArrayBuffer.empty[Via]
  var hilo: Thread = _ //new Thread(Simulacion)

  def cargar() {

    val niquia = new Interseccion(300, 12000, "Niquia")
    val lauraAuto = new Interseccion(2400, 11400, "M. Laura Auto")
    val lauraReg = new Interseccion(2400, 12600, "M. Laura Reg")
    val ptoCero = new Interseccion(5400, 12000, "Pto 0")
    val mino = new Interseccion(6300, 15000, "Minorista")
    val villa = new Interseccion(6300, 19500, "Villanueva")
    val ig65 = new Interseccion(5400, 10500, "65 Igu")
    val robledo = new Interseccion(5400, 1500, "Exito Rob")
    val colReg = new Interseccion(8250, 12000, "Col Reg")
    val colFerr = new Interseccion(8250, 15000, "Col Ferr")
    val col65 = new Interseccion(8250, 10500, "Col 65")
    val col80 = new Interseccion(8250, 1500, "Col 80")
    val juanOr = new Interseccion(10500, 19500, "Sn Juan Ori")
    val maca = new Interseccion(10500, 12000, "Macarena")
    val expo = new Interseccion(12000, 13500, "Exposiciones")
    val reg30 = new Interseccion(13500, 15000, "Reg 30")
    val monte = new Interseccion(16500, 15000, "Monterrey")
    val agua = new Interseccion(19500, 15000, "Aguacatala")
    val viva = new Interseccion(21000, 15000, "Viva Env")
    val mayor = new Interseccion(23400, 15000, "Mayorca")
    val ferrCol = new Interseccion(8250, 15000, "Ferr Col")
    val ferrJuan = new Interseccion(10500, 15000, "Alpujarra")
    val sanDiego = new Interseccion(12000, 19500, "San Diego")
    val premium = new Interseccion(13500, 19500, "Premium")
    val pp = new Interseccion(16500, 19500, "Parque Pob")
    val santafe = new Interseccion(19500, 18750, "Santa Fe")
    val pqEnv = new Interseccion(21000, 18000, "Envigado")
    val juan65 = new Interseccion(10500, 10500, "Juan 65")
    val juan80 = new Interseccion(10500, 1500, "Juan 80")
    val _33_65 = new Interseccion(12000, 10500, "33 con 65")
    val bule = new Interseccion(12000, 7500, "Bulerias")
    val gema = new Interseccion(12000, 1500, "St Gema")
    val _30_65 = new Interseccion(13500, 10500, "30 con 65")
    val _30_70 = new Interseccion(13500, 4500, "30 con 70")
    val _30_80 = new Interseccion(13500, 1500, "30 con 80")
    val bol65 = new Interseccion(11100, 10500, "Boliv con 65")
    val gu10 = new Interseccion(16500, 12000, "Guay con 10")
    val terminal = new Interseccion(16500, 10500, "Term Sur")
    val gu30 = new Interseccion(13500, 12000, "Guay 30")
    val gu80 = new Interseccion(19500, 12000, "Guay 80")
    val _65_80 = new Interseccion(19500, 10500, "65 con 30")
    val gu_37S = new Interseccion(21000, 12000, "Guay con 37S")
    //creacion de vias con camaras de fotodeteccion
    
    
    val v1 = new Via(mino, villa, 60, TipoVia("Calle"), Sentido.dobleVia, "58", Some("Oriental"))
    val cam1 = new CamaraFotoDeteccion(v1.distancia/2)
    v1.camaraDeFotoDeteccion = Some(cam1)
    val v2 = new Via(viva, mayor, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional"))
    val cam2 = new CamaraFotoDeteccion(v2.distancia/2)
    v2.camaraDeFotoDeteccion = Some(cam2)
    val v3 = new Via(ferrCol, colReg, 60, TipoVia("Calle"), Sentido.dobleVia, "450", Some("Colombia"))
    val cam3 = new CamaraFotoDeteccion(v3.distancia/2)
    v3.camaraDeFotoDeteccion = Some(cam3)
    val v4 = new Via(_33_65, bule, 60, TipoVia("Calle"), Sentido.dobleVia, "33", Some("33"))
    val cam4 = new CamaraFotoDeteccion(v4.distancia/2)
    v4.camaraDeFotoDeteccion = Some(cam4)
    val v5 = new Via(maca, bol65, 60, TipoVia("Diagonal"), Sentido.dobleVia, "74B", Some("Boliv"))
    val cam5 = new CamaraFotoDeteccion(v5.distancia/2)
    v5.camaraDeFotoDeteccion = Some(cam5)
    val v6 = new Via(_33_65, bol65, 60, TipoVia("Carrera"), Sentido.unaVia, "65", Some("65"))
    val cam6 = new CamaraFotoDeteccion(v6.distancia/2)
    v6.camaraDeFotoDeteccion = Some(cam6)
    val v7 = new Via(robledo, col80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80"))
    val cam7 = new CamaraFotoDeteccion(v7.distancia/2)
    v7.camaraDeFotoDeteccion = Some(cam7)
    val v8 = new Via(agua, santafe, 60, TipoVia("Calle"), Sentido.dobleVia, "12S", Some("80"))
    val cam8 = new CamaraFotoDeteccion(v8.distancia/2)
    v8.camaraDeFotoDeteccion = Some(cam8)
    
    
    val vias = ArrayBuffer(
      new Via(niquia, lauraAuto, 80, TipoVia("Carrera"), Sentido.dobleVia, "64C", Some("Auto Norte")),
      new Via(niquia, lauraReg, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(lauraAuto, lauraReg, 60, TipoVia("Calle"), Sentido.dobleVia, "94", Some("Pte Madre Laura")),
      new Via(lauraAuto, ptoCero, 80, TipoVia("Carrera"), Sentido.dobleVia, "64C", Some("Auto Norte")),
      new Via(lauraReg, ptoCero, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(ptoCero, mino, 60, TipoVia("Calle"), Sentido.dobleVia, "58", Some("Oriental")),
      
      new Via(mino, villa, 60, TipoVia("Calle"), Sentido.dobleVia, "58", Some("Oriental")),
      
      new Via(ptoCero, ig65, 60, TipoVia("Calle"), Sentido.dobleVia, "55", Some("Iguaná")),
      new Via(ig65, robledo, 60, TipoVia("Calle"), Sentido.dobleVia, "55", Some("Iguaná")),
      new Via(ptoCero, colReg, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(colReg, maca, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(maca, expo, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(expo, reg30, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(reg30, monte, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(monte, agua, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(agua, viva, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      
      new Via(viva, mayor, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      
      new Via(mino, ferrCol, 60, TipoVia("Carrera"), Sentido.dobleVia, "55", Some("Ferrocarril")),
      new Via(ferrCol, ferrJuan, 60, TipoVia("Carrera"), Sentido.dobleVia, "55", Some("Ferrocarril")),
      new Via(ferrJuan, expo, 60, TipoVia("Carrera"), Sentido.dobleVia, "55", Some("Ferrocarril")),
      new Via(villa, juanOr, 60, TipoVia("Carrera"), Sentido.dobleVia, "46", Some("Oriental")),
      new Via(juanOr, sanDiego, 60, TipoVia("Carrera"), Sentido.dobleVia, "46", Some("Oriental")),
      new Via(sanDiego, premium, 60, TipoVia("Carrera"), Sentido.dobleVia, "43A", Some("Av Pob")),
      new Via(premium, pp, 60, TipoVia("Carrera"), Sentido.dobleVia, "43A", Some("Av Pob")),
      new Via(pp, santafe, 60, TipoVia("Carrera"), Sentido.dobleVia, "43A", Some("Av Pob")),
      new Via(santafe, pqEnv, 60, TipoVia("Carrera"), Sentido.dobleVia, "43A", Some("Av Pob")),
      new Via(pqEnv, mayor, 60, TipoVia("Carrera"), Sentido.dobleVia, "43A", Some("Av Pob")),
      
      new Via(ferrCol, colReg, 60, TipoVia("Calle"), Sentido.dobleVia, "450", Some("Colombia")),
      
      new Via(colReg, col65, 60, TipoVia("Calle"), Sentido.dobleVia, "450", Some("Colombia")),
      new Via(col65, col80, 60, TipoVia("Calle"), Sentido.dobleVia, "450", Some("Colombia")),
      new Via(juanOr, ferrJuan, 60, TipoVia("Calle"), Sentido.dobleVia, "44", Some("Sn Juan")),
      new Via(ferrJuan, maca, 60, TipoVia("Calle"), Sentido.dobleVia, "44", Some("Sn Juan")),
      new Via(maca, juan65, 60, TipoVia("Calle"), Sentido.dobleVia, "44", Some("Sn Juan")),
      new Via(juan65, juan80, 60, TipoVia("Calle"), Sentido.dobleVia, "44", Some("Sn Juan")),
      new Via(sanDiego, expo, 60, TipoVia("Calle"), Sentido.dobleVia, "33", Some("33")),
      new Via(expo, _33_65, 60, TipoVia("Calle"), Sentido.dobleVia, "33", Some("33")),
      
      new Via(_33_65, bule, 60, TipoVia("Calle"), Sentido.dobleVia, "33", Some("33")),
      
      new Via(bule, gema, 60, TipoVia("Calle"), Sentido.dobleVia, "33", Some("33")),
      new Via(premium, reg30, 60, TipoVia("Calle"), Sentido.dobleVia, "30", Some("30")),
      new Via(reg30, _30_65, 60, TipoVia("Calle"), Sentido.dobleVia, "30", Some("30")),
      new Via(_30_65, _30_70, 60, TipoVia("Calle"), Sentido.dobleVia, "30", Some("30")),
      new Via(_30_70, _30_80, 60, TipoVia("Calle"), Sentido.dobleVia, "30", Some("30")),
      
      new Via(maca, bol65, 60, TipoVia("Diagonal"), Sentido.dobleVia, "74B", Some("Boliv")),
      
      new Via(bol65, bule, 60, TipoVia("Diagonal"), Sentido.dobleVia, "74B", Some("Boliv")),
      new Via(bule, _30_70, 60, TipoVia("Diagonal"), Sentido.dobleVia, "74B", Some("Boliv")),
      new Via(juan80, bule, 60, TipoVia("Transversal"), Sentido.dobleVia, "39B", Some("Nutibara")),
      new Via(pp, monte, 60, TipoVia("Calle"), Sentido.dobleVia, "10", Some("10")),
      new Via(monte, gu10, 60, TipoVia("Calle"), Sentido.dobleVia, "10", Some("10")),
      new Via(gu10, terminal, 60, TipoVia("Calle"), Sentido.dobleVia, "10", Some("10")),
      new Via(expo, gu30, 60, TipoVia("Carrera"), Sentido.dobleVia, "52", Some("Av Guay")),
      new Via(gu30, gu10, 60, TipoVia("Carrera"), Sentido.dobleVia, "52", Some("Av Guay")),
      new Via(gu10, gu80, 60, TipoVia("Carrera"), Sentido.dobleVia, "52", Some("Av Guay")),
      new Via(gu80, gu_37S, 60, TipoVia("Carrera"), Sentido.dobleVia, "52", Some("Av Guay")),
      new Via(lauraAuto, ig65, 60, TipoVia("Carrera"), Sentido.dobleVia, "65", Some("65")),
      new Via(ig65, col65, 60, TipoVia("Carrera"), Sentido.dobleVia, "65", Some("65")),
      new Via(juan65, col65, 60, TipoVia("Carrera"), Sentido.unaVia, "65", Some("65")),
      new Via(bol65, juan65, 60, TipoVia("Carrera"), Sentido.unaVia, "65", Some("65")),
      
      new Via(_33_65, bol65, 60, TipoVia("Carrera"), Sentido.unaVia, "65", Some("65")),
      
      new Via(_30_65, _33_65, 60, TipoVia("Carrera"), Sentido.unaVia, "65", Some("65")),
      new Via(_30_65, terminal, 60, TipoVia("Carrera"), Sentido.dobleVia, "65", Some("65")),
      new Via(terminal, _65_80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("65")),
      
      new Via(robledo, col80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      
      new Via(col80, juan80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      new Via(juan80, gema, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      new Via(gema, _30_80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      new Via(_30_80, _65_80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      new Via(_65_80, gu80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      new Via(gu80, agua, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      
      new Via(agua, santafe, 60, TipoVia("Calle"), Sentido.dobleVia, "12S", Some("80")),
      
      new Via(viva, pqEnv, 60, TipoVia("Calle"), Sentido.dobleVia, "37S", Some("37S")),
      new Via(viva, gu_37S, 60, TipoVia("Calle"), Sentido.dobleVia, "63", Some("37S")))
    listaVias = vias

    var nodosSemaforos = ArrayBuffer[NodoSemaforo]()
    def cargarSemaforosVia(via: Via) = {
      val nodoInicio = NodoSemaforo(via.origen)()
      val nodoFin = NodoSemaforo(via.fin)()
      via.sentido match {
        case Sentido("Un sentido") => {
          val indice = nodosSemaforos.indexOf(nodoFin)
          if (indice == -1) {
            var x: Double = 0
            var y: Double = 0
            via.angulo match {
              case 90.0  => { x = via.fin.x; y = via.fin.y - 10 }
              case -90.0 => { x = via.fin.x; y = via.fin.y + 10 }
              case ang   => if (ang > 90) { x = via.fin.x + 10; y = (via.fin.x + 10) * math.tan(via.angulo) } else { x = via.fin.x - 10; y = (via.fin.x - 10) * math.tan(via.angulo) }
            }
            val semaforoFin = Semaforo(x, y, EstadoVerde, via)

          }
        }
        case Sentido("Doble via") => {
          val indiceFin = nodosSemaforos.indexOf(nodoFin)
          val indiceInicio = nodosSemaforos.indexOf(nodoInicio)
          if (indiceFin == -1) {
            var x: Double = 0
            var y: Double = 0
            via.angulo match {
              case 90.0  => { x = via.fin.x; y = via.fin.y - 10 }
              case -90.0 => { x = via.fin.x; y = via.fin.y + 10 }
              case ang   => if (ang > 90) { x = via.fin.x + 10; y = (via.fin.x + 10) * math.tan(via.angulo) } else { x = via.fin.x - 10; y = (via.fin.x - 10) * math.tan(via.angulo) }
            }
            val semaforoFin = Semaforo(x, y, EstadoVerde, via)
          }
          if (indiceInicio == -1) {
            var x: Double = 0
            var y: Double = 0
            via.angulo match {
              case 90.0  => { x = via.origen.x; y = via.origen.y + 10 }
              case -90.0 => { x = via.origen.x; y = via.origen.y - 10 }
              case ang   => if (ang > 90) { x = via.origen.x - 10; y = (via.origen.x - 10) * math.tan(via.angulo) } else { x = via.origen.x + 10; y = (via.origen.x + 10) * math.tan(via.angulo) }
            }
            val semaforoFin = Semaforo(x, y, EstadoVerde, via)
          }
        }
      }
    }
    listaVias.foreach(cargarSemaforosVia)
    grafo.construir(vias)
    val grafico = Grafico
    grafico.iniciarGrafico(vias)

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
    Simulacion.t = 0
  }

  def run() {
    for (i <- 0 until Simulacion.totalVehiculos) {
      Viaje.apply()
    }
    Simulacion.running = true

    while (!Viaje.listaDeVehiculosSimulacion.isEmpty && Simulacion.running) {
      val grafico = Grafico
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
      //calculoDeResultados(Viaje.listaDeVehiculosSimulacionParaCalculos)   DESCOMENTAR LUEGO, SON LOS RESULTADOS
    }

    //Fin Calculos Finales

  }
  def calculoDeResultados(listaDeVehiculosSimulacionParaCalculos: ArrayBuffer[Viaje]): Unit = {

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

    //creacion de objetos para la creacion de los resultados
    val salidaVehiculos = new SalidaVehiculos(totalVehiculos, totalCarros, totalMotos, totalBuses, totalCamiones, totalMototaxis)
    val vehiculosEnInterseccion = new VehiculosEnInterseccion(promedioOrigen, promedioDestino, sinOrigen, sinDestino)
    val mallaVial = new MallaVial(vias, intersecciones, viasUnSentido, viasDobleSentido, velMinimaVias, velMaximaVias, longitudPromedio, vehiculosEnInterseccion)
    val tiempos = new Tiempos(tiempoSimulacion, tiempoRealidad)
    val salidaVelocidades = new SalidaVelocidades(velVehiMin, velVehiMax, velVehiProm)
    val distancias = new Distancias(distanciaMinima, distanciaMaxima, distanciaPromedio)

    val resultados = new ResultadosSimulacion(salidaVehiculos, mallaVial, tiempos, salidaVelocidades, distancias)
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