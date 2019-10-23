/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ll.pkg1;

import java.util.HashMap;

/**
 *
 * @author ferch5003
 */
public class TablaM {

    private HashMap<String, HashMap<String, String>> tablaM;

    public HashMap<String, HashMap<String, String>> getTablaM() {
        return tablaM;
    }

    public TablaM(GSVicio gSVicio, Primero primeros, Siguiente siguientes) {
        this.tablaM = new HashMap<>();

        construirTablaM(gSVicio);

        calcularTablaM(primeros, siguientes);
    }

    private void construirTablaM(GSVicio gSVicio) {
        for (String noTerminal : gSVicio.getNoTerminales()) {
            this.tablaM.put(noTerminal, new HashMap<>());
            for (String terminal : gSVicio.getTerminales()) {
                this.tablaM.get(noTerminal).put(terminal, "");
            }
            this.tablaM.get(noTerminal).put("$", "");
        }
    }

    private void calcularTablaM(Primero primeros, Siguiente siguientes) {
        primeros.getPrimeros().forEach((noTerminal, terminales) -> {
            for (String terminal : terminales) {
                primeros.getValoresM().get(noTerminal).forEach((produccion, produce) -> {
                    if (produce.contains("&")) {
                        for (String simbolo : siguientes.getSiguientes().get(noTerminal)) {
                            String valor = noTerminal + "->" + produccion;
                            this.tablaM.get(noTerminal).put(simbolo, valor);
                        }
                    } else {
                        if (!produccion.equals("&")) {
                            if (produce.contains(terminal)) {
                                String valor = noTerminal + "->" + produccion;
                                this.tablaM.get(noTerminal).put(terminal, valor);
                            }
                        } else {
                            for (String vacio : siguientes.getSiguientes().get(noTerminal)) {
                                String valor = noTerminal + "->&";
                                this.tablaM.get(noTerminal).put(vacio, valor);
                            }
                        }
                    }
                });
            }
        });
    }
}
