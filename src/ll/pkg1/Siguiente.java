/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ll.pkg1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
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
            for (String noTermAux : gSVicio.getNoTerminales()) {
                String[] producciones = gSVicio.getProducciones().get(noTermAux).split(" ");
                for (String produccion : producciones) {
                    if (produccion.contains(noTerminal)) {
                        int indiceNT = produccion.indexOf(noTerminal);
                        int tamañoProd = produccion.length() - 1;
                        if (produccion.contains("'")) {
                            tamañoProd--;
                        }
                        if (tamañoProd > indiceNT) {
                            // S(B) = P(beta) | S(B) = P(beta) U S(A)
                            String beta = produccion.substring(indiceNT + 1, indiceNT + 2);
                            if (esTerminal(beta)) {
                                this.siguientes.get(noTerminal).add(beta);
                            } else {
                                if (produccion.contains("'")) {
                                    if (produccion.substring(indiceNT + 2, indiceNT + 3).equals("'")) {
                                        beta = produccion.substring(indiceNT + 1, produccion.length());
                                    }
                                }
                                Set<String> conjunto = primeros.getPrimeros().get(beta);
                                if (conjunto.contains("&")) {
                                    conjunto.remove("&");
                                    this.siguientes.get(noTerminal).addAll(conjunto);
                                    this.nTSiguientes.get(noTerminal).add(noTermAux);
                                } else {
                                    this.siguientes.get(noTerminal).addAll(conjunto);
                                }
                            }
                        } else {
                            // S(B) = S(A)
                            this.nTSiguientes.get(noTerminal).add(noTermAux);
                        }
                    }
                }
            }
        }
    }

    private void calcularSiguiente(GSVicio gSVicio) {
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

    private boolean esTerminal(String cadena) {
        return Pattern.matches("[A-Z]", cadena) ? false : true;
    }
}
