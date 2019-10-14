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
public class Primero {

    private HashMap<String, Set<String>> primeros;
    private HashMap<String, String> producciones;
    private ArrayList<String> noTerminales;
    private HashMap<String, Set<String>> nTPrimeros;

    public Primero(GSVicio gSVicio) {
        this.primeros = new HashMap<>();
        this.nTPrimeros = new HashMap<>();
        this.producciones = gSVicio.getProducciones();
        this.noTerminales = gSVicio.getNoTerminales();

        construirCiclos(gSVicio);

        construirProducciones(gSVicio);

        construirPrimero(gSVicio);

        verifTerceraRegla();

        verifCiclos();
    }

    public ArrayList<String> getNoTerminales() {
        return noTerminales;
    }

    public HashMap<String, Set<String>> getPrimeros() {
        return primeros;
    }

    public HashMap<String, String> getProducciones() {
        return producciones;
    }

    private void construirCiclos(GSVicio gSVicio) {
        for (String noTerminal : gSVicio.getNoTerminales()) {
            this.nTPrimeros.put(noTerminal, new HashSet<>());
        }
    }

    private void construirProducciones(GSVicio gSVicio) {
        for (String noTerminal : gSVicio.getNoTerminales()) {
            this.primeros.put(noTerminal, new HashSet<>());
            for (String noTermAux : gSVicio.getNoTerminales()) {
                String[] producciones = gSVicio.getProducciones().get(noTermAux).split(" ");
                for (String produccion : producciones) {
                    String primeraPos = produccion.substring(0, 1);
                    if (primeraPos.equals(noTerminal)) {
                        String[] prodB = gSVicio.getProducciones().get(noTerminal).split(" ");
                        for (String simbolo : prodB) {
                            if (simbolo.substring(0, 1).equals(noTermAux)) {
                                this.nTPrimeros.get(noTerminal).add(noTermAux);
                                this.nTPrimeros.get(noTermAux).add(noTerminal);
                            }
                        }
                    }
                }
            }
        }
    }

    private void construirPrimero(GSVicio gSVicio) {
        for (String noTerminal : gSVicio.getNoTerminales()) {
            String produccion = gSVicio.getProducciones().get(noTerminal);
            String[] producciones = produccion.split(" ");
            Queue<String> colaProd = new LinkedList<>();
            for (String prod : producciones) {
                colaProd.add(prod);
            }
            calcularPrimero(noTerminal, colaProd);
        }
    }

    private void verifTerceraRegla() {
        this.producciones.forEach((k, v) -> {
            int epsilon = 0;
            if (!esTerminal(v.substring(0, 1))) {
                for (char a : v.toCharArray()) {
                    String simbolo = Character.toString(a);
                    if (!esTerminal(simbolo)) {
                        if (this.primeros.get(simbolo).contains("&")) {
                            epsilon++;
                        }
                    }
                }
                if (epsilon < v.length()) {
                    this.primeros.get(k).remove("&");
                }
            }
        });
    }

    private void verifCiclos() {
        this.nTPrimeros.forEach((noTerminal, ciclo) -> {
            Set<String> union = new HashSet<>();
            Set<String> A = this.primeros.get(noTerminal);
            union.addAll(A);
            for (String noTerm : ciclo) {
                Set<String> B = this.primeros.get(noTerm);
                union.addAll(B);
            }
            for (String noTerm : ciclo) {
                this.primeros.get(noTerm).addAll(union);
            }
            this.primeros.get(noTerminal).addAll(union);
        });
    }

    private void calcularPrimero(String A, Queue<String> producciones) {
        if (!producciones.isEmpty()) {
            String primeraCad = producciones.peek().substring(0, 1);
            if (esTerminal(primeraCad)) {
                primeraRegla(A, primeraCad);
                producciones.remove();
            } else {
                segundaRegla(A, primeraCad, producciones, producciones.peek());
                producciones.remove();
            }
            calcularPrimero(A, producciones);
        }
    }

    private void primeraRegla(String A, String primeraCad) {
        this.primeros.get(A).add(primeraCad);
    }

    private void segundaRegla(String A, String primeraCad, Queue<String> producciones, String produccion) {
        if (!this.producciones.get(primeraCad).isEmpty()) {
            String[] prodB = this.producciones.get(primeraCad).split(" ");
            Queue<String> colaProd = new LinkedList<>();
            for (String prod : prodB) {
                String primeraPos = prod.substring(0, 1);
                if (esTerminal(primeraPos)) {
                    colaProd.add(prod);
                } else if (!this.nTPrimeros.get(primeraPos).contains(primeraCad)) {
                    colaProd.add(prod);
                }
            }
            if (!colaProd.contains("&")) {
                calcularPrimero(primeraCad, colaProd);
                Set<String> B = this.primeros.get(primeraCad);
                this.primeros.get(A).addAll(B);
            } else {
                terceraRegla(A, produccion);
            }
        }
    }

    private void terceraRegla(String A, String produccion) {
        if (produccion.length() > 1) {
            for (char a : produccion.toCharArray()) {
                String simbolo = Character.toString(a);
                if (esTerminal(simbolo)) {
                    primeraRegla(A, simbolo);
                } else {
                    String[] prodC = this.producciones.get(simbolo).split(" ");
                    Queue<String> colaProdB = new LinkedList<>();
                    for (String prod : prodC) {
                        String primeraPos = prod.substring(0, 1);
                        if (esTerminal(primeraPos)) {
                            colaProdB.add(prod);
                        } else if (!this.nTPrimeros.get(primeraPos).contains(simbolo)) {
                            colaProdB.add(prod);
                        }
                    }
                    calcularPrimero(simbolo, colaProdB);
                    Set<String> B = this.primeros.get(simbolo);
                    this.primeros.get(A).addAll(B);
                }
            }
        }
    }

    private boolean esTerminal(String cadena) {
        return Pattern.matches("[A-Z]", cadena) ? false : true;
    }
}
