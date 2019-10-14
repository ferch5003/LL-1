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
        this.producciones = new HashMap<>();
        this.noTerminales = gSVicio.getNoTerminales();

        gSVicio.getProducciones().forEach((noTerminal, producciones) -> {
            this.producciones.put(noTerminal, producciones);
        });

        construirNTPrimeros(gSVicio);

        construirProducciones(gSVicio);

        construirPrimero(gSVicio);

        verifCiclos();

        verifTerceraRegla(gSVicio);
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

    private void construirNTPrimeros(GSVicio gSVicio) {
        for (String noTerminal : gSVicio.getNoTerminales()) {
            this.nTPrimeros.put(noTerminal, new HashSet<>());
        }
    }

    private void construirProducciones(GSVicio gSVicio) {
        for (String noTerminal : gSVicio.getNoTerminales()) {
            this.primeros.put(noTerminal, new HashSet<>());
            String[] producciones = gSVicio.getProducciones().get(noTerminal).split(" ");
            for (String produccion : producciones) {
                String simbolo = produccion.substring(0, 1);
                if (!esTerminal(simbolo)) {
                    String nProd = "";
                    String[] aProd = this.producciones.get(noTerminal).split(" ");
                    for (String prod : aProd) {
                        if (!simbolo.equals(prod.substring(0, 1))) {
                            nProd += prod + " ";
                        }
                    }
                    nProd = nProd.trim();
                    if (this.producciones.get(simbolo).contains("&")) {
                        for (char s : produccion.toCharArray()) {
                            String simb = Character.toString(s);
                            this.nTPrimeros.get(noTerminal).add(simb);
                        }
                    } else {
                        this.nTPrimeros.get(noTerminal).add(simbolo);
                    }
                    this.producciones.put(noTerminal, nProd);
                }
            }
        }
    }

    private void construirPrimero(GSVicio gSVicio) {
        for (String noTerminal : gSVicio.getNoTerminales()) {
            String[] producciones = gSVicio.getProducciones().get(noTerminal).split(" ");
            Queue<String> colaProd = new LinkedList<>();
            for (String prod : producciones) {
                if (!prod.isEmpty()) {
                    colaProd.add(prod);
                }
            }
            calcularPrimero(noTerminal, colaProd);
        }
    }

    private void verifTerceraRegla(GSVicio gSVicio) {
        gSVicio.getProducciones().forEach((noTerminal, producciones) -> {
            int epsilon = 0;
            if (!producciones.isEmpty()) {
                if (!esTerminal(producciones.substring(0, 1))) {
                    for (char a : producciones.toCharArray()) {
                        String simbolo = Character.toString(a);
                        if (!esTerminal(simbolo)) {
                            if (this.primeros.get(simbolo).contains("&")) {
                                epsilon++;
                            }
                        }
                    }
                    if (epsilon < producciones.length()) {
                        this.primeros.get(noTerminal).remove("&");
                    }
                }
            }
        });
    }

    private void verifCiclos() {
        int ultimaPos = this.getNoTerminales().size() - 1;
        for (int i = ultimaPos; i >= 0; i--) {
            String noTerminal = this.noTerminales.get(i);
            Set<String> ciclo = this.nTPrimeros.get(noTerminal);
            Set<String> union = new HashSet<>();
            Set<String> A = this.primeros.get(noTerminal);
            union.addAll(A);
            for (String simbolo : ciclo) {
                if (esTerminal(simbolo)) {
                    primeraRegla(noTerminal, simbolo);
                } else {
                    Set<String> B = this.primeros.get(simbolo);
                    union.addAll(B);
                }
            }
            this.primeros.get(noTerminal).addAll(union);
        }
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
            }
        }
    }

    private boolean esTerminal(String cadena) {
        return Pattern.matches("[A-Z]", cadena) ? false : true;
    }
}
