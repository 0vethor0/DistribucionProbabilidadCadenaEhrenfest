/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package distribucionprobabilidadcadenaehrenfest;

import com.formdev.flatlaf.FlatLightLaf;

/**
 *
 * @author Personal
 */
public class DistribucionProbabilidadCadenaEhrenfest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    try {
        FlatLightLaf.setup();
    } catch (Exception ex) {
        System.err.println("Falló con FlatLightLaf en .java");
        ex.printStackTrace();
    }

        
        JFrmDistribucionEhrenfest form = new JFrmDistribucionEhrenfest();
        form.setVisible(true);
    }
    
}
