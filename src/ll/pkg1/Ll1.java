/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ll.pkg1;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 *
 * @author ferch5003
 */
public class Ll1 {

    private Gramatica gramatica;
    private GSVicio gSVicio;
    private Primero primeros;
    private Siguiente siguientes;
    private TablaM tablaM;

    public Ll1(String gramCad) throws IOException {
        this.gramatica = new Gramatica(gramCad);
        this.gSVicio = new GSVicio(gramatica);
        this.primeros = new Primero(gSVicio);
        this.siguientes = new Siguiente(gSVicio, primeros);
        this.tablaM = new TablaM(gSVicio, primeros, siguientes);
    }

    public Ll1(File gramArc) throws IOException {
        this.gramatica = new Gramatica(gramArc);
        this.gSVicio = new GSVicio(gramatica);
        this.primeros = new Primero(gSVicio);
        this.siguientes = new Siguiente(gSVicio, primeros);
        this.tablaM = new TablaM(gSVicio, primeros, siguientes);
    }

    public void resultados() {
        this.gramatica.resultados();
        this.gSVicio.resultados();
        this.primeros.resultados();
        this.siguientes.resultados();
        this.tablaM.resultados();
    }

    private boolean esTerminal(String cadena) {
        return Pattern.matches("[A-Z]", cadena) ? false : true;
    }

    public String getStack(Stack<String> S) {
        Iterator value = S.iterator();
        String m = "";
        while (value.hasNext()) {
            m = m + value.next();
        }
        return m;
    }

    private String espaciador(String stack, String check, String Prod) {
        String imprimir = stack;
        for (int i = 0; i < 40 - stack.length(); i++) {
            imprimir += " ";
        }
        imprimir += check;
        for (int i = 0; i < 40 - check.length(); i++) {
            imprimir += " ";
        }
        imprimir += Prod;
        return imprimir;
    }

    public void verificarCadena(String cadena) {

        String check = cadena + "$";
        check = check.replaceAll("&", "");

        Stack<String> stack = new Stack<String>();
        stack.push("$");
        stack.push(this.gSVicio.getnTInicial());
        String X, a;
        System.out.println("Pila                                    "
                + "Entrada                                 "
                + "Salida");
        String imprimir;
        do {
            a = check.charAt(0) + "";
            X = stack.peek();
            if (esTerminal(X + "") || X.equals('$')) {
                if (X.equals(a)) {
                    imprimir = espaciador(getStack(stack), check, " ");
                    System.out.println(imprimir);
                    stack.pop();
                    check = check.substring(1);
                } else {
                    imprimir = espaciador(getStack(stack), check, "Error");
                    System.out.println(imprimir);
                    break;
                }
            } else {
                HashMap<String, String> Hash = tablaM.getTablaM().get(X + "");
                String Prod = Hash.get(a + "");
                if (Prod == null) {
                    imprimir = espaciador(getStack(stack), check, "Error");
                    System.out.println(imprimir);
                    break;
                }
                imprimir = espaciador(getStack(stack), check, Prod);
                System.out.println(imprimir);
                if (!Prod.equals("")) {
                    String f = Prod.charAt(0) + "";
                    if (f.equals(X) || (f.concat("'").equals(X))) {
                        stack.pop();
                        String p;
                        String h = Prod.charAt(1) + "";
                        if (h.equals("'")) {
                            p = Prod.substring(4);
                        } else {
                            p = Prod.substring(3);
                        }
                        boolean sw = true;
                        if (!p.equals("&")) {
                            for (int i = p.length() - 1; i >= 0; i--) {
                                String comp = p.charAt(i) + "";
                                if (comp.equals("'")) {
                                    sw = false;
                                } else if (sw == false) {
                                    stack.push(p.charAt(i) + "'");
                                    sw = true;
                                } else {
                                    stack.push(p.charAt(i) + "");
                                }
                            }
                        }
                    } else {
                        imprimir = espaciador(getStack(stack), check, "Error");
                        System.out.println(imprimir);
                        break;
                    }
                } else {
                    imprimir = espaciador(getStack(stack), check, "Error");
                    System.out.println(imprimir);
                    break;
                }
            }
        } while (!X.equals("$"));

        if (X.equals("$") && stack.empty()) {
            System.out.println("Cadena aceptada\n");
        }
    }

}
