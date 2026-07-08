package edu.uam.educore.view;

import edu.uam.educore.controller.SeccionController;
import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.academico.Seccion;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;
import java.util.List;
import java.util.Scanner;

public class SeccionView extends VistaBase {

  private final SeccionController controller;

  public SeccionView(
      Scanner scanner,
      Repositorio<Seccion> seccionRepo,
      Repositorio<Empleado> empleadoRepo,
      Repositorio<Estudiante> estudianteRepo,
      Repositorio<Edificio> edificioRepo) {
    super(scanner);
    this.controller = new SeccionController(seccionRepo, empleadoRepo, estudianteRepo, edificioRepo);
  }

  public void iniciar() {
    boolean volver = false;

    while (!volver) {
      System.out.println("\n--- GESTIÓN DE SECCIONES ---");
      System.out.println("1. Registrar sección");
      System.out.println("2. Listar secciones");
      System.out.println("3. Agregar estudiante a sección");
      System.out.println("4. Remover estudiante de sección");
      System.out.println("5. Eliminar sección");
      System.out.println("0. Volver");
      System.out.print("Seleccione una opción: ");

      int opcion = leerEntero();

      switch (opcion) {
        case 1 -> registrar();
        case 2 -> listar();
        case 3 -> agregarEstudiante();
        case 4 -> removerEstudiante();
        case 5 -> eliminar();
        case 0 -> volver = true;
        default -> mostrarError("Opción inválida.");
      }
    }
  }

  private void registrar() {
    try {
      String codigo = leerTexto("Código de la sección: ");
      String nombre = leerTexto("Nombre del curso: ");
      int aulaId = leerEntero("ID del aula: ");
      int docenteId = leerEntero("ID del docente: ");

      Seccion seccion = controller.registrar(codigo, nombre, aulaId, docenteId);
      mostrarMensaje("Sección registrada correctamente. ID: " + seccion.getId());

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void listar() {
    try {
      List<Seccion> secciones = controller.listar();

      if (secciones.isEmpty()) {
        mostrarMensaje("No hay secciones registradas.");
        return;
      }

      for (Seccion seccion : secciones) {
        System.out.println("ID: " + seccion.getId() + " | " + seccion.getInfo());
      }

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void agregarEstudiante() {
    try {
      int seccionId = leerEntero("ID de la sección: ");
      int estudianteId = leerEntero("ID del estudiante: ");

      controller.agregarEstudiante(seccionId, estudianteId);
      mostrarMensaje("Estudiante agregado correctamente.");

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void removerEstudiante() {
    try {
      int seccionId = leerEntero("ID de la sección: ");
      int estudianteId = leerEntero("ID del estudiante a remover: ");

      controller.removerEstudiante(seccionId, estudianteId);
      mostrarMensaje("Estudiante removido correctamente.");

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void eliminar() {
    try {
      int id = leerEntero("ID de la sección a eliminar: ");
      String confirmacion = leerTexto("¿Seguro que desea eliminar la sección? (S/N): ");

      if (!confirmacion.equalsIgnoreCase("S")) {
        mostrarMensaje("Eliminación cancelada.");
        return;
      }

      controller.eliminar(id);
      mostrarMensaje("Sección eliminada correctamente.");

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }
}
