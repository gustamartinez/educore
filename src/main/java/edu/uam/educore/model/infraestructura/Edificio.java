package edu.uam.educore.model.infraestructura;

import java.util.ArrayList;
import java.util.List;

public class Edificio {

    private int id;
    private String codigo;
    private String nombre;
    private List<Aula> aulas;

    public Edificio(int id, String codigo, String nombre) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Código y nombre del edificio son obligatorios.");
        }

        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.aulas = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Aula> getAulas() {
        return new ArrayList<>(aulas);
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del edificio es obligatorio.");
        }
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del edificio es obligatorio.");
        }
        this.nombre = nombre;
    }

    public void agregarAula(Aula aula) {
        if (aula == null) {
            throw new IllegalArgumentException("El aula es obligatoria.");
        }

        for (Aula a : aulas) {
            if (a.getNumero().equalsIgnoreCase(aula.getNumero())) {
                throw new IllegalArgumentException("Ya existe un aula con ese número en el edificio.");
            }
        }

        aulas.add(aula);
    }

    public boolean eliminarAula(int aulaId) {
        return aulas.removeIf(aula -> aula.getId() == aulaId);
    }

    public Aula buscarAulaPorNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            return null;
        }

        for (Aula aula : aulas) {
            if (aula.getNumero().equalsIgnoreCase(numero)) {
                return aula;
            }
        }
        return null;
    }

    public Aula buscarAulaPorId(int id) {
        for (Aula aula : aulas) {
            if (aula.getId() == id) {
                return aula;
            }
        }
        return null;
    }

    public String getInfo() {
        return String.format(
                "Edificio %s - %s | Aulas: %d",
                codigo,
                nombre,
                aulas.size());
    }
}