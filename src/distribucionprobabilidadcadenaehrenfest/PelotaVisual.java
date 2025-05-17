package distribucionprobabilidadcadenaehrenfest;

//package distribucionprobabilidadcadenaehrenfest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class PelotaVisual {
    public double x, y; // Posición actual
    public double targetX, targetY; // Posición destino
    public boolean enMovimiento;
    private Color color;
    private int radio;
    private double velocidad = 2.0; // Píxeles por frame de animación

    // Enum para saber en qué urna está o si se está moviendo hacia una
    public enum UrnaDestino { A, B, NINGUNA }
    public UrnaDestino urnaObjetivo = UrnaDestino.NINGUNA;


    public PelotaVisual(double x, double y, int radio, Color color) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.radio = radio;
        this.color = color;
        this.enMovimiento = true;
    }

    public void dibujar(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval((int) (x - radio), (int) (y - radio), radio * 2, radio * 2);
    }

    public void moverHacia(double tx, double ty, UrnaDestino objetivo) {
        this.targetX = tx;
        this.targetY = ty;
        this.enMovimiento = true;
        this.urnaObjetivo = objetivo;
    }

    // Devuelve true si aún se está moviendo, false si llegó
    public boolean actualizarPosicion() {
        if (!enMovimiento) {
            return false;
        }

        double dx = targetX - x;
        double dy = targetY - y;
        double distancia = Math.sqrt(dx * dx + dy * dy);

        if (distancia < velocidad) {
            x = targetX;
            y = targetY;
            enMovimiento = false;
            return false; // Llegó
        } else {
            x += (dx / distancia) * velocidad;
            y += (dy / distancia) * velocidad;
            return true; // Sigue moviéndose
        }
    }

    public Point getPosicion() {
        return new Point((int)x, (int)y);
    }

    public void setPosicion(double x, double y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.enMovimiento = true;
    }

    public boolean isEnMovimiento() {
        return enMovimiento;
    }
    
    public UrnaDestino getUrnaObjetivo() {
        return urnaObjetivo;
    }

    public void setUrnaObjetivo(UrnaDestino urnaObjetivo) {
        this.urnaObjetivo = urnaObjetivo;
    }
}
