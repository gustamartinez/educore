package edu.uam.educore.view;

import edu.uam.educore.controller.EmpleadoController;
import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.TipoEmpleado;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class EmpleadoView extends VistaBase {

  private final EmpleadoController controller;

  public EmpleadoView(Scanner scanner, Repositorio<Empleado> repo) {
    super(scanner);
    this.controller = new EmpleadoController(repo);
  }

  public void iniciar() {
    boolean volver = false;

    while (!volver) {
      System.out.println("\n--- GESTIÓN DE EMPLEADOS ---");
      System.out.println("1. Registrar empleado");
      System.out.println("2. Listar empleados");
      System.out.println("3. Buscar empleado");
      System.out.println("4. Actualizar empleado");
      System.out.println("5. Eliminar empleado");
      System.out.println("0. Volver al menú principal");
      System.out.print("Seleccione una opción: ");

      int opcion = leerEntero();

      switch (opcion) {
        case 1 -> registrar();
        case 2 -> listar();
        case 3 -> buscar();
        case 4 -> actualizar();
        case 5 -> eliminar();
        case 0 -> volver = true;
        default -> mostrarError("Opción inválida. Ingrese un número del 0 al 5.");
      }
    }
  }

  private void registrar() {
    try {
      String nombre = leerTexto("Nombre: ");
      String apellidos = leerTexto("Apellidos: ");
      String email = leerTexto("Email: ");
      double salario = leerDecimal("Salario: ");
      LocalDate fechaIngreso = leerFecha("Fecha de ingreso (AAAA-MM-DD): ");
      TipoEmpleado tipo = leerTipoEmpleado();

      Empleado empleado =
          controller.registrar(nombre, apellidos, email, salario, fechaIngreso, tipo);

      mostrarMensaje("Empleado registrado correctamente. ID: " + empleado.getId());

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void listar() {
    try {
      List<Empleado> empleados = controller.listar();

      if (empleados.isEmpty()) {
        mostrarMensaje("No hay empleados registrados.");
        return;
      }

      for (Empleado e : empleados) {
        System.out.println("ID: " + e.getId() + " | " + e.getInfo());
      }

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void buscar() {
    try {
      int id = leerEntero("Ingrese el ID del empleado: ");
      Empleado empleado = controller.buscarPorId(id);

      if (empleado == null) {
        mostrarError("No existe empleado con ID " + id + ".");
      } else {
        System.out.println("ID: " + empleado.getId() + " | " + empleado.getInfo());
      }

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void actualizar() {
    try {
      int id = leerEntero("ID del empleado a actualizar: ");
      String nombre = leerTexto("Nuevo nombre: ");
      String apellidos = leerTexto("Nuevos apellidos: ");
      String email = leerTexto("Nuevo email: ");
      double salario = leerDecimal("Nuevo salario: ");
      LocalDate fechaIngreso = leerFecha("Nueva fecha de ingreso (AAAA-MM-DD): ");
      TipoEmpleado tipo = leerTipoEmpleado();

      controller.actualizar(id, nombre, apellidos, email, salario, fechaIngreso, tipo);

      mostrarMensaje("Empleado actualizado correctamente.");

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void eliminar() {
    try {
      int id = leerEntero("ID del empleado a eliminar: ");
      String confirmacion = leerTexto("¿Seguro que desea eliminarlo? (S/N): ");

      if (!confirmacion.equalsIgnoreCase("S")) {
        mostrarMensaje("Eliminación cancelada.");
        return;
      }

      controller.eliminar(id);
      mostrarMensaje("Empleado eliminado correctamente.");

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private TipoEmpleado leerTipoEmpleado() {
    System.out.println("\nTipos de empleado:");
    TipoEmpleado[] tipos = TipoEmpleado.values();

    for (int i = 0; i < tipos.length; i++) {
      System.out.println((i + 1) + ". " + tipos[i]);
    }

    int opcion = leerEntero("Seleccione el tipo: ");

    if (opcion < 1 || opcion > tipos.length) {
      throw new IllegalArgumentException("Tipo de empleado inválido.");
    }

    return tipos[opcion - 1];
  }
}