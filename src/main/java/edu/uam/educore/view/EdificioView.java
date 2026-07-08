package edu.uam.educore.view;

import edu.uam.educore.controller.EdificioController;
import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.infraestructura.TipoAula;
import java.util.List;
import java.util.Scanner;

public class EdificioView extends VistaBase {

  private final EdificioController controller;

  public EdificioView(Scanner scanner, Repositorio<Edificio> edificioRepo) {
    super(scanner);
    this.controller = new EdificioController(edificioRepo);
  }

  public void iniciar() {
    boolean volver = false;

    while (!volver) {
      System.out.println("\n--- GESTIÓN DE EDIFICIOS Y AULAS ---");
      System.out.println("1. Registrar edificio");
      System.out.println("2. Listar edificios");
      System.out.println("3. Buscar edificio por ID");
      System.out.println("4. Agregar aula a edificio");
      System.out.println("5. Listar aulas de edificio");
      System.out.println("6. Eliminar aula");
      System.out.println("7. Eliminar edificio");
      System.out.println("0. Volver");
      System.out.print("Seleccione una opción: ");

      int opcion = leerEntero();

      switch (opcion) {
        case 1 -> registrarEdificio();
        case 2 -> listarEdificios();
        case 3 -> buscarEdificio();
        case 4 -> agregarAula();
        case 5 -> listarAulas();
        case 6 -> eliminarAula();
        case 7 -> eliminarEdificio();
        case 0 -> volver = true;
        default -> mostrarError("Opción inválida.");
      }
    }
  }

  private void registrarEdificio() {
    try {
      String codigo = leerTexto("Código del edificio: ");
      String nombre = leerTexto("Nombre del edificio: ");

      Edificio edificio = controller.registrarEdificio(codigo, nombre);
      mostrarMensaje("Edificio registrado correctamente. ID: " + edificio.getId());

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void listarEdificios() {
    try {
      List<Edificio> edificios = controller.listarEdificios();

      if (edificios.isEmpty()) {
        mostrarMensaje("No hay edificios registrados.");
        return;
      }

      for (Edificio edificio : edificios) {
        System.out.println("ID: " + edificio.getId() + " | " + edificio.getInfo());
      }

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void buscarEdificio() {
    try {
      int id = leerEntero("ID del edificio: ");
      Edificio edificio = controller.buscarEdificioPorId(id);

      if (edificio == null) {
        mostrarError("No existe edificio con ID " + id + ".");
        return;
      }

      System.out.println("ID: " + edificio.getId() + " | " + edificio.getInfo());

      if (edificio.getAulas().isEmpty()) {
        mostrarMensaje("Este edificio no tiene aulas.");
      } else {
        for (Aula aula : edificio.getAulas()) {
          System.out.println("  ID Aula: " + aula.getId() + " | " + aula.getInfo());
        }
      }

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void agregarAula() {
    try {
      int edificioId = leerEntero("ID del edificio: ");
      String numero = leerTexto("Número del aula: ");
      int capacidad = leerEntero("Capacidad: ");
      TipoAula tipo = leerTipoAula();

      Aula aula = controller.agregarAula(edificioId, numero, capacidad, tipo);
      mostrarMensaje("Aula agregada correctamente. ID: " + aula.getId());

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void listarAulas() {
    try {
      int edificioId = leerEntero("ID del edificio: ");
      List<Aula> aulas = controller.listarAulas(edificioId);

      if (aulas.isEmpty()) {
        mostrarMensaje("Este edificio no tiene aulas.");
        return;
      }

      for (Aula aula : aulas) {
        System.out.println("ID Aula: " + aula.getId() + " | " + aula.getInfo());
      }

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void eliminarAula() {
    try {
      int edificioId = leerEntero("ID del edificio: ");
      int aulaId = leerEntero("ID del aula a eliminar: ");
      String confirmacion = leerTexto("¿Seguro que desea eliminar el aula? (S/N): ");

      if (!confirmacion.equalsIgnoreCase("S")) {
        mostrarMensaje("Eliminación cancelada.");
        return;
      }

      controller.eliminarAula(edificioId, aulaId);
      mostrarMensaje("Aula eliminada correctamente.");

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void eliminarEdificio() {
    try {
      int id = leerEntero("ID del edificio a eliminar: ");
      String confirmacion = leerTexto("¿Seguro que desea eliminar el edificio? (S/N): ");

      if (!confirmacion.equalsIgnoreCase("S")) {
        mostrarMensaje("Eliminación cancelada.");
        return;
      }

      controller.eliminarEdificio(id);
      mostrarMensaje("Edificio eliminado correctamente.");

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private TipoAula leerTipoAula() {
    System.out.println("\nTipos de aula:");
    TipoAula[] tipos = TipoAula.values();

    for (int i = 0; i < tipos.length; i++) {
      System.out.println((i + 1) + ". " + tipos[i]);
    }

    int opcion = leerEntero("Seleccione el tipo: ");

    if (opcion < 1 || opcion > tipos.length) {
      throw new IllegalArgumentException("Tipo de aula inválido.");
    }

    return tipos[opcion - 1];
  }
}