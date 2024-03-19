package com.example;

public interface BTreeInterface<E extends Comparable<E>> {
    /**
     * Verifica si el árbol está vacío.
     * @return true si el árbol está vacío, false en caso contrario.
     */
    boolean isEmpty();
    /**
     * Inserta una nueva clave en el árbol B.
     * @param newKey La nueva clave que se va a insertar.
     */
    void insert(E newKey);
    // Devuelve una representación en forma de cadena del árbol B
    String toString();
    /**
     * Busca una clave en el árbol B y muestra su ubicación si se encuentra.
     * 
     * @param key La clave que se desea buscar.
     * @return true si la clave se encuentra en el árbol, false de lo contrario.
     */
    boolean search(E key);
    // Elimina una clave específica del árbol B
    void remove(E cl);
    // Verifica si el árbol B es válido según las reglas de un árbol B
    boolean isValidBTree();

}


