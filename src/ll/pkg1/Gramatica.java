/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ll.pkg1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author ferch5003
 */
public class Gramatica {

    private ArrayList<String> terminales;
    private ArrayList<String> noTerminales;
    private String nTInicial;
    private HashMap<String, String> producciones;

    public Gramatica(BufferedReader gramatica) throws FileNotFoundException, IOException {

        this.terminales = new ArrayList<>();
        this.noTerminales = new ArrayList<>();
        this.producciones = new HashMap<>();

        String linea;
        String gramaticaConvertida = "";
        while ((linea = gramatica.readLine()) != null) {
            linea = linea.trim();
            gramaticaConvertida += linea + " ";
        }

        String[] arregloGramatica = gramaticaConvertida.split(" ");

        buscarTerminales(arregloGramatica);
        buscarNoTerminales(arregloGramatica);
        buscarProducciones(arregloGramatica);

        this.nTInicial = buscarNTInicial(arregloGramatica);
    }

    public ArrayList<String> getTerminales() {
        return terminales;
    }

    public ArrayList<String> getNoTerminales() {
        return noTerminales;
    }

    public String getnTInicial() {
        return nTInicial;
    }

    public HashMap<String, String> getProducciones() {
        return producciones;
    }

    private void buscarTerminales(String[] gramatica) {
        for (String linea : gramatica) {
            int indiceProduce = linea.indexOf(">");
            String cadenaTerminales = linea.substring(indiceProduce + 1, linea.length());
            cadenaTerminales = cadenaTerminales.replaceAll("([A-Z])", "");
            for (int i = 0; i < cadenaTerminales.length(); i++) {
                String simbolo = cadenaTerminales.substring(i, i + 1);
                if (!simbolo.equals("&") && !this.terminales.contains(simbolo)) {
                    this.terminales.add(simbolo);
                }
            }
        }
    }

    private void buscarNoTerminales(String[] gramatica) throws IOException {
        for (String linea : gramatica) {
            int indiceNTerminal = linea.indexOf("-");
            String cadenaNTerminales = linea.substring(0, indiceNTerminal);
            if (!this.noTerminales.contains(cadenaNTerminales)) {
                this.noTerminales.add(cadenaNTerminales);
            }
        }
    }

    private void buscarProducciones(String[] gramatica) throws IOException {
        for (String linea : gramatica) {
            String[] expresiones = linea.split("->");
            if (!this.producciones.containsKey(expresiones[0])) {
                this.producciones.put(expresiones[0], expresiones[1]);
            } else {
                String alternancia = this.producciones.get(expresiones[0]) + " " + expresiones[1];
                this.producciones.put(expresiones[0], alternancia);
            }
        }
    }

    private String buscarNTInicial(String[] gramatica) throws IOException {
        String primeraExpresion = gramatica[0];
        int indiceNTerminal = primeraExpresion.indexOf("-");
        String simboloInicio = primeraExpresion.substring(0, indiceNTerminal);
        return simboloInicio;
    }
}
