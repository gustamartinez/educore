package edu.uam.educore.controller;

import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.academico.Seccion;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;
import edu.uam.educore.model.personas.TipoEmpleado;
import java.util.List;
import java.util.Optional;

public class SeccionController {

  private final Repositorio<Seccion> seccionRepo;
  private final Repositorio<Empleado> empleadoRepo;
  private final Repositorio<Estudiante> estudianteRepo;
  private final Repositorio<Edificio> edificioRepo;
  private int proximoId = 1;

  public SeccionController(
      Repositorio<Seccion> seccionRepo,
      Repositorio<Empleado> empleadoRepo,
      Repositorio<Estudiante> estudianteRepo,
      Repositorio<Edificio> edificioRepo) {

    if (seccionRepo == null || empleadoRepo == null || estudianteRepo == null || edificioRepo == null) {
      throw new IllegalArgumentException("Los repositorios son obligatorios.");
    }

    this.seccionRepo = seccionRepo;
    this.empleadoRepo = empleadoRepo;
    this.estudianteRepo = estudianteRepo;
    this.edificioRepo = edificioRepo;
  }

  public Seccion registrar(String codigo, String nombre, int aulaId, int docenteId) throws Exception {
    if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("Código y nombre de la sección son obligatorios.");
    }

    if (aulaId <= 0 || docenteId <= 0) {
      throw new IllegalArgumentException("Los IDs de aula y docente deben ser mayores que cero.");
    }

    for (Seccion s : seccionRepo.buscarTodos()) {
      if (s.getCodigo().equalsIgnoreCase(codigo)) {
        throw new IllegalArgumentException("Ya existe una sección con el código " + codigo + ".");
      }
    }

    Empleado docente = empleadoRepo.buscarPorId(docenteId).orElse(null);

    if (docente == null) {
      throw new IllegalArgumentException("No existe empleado con ID " + docenteId + ".");
    }

    if (docente.getTipoEmpleado() != TipoEmpleado.DOCENTE) {
      throw new IllegalArgumentException("El empleado indicado no es DOCENTE.");
    }

    Aula aula = buscarAulaPorId(aulaId);

    if (aula == null) {
      throw new IllegalArgumentException("No existe aula con ID " + aulaId + ".");
    }

    Seccion seccion = new Seccion(proximoId, codigo, nombre, docente, aula);
    seccionRepo.guardar(seccion);
    proximoId++;

    return seccion;
  }

  public List<Seccion> listar() throws Exception {
    return seccionRepo.buscarTodos();
  }

  public Seccion buscarPorId(int id) throws Exception {
    Optional<Seccion> resultado = seccionRepo.buscarPorId(id);
    return resultado.orElse(null);
  }

  public void agregarEstudiante(int seccionId, int estudianteId) throws Exception {
    if (seccionId <= 0 || estudianteId <= 0) {
      throw new IllegalArgumentException("Los IDs deben ser mayores que cero.");
    }

    Seccion seccion = buscarPorId(seccionId);

    if (seccion == null) {
      throw new IllegalArgumentException("No existe sección con ID " + seccionId + ".");
    }

    Estudiante estudiante = estudianteRepo.buscarPorId(estudianteId).orElse(null);

    if (estudiante == null) {
      throw new IllegalArgumentException("No existe estudiante con ID " + estudianteId + ".");
    }

    for (Estudiante e : seccion.getEstudiantes()) {
      if (e.getId() == estudianteId) {
        throw new IllegalArgumentException("El estudiante ya está inscrito en la sección.");
      }
    }

    seccion.agregarEstudiante(estudiante);
    seccionRepo.actualizar(seccion);
  }

  public void removerEstudiante(int seccionId, int estudianteId) throws Exception {
    if (seccionId <= 0 || estudianteId <= 0) {
      throw new IllegalArgumentException("Los IDs deben ser mayores que cero.");
    }

    Seccion seccion = buscarPorId(seccionId);

    if (seccion == null) {
      throw new IllegalArgumentException("No existe sección con ID " + seccionId + ".");
    }

    boolean removido = seccion.removerEstudiante(estudianteId);

    if (!removido) {
      throw new IllegalArgumentException("El estudiante no está inscrito en la sección.");
    }

    seccionRepo.actualizar(seccion);
  }

  public void eliminar(int id) throws Exception {
    if (id <= 0) {
      throw new IllegalArgumentException("El ID debe ser mayor que cero.");
    }

    Seccion seccion = buscarPorId(id);

    if (seccion == null) {
      throw new IllegalArgumentException("No existe sección con ID " + id + ".");
    }

    if (!seccion.getEstudiantes().isEmpty()) {
      throw new IllegalArgumentException(
          "No se puede eliminar la sección porque tiene estudiantes inscritos.");
    }

    seccionRepo.eliminar(id);
  }

  private Aula buscarAulaPorId(int aulaId) throws Exception {
    for (Edificio edificio : edificioRepo.buscarTodos()) {
      Aula aula = edificio.buscarAulaPorId(aulaId);

      if (aula != null) {
        return aula;
      }
    }

    return null;
  }
}