package distribucionprobabilidadcadenaehrenfest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.BorderFactory;


public class UrnasPanel extends JPanel {
    private List<PelotaVisual> pelotasEnUrnaA;
    private List<PelotaVisual> pelotasEnUrnaB;
    private List<PelotaVisual> pelotasEnMovimiento;

    private Rectangle rectUrnaA;
    private Rectangle rectUrnaB;
    private final int ANCHO_URNA = 150;
    private final int ALTO_URNA = 200;
    private final int ESPACIO_ENTRE_URNAS = 50;
    private final int MARGEN = 20;
    private final int RADIO_PELOTA = 8;

    private Timer animationTimer;
    private Random random = new Random();
    private int m_totalBolas = 0; // Total de bolas en el sistema

    public UrnasPanel() {
        this.pelotasEnUrnaA = new ArrayList<>();
        this.pelotasEnUrnaB = new ArrayList<>();
        this.pelotasEnMovimiento = new ArrayList<>();

        setPreferredSize(new Dimension(ANCHO_URNA * 2 + ESPACIO_ENTRE_URNAS + MARGEN * 2, ALTO_URNA + MARGEN * 2));
        setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createTitledBorder("Simulación Visual Urnas"));

        // Timer para la animación
        animationTimer = new Timer(30, e -> { // Aproximadamente 33 FPS
            actualizarAnimacion();
            repaint();
        });
    }

    public void inicializarPelotas(int numA, int numB, int totalBolasSistema) {
        this.m_totalBolas = totalBolasSistema;
        pelotasEnUrnaA.clear();
        pelotasEnUrnaB.clear();
        pelotasEnMovimiento.clear();
        animationTimer.stop(); // Detener animación si estaba corriendo

        // Definir rectángulos de las urnas basado en el tamaño del panel
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        if (panelWidth == 0 || panelHeight == 0) { // Si el panel aún no tiene tamaño
            panelWidth = ANCHO_URNA * 2 + ESPACIO_ENTRE_URNAS + MARGEN * 2;
            panelHeight = ALTO_URNA + MARGEN * 2;
        }
        
        int urnaAY = (panelHeight - ALTO_URNA) / 2;
        int urnaAX = MARGEN;
        rectUrnaA = new Rectangle(urnaAX, urnaAY, ANCHO_URNA, ALTO_URNA);

        int urnaBX = MARGEN + ANCHO_URNA + ESPACIO_ENTRE_URNAS;
        rectUrnaB = new Rectangle(urnaBX, urnaAY, ANCHO_URNA, ALTO_URNA);


        for (int i = 0; i < numA; i++) {
            PelotaVisual p = crearPelotaEnUrna(rectUrnaA, Color.BLUE);
            pelotasEnUrnaA.add(p);
        }
        for (int i = 0; i < numB; i++) {
            PelotaVisual p = crearPelotaEnUrna(rectUrnaB, Color.RED);
            pelotasEnUrnaB.add(p);
        }
        repaint();
        if (!animationTimer.isRunning()){
             animationTimer.start();
        }
    }

    private PelotaVisual crearPelotaEnUrna(Rectangle urnaRect, Color color) {
        // Posición aleatoria dentro de la urna, evitando bordes
        int x = urnaRect.x + RADIO_PELOTA + random.nextInt(urnaRect.width - 2 * RADIO_PELOTA);
        int y = urnaRect.y + RADIO_PELOTA + random.nextInt(urnaRect.height - 2 * RADIO_PELOTA);
        return new PelotaVisual(x, y, RADIO_PELOTA, color);
    }

    // Método para iniciar el movimiento de una pelota
    // Este será llamado desde el SwingWorker en JFrmDistribucionEhrenfest
    public synchronized void simularMovimientoUnaPelota() {
        if (m_totalBolas == 0) return; // No hay bolas, no hay nada que mover

        int bolasEnA = pelotasEnUrnaA.size();
        // Probabilidad de que la bola seleccionada (al azar entre m) esté en A
        double probBolaEnA = (double) bolasEnA / m_totalBolas; 

        boolean moverDeAaB;

        // Decidir si la bola "seleccionada al azar" viene de A o de B
        // Esto es equivalente a: si hay 'i' bolas en A, la prob de sacar una de A es i/m
        // y la prob de sacar una de B es (m-i)/m.
        // Si se saca de A, se va a B. Si se saca de B, se va a A.
        
        if (random.nextDouble() < probBolaEnA) { // Bola seleccionada es de A (y se moverá a B)
            moverDeAaB = true;
        } else { // Bola seleccionada es de B (y se moverá a A)
            moverDeAaB = false;
        }

        if (moverDeAaB) {
            if (!pelotasEnUrnaA.isEmpty()) {
                PelotaVisual p = pelotasEnUrnaA.remove(random.nextInt(pelotasEnUrnaA.size()));
                int targetX = rectUrnaB.x + RADIO_PELOTA + random.nextInt(rectUrnaB.width - 2 * RADIO_PELOTA);
                int targetY = rectUrnaB.y + RADIO_PELOTA + random.nextInt(rectUrnaB.height - 2 * RADIO_PELOTA);
                p.moverHacia(targetX, targetY, PelotaVisual.UrnaDestino.B);
                pelotasEnMovimiento.add(p);
            }
        } else { // Mover de B a A
            if (!pelotasEnUrnaB.isEmpty()) {
                PelotaVisual p = pelotasEnUrnaB.remove(random.nextInt(pelotasEnUrnaB.size()));
                int targetX = rectUrnaA.x + RADIO_PELOTA + random.nextInt(rectUrnaA.width - 2 * RADIO_PELOTA);
                int targetY = rectUrnaA.y + RADIO_PELOTA + random.nextInt(rectUrnaA.height - 2 * RADIO_PELOTA);
                p.moverHacia(targetX, targetY, PelotaVisual.UrnaDestino.A);
                pelotasEnMovimiento.add(p);
            }
        }
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }


    private synchronized void actualizarAnimacion() {
        List<PelotaVisual> pelotasLlegaron = new ArrayList<>();
        for (PelotaVisual p : pelotasEnMovimiento) {
            if (!p.actualizarPosicion()) { // Si la pelota llegó (actualizarPosicion devuelve false)
                pelotasLlegaron.add(p);
            }
        }

        for (PelotaVisual pLlegada : pelotasLlegaron) {
            pelotasEnMovimiento.remove(pLlegada);
            if (pLlegada.getUrnaObjetivo() == PelotaVisual.UrnaDestino.A) {
                pelotasEnUrnaA.add(pLlegada);
            } else if (pLlegada.getUrnaObjetivo() == PelotaVisual.UrnaDestino.B) {
                pelotasEnUrnaB.add(pLlegada);
            }
            pLlegada.setUrnaObjetivo(PelotaVisual.UrnaDestino.NINGUNA); // Resetear
        }
         if (pelotasEnMovimiento.isEmpty() && pelotasEnUrnaA.isEmpty() && pelotasEnUrnaB.isEmpty() && m_totalBolas == 0) {
            // No hacer nada o detener timer si no hay pelotas en absoluto y m_totalBolas es 0
         } else if (pelotasEnMovimiento.isEmpty() && (pelotasEnUrnaA.size() + pelotasEnUrnaB.size() == m_totalBolas)) {
            // Si no hay pelotas moviéndose Y todas las pelotas están en las urnas,
            // se podría detener el timer si no se esperan más movimientos automáticos.
            // Por ahora lo dejamos corriendo por si el usuario pide más transiciones.
         }
    }
    
    public int getNumBolasEnA() {
        return pelotasEnUrnaA.size();
    }

    public int getNumBolasEnB() {
        return pelotasEnUrnaB.size();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Asegurar que los rectángulos estén definidos
        if (rectUrnaA == null || rectUrnaB == null) {
             int panelWidth = getWidth();
             int panelHeight = getHeight();
             if (panelWidth == 0 || panelHeight == 0) { // Si el panel aún no tiene tamaño
                 panelWidth = ANCHO_URNA * 2 + ESPACIO_ENTRE_URNAS + MARGEN * 2;
                 panelHeight = ALTO_URNA + MARGEN * 2;
             }
            
            int urnaAY = (panelHeight - ALTO_URNA) / 2;
            int urnaAX = MARGEN;
            rectUrnaA = new Rectangle(urnaAX, urnaAY, ANCHO_URNA, ALTO_URNA);

            int urnaBX = MARGEN + ANCHO_URNA + ESPACIO_ENTRE_URNAS;
            rectUrnaB = new Rectangle(urnaBX, urnaAY, ANCHO_URNA, ALTO_URNA);
        }


        // Dibujar Urna A
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(rectUrnaA.x, rectUrnaA.y, rectUrnaA.width, rectUrnaA.height);
        g2d.drawString("Urna A ("+pelotasEnUrnaA.size()+")", rectUrnaA.x + 5, rectUrnaA.y - 5);

        // Dibujar Urna B
        g2d.drawRect(rectUrnaB.x, rectUrnaB.y, rectUrnaB.width, rectUrnaB.height);
        g2d.drawString("Urna B ("+pelotasEnUrnaB.size()+")", rectUrnaB.x + 5, rectUrnaB.y - 5);

        // Dibujar pelotas
        // Usar copias para evitar ConcurrentModificationException si la lista se modifica mientras se itera
        // Aunque con la sincronización y el timer de Swing, debería ser seguro.
        // Para mayor robustez, se puede iterar sobre copias.
        synchronized(this) { // Sincronizar el acceso a las listas de pelotas
            for (PelotaVisual p : new ArrayList<>(pelotasEnUrnaA)) {
                p.dibujar(g2d);
            }
            for (PelotaVisual p : new ArrayList<>(pelotasEnUrnaB)) {
                p.dibujar(g2d);
            }
            for (PelotaVisual p : new ArrayList<>(pelotasEnMovimiento)) {
                p.dibujar(g2d);
            }
        }
    }
}