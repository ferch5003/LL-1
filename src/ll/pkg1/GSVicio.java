/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ll.pkg1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author ferch5003
 */
public class GSVicio {

    private ArrayList<String> terminales;
    private ArrayList<String> noTerminales;
    private String nTInicial;
    private HashMap<String, ArrayList<String>> producciones;

    public GSVicio(Gramatica gramatica) {
        this.terminales = gramatica.getTerminales();
        this.nTInicial = gramatica.getnTInicial();
        this.noTerminales = gramatica.getNoTerminales();
        this.producciones = new HashMap<>();

        gramatica.getProducciones().forEach((noTerminal, producciones) -> {
            if (esRecursivo(noTerminal, producciones)) {
                quitarRecursividad(noTerminal, producciones);
            } else {
                this.producciones.put(noTerminal, producciones);
            }
        });

        llamadaFactorizacion();

        construirTerminales();
    }

    private String asignarNuevoNTerminal() {
        for (char A = 'A'; A <= 'Z'; A++) {
            String noTerminal = Character.toString(A);
            if(!this.noTerminales.contains(noTerminal)){
                return noTerminal;
            }
        }
        return "A";
    }

    private void construirTerminales() {
        this.terminales = new ArrayList<>();
        for (String noTerminal : this.noTerminales) {
            ArrayList<String> gramatica = this.producciones.get(noTerminal);
            for (String expresion : gramatica) {
                String cadenaTerminales = expresion.replaceAll("([A-Z]'*)", "");
                for (int i = 0; i < cadenaTerminales.length(); i++) {
                    String simbolo = cadenaTerminales.substring(i, i + 1);
                    if (!simbolo.equals("&") && !this.terminales.contains(simbolo)) {
                        this.terminales.add(simbolo);
                    }
                }
            }
        }
    }

    private boolean esRecursivo(String noTerminal, ArrayList<String> producciones) {
        int i = 0;
        while (i < producciones.size()) {
            if (producciones.get(i).substring(0, 1).equals(noTerminal)) {
                return true;
            }
            i++;
        }
        return false;
    }

    private void quitarRecursividad(String A, ArrayList<String> producciones) {
        ArrayList<String> alfa = new ArrayList<>();
        ArrayList<String> beta = new ArrayList<>();
        for (String produccion : producciones) {
            if (A.equals(produccion.substring(0, 1))) {
                int tamañoProd = produccion.length();
                alfa.add(produccion.substring(1, tamañoProd));
            } else {
                beta.add(produccion);
            }
        }
        asignarNRecursivos(A, alfa, beta);
    }

    private void asignarNRecursivos(String A, ArrayList<String> alfa, ArrayList<String> beta) {
        String AP = asignarNuevoNTerminal();
        ArrayList<String> noRA = new ArrayList<>();
        for (String produccion : beta) {
            noRA.add(produccion + AP);
        }
        ArrayList<String> noRAP = new ArrayList<>();
        for (String produccion : alfa) {
            noRAP.add(produccion + AP);
        }
        noRAP.add("&");
        this.producciones.put(A, noRA);
        this.producciones.put(AP, noRAP);
        int indiceA = this.noTerminales.indexOf(A);
        this.noTerminales.add(indiceA + 1, AP);
    }

    private void llamadaFactorizacion() {
        boolean factorizado = false;
        for (String noTerminal : this.noTerminales) {
            ArrayList<String> producciones = this.producciones.get(noTerminal);
            Set<Integer> indicesFact = new HashSet<>();
            do {
                indicesFact = esFactorizable(noTerminal, producciones);
                if (!indicesFact.isEmpty()) {
                    factorizar(noTerminal, producciones, indicesFact);
                    factorizado = true;
                    break;
                } else {
                    factorizado = false;
                }
            } while (!indicesFact.isEmpty());
            if (factorizado) {
                break;
            }
        }
        if (factorizado) {
            llamadaFactorizacion();
        }
    }

    private Set<Integer> esFactorizable(String A, ArrayList<String> producciones) {
        Set<Integer> indicesFact = new HashSet<>();
        int i = 0;
        while (i < producciones.size()) {
            indicesFact.add(i);
            for (int j = 0; j < producciones.size(); j++) {
                if (j == i) {
                    continue;
                }
                String primeroI = producciones.get(i).substring(0, 1);
                String primeroJ = producciones.get(j).substring(0, 1);
                if (primeroI.equals(primeroJ)) {
                    indicesFact.add(j);
                }
            }
            if (indicesFact.size() > 1) {
                break;
            } else {
                indicesFact.removeAll(indicesFact);
            }
            i++;
        }
        return indicesFact;
    }

    private void factorizar(String A, ArrayList<String> producciones, Set<Integer> indicesFact) {
        Iterator iter = indicesFact.iterator();
        Integer primeraPos = (Integer) iter.next();
        String primerProd = producciones.get(primeraPos);
        String cadenaMax = "";
        int i = 0;
        boolean cadenaNoTer = true;
        while (cadenaNoTer) {
            int iguales = 0;
            for (Integer indice : indicesFact) {
                if (i >= primerProd.length() || i >= producciones.get(indice).length()) {
                    break;
                }
                if (primerProd.equals(producciones.get(indice))) {
                    continue;
                }
                String compararP = primerProd.substring(0, i + 1);
                String compararS = producciones.get(indice).substring(0, i + 1);
                if (compararP.equals(compararS)) {
                    iguales++;
                }
            }
            if (iguales == indicesFact.size() - 1) {
                cadenaMax = primerProd.substring(0, i + 1);
            } else {
                cadenaNoTer = false;
            }
            i++;
        }
        asignarFactores(A, producciones, indicesFact, cadenaMax);
    }

    private void asignarFactores(String A, ArrayList<String> producciones, Set<Integer> indicesFact, String cadenaMax) {
        String AP = asignarNuevoNTerminal();
        ArrayList<String> prodANueva = new ArrayList<>();
        prodANueva.add(cadenaMax + AP);
        ArrayList<String> prodAPNueva = new ArrayList<>();
        for (int j = 0; j < producciones.size(); j++) {
            if (!indicesFact.contains(j)) {
                prodANueva.add(producciones.get(j));
            } else {
                producciones.set(j, producciones.get(j).replace(cadenaMax, ""));
                if (producciones.get(j).equals("")) {
                    producciones.set(j, "&");
                }
                prodAPNueva.add(producciones.get(j));
            }
        }
        this.producciones.put(A, prodANueva);
        this.producciones.put(AP, prodAPNueva);
        int indiceA = this.noTerminales.indexOf(A);
        this.noTerminales.add(indiceA + 1, AP);
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

    public HashMap<String, ArrayList<String>> getProducciones() {
        return producciones;
    }

}
