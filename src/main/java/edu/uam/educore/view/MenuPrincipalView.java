package edu.uam.educore.view;

import edu.uam.educore.dao.ListaEdificioRepo;
import edu.uam.educore.dao.ListaEmpleadoRepo;
import edu.uam.educore.dao.ListaEstudianteRepo;
import edu.uam.educore.dao.ListaSeccionRepo;
import java.util.Scanner;

public class MenuPrincipalView extends VistaBase {

  private final EstudianteView estudianteView;
  private final EmpleadoView empleadoView;
  private final AcademicoView academicoView;

  public MenuPrincipalView(Scanner scanner) {
    super(scanner);

    ListaEstudianteRepo estudianteRepo = new ListaEstudianteRepo();
    ListaEmpleadoRepo empleadoRepo = new ListaEmpleadoRepo();
    ListaEdificioRepo edificioRepo = new ListaEdificioRepo();
    ListaSeccionRepo seccionRepo = new ListaSeccionRepo();

    this.estudianteView = new EstudianteView(scanner, estudianteRepo);
    this.empleadoView = new EmpleadoView(scanner, empleadoRepo);
    this.academicoView =
        new AcademicoView(scanner, edificioRepo, seccionRepo, empleadoRepo, estudianteRepo);
  }

  public void iniciar() {
    mostrarBienvenida();
    boolean corriendo = true;

    while (corriendo) {
      switch (mostrarMenuPrincipal()) {
        case 1 -> estudianteView.iniciar();
        case 2 -> empleadoView.iniciar();
        case 3 -> academicoView.iniciar();
        case 0 -> {
          mostrarMensaje("¡Hasta pronto!");
          corriendo = false;
        }
        default -> mostrarError("Opción inválida. Ingrese un número del 0 al 3.");
      }
    }
  }

  public void mostrarBienvenida() {
    System.out.println("╔══════════════════════════════════════╗");
    System.out.println("║        EduCore v1.0                  ║");
    System.out.println("║  Sistema de Administración Educativa ║");
    System.out.println("╚══════════════════════════════════════╝");
  }

  public int mostrarMenuPrincipal() {
    System.out.println("\n--- MENÚ PRINCIPAL ---");
    System.out.println("1. Gestión de Estudiantes");
    System.out.println("2. Gestión de Empleados");
    System.out.println("3. Gestión Académica (Edificios, Aulas, Secciones)");
    System.out.println("0. Salir");
    System.out.print("Seleccione una opción: ");
    return leerEntero();
  }
}