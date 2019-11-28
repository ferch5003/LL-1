# LL-1

Este trabajo se encarga de hacer el algoritmo  LL(1), tanto quitarle los vicios a una gramatica independiente del contexto (Recursiva a la izquierda y Factorizable por la izquierda), como hallar los conjuntos PRIMEROS y SIGUIENTES de la gramatica, y obtener su tabla M (tabla de análisis sintáctico).

Nota: La tabla M puede que no funcione para cualquier gramatica, ya que la tabla puede tener dos entradas en la misma tabla

Una gramatica es LL(1) si:

Dada la gramatica

A->αi|αi+1|αi+2|...|αn

1. No tiene recursividad a la izquierda
2. PRIMERO(αi) ∩ PRIMERO(αj) = ∅ para todo i ≠ j (No es factorizable por la izquierda)
3. Si αi ⇒ *ε entonces:
    3.a.
        αj ⇒*ε para todo i ≠ j
    3.b.
        PRIMERO(αj) ∩ SIGUIENTE(A) = ∅ para todo i≠j

# Funcionamiento

El proyecto se inicializa en la clase LL1.java que se encuentra en ll.pkg1, se puede usar de manera grafica, o se puede usar desde la consola:

    1. Opción grafica
    2. Consola

Si se escoge la opción consola se debe usar la instancia de la clase Ll1.java.

# Uso

Se puede pasar a la instancia Ll1 un String o un File, en el cual se debe organizar de esta manera:

String

    "E->E+T\n"
    + "E->T\n"
    + "T->T*F\n"
    + "T->F\n"
    + "F->i\n"
    + "F->(E)";

File (Archivo .txt o un tipo de documento de texto sencillo)

    E->E+T
    E->T
    T->T*F
    T->F
    F->i
    F->(E)

Las salidas seran respectivamente:

    1. Gramatica original
    2. Gramatica sin vicios
    3. Conjunto de PRIMEROS
    4. Conjunto de SIGUIENTES
    5. Tabla de análisis sintáctico

Y ademas se dara la opción de poder verificar una cadena.