package com.wam.simulmed.grafico

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import com.wam.simulmed.ciudad._
import com.wam.simulmed.inmovil._
import com.wam.simulmed.movil._
//Libreria para frames
import javax.swing.JFrame
//Libreria para lineas
import java.awt.BasicStroke;
//Libreria para colores
import java.awt.Color;
//JfreeChart
import org.jfree.chart.ChartFrame
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.chart.ChartFrame
import org.jfree.chart.axis.ValueAxis
import org.jfree.chart.annotations.XYTextAnnotation
import java.awt.event.KeyListener

import java.util.Random

//Figuras geometricas
import java.awt.geom.Ellipse2D
import java.awt.Rectangle
import java.awt.Polygon

object Grafico{
  
  val coloresIntersecciones:Map[Interseccion, String]=Map()
  val dataset: XYSeriesCollection = new XYSeriesCollection();
  val renderer: XYLineAndShapeRenderer = new XYLineAndShapeRenderer()
  var plot: XYPlot = null
  var ventana: ChartFrame = null;
  
  var punto1: XYSeries = new XYSeries("1")
  punto1.add(0, 0)
  dataset.addSeries(punto1)
  
  def iniciarGrafico(vias:ArrayBuffer[Via],semaforos:ArrayBuffer[Semaforo]){
  	
    this.renderer.setAutoPopulateSeriesStroke(false)
  	this.renderer.setAutoPopulateSeriesPaint(false)
  	
    this.renderer.setBaseStroke(new BasicStroke(4))
    this.renderer.setBasePaint(Color.decode("#cccccc"))
    
    cargarMapa(vias)
    cargarSemaforos(semaforos)
    
    val xyScatterChart: JFreeChart = ChartFactory.createScatterPlot(
  	null, 
  	null, 
  	null, 
  	this.dataset,
  	PlotOrientation.VERTICAL, false, false, false)


  	this.plot=xyScatterChart.getXYPlot()

  	this.plot.setBackgroundPaint(Color.WHITE)
  	

  	val range:  ValueAxis = plot.getRangeAxis()
    range.setVisible(false)
    val domain: ValueAxis = plot.getDomainAxis()
    domain.setVisible(false)
    
    this.cargarIntersecciones(plot)

  	this.plot.setRenderer(this.renderer)
  	

  	this.ventana = new ChartFrame("vehTraffic", xyScatterChart)
  	ventana.setVisible(true)
  	ventana.setSize(1300, 700)
  	ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  	ventana.addKeyListener(KeyListen)
  }
  
  def cargarMapa(vias:ArrayBuffer[Via]){
    vias.foreach(via=>{
      val nuevaVia: XYSeries  = new XYSeries(via.nombre+"-"+via.origen.nombre+"-"+via.fin.nombre)
    	nuevaVia.add(via.origen.xi, via.origen.yi)
    	nuevaVia.add(via.fin.xi, via.fin.yi)
    	if(via.camaraDeFotoDeteccion.isDefined){
    	  val camara: XYSeries = new XYSeries("camara-"+via.nombre+"-"+via.origen.nombre+"-"+via.fin.nombre)
    	  val posicion=via.camaraDeFotoDeteccion.get.posicion
    	  camara.add(posicion.x, posicion.y)
    	  this.dataset.addSeries(camara)
    	  this.renderer.setSeriesShape(this.dataset.getSeriesCount-1, new Rectangle(-4,-4,4,4))
    	  this.renderer.setSeriesPaint(this.dataset.getSeriesCount-1,Color.blue)
    	}
      this.dataset.addSeries(nuevaVia)
      renderer.setSeriesShapesVisible(dataset.getSeriesCount-1, false)
    })
  }
  
  def cargarSemaforos(semaforos:ArrayBuffer[Semaforo]){
    semaforos.foreach(semaforo=>{
      val semaforoGrafico: XYSeries = new XYSeries(semaforo.id)
      println(semaforo.x, semaforo.x)
  	  semaforoGrafico.add(semaforo.x, semaforo.y)
  	  this.dataset.addSeries(semaforoGrafico)
  	  this.renderer.setSeriesShape(this.dataset.getSeriesCount-1, new Rectangle(-4,-4,4,4))
  	  
  	  semaforo.estado match{
        case EstadoRojo => this.renderer.setSeriesPaint(this.dataset.getSeriesCount-1,Color.RED)
        case EstadoVerde => this.renderer.setSeriesPaint(this.dataset.getSeriesCount-1,Color.GREEN)
      }
    })
  }
  
  def borrarVehiculos(vehiculosSimulacion: ArrayBuffer[Viaje]) = {
    vehiculosSimulacion.foreach(Viaje => {
      dataset.removeSeries(dataset.getSeriesIndex(Viaje.vehiculo.placa))
    })
  }
  
  def cargarIntersecciones(plot:XYPlot){
    val random = new Random()
    val intersecciones:ArrayBuffer[Interseccion]=GrafoVias.listaDeNodos
    intersecciones.foreach(interseccion=>{
      val colorHex=randomHex()
      this.renderer.setSeriesPaint(this.dataset.getSeriesCount,Color.decode(colorHex))
      val label: XYTextAnnotation = new XYTextAnnotation(interseccion.nombre.getOrElse(""),interseccion.xi, interseccion.yi+(350)*((random.nextFloat()*2).round-1))
    	label.setPaint(Color.decode(colorHex))
    	plot.addAnnotation(label)
    	this.coloresIntersecciones+=(interseccion->colorHex)
    })
  }
  
  
  def cargarVehiculo(Viaje:Viaje){
    val vehiculo=Viaje.vehiculo
    val punto=vehiculo.posicion
    val vehiculoGrafico: XYSeries = new XYSeries(vehiculo.placa)
    vehiculoGrafico.add(punto.x, punto.y)
    this.dataset.addSeries(vehiculoGrafico)
    vehiculo match{
      case vehiculo:Bus=>{
        this.renderer.setSeriesShape(this.dataset.getSeriesCount-1, new Polygon(Array(-3,3,5,0,-5),Array(-6,-6,0,4,0),5))
        this.renderer.setSeriesPaint(this.dataset.getSeriesCount-1,Color.decode(this.coloresIntersecciones(Viaje.interseccionesCompletas.last)))
      }
      case vehiculo:Camion=>{
        this.renderer.setSeriesShape(this.dataset.getSeriesCount-1, new Rectangle(-2,-4,4,10))
        this.renderer.setSeriesPaint(this.dataset.getSeriesCount-1,Color.decode(this.coloresIntersecciones(Viaje.interseccionesCompletas.last)))
      }
      case vehiculo:Carro=>{
        this.renderer.setSeriesShape(this.dataset.getSeriesCount-1, new Ellipse2D.Double(-4,-4,8,10))
        this.renderer.setSeriesPaint(this.dataset.getSeriesCount-1,Color.decode(this.coloresIntersecciones(Viaje.interseccionesCompletas.last)))
      }
      case vehiculo:Moto=>{
        this.renderer.setSeriesShape(this.dataset.getSeriesCount-1, new Polygon(Array(-4, 0, +4, 0),Array(4, 2, 4, -4),4))
        this.renderer.setSeriesPaint(this.dataset.getSeriesCount-1,Color.decode(this.coloresIntersecciones(Viaje.interseccionesCompletas.last)))
      }
      case vehiculo:MotoTaxi=>{
        this.renderer.setSeriesShape(this.dataset.getSeriesCount-1, new Polygon(Array(-5,0,5),Array(-5,5,-5),3))
        this.renderer.setSeriesPaint(this.dataset.getSeriesCount-1,Color.decode(this.coloresIntersecciones(Viaje.interseccionesCompletas.last)))
      }
    }
    
  }
  
  def actualizarVehiculo(Viaje:Viaje){
    val vehiculo=Viaje.vehiculo
    val punto=vehiculo.posicion
    val vehiculoGrafico: XYSeries = this.dataset.getSeries(vehiculo.placa)
    vehiculoGrafico.clear()
    vehiculoGrafico.add(punto.x, punto.y)
  }
  
  def randomHex():String={
      val random = new Random()
      var letters:Array[String] = "0123456789ABCDEF".split("")
      var color = "#"
      for (i<- 0 to 6) {
        val index:Int=((random.nextFloat()*15).round)
        color=color+letters(index)
      }
      color
  }
}