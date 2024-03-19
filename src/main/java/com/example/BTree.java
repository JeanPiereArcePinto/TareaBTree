package com.example;


import java.util.LinkedList;
import java.util.Queue;


public class BTree<E extends Comparable<E>> implements BTreeInterface<E> {
	private BNode<E> root; // raiz del árbol
	private int orden;

	private boolean up; // Indica si se necesita dividir un nodo durante la inserción
	private BNode<E> nDes; // Almacena el nodo hijo derecho durante la inserción
	private boolean upDelete; //// Indica si se necesita ajustar el árbol después de la eliminación
	// Constructor
	public BTree(int orden) {
		this.orden = orden;
		this.root = null;
	}

	/**
	 * Verifica si el árbol está vacío.
	 * @return true si el árbol está vacío, false en caso contrario.
	 */
	@Override
	public boolean isEmpty() {
		return this.root == null;
	}

	/**
	 * Inserta una nueva clave en el árbol B.
	 * @param newKey La nueva clave que se va a insertar.
	 */
	@Override
	public void insert(E newKey) {
		// Inicializar la bandera de división como falsa
		up = false;
		// Realizar la inserción de la nueva clave y obtener la mediana, si es necesario
		E median = push(root, newKey);

		// Si se ha producido una división en la raíz
		if (up) {
			// Crear un nuevo nodo para ser la nueva raíz
			BNode<E> newRoot = new BNode<>(orden);
			newRoot.count = 1;
			newRoot.keys.set(0, median);
			newRoot.childs.set(0, root);
			newRoot.childs.set(1, nDes);
			// Asignar el padre al antiguo nodo raíz y al nuevo nodo derecho
			if (root != null) {
				root.parentId = newRoot.idNode;
				nDes.parentId = newRoot.idNode;
			}

			// Asignar el nuevo nodo como la raíz del árbol
			newRoot.parentId = -1;
			root = newRoot;
		}
	}

	/**
	 * Inserta una nueva clave en el árbol B.
	 * 
	 * @param currentNode El nodo actual en el que se realizará la inserción.
	 * @param newKey      La nueva clave que se va a insertar.
	 * @return La mediana del nodo actual después de la inserción.
	 */
	private E push(BNode<E> currentNode, E newKey) {
		int[] searchPosition = new int[1]; // Almacena la posición de búsqueda
		E median; // Mediana del nodo

		// Si el nodo actual es nulo, indica que estamos en una hoja y se inserta la
		// clave
		if (currentNode == null) {
			up = true;
			nDes = null; // El nodo hijo derecho es nulo
			return newKey;
		} else {
			boolean foundKey;

			// Buscar la posición donde se insertará la nueva clave en el nodo actual
			foundKey = currentNode.searchNode(newKey, searchPosition);

			// Si la clave ya existe en el nodo, se indica y se aborta la inserción
			if (foundKey) {
				// TOOD:
				System.out.println("Clave duplicada\n");
				up = false;
				return null;
			}

			// Realizar la inserción recursivamente en el nodo hijo correspondiente
			// (Buscamos la hoja)
			median = push(currentNode.childs.get(searchPosition[0]), newKey);

			// Si se ha subido una clave desde un nodo hijo
			if (up) {
				// Si el nodo actual está lleno, dividirlo
				if (currentNode.nodeFull())
					median = dividedNode(currentNode, median, searchPosition[0]);
				else {
					// Insertar la nueva clave en el nodo actual
					up = false;
					putNode(currentNode, median, nDes, searchPosition[0]);
				}
			}

			// Devolver la mediana del nodo actual después de la inserción
			return median;
		}
	}

	/**
	 * Inserta una nueva clave y un nuevo hijo derecho en el nodo actual.
	 * 
	 * @param currentNode El nodo actual donde se realizará la inserción.
	 * @param newKey      La nueva clave que se insertará.
	 * @param rightChild  El nuevo hijo derecho asociado a la nueva clave.
	 * @param position    La posición en la que se insertará la nueva clave y el
	 *                    nuevo hijo derecho.
	 */
	private void putNode(BNode<E> currentNode, E newKey, BNode<E> rightChild, int position) {
		int index;

		// Desplazar las claves y los hijos a la derecha para hacer espacio para la
		// nueva clave y el nuevo hijo derecho
		for (index = currentNode.count - 1; index >= position; index--) {
			currentNode.keys.set(index + 1, currentNode.keys.get(index)); // Desplaza la clave hacia la derecha
			currentNode.childs.set(index + 2, currentNode.childs.get(index + 1)); // Desplaza el hijo hacia la derecha
		}

		// Insertar la nueva clave y el nuevo hijo derecho en las posiciones
		// especificadas
		currentNode.keys.set(position, newKey);
		currentNode.childs.set(position + 1, rightChild);

		if (rightChild != null)
			rightChild.parentId = currentNode.idNode; //Actualiza las id 
		// Incrementar el contador de claves en el nodo actual
		currentNode.count++;
	}

	/**
	 * Crea un nuevo nodo que contendrá las claves y los hijos que se moverán a la
	 * derecha después de la división.
	 * 
	 * @param current        El nodo actual que se va a dividir.
	 * @param medianPosition La posición de la mediana que separa las claves y los
	 *                       hijos que permanecerán en el nodo actual de los que se
	 *                       moverán al nuevo nodo.
	 * @return El nuevo nodo que contiene las claves y los hijos que se moverán a la
	 *         derecha después de la división.
	 */
	private BNode<E> createRightNode(BNode<E> current, int medianPosition) {
		// Se crea un nuevo nodo para almacenar las claves y los hijos que se moverán a
		// la derecha después de la división
		BNode<E> rightNode = new BNode<>(this.orden);

		// Se copian las claves y los hijos al nuevo nodo a partir del medianposition
		for (int i = medianPosition; i < this.orden - 1; i++) {
			rightNode.keys.set(i - medianPosition, current.keys.get(i));
			rightNode.childs.set(i - medianPosition + 1, current.childs.get(i + 1));
			// Limpiar los hijos que ya no se utilizan en el nodo current
			current.childs.set(i + 1, null);
		}

		// Se actualiza el contador de claves en el nuevo nodo
		rightNode.count = (this.orden - 1) - medianPosition;

		return rightNode;
	}

	/**
	 * Ajusta los punteros de los hijos del nodo actual y el nuevo nodo creado
	 * después de la división.
	 * 
	 * @param current   El nodo actual que se ha dividido.
	 * @param rightNode El nuevo nodo que se ha creado para almacenar las claves y
	 *                  los hijos que se moverán a la derecha después de la
	 *                  división.
	 */
	private void adjustChildPointers(BNode<E> current, BNode<E> rightNode) {
		// Se ajustan los punteros de los hijos
		rightNode.childs.set(0, current.childs.get(current.count)); // Se asigna el hijo derecho del nodo actual al nuevo nodo


		if (rightNode.childs.get(0) != null)
			rightNode.childs.get(0).parentId = rightNode.idNode;

		current.childs.set(current.count, null); // Se elimina el hijo trasladado del nodo actual

		for (int i = 0; rightNode.childs.get(i) != null && i <= rightNode.count; i++) {

			rightNode.childs.get(i).parentId = rightNode.idNode; // Actualizar el identificador del padre

		}

		// Si el hijo derecho del nuevo nodo no es nulo, se establece el identificador
		// del nodo padre en el hijo derecho
		if (rightNode.childs.get(0) != null)
			rightNode.childs.get(0).parentId = rightNode.idNode;
	}

	/**
	 * Inserta una nueva clave en el nodo actual o en el nuevo nodo creado después
	 * de la división.
	 * 
	 * @param current        El nodo actual en el que se va a insertar la nueva
	 *                       clave.
	 * @param newKey         La nueva clave que se va a insertar.
	 * @param rightChild     El hijo derecho asociado a la nueva clave.
	 * @param position       La posición en la que se va a insertar la nueva clave.
	 * @param medianPosition La posición de la mediana que separa las claves y los
	 *                       hijos que permanecerán en el nodo actual de los que se
	 *                       moverán al nuevo nodo.
	 */
	private void insertKeyInNode(BNode<E> current, E newKey, BNode<E> rightChild, int position, int medianPosition) {
		// Se inserta la nueva clave en el nodo correspondiente
		if (rightChild != null)
			rightChild.parentId = current.idNode;
		if (position <= this.orden / 2)
			putNode(current, newKey, rightChild, position); // Inserta la nueva clave en el nodo actual
		else
			putNode(nDes, newKey, rightChild, position - medianPosition); // Inserta la nueva clave en el nuevo nodo
	}

	/**
	 * Divide un nodo en dos y maneja la inserción de una nueva clave.
	 * 
	 * @param current  El nodo actual que se va a dividir.
	 * @param newKey   La nueva clave que se va a insertar.
	 * @param position La posición en la que se insertará la nueva clave en el nodo
	 *                 actual.
	 * @return La mediana del nodo actual después de la división.
	 */
	private E dividedNode(BNode<E> current, E newKey, int position) {
		// Se almacena el nodo hijo derecho en nDes
		BNode<E> rightChild = nDes;

		// Se calcula la posición de la mediana, si es verdadero toma la primera parte si no la segunda
		int medianPosition = (position <= this.orden / 2) ? this.orden / 2 : this.orden / 2 + 1;

		// Se crea el nuevo nodo derecho
		nDes = createRightNode(current, medianPosition);
		nDes.parentId = current.parentId;

		// Se actualiza el contador de claves en el nodo actual
		current.count = medianPosition;

		// Se inserta la nueva clave en el nodo correspondiente
		insertKeyInNode(current, newKey, rightChild, position, medianPosition);

		// Se obtiene la mediana del nodo actual después de la división
		E median = current.keys.get(current.count - 1);

		// Se ajustan los punteros de los hijos
		adjustChildPointers(current, nDes);

		// Se decrementa el contador de claves en el nodo actual
		current.count--;

		// Se devuelve la mediana
		return median;
	}

	@Override
	public String toString() {
		if (isEmpty()) {
			return "BTree is empty...";
		} else {
			return writeTree(root);
		}
	}

	private String writeTree(BNode<E> current) {
		if(current.nodeEmpty()) return "Arbol Vacio"; // Verifica si el nodo actual es nulo o si el nodo está vacío
		StringBuilder sb = new StringBuilder(); // Crea un StringBuilder para construir la representación del árbol
		Queue<BNode<E>> queue = new LinkedList<>(); // Crea una cola para recorrer el árbol en orden por niveles
		queue.offer(current);  // Agrega el nodo actual a la cola
		// Encabezado de la tabla
		sb.append("+-------------------------------------------------+\n");
		sb.append("| Id.Nodo |    ClavesNodo    | Id.Padre | Id.Hijos |\n");
		sb.append("+-------------------------------------------------+\n");

		while (!queue.isEmpty()) { // Recorre el árbol en orden por niveles
			BNode<E> node = queue.poll();
			// Agregar fila de la tabla para el nodo actual
			sb.append(String.format("|  %-6d |  %-15s |  %-7s |  %-7s|\n", node.idNode, node.toString(), node.parentId,
					getChildrenIds(node)));

			// Agregar separador de fila
			sb.append("+-------------------------------------------------+\n");

			// Agregar los hijos del nodo actual a la cola
			for (int i = 0; i <= node.count; i++) {
				BNode<E> child = node.childs.get(i);
				if (child != null) {
					queue.offer(child);
				}
			}
		}
		return sb.toString(); // Devuelve la representación en forma de tabla del árbol como una cadena
	}

	/**
	 * Obtiene una cadena que representa los identificadores de los hijos del nodo dado.
	 * @param node El nodo del que se obtienen los identificadores de los hijos.
	 * @return Una cadena que contiene los identificadores de los hijos del nodo, separados por comas y espacios.
	 */

	private String getChildrenIds(BNode<E> node) { 
		StringBuilder sb = new StringBuilder(); // Inicialización del StringBuilder para almacenar los identificadores de los hijos
		sb.append("[");
		for (int i = 0; i <= node.count; i++) { // Iterar sobre los hijos del nodo
			BNode<E> child = node.childs.get(i); // Obtener el hijo en la posición actual
			if (child != null) {  // Verificar si el hijo es nulo
				sb.append(child.idNode); // Agregar el identificador del hijo a la cadena
				if (i < node.count) { // Verificar si no es el último hijo
					sb.append(",");  // Agregar una coma como separador si no es el último hijo
				}
			}
		}
		sb.append("]");
		return sb.toString(); // Devolver la cadena de identificadores de los hijos
	}

	/**
	 * Encuentra el padre del nodo especificado en el árbol B.
	 *
	 * @param current El nodo actual que se está examinando.
	 * @param node    El nodo hijo para el cual se busca el padre.
	 * @return El nodo padre del nodo especificado, o null si no se encuentra.
	 */
	private BNode<E> findParent(BNode<E> current, BNode<E> node) {
		// Si el nodo actual es nulo o contiene al nodo buscado, devuelve el nodo actual
		if (current == null || current.childs.contains(node)) {
			return current;
		}

		// Busca recursivamente en los hijos del nodo actual
		for (BNode<E> child : current.childs) {
			// Llama recursivamente a findParent con el hijo actual y el nodo buscado
			BNode<E> parent = findParent(child, node);

			// Si encuentra el padre, lo devuelve
			if (parent != null) {
				return parent;
			}
		}

		// Si no se encuentra el padre, devuelve null
		return null;
	}

	// Ejercicio 1
	/**
	 * Busca una clave en el árbol B y muestra su ubicación si se encuentra.
	 * 
	 * @param key La clave que se desea buscar.
	 * @return true si la clave se encuentra en el árbol, false de lo contrario.
	 */
	@Override
	public boolean search(E key) {
		// Buscar el nodo que contiene la clave
		BNode<E> node = searchNode(root, key);
		if (node != null) {
			// Buscar la posición de la clave en el nodo
			int position = node.searchKey(key);
			// Mostrar la ubicación de la clave
			System.out.printf("%s se encuentra en el nodo %d en la posición %d\n", key.toString(), node.idNode,
					position);
			return true;
		}
		return false;
	}

	/**
	 * Busca una clave en el árbol B recursivamente.
	 * 
	 * @param current El nodo actual en el que se realiza la búsqueda.
	 * @param key     La clave que se desea buscar.
	 * @return El nodo que contiene la clave si se encuentra, null de lo contrario.
	 */
	private BNode<E> searchNode(BNode<E> current, E key) {
		// Verificar si el nodo actual es nulo
		if (current == null) {
			return null;
		}

		int[] posResult = new int[1];
		// Buscar la clave en el nodo actual
		if (current.searchNode(key, posResult)) {
			return current; // Clave encontrada en el nodo actual
		} else if (current.isLeaf()) {
			return null; // Clave no encontrada en nodo hoja
		} else {
			int position = posResult[0];
			// Descender al hijo correspondiente y continuar la búsqueda
			return searchNode(current.childs.get(position), key);
		}
	}

	// Ejercicio2
	@Override
	public void remove(E cl) {
		upDelete = false; // Indicar si se necesita ajustar el árbol después de la eliminación
		// Iniciar la eliminación recursiva desde la raíz
		BNode<E> result = removeNode(root, cl);
		// Si la raíz quedó vacía después de la eliminación y tiene hijos,
		// actualizar la raíz con el primer hijo
		if (upDelete && result != null && result.count == 0 && result.childs.get(0) != null) {
			root = root.childs.get(0);
			root.parentId = -1; // Actualizar el identificador del padre de la nueva raíz
		}
	}

	private BNode<E> removeNode(BNode<E> current, E cl) {
		// Verificar si el nodo actual es nulo
		if (current == null) {
			System.out.println("La clave no se encuentra en el árbol.");
			return null;
		} else { // Busca la clave que se desea eliminar
			int pos[] = new int[1];
			boolean found = current.searchNode(cl, pos);

			if (found) {
				removeFromNode(current, pos[0]);// Eliminar la clave del nodo actual
				return current;

			} else {
				// Buscar en el hijo correspondiente
				removeNode(current.childs.get(pos[0]), cl);

				// Verificar si el nodo actual es la raíz y si tiene hijos
				if (upDelete && current.count < orden / 2) {
					// Ajustar raiz
					adjustNode(current);
					upDelete = true;

				}

				return current;
			}
		}
	}

	private boolean removeFromNode(BNode<E> current, int pos) {
		boolean result = false;
		// Verificar si el nodo actual es una hoja
		if (current.isLeaf()) {
			current.keys.remove(pos); // Eliminar la clave del nodo actual
			current.keys.add(null); // Añadir un valor nulo al final para mantener el tamaño
			current.count--; // Decrementar el contador de claves en el nodo
			result = true; // La eliminación fue exitosa
			// Verificar si el nodo actual queda por debajo del mínimo permitido de claves después de la eliminación
			if (current.count < orden / 2) {
				// Realizar la fusión o redistribución con los nodos vecinos si es necesario
				adjustNode(current);
			}
		} else {
		// Si no es hoja obtiene el predecesor de la clave a eliminar
			BNode<E> pred = current.childs.get(pos);
			E predKey = getPredecessor(pred);
			current.keys.set(pos, predKey); // Reemplazar la clave a eliminar por el predecesor y eliminar el predecesor
			removeNode(pred, predKey);
			
			if(current.parentId == -1) return result; // en arriba se resuelve la raiz;
		// Verificar si el nodo actual tiene menos claves de las necesarias después de la eliminación
			if (current.count < orden / 2) {
				// Realizar la fusión o redistribución con los nodos vecinos si es necesario
				adjustNode(current);
			}
		}
		return result; // Devolver el resultado de la eliminación
	}

	private void adjustNode(BNode<E> current) {

		// si es root se regresa al método remove y se ajusta
		if(current.equals(root)){
			upDelete = true;
			return;
		}

		BNode<E> parent = findParent(root, current);

		// Encontrar la posición del nodo actual en el padre
		int posInParent = parent.childs.indexOf(current);

		// Intentar redistribuir con el vecino izquierdo
		if (posInParent > 0 && parent.childs.get(posInParent - 1).count > orden / 2) {
			redistributeWithLeftSibling(parent, posInParent);
			upDelete = false;
		}
		// Intentar redistribuir con el vecino derecho
		else if (posInParent < parent.count && parent.childs.get(posInParent + 1).count > orden / 2) {
			redistributeWithRightSibling(parent, posInParent);
			upDelete = false;
		}
		// Fusionar con el vecino izquierdo si es posible
		else if (posInParent > 0) {
			mergeWithLeftSibling(parent, posInParent);
			upDelete = true;
		}
		// Fusionar con el vecino derecho si es posible
		else if (posInParent < parent.count) {
			mergeWithRightSibling(parent, posInParent);
			upDelete = true;
		}
		// Si no hay vecinos disponibles, fusionar con el padre
		else {
			mergeWithParent(parent);
			upDelete = true;
		}
	}

	private void redistributeWithLeftSibling(BNode<E> parent, int posInParent) {
		BNode<E> current = parent.childs.get(posInParent); // Obtiene el nodo actual del padre
		BNode<E> leftSibling = parent.childs.get(posInParent - 1); // Obtiene el hermano izquierdo del padre

		// Mover una clave y un hijo del nodo padre al nodo actual
		current.keys.add(0, parent.keys.get(posInParent - 1)); // Agrega la clave del padre al inicio del nodo actual
		current.keys.remove(current.keys.size() - 1); // Elimina la última clave del nodo actual
		current.childs.add(0, leftSibling.childs.get(leftSibling.count)); // Agrega el hijo del hermano izquierdo al inicio del nodo actual
		current.childs.remove(current.childs.size() - 1); // Elimina el último hijo del nodo actual
		current.count++; // Incrementa el contador de claves en el nodo actual

		// Actualizar la clave en el padre con la última clave del vecino izquierdo
		parent.keys.set(posInParent - 1, leftSibling.keys.get(leftSibling.count - 1));

		// Eliminar la última clave del vecino izquierdo
		leftSibling.keys.remove(leftSibling.count - 1);
		leftSibling.keys.add(null); // Agrega un valor nulo al final para mantener el tamaño
		leftSibling.childs.remove(leftSibling.count); // Elimina el último hijo del hermano izquierdo
		leftSibling.childs.add(null); // Agrega un valor nulo al final para mantener el tamaño
		leftSibling.count--; // Decrementa el contador de claves en el hermano izquierdo
	}

	private void redistributeWithRightSibling(BNode<E> parent, int posInParent) {
		BNode<E> current = parent.childs.get(posInParent); // Obtiene el nodo actual del padre
		BNode<E> rightSibling = parent.childs.get(posInParent + 1); // Obtiene el hermano derecho del padre

		// Mover una clave y un hijo del padre al nodo actual
		// current.keys.add(parent.keys.get(posInParent));
		// current.childs.add(rightSibling.childs.get(0));

		current.keys.set(current.count, parent.keys.get(posInParent)); // Agrega la clave del padre al final del nodo actual
		current.childs.set(current.count + 1, rightSibling.childs.get(0)); // Agrega el hijo del vecino derecho al final del nodo actual

		current.count++; // Incrementa el contador de claves en el nodo actual

		// Actualizar la clave en el padre con la primera clave del vecino derecho
		parent.keys.set(posInParent, rightSibling.keys.get(0));

		// Eliminar la primera clave del vecino derecho
		rightSibling.keys.remove(0);
		rightSibling.keys.add(null); // Agrega un valor nulo al final para mantener el tamaño
		rightSibling.childs.remove(0);
		rightSibling.childs.add(null); // Agrega un valor nulo al final para mantener el tamaño

		rightSibling.count--; // Decrementa el contador de claves en el vecino derecho
	}

	private void mergeWithLeftSibling(BNode<E> parent, int posInParent) {
		BNode<E> current = parent.childs.get(posInParent); // Obtiene el nodo actual del padre
		BNode<E> leftSibling = parent.childs.get(posInParent - 1); // Obtiene el hermano izquierdo del padre

		// Mover la clave del padre al vecino izquierdo //
		// leftSibling.keys.add(parent.keys.get(posInParent - 1));
		leftSibling.keys.set(leftSibling.count, parent.keys.get(posInParent - 1));
		leftSibling.count++;

		// Mover todas las claves y los hijos del nodo actual al vecino izquierdo .//
		for (int i = 0; i < current.count; i++) {
			leftSibling.keys.set(leftSibling.count, current.keys.get(i)); // Agrega las claves del nodo actual al vecino izquierdo
			leftSibling.childs.set(leftSibling.count, current.childs.get(i)); // Agrega los hijos del nodo actual al vecino izquierdo
			// Actualizar parentId en el vecino izquierdo
			if (leftSibling.childs.get(leftSibling.count) != null) {
				leftSibling.childs.get(leftSibling.count).parentId = leftSibling.idNode;
			}

			leftSibling.count++; // Incrementa el contador de claves en el vecino izquierdo
		}
		leftSibling.childs.set(leftSibling.count, current.childs.get(current.count)); // Agrega el último hijo del nodo actual al vecino izquierdo
		// Actualizar parentId en el vecino izquierdo
		if (leftSibling.childs.get(leftSibling.count) != null) {
			leftSibling.childs.get(leftSibling.count).parentId = leftSibling.idNode;
		}

		// Actualizar el padre eliminando la clave y el nodo actual
		parent.keys.remove(posInParent - 1); // Elimina la clave del padre
		parent.keys.add(null); // Agrega un valor nulo al final para mantener el tamaño del arreglo
		parent.childs.remove(posInParent); // Elimina el nodo actual
		parent.childs.add(null); // Agrega un valor nulo al final para mantener el tamaño del arreglo
		parent.count--; // Decrementa el contador de claves en el padre

	}

	private void mergeWithRightSibling(BNode<E> parent, int posInParent) {
		BNode<E> current = parent.childs.get(posInParent); // Obtiene el nodo actual del padre
		BNode<E> rightSibling = parent.childs.get(posInParent + 1); // Obtiene el hermano derecho del padre

		// Mover la clave del padre al nodo actual
		rightSibling.keys.add(0, parent.keys.get(posInParent)); // Agrega la clave del padre al inicio del vecino derecho
		rightSibling.keys.remove(rightSibling.keys.size() - 1); // Elimina la última clave del vecino derecho
		rightSibling.count++; // Incrementa el contador de claves en el vecino derecho

		// Mover todas las claves y los hijos del vecino derecho al nodo actual
		for (int i = 0; i < current.count; i++) {
			rightSibling.keys.add(0, current.keys.get(i)); // Agrega las claves del nodo actual al vecino derecho
			rightSibling.keys.remove(rightSibling.keys.size() - 1); // Elimina la última clave del vecino derecho
			rightSibling.childs.add(0, current.childs.get(i)); // Agrega los hijos del nodo actual al vecino derecho
			rightSibling.childs.remove(rightSibling.childs.size() - 1); // Elimina el último hijo del vecino derecho

			// Actualizar parentId en el nodo actual
			if (rightSibling.childs.get(0) != null) {
				rightSibling.childs.get(0).parentId = rightSibling.idNode;
			}
			rightSibling.count++; // Incrementa el contador de claves en el vecino derecho
		}
		rightSibling.childs.add(0, current.childs.get(current.count)); // Agrega el último hijo del nodo actual al vecino derecho
		rightSibling.childs.remove(rightSibling.childs.size() - 1); // Elimina el último hijo del vecino derecho

		// Actualizar el padre eliminando la clave y el vecino izquierdo
		parent.keys.remove(posInParent); // Elimina la clave del padre
		parent.keys.add(null); // Agrega un valor nulo al final para mantener el tamaño del arreglo
		parent.childs.remove(posInParent); // Elimina el nodo actual
		parent.childs.add(null); // Agrega un valor nulo al final para mantener el tamaño del arreglo

		parent.count--; // Decrementa el contador de claves en el padre

	}

	private void mergeWithParent(BNode<E> parent) {
		// Encontrar la posición del nodo actual en el padre
		int posInParent = parent.childs.indexOf(this);

		// Fusionar con el padre
		mergeWithLeftSibling(parent, posInParent + 1);
	}

	private E getPredecessor(BNode<E> current) {
		// Mientras el nodo actual no sea una hoja, continúa avanzando al hijo derecho
		while (!current.isLeaf()) {
			current = current.childs.get(current.count); // Avanza al hijo derecho
		}
		// Una vez que se llega a una hoja, devuelve la última clave del nodo como predecesor
		return current.keys.get(current.count - 1); 
	}

	private E getSuccessor(BNode<E> current) {
		while (!current.isLeaf()) {
			current = current.childs.get(0);
		}
		return current.keys.get(0);
	}

	@Override
	public boolean isValidBTree() {
		return isValidBTree(root);
	}
	// comprueba si el árbol B cumple con las propiedades de un árbol B válido
	private boolean isValidBTree(BNode<E> node) {
		// Verifica si el nodo actual es nulo
		if (node == null) {
			return true;
		}

		if (node.count > orden - 1) {
			return false;
		}

		if (!node.isLeaf()) {
			for (BNode<E> child : node.childs) {
				if (child != null && !isValidBTree(child)) {
					return false;
				}
			}
		}
		return true;
	}
}