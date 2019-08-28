package com.wam.simulmed.grafico

import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import java.awt.event.KeyEvent
import com.wam.simulmed.inmovil.Simulacion

object KeyListen extends KeyListener {
  def keyPressed(event: KeyEvent) {

    if (event.getKeyCode == 116) {

        Simulacion.start()
      
    }

    if (event.getKeyCode == 117) {
      Simulacion.stop()
    }
    
    if (event.getKeyCode == 113) {
      Simulacion.guardarDatos()
      Simulacion.stop()
    }

  }

  def keyReleased(event: KeyEvent) {
  }

  def keyTyped(event: KeyEvent) {
  }
}