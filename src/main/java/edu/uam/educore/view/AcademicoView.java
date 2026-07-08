package edu.uam.educore.view;

import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.academico.Seccion;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;
import java.util.Scanner;

public class AcademicoView extends VistaBase {

  private final EdificioView edificioView;
  private final SeccionView seccionView;

  public AcademicoView(
      Scanner scanner,
      Repositorio<Edificio> edificioRepo,
      Repositorio<Seccion> seccionRepo,
      Repositorio<Empleado> empleadoRepo,
      Repositorio<Estudiante> estudianteRepo) {
    super(scanner);
    this.edificioView = new EdificioView(scanner, edificioRepo);
    this.seccionView = new SeccionView(scanner, seccionRepo, empleadoRepo, estudianteRepo, edificioRepo);
  }

  public void iniciar() {
    boolean volver = false;

    while (!volver) {
      System.out.println("\n--- GESTIÓN ACADÉMICA ---");
      System.out.println("1. Edificios y Aulas");
      System.out.println("2. Secciones");
      System.out.println("0. Volver al menú principal");
      System.out.print("Seleccione una opción: ");

      int opcion = leerEntero();

      switch (opcion) {
        case 1 -> edificioView.iniciar();
        case 2 -> seccionView.iniciar();
        case 0 -> volver = true;
        default -> mostrarError("Opción inválida.");
      }
    }
  }
}
