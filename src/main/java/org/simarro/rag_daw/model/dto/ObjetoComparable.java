package org.simarro.rag_daw.model.dto;

public class ObjetoComparable<T extends Comparable<T>> implements Comparable<ObjetoComparable<T>> {
    private T atributo;

    // Constructor
    public ObjetoComparable(T atributo) {
        this.atributo = atributo;
    }

    // Getter
    public T getAtributo() {
        return atributo;
    }

    // Setter
    public void setAtributo(T atributo) {
        this.atributo = atributo;
    }

    // Implementación del método compareTo para comparar atributos
    @Override
    public int compareTo(ObjetoComparable<T> otro) {
        if (this.atributo == null && otro.atributo == null) {
            return 0; // Ambos son nulos, son iguales
        } else if (this.atributo == null) {
            return -1; // El actual es nulo, se considera menor
        } else if (otro.atributo == null) {
            return 1; // El otro es nulo, se considera mayor
        }
        // Compara usando el método compareTo del atributo
        return this.atributo.compareTo(otro.getAtributo());
    }

    // Sobrescritura de equals para comparar objetos
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ObjetoComparable<?> that = (ObjetoComparable<?>) obj;
        return atributo != null ? atributo.equals(that.atributo) : that.atributo == null;
    }

    // Sobrescritura de hashCode
    @Override
    public int hashCode() {
        return atributo != null ? atributo.hashCode() : 0;
    }

    // Representación como cadena
    @Override
    public String toString() {
        return "ObjetoComparable{" +
                "atributo=" + atributo +
                '}';
    }
}