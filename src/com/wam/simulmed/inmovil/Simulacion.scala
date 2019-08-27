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
  var ts: Double = 0
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
  var nodosSemaforos=ArrayBuffer.empty[NodoSemaforo]
  var hilo: Thread = _ //new Thread(Simulacion)

  def cargar() {

    val niquia = new Interseccion(300, 12000, Some("Niquia"))
    val lauraAuto = new Interseccion(2400, 11400, Some("M. Laura Auto"))
    val lauraReg = new Interseccion(2400, 12600, Some("M. Laura Reg"))
    val ptoCero = new Interseccion(5400, 12000, Some("Pto 0"))
    val mino = new Interseccion(6300, 15000, Some("Minorista"))
    val villa = new Interseccion(6300, 19500, Some("Villanueva"))
    val ig65 = new Interseccion(5400, 10500, Some("65 Igu"))
    val robledo = new Interseccion(5400, 1500, Some("Exito Rob"))
    val colReg = new Interseccion(8250, 12000, Some("Col Reg"))
    val colFerr = new Interseccion(8250, 15000, Some("Col Ferr"))
    val col65 = new Interseccion(8250, 10500, Some("Col 65"))
    val col80 = new Interseccion(8250, 1500, Some("Col 80"))
    val juanOr = new Interseccion(10500, 19500, Some("Sn Juan Ori"))
    val maca = new Interseccion(10500, 12000, Some("Macarena"))
    val expo = new Interseccion(12000, 13500, Some("Exposiciones"))
    val reg30 = new Interseccion(13500, 15000, Some("Reg 30"))
    val monte = new Interseccion(16500, 15000, Some("Monterrey"))
    val agua = new Interseccion(19500, 15000, Some("Aguacatala"))
    val viva = new Interseccion(21000, 15000, Some("Viva Env"))
    val mayor = new Interseccion(23400, 15000, Some("Mayorca"))
    val ferrCol = new Interseccion(8250, 15000, Some("Ferr Col"))
    val ferrJuan = new Interseccion(10500, 15000, Some("Alpujarra"))
    val sanDiego = new Interseccion(12000, 19500, Some("San Diego"))
    val premium = new Interseccion(13500, 19500, Some("Premium"))
    val pp = new Interseccion(16500, 19500, Some("Parque Pob"))
    val santafe = new Interseccion(19500, 18750, Some("Santa Fe"))
    val pqEnv = new Interseccion(21000, 18000, Some("Envigado"))
    val juan65 = new Interseccion(10500, 10500, Some("Juan 65"))
    val juan80 = new Interseccion(10500, 1500, Some("Juan 80"))
    val _33_65 = new Interseccion(12000, 10500, Some("33 con 65"))
    val bule = new Interseccion(12000, 7500, Some("Bulerias"))
    val gema = new Interseccion(12000, 1500, Some("St Gema"))
    val _30_65 = new Interseccion(13500, 10500, Some("30 con 65"))
    val _30_70 = new Interseccion(13500, 4500, Some("30 con 70"))
    val _30_80 = new Interseccion(13500, 1500, Some("30 con 80"))
    val bol65 = new Interseccion(11100, 10500, Some("Boliv con 65"))
    val gu10 = new Interseccion(16500, 12000, Some("Guay con 10"))
    val terminal = new Interseccion(16500, 10500, Some("Term Sur"))
    val gu30 = new Interseccion(13500, 12000, Some("Guay 30"))
    val gu80 = new Interseccion(19500, 12000, Some("Guay 80"))
    val _65_80 = new Interseccion(19500, 10500, Some("65 con 30"))
    val gu_37S = new Interseccion(21000, 12000, Some("Guay con 37S"))
    //creacion de vias con camaras de fotodeteccion

    val v1 = new Via(mino, villa, 60, TipoVia("Calle"), Sentido.dobleVia, "58", Some("Oriental"))
    val cam1 = new CamaraFotoDeteccion(v1)
    v1.camaraDeFotoDeteccion = Some(cam1)
    val v2 = new Via(viva, mayor, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional"))
    val cam2 = new CamaraFotoDeteccion(v2)
    v2.camaraDeFotoDeteccion = Some(cam2)
    val v3 = new Via(ferrCol, colReg, 60, TipoVia("Calle"), Sentido.dobleVia, "450", Some("Colombia"))
    val cam3 = new CamaraFotoDeteccion(v3)
    v3.camaraDeFotoDeteccion = Some(cam3)
    val v4 = new Via(_33_65, bule, 60, TipoVia("Calle"), Sentido.dobleVia, "33", Some("33"))
    val cam4 = new CamaraFotoDeteccion(v4)
    v4.camaraDeFotoDeteccion = Some(cam4)
    val v5 = new Via(maca, bol65, 60, TipoVia("Diagonal"), Sentido.dobleVia, "74B", Some("Boliv"))
    val cam5 = new CamaraFotoDeteccion(v5)
    v5.camaraDeFotoDeteccion = Some(cam5)
    val v6 = new Via(_33_65, bol65, 60, TipoVia("Carrera"), Sentido.unaVia, "65", Some("65"))
    val cam6 = new CamaraFotoDeteccion(v6)
    v6.camaraDeFotoDeteccion = Some(cam6)
    val v7 = new Via(robledo, col80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80"))
    val cam7 = new CamaraFotoDeteccion(v7)
    v7.camaraDeFotoDeteccion = Some(cam7)
    val v8 = new Via(agua, santafe, 60, TipoVia("Calle"), Sentido.dobleVia, "12S", Some("80"))
    val cam8 = new CamaraFotoDeteccion(v8)
    v8.camaraDeFotoDeteccion = Some(cam8)
    val v9 = new Via(_30_80, _65_80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80"))
    val cam9 = new CamaraFotoDeteccion(v9)
    v9.camaraDeFotoDeteccion = Some(cam9)

    val vias = ArrayBuffer(
      new Via(niquia, lauraAuto, 80, TipoVia("Carrera"), Sentido.dobleVia, "64C", Some("Auto Norte")),
      new Via(niquia, lauraReg, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(lauraAuto, lauraReg, 60, TipoVia("Calle"), Sentido.dobleVia, "94", Some("Pte Madre Laura")),
      new Via(lauraAuto, ptoCero, 80, TipoVia("Carrera"), Sentido.dobleVia, "64C", Some("Auto Norte")),
      new Via(lauraReg, ptoCero, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(ptoCero, mino, 60, TipoVia("Calle"), Sentido.dobleVia, "58", Some("Oriental")),
      v1,
      new Via(ptoCero, ig65, 60, TipoVia("Calle"), Sentido.dobleVia, "55", Some("Iguaná")),
      new Via(ig65, robledo, 60, TipoVia("Calle"), Sentido.dobleVia, "55", Some("Iguaná")),
      new Via(ptoCero, colReg, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(colReg, maca, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(maca, expo, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(expo, reg30, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(reg30, monte, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(monte, agua, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      new Via(agua, viva, 80, TipoVia("Carrera"), Sentido.dobleVia, "62", Some("Regional")),
      v2,
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
      v3,
      new Via(colReg, col65, 60, TipoVia("Calle"), Sentido.dobleVia, "450", Some("Colombia")),
      new Via(col65, col80, 60, TipoVia("Calle"), Sentido.dobleVia, "450", Some("Colombia")),
      new Via(juanOr, ferrJuan, 60, TipoVia("Calle"), Sentido.dobleVia, "44", Some("Sn Juan")),
      new Via(ferrJuan, maca, 60, TipoVia("Calle"), Sentido.dobleVia, "44", Some("Sn Juan")),
      new Via(maca, juan65, 60, TipoVia("Calle"), Sentido.dobleVia, "44", Some("Sn Juan")),
      new Via(juan65, juan80, 60, TipoVia("Calle"), Sentido.dobleVia, "44", Some("Sn Juan")),
      new Via(sanDiego, expo, 60, TipoVia("Calle"), Sentido.dobleVia, "33", Some("33")),
      new Via(expo, _33_65, 60, TipoVia("Calle"), Sentido.dobleVia, "33", Some("33")),
      v4,
      new Via(bule, gema, 60, TipoVia("Calle"), Sentido.dobleVia, "33", Some("33")),
      new Via(premium, reg30, 60, TipoVia("Calle"), Sentido.dobleVia, "30", Some("30")),
      new Via(reg30, _30_65, 60, TipoVia("Calle"), Sentido.dobleVia, "30", Some("30")),
      new Via(_30_65, _30_70, 60, TipoVia("Calle"), Sentido.dobleVia, "30", Some("30")),
      new Via(_30_70, _30_80, 60, TipoVia("Calle"), Sentido.dobleVia, "30", Some("30")),
      v5,
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
      v6,
      new Via(_30_65, _33_65, 60, TipoVia("Carrera"), Sentido.unaVia, "65", Some("65")),
      new Via(_30_65, terminal, 60, TipoVia("Carrera"), Sentido.dobleVia, "65", Some("65")),
      new Via(terminal, _65_80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("65")),
      v7,
      new Via(col80, juan80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      new Via(juan80, gema, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      new Via(gema, _30_80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),

      v9,

      new Via(_65_80, gu80, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      new Via(gu80, agua, 60, TipoVia("Carrera"), Sentido.dobleVia, "80", Some("80")),
      v8,
      new Via(viva, pqEnv, 60, TipoVia("Calle"), Sentido.dobleVia, "37S", Some("37S")),
      new Via(viva, gu_37S, 60, TipoVia("Calle"), Sentido.dobleVia, "63", Some("37S")))
    listaVias = vias

    val nodosSemaforos = ArrayBuffer[NodoSemaforo]()
    var numeroSemaforos=0
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
            val semaforoFin = Semaforo(numeroSemaforos.toString(),EstadoVerde, via,via.fin)
            via.semaforoFin=Some(semaforoFin)
            numeroSemaforos+=1
            nodoFin.addSemaforo(semaforoFin)
            nodosSemaforos+=nodoFin
          }else{
            val semaforoFin = Semaforo(numeroSemaforos.toString(),EstadoRojo, via,via.fin)
            via.semaforoFin=Some(semaforoFin)
            numeroSemaforos+=1
            nodosSemaforos(indiceFin).addSemaforo(semaforoFin)
          }
          if (indiceInicio == -1) {
            val semaforoInicio = Semaforo(numeroSemaforos.toString(),EstadoVerde, via,via.origen,true)
            via.semaforoInicio=Some(semaforoInicio)
            numeroSemaforos+=1
            nodoInicio.addSemaforo(semaforoInicio)
            nodosSemaforos+=nodoInicio
          }else{
            val semaforoInicio = Semaforo(numeroSemaforos.toString(),EstadoRojo, via,via.origen,true)
            via.semaforoInicio=Some(semaforoInicio)
            numeroSemaforos+=1
            nodosSemaforos(indiceInicio).addSemaforo(semaforoInicio)
          }
        }
      }
    }
    listaVias.foreach(cargarSemaforosVia)
    this.nodosSemaforos=nodosSemaforos
    grafo.construir(vias)
    val grafico = Grafico
    grafico.iniciarGrafico(vias,nodosSemaforos.map(_.getSemaforos()).flatten)

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
      Simulacion.ts += Simulacion.dt
      while (Simulacion.ts >= Simulacion.estadoSemaforo.getTiempo) {
        println(Simulacion.estadoSemaforo)
        Simulacion.ts-=Simulacion.estadoSemaforo.getTiempo
        Grafico.actualizarSemaforos(this.nodosSemaforos)
        Simulacion.estadoSemaforo=Simulacion.estadoSemaforo.avanzarEstado(Simulacion.estadoSemaforo)
        if(Simulacion.estadoSemaforo==EstadoRojo) Simulacion.estadoSemaforo=Simulacion.estadoSemaforo.avanzarEstado(Simulacion.estadoSemaforo)
      }
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
    var promedioExcesoComparendos:Double = 0
    if(cantidadComparendos > 0){
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