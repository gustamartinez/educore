package edu.uam.educore.model.academico;

import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;
import java.util.ArrayList;
import java.util.List;

public class Seccion {

    private int id;
    private String codigo;
    private String nombre;
    private Empleado docente;
    private Aula aula;
    private List<Estudiante> estudiantes;

    public Seccion(int id, String codigo, String nombre, Empleado docente, Aula aula) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Código y nombre de la sección son obligatorios.");
        }

        if (docente == null) {
            throw new IllegalArgumentException("El docente es obligatorio.");
        }

        if (aula == null) {
            throw new IllegalArgumentException("El aula es obligatoria.");
        }

        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.docente = docente;
        this.aula = aula;
        this.estudiantes = new ArrayList<>();
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

    public Empleado getDocente() {
        return docente;
    }

    public Aula getAula() {
        return aula;
    }

    public List<Estudiante> getEstudiantes() {
        return new ArrayList<>(estudiantes);
    }

    public void agregarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante es obligatorio.");
        }

        for (Estudiante e : estudiantes) {
            if (e.getId() == estudiante.getId()) {
                throw new IllegalArgumentException("El estudiante ya está inscrito en la sección.");
            }
        }

        if (estudiantes.size() >= aula.getCapacidad()) {
            throw new IllegalArgumentException("No hay cupo disponible en el aula.");
        }

        estudiantes.add(estudiante);
    }

    public boolean removerEstudiante(int estudianteId) {
        return estudiantes.removeIf(estudiante -> estudiante.getId() == estudianteId);
    }

    public String getInfo() {
        return String.format(
                "Sección %s - %s | Aula: %s | Docente: %s %s | Estudiantes: %d",
                codigo,
                nombre,
                aula.getNumero(),
                docente.getNombre(),
                docente.getApellidos(),
                estudiantes.size());
    }
}