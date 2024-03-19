package com.example;



public class App {
    public static void main(String[] args) {
        // Creación de una instancia de BTree con un orden de 5
        BTreeInterface<Integer> tree = new BTree<Integer>(5);
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(60);
        tree.insert(70);
        tree.insert(80);
        tree.insert(90);
        tree.insert(100);
        tree.insert(110);
        tree.insert(120);
        tree.insert(130);
        tree.insert(140);
        tree.insert(150);
        tree.insert(160);
        tree.insert(170);
        tree.insert(180);
        tree.insert(190);
        tree.insert(200);

        System.out.println(tree.toString());
        //System.out.println(tree.search(10))
        //System.out.println(tree.search(200))
        tree.remove(200); // Eliminar nodo que requiere fusión con hermano izquierdo.
        System.out.println("delete 200");
        System.out.println(tree.toString());
        tree.remove(170); // Eliminar nodo hoja
        System.out.println("delete 170");
        System.out.println(tree.toString());
        tree.remove(160); // Eliminar nodo hoja
        System.out.println("delete 160");
        System.out.println(tree.toString());
        tree.remove(140); // Eliminar nodo que requiere fusión con hermano izquierdo y disminuye la altura del arbol
        System.out.println("delete 140");
        System.out.println(tree.toString());
        tree.remove(100); // Eliminar nodo hoja
        System.out.println("delete 100");
        System.out.println(tree.toString());
        tree.remove(80); // Eliminar nodo que causa redistribución de claves con hermano derecho.
        System.out.println("delete 80");
        System.out.println(tree.toString());
        tree.remove(60); // Eliminar nodo que requiere fusión con hermano izquierdo
        System.out.println("delete 60");
        System.out.println(tree.toString());
        tree.remove(30); // Eliminar nodo hoja
        System.out.println("delete 30");
        System.out.println(tree.toString());

    }
}