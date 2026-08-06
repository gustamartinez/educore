package edu.uam.educore.api;

import edu.uam.educore.api.Dtos.AulaDto;
import edu.uam.educore.api.Dtos.AulaRequest;
import edu.uam.educore.api.Dtos.EdificioDto;
import edu.uam.educore.api.Dtos.EdificioRequest;
import edu.uam.educore.api.Dtos.EmpleadoDto;
import edu.uam.educore.api.Dtos.EmpleadoRequest;
import edu.uam.educore.api.Dtos.EstudianteDto;
import edu.uam.educore.api.Dtos.EstudianteRequest;
import edu.uam.educore.api.Dtos.MatriculaRequest;
import edu.uam.educore.controller.EdificioController;
import edu.uam.educore.controller.EmpleadoController;
import edu.uam.educore.controller.EstudianteController;
import edu.uam.educore.dao.EdificioRepoSql;
import edu.uam.educore.dao.EmpleadoRepoSql;
import edu.uam.educore.dao.EstudianteRepoSql;
import edu.uam.educore.dao.ListaEstudianteRepo;
import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.db.ConfiguracionBD;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.infraestructura.TipoAula;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ServidorApi {

  public static void iniciar(int puerto) throws IOException {

    Repositorio<Estudiante> estudianteRepo;

    try {
      estudianteRepo =
          new EstudianteRepoSql(
              ConfiguracionBD.desdeArchivo(".env"));
    } catch (IOException e) {
      estudianteRepo = new ListaEstudianteRepo();
    }

    EstudianteController estudianteController =
        new EstudianteController(estudianteRepo);

    EmpleadoController empleadoController =
        new EmpleadoController(
            new EmpleadoRepoSql(
                ConfiguracionBD.desdeArchivo(".env")));

    EdificioController edificioController =
        new EdificioController(
            new EdificioRepoSql(
                ConfiguracionBD.desdeArchivo(".env")));

    Javalin app =
        Javalin.create(
            cfg -> {

              cfg.bundledPlugins.enableDevLogging();
              cfg.spaRoot.addFile("/", "/web/index.html");

              cfg.routes.exception(
                  IllegalArgumentException.class,
                  (e, ctx) ->
                      ctx.status(400)
                          .json(Map.of("error", e.getMessage())));

              cfg.routes.exception(
                  Exception.class,
                  (e, ctx) ->
                      ctx.status(500)
                          .json(Map.of("error", e.getMessage())));

              registrarEstudiantes(
                  cfg,
                  estudianteController);

              registrarEmpleados(
                  cfg,
                  empleadoController);

              registrarEdificios(
                  cfg,
                  edificioController);

              registrarSecciones(cfg);
              registrarMatricula(cfg);
              registrarReporte(cfg);
            });

    app.start(puerto);

    System.out.println(
        "API EduCore escuchando en http://localhost:" + puerto);
  }

  // ── Estudiantes ──

  private static void registrarEstudiantes(
      JavalinConfig cfg,
      EstudianteController controller) {

    cfg.routes.get(
        "/api/estudiantes",
        ctx -> {

          List<EstudianteDto> lista =
              controller.listar()
                  .stream()
                  .map(EstudianteDto::desde)
                  .toList();

          ctx.json(lista);
        });

    cfg.routes.post(
        "/api/estudiantes",
        ctx -> {

          EstudianteRequest r =
              ctx.bodyAsClass(
                  EstudianteRequest.class);

          Estudiante creado =
              "BECADO".equalsIgnoreCase(r.tipo())
                  ? controller.registrarBecado(
                      r.nombre(),
                      r.apellidos(),
                      r.email(),
                      r.carnet(),
                      r.porcentajeBeca() != null
                          ? r.porcentajeBeca()
                          : 0.0)
                  : controller.registrarRegular(
                      r.nombre(),
                      r.apellidos(),
                      r.email(),
                      r.carnet());

          ctx.status(201)
              .json(EstudianteDto.desde(creado));
        });

    cfg.routes.put(
        "/api/estudiantes/{id}",
        ctx -> {

          int id =
              Integer.parseInt(
                  ctx.pathParam("id"));

          EstudianteRequest r =
              ctx.bodyAsClass(
                  EstudianteRequest.class);

          Estudiante estudiante =
              controller.actualizar(
                  id,
                  r.nombre(),
                  r.apellidos(),
                  r.email(),
                  r.carnet(),
                  r.porcentajeBeca());

          ctx.json(
              EstudianteDto.desde(estudiante));
        });

    cfg.routes.delete(
        "/api/estudiantes/{id}",
        ctx -> {

          controller.eliminar(
              Integer.parseInt(
                  ctx.pathParam("id")));

          ctx.status(204);
        });
  }

  // ── Empleados ──

  private static void registrarEmpleados(
      JavalinConfig cfg,
      EmpleadoController controller) {

    cfg.routes.get(
        "/api/empleados",
        ctx -> {

          List<Empleado> empleados =
              controller.listar();

          ctx.json(
              EmpleadoDto.listaDesde(empleados));
        });

    cfg.routes.post(
        "/api/empleados",
        ctx -> {

          EmpleadoRequest r =
              ctx.bodyAsClass(
                  EmpleadoRequest.class);

          Empleado creado =
              controller.registrar(
                  r.nombre(),
                  r.apellidos(),
                  r.email(),
                  r.salario(),
                  LocalDate.parse(
                      r.fechaIngreso()),
                  r.tipo());

          ctx.status(201)
              .json(EmpleadoDto.desde(creado));
        });

    cfg.routes.put(
        "/api/empleados/{id}",
        ctx -> {

          int id =
              Integer.parseInt(
                  ctx.pathParam("id"));

          EmpleadoRequest r =
              ctx.bodyAsClass(
                  EmpleadoRequest.class);

          Empleado actualizado =
              controller.actualizar(
                  id,
                  r.nombre(),
                  r.apellidos(),
                  r.email(),
                  r.salario(),
                  LocalDate.parse(
                      r.fechaIngreso()),
                  r.tipo());

          ctx.json(
              EmpleadoDto.desde(actualizado));
        });

    cfg.routes.delete(
        "/api/empleados/{id}",
        ctx -> {

          controller.eliminar(
              Integer.parseInt(
                  ctx.pathParam("id")));

          ctx.status(204);
        });
  }

  // ── Edificios / Aulas ──

  private static void registrarEdificios(
      JavalinConfig cfg,
      EdificioController controller) {

    cfg.routes.get(
        "/api/edificios",
        ctx -> {

          List<Edificio> edificios =
              controller.listarEdificios();

          ctx.json(
              EdificioDto.listaDesde(edificios));
        });

    cfg.routes.post(
        "/api/edificios",
        ctx -> {

          EdificioRequest r =
              ctx.bodyAsClass(
                  EdificioRequest.class);

          Edificio creado =
              controller.registrarEdificio(
                  r.codigo(),
                  r.nombre());

          ctx.status(201)
              .json(
                  EdificioDto.desde(creado));
        });

    cfg.routes.put(
    "/api/edificios/{id}",
    ctx -> {

      int id =
          Integer.parseInt(
              ctx.pathParam("id"));

      EdificioRequest r =
          ctx.bodyAsClass(
              EdificioRequest.class);

      Edificio actualizado =
          controller.actualizarEdificio(
              id,
              r.codigo(),
              r.nombre());

      ctx.json(
          EdificioDto.desde(actualizado));
    });

    cfg.routes.delete(
        "/api/edificios/{id}",
        ctx -> {

          controller.eliminarEdificio(
              Integer.parseInt(
                  ctx.pathParam("id")));

          ctx.status(204);
        });

    cfg.routes.post(
        "/api/edificios/{id}/aulas",
        ctx -> {

          int edificioId =
              Integer.parseInt(
                  ctx.pathParam("id"));

          AulaRequest r =
              ctx.bodyAsClass(
                  AulaRequest.class);

          Aula aula =
              controller.agregarAula(
                  edificioId,
                  r.numero(),
                  r.capacidad(),
                  r.tipo() != null
                      ? r.tipo()
                      : TipoAula.REGULAR);

          ctx.status(201)
              .json(
                  AulaDto.desde(aula));
        });
    cfg.routes.put(
    "/api/edificios/{id}/aulas/{aulaId}",
    ctx -> {

      int edificioId =
          Integer.parseInt(
              ctx.pathParam("id"));

      int aulaId =
          Integer.parseInt(
              ctx.pathParam("aulaId"));

      AulaRequest r =
          ctx.bodyAsClass(
              AulaRequest.class);

      Aula aula =
          controller.actualizarAula(
              edificioId,
              aulaId,
              r.numero(),
              r.capacidad(),
              r.tipo() != null
                  ? r.tipo()
                  : TipoAula.REGULAR);

      ctx.json(AulaDto.desde(aula));
    });
    cfg.routes.delete(
    "/api/edificios/{id}/aulas/{aulaId}",
    ctx -> {

      int edificioId =
          Integer.parseInt(
              ctx.pathParam("id"));

      int aulaId =
          Integer.parseInt(
              ctx.pathParam("aulaId"));

      controller.eliminarAula(
          edificioId,
          aulaId);

      ctx.status(204);
    });
  }

  // ── Secciones ──

  private static void registrarSecciones(
      JavalinConfig cfg) {

    cfg.routes.get(
        "/api/secciones",
        ctx ->
            ctx.status(501)
                .json(
                    Map.of(
                        "error",
                        "secciones: pendiente de implementar")));

    cfg.routes.post(
        "/api/secciones",
        ctx ->
            ctx.status(501)
                .json(
                    Map.of(
                        "error",
                        "secciones: pendiente de implementar")));

    cfg.routes.put(
        "/api/secciones/{id}",
        ctx ->
            ctx.status(501)
                .json(
                    Map.of(
                        "error",
                        "secciones: pendiente de implementar")));

    cfg.routes.delete(
        "/api/secciones/{id}",
        ctx ->
            ctx.status(501)
                .json(
                    Map.of(
                        "error",
                        "secciones: pendiente de implementar")));

    cfg.routes.post(
        "/api/secciones/{id}/estudiantes",
        ctx ->
            ctx.status(501)
                .json(
                    Map.of(
                        "error",
                        "secciones: pendiente de implementar")));

    cfg.routes.delete(
        "/api/secciones/{id}/estudiantes/{estudianteId}",
        ctx ->
            ctx.status(501)
                .json(
                    Map.of(
                        "error",
                        "secciones: pendiente de implementar")));
  }

  // ── Matrícula ──

  private static void registrarMatricula(
      JavalinConfig cfg) {

    cfg.routes.post(
        "/api/matricula",
        ctx -> {

          MatriculaRequest r =
              ctx.bodyAsClass(
                  MatriculaRequest.class);

          String archivo =
              r.archivo() != null
                  ? r.archivo()
                  : "matriculas.csv";

          String contenido =
              r.contenido() != null
                  ? r.contenido()
                  : "";

          Path entrada =
              Path.of(
                  System.getenv(
                      "ENTRADA_DIR"));

          Files.createDirectories(entrada);

          Files.writeString(
              entrada.resolve(archivo),
              contenido);

          String host =
              System.getenv(
                  "MATRICULA_HOST");

          int puertoMatricula =
              Integer.parseInt(
                  System.getenv(
                      "MATRICULA_PORT"));

          try (Socket socket =
                  new Socket(
                      host,
                      puertoMatricula);
              PrintWriter out =
                  new PrintWriter(
                      socket.getOutputStream(),
                      true,
                      StandardCharsets.UTF_8);
              BufferedReader in =
                  new BufferedReader(
                      new InputStreamReader(
                          socket.getInputStream(),
                          StandardCharsets.UTF_8))) {

            out.println(
                "MATRICULAR " + archivo);

            String respuesta =
                in.readLine();

            ctx.json(
                Map.of(
                    "respuesta",
                    respuesta != null
                        ? respuesta
                        : "sin respuesta del servicio de matricula"));
          }
        });
  }

  // ── Reporte ──

  private static void registrarReporte(
      JavalinConfig cfg) {

    cfg.routes.post(
        "/api/reporte",
        ctx -> {

          String host =
              System.getenv(
                  "REPORTE_HOST");

          int puertoReporte =
              Integer.parseInt(
                  System.getenv(
                      "REPORTE_PORT"));

          try (Socket socket =
                  new Socket(
                      host,
                      puertoReporte);
              PrintWriter out =
                  new PrintWriter(
                      socket.getOutputStream(),
                      true,
                      StandardCharsets.UTF_8);
              BufferedReader in =
                  new BufferedReader(
                      new InputStreamReader(
                          socket.getInputStream(),
                          StandardCharsets.UTF_8))) {

            out.println("REPORTE");

            String encabezado =
                in.readLine();

            if (encabezado == null
                || !encabezado.startsWith("200 ")) {

              ctx.status(502)
                  .json(
                      Map.of(
                          "error",
                          "reporte no disponible: "
                              + encabezado));

              return;
            }

            int lineas =
                Integer.parseInt(
                    encabezado
                        .substring(
                            "200 ".length())
                        .trim());

            StringBuilder contenido =
                new StringBuilder();

            for (int i = 0;
                i < lineas;
                i++) {

              String linea =
                  in.readLine();

              contenido
                  .append(
                      linea == null
                          ? ""
                          : linea)
                  .append("\n");
            }

            ctx.contentType(
                "text/plain; charset=utf-8");

            ctx.header(
                "Content-Disposition",
                "attachment; filename=\"reporte.txt\"");

            ctx.result(
                contenido.toString());
          }
        });
  }
}