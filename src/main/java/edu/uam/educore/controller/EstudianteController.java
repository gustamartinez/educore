package edu.uam.educore.controller;

import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.personas.*;
import edu.uam.educore.util.Validador;
import java.util.List;
import java.util.Optional;
//prueba git
public class EstudianteController {

  private final Repositorio<Estudiante> repo;
  private int proximoId = 1;

  public EstudianteController(Repositorio<Estudiante> repo) {
    if (repo == null) {
      throw new IllegalArgumentException("El repositorio de estudiantes es obligatorio.");
    }
    this.repo = repo;
  }

  public Estudiante registrarRegular(String nombre, String apellidos, String email, String carnet)
      throws Exception {
    validarBase(nombre, apellidos, email, carnet);
    validarDatosUnicos(email, carnet, 0);

    EstudianteRegular e = new EstudianteRegular(proximoId, nombre, apellidos, email, carnet);
    repo.guardar(e);
    proximoId++;
    return e;
  }

  public Estudiante registrarBecado(
      String nombre, String apellidos, String email, String carnet, double porcentajeBeca)
      throws Exception {
    validarBase(nombre, apellidos, email, carnet);
    validarDatosUnicos(email, carnet, 0);

    if (!Validador.validarPorcentajeBeca(porcentajeBeca)) {
      throw new IllegalArgumentException("Porcentaje de beca inválido (debe ser entre 0.0 y 1.0).");
    }

    EstudianteBecado e =
        new EstudianteBecado(proximoId, nombre, apellidos, email, carnet, porcentajeBeca);
    repo.guardar(e);
    proximoId++;
    return e;
  }

  public List<Estudiante> listar() throws Exception {
    return repo.buscarTodos();
  }

  public Estudiante buscarPorId(int id) throws Exception {
    Optional<Estudiante> resultado = repo.buscarPorId(id);
    return resultado.orElse(null);
  }

  public Estudiante actualizar(int id, String nombre, String apellidos, String email, String carnet)
      throws Exception {
    Estudiante e = buscarPorId(id);

    if (e == null) {
      throw new IllegalArgumentException("No existe estudiante con ID " + id + ".");
    }

    validarBase(nombre, apellidos, email, carnet);
    validarDatosUnicos(email, carnet, id);

    e.setNombre(nombre);
    e.setApellidos(apellidos);
    e.setEmail(email);
    e.setCarnet(carnet);

    repo.actualizar(e);
    return e;
  }

  public void eliminar(int id) throws Exception {
    Estudiante e = buscarPorId(id);

    if (e == null) {
      throw new IllegalArgumentException("No existe estudiante con ID " + id + ".");
    }

    repo.eliminar(id);
  }

  private void validarBase(String nombre, String apellidos, String email, String carnet) {
    if (nombre == null || nombre.isBlank()
        || apellidos == null || apellidos.isBlank()
        || carnet == null || carnet.isBlank()) {
      throw new IllegalArgumentException("Nombre, apellidos y carnet son obligatorios.");
    }

    if (!Validador.validarEmail(email)) {
      throw new IllegalArgumentException("Email inválido (debe contener '@' y '.').");
    }
  }

  private void validarDatosUnicos(String email, String carnet, int idActual) throws Exception {
    for (Estudiante e : repo.buscarTodos()) {
      if (e.getEmail().equalsIgnoreCase(email) && e.getId() != idActual) {
        throw new IllegalArgumentException("Ya existe un estudiante con el email " + email + ".");
      }

      if (e.getCarnet().equalsIgnoreCase(carnet) && e.getId() != idActual) {
        throw new IllegalArgumentException("Ya existe un estudiante con el carné " + carnet + ".");
      }
    }
  }
}