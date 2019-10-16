/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ll.pkg1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author ferch5003
 */
public class Siguiente {

    private HashMap<String, Set<String>> siguientes;
    private HashMap<String, String> producciones;
    private ArrayList<String> noTerminales;
    private HashMap<String, Set<String>> nTSiguientes;

    public Siguiente(GSVicio gSVicio, Primero primeros) {
        this.siguientes = new HashMap<>();
        this.nTSiguientes = new HashMap<>();
        this.producciones = gSVicio.getProducciones();
        this.noTerminales = gSVicio.getNoTerminales();

        construirNTSiguientes(gSVicio);

        construirSiguiente(gSVicio, primeros);

        calcularSiguiente(gSVicio);
    }

    public HashMap<String, Set<String>> getSiguientes() {
        return siguientes;
    }

    private Set<String> construirProducciones(String produccion, String B, String A, Primero primeros) {
        Set<String> prodConstr = new HashSet<>();
        while (produccion.contains(B)) {
            produccion = produccion.substring(produccion.indexOf(B) + 1, produccion.length());
            if (produccion.equals("")) {
                this.nTSiguientes.get(B).add(A);
                break;
            }
            prodConstr.add(produccion);
        }
        return prodConstr;
    }

    private void construirNTSiguientes(GSVicio gSVicio) {
        for (String noTerminal : gSVicio.getNoTerminales()) {
            this.nTSiguientes.put(noTerminal, new HashSet<>());
            this.siguientes.put(noTerminal, new HashSet<>());
        }
        String estadoInicial = gSVicio.getNoTerminales().get(0);
        this.siguientes.get(estadoInicial).add("$");
    }

    private void construirSiguiente(GSVicio gSVicio, Primero primeros) {
        for (String noTerminal : gSVicio.getNoTerminales()) {
            System.out.println("No term: " + noTerminal);
            for (String noTermAux : gSVicio.getNoTerminales()) {
                String[] producciones = gSVicio.getProducciones().get(noTermAux).split(" ");
                for (String produccion : producciones) {
                    Set<String> prodConstr = construirProducciones(produccion, noTerminal, noTermAux, primeros);
                    if (!prodConstr.isEmpty()) {
                        for (String prod : prodConstr) {
                            for (int i = 0; i < prod.length(); i++) {
                                String simbolo = prod.substring(i, i + 1);
                                if (esTerminal(simbolo)) {
                                    this.siguientes.get(noTerminal).add(simbolo);
                                    break;
                                } else {
                                    Set<String> conjPrimero = primeros.getPrimeros().get(simbolo);
                                    if (!conjPrimero.contains("&")) {
                                        this.siguientes.get(noTerminal).addAll(conjPrimero);
                                    } else {
                                        System.out.println("simbolo: " + simbolo);
                                        if (prod.length() == 1) {
                                            this.siguientes.get(noTerminal).addAll(conjPrimero);
                                            this.nTSiguientes.get(noTerminal).add(noTermAux);
                                        } else {
                                            this.siguientes.get(noTerminal).addAll(conjPrimero);
                                        }
                                        this.siguientes.get(noTerminal).remove("&");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void calcularSiguiente(GSVicio gSVicio) {
        for (int j = 0; j < 2; j++) {
            for (String noTerminal : gSVicio.getNoTerminales()) {
                Set<String> ciclo = this.nTSiguientes.get(noTerminal);
                Set<String> union = new HashSet<>();
                Set<String> A = this.siguientes.get(noTerminal);
                union.addAll(A);
                for (String simbolo : ciclo) {
                    Set<String> B = this.siguientes.get(simbolo);
                    union.addAll(B);
                }
                this.siguientes.get(noTerminal).addAll(union);
            }
        }
    }

    private boolean esTerminal(String cadena) {
        return Pattern.matches("[A-Z]", cadena) ? false : true;
    }
}
