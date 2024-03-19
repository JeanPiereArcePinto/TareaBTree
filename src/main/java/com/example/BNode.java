package com.example;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
	protected ArrayList<E> keys; // ArrayList que almacena las claves del nodo
	protected ArrayList<BNode<E>> childs; // ArrayList que almacena los nodos hijos
	protected int count; // indica la cantidad de claves del nodo
	protected int idNode; // identificador de cada nodo
	public int parentId; // indica el nodo padre
	private static int nextId = 0; // genera identificadores para los nodos
	private int orden; // estable el orden del arbolB

	public BNode(int orden) {
		// Inicialización del ArrayList de claves
		this.keys = new ArrayList<E>(orden - 2); // Reserva espacio para (orden - 1) claves y uno extra para expansión
		// Inicialización del ArrayList de hijos
		this.childs = new ArrayList<BNode<E>>(orden - 1);
		// Inicialización del contador de claves en 0
		this.count = 0;
		// Asignación de un identificador único al nodo
		this.idNode = nextId++;
		// Asignación del parámetro de orden al campo orden
		this.orden = orden;
		// Ciclo para inicializar las listas de claves y de hijos con valores nulos
		for (int i = 0; i < orden - 1; i++) {
			this.keys.add(null);
			this.childs.add(null);
		}
		// Agrega un nodo hijo adicional para ajustar el tamaño del ArrayList de hijos
		this.childs.add(null);
	}

	// Comprueba si el nodo actual está lleno
	public boolean nodeFull() {
		return this.orden - 1 == count;
	}

	// Comprueba si el nodo actual está vacío
	public boolean nodeEmpty() {
		return this.count == 0;
	}

	// Busca una clave en el nodo actual; si la encuentra, devuelve verdadero y
	// la posición donde se encuentra, de lo contrario, devuelve falso y el
	// posición del hijo donde debe descender para buscar la clave

	public boolean searchNode(E cl, int posresult[]) {
		boolean flag = false; // indica si se encontró una posición para la clave buscada
		posresult[0] = 0; // almacenar la posición de la clave o la posición del hijo donde debería descender la búsqueda
		boolean result = true; //  indica si se ha encontrado la clave en el nodo actual
		// indexOf busca la cl en la lista de keys del nodo actual
		// si la clave se encuentra en el nodo, pos contrendrá su posición en la lista
		int pos = keys.indexOf(cl); 
		// si la clave no se encuentra en el nodo actual
		if (pos == -1) { 
			result = false; // marca el result como falso
			// Itera sobre las claves del nodo para encontrar la posición donde debería estar la clave
			for (int i = 0; i < keys.size(); i++) {
				// Si la clave actual es nula o mayor que la clave buscada
				if (keys.get(i) == null || keys.get(i).compareTo(cl) > 0) { 
					pos = i; // Establece la posición donde debería estar la clave
					flag = true; // Marca que se encontró una posición
					break;
				}
			}
			 // Si no se encontró una posición, la clave debería descender al último hijo del nodo
			if (!flag) {
				pos = childs.size() - 1; // Establece la posición como el último hijo del nodo
			}
		}
		
		posresult[0] = pos; // Guarda la posición de la clave o del hijo
		return result; // Devuelve el resultado de la búsqueda
	}

	// Devolver las claves encontradas en el nodo
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		// sb.append(idNode); // Agregar el idNode al inicio
		// Agrega las claves del nodo separadas por comas
		for (int i = 0; i < count; i++) {
			sb.append(keys.get(i));
			sb.append(", ");
		}
		// elimina el ultimo caracter
		sb.delete(sb.length() <= 2 ? 0 : sb.length() - 2, sb.length());
		sb.append(")");
		return sb.toString();
	}
	// Verifica si el nodo es una hoja
	public boolean isLeaf() {
		// Itera sobre los hijos del nodo
		for (BNode<E> child : childs) {
			if (child != null) {
				return false; // Si hay al menos un hijo no nulo, el nodo no es una hoja
			}
		}
		return true; // El nodo es una hoja si todos los hijos son nulos
	}

	/**
	 * Busca una clave en el nodo.
	 * 
	 * @param key La clave que se desea buscar.
	 * @return La posición de la clave si se encuentra en el nodo, de lo contrario,
	 *         devuelve -1.
	 */
	public int searchKey(E key) {
		// Itera sobre las claves del nodo
		for (int i = 0; i < count; i++) {
			if (keys.get(i).equals(key)) {
				return i; // Devuelve la posición de la clave si se encuentra
			}
		}
		return -1; // Devuelve -1 si la clave no se encuentra en el nodo
	}

}