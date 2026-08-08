package edu.uam.educore.dao;

import edu.uam.educore.db.Conexion;
import edu.uam.educore.db.ConfiguracionBD;
import edu.uam.educore.model.academico.Seccion;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.infraestructura.TipoAula;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;
import edu.uam.educore.model.personas.EstudianteBecado;
import edu.uam.educore.model.personas.EstudianteRegular;
import edu.uam.educore.model.personas.TipoEmpleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeccionRepoSql extends Repositorio<Seccion> {

  private final ConfiguracionBD config;

  public SeccionRepoSql(ConfiguracionBD config) {
    this.config = config;
  }

  private Connection abrir() throws Exception {
    return Conexion.getConnection(
        config.url(),
        config.usuario(),
        config.contrasena());
  }

  @Override
  public void guardar(Seccion seccion) throws Exception {
    String sql =
        "INSERT INTO seccion (id, codigo, nombre, docente_id, aula_id) "
            + "VALUES (?, ?, ?, ?, ?)";

    try (Connection con = abrir();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, seccion.getId());
      ps.setString(2, seccion.getCodigo());
      ps.setString(3, seccion.getNombre());
      ps.setInt(4, seccion.getDocente().getId());
      ps.setInt(5, seccion.getAula().getId());

      ps.executeUpdate();
    }
  }

  @Override
  public void actualizar(Seccion seccion) throws Exception {
    String sql =
        "UPDATE seccion "
            + "SET codigo=?, nombre=?, docente_id=?, aula_id=? "
            + "WHERE id=?";

    try (Connection con = abrir()) {
      con.setAutoCommit(false);

      try {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
          ps.setString(1, seccion.getCodigo());
          ps.setString(2, seccion.getNombre());
          ps.setInt(3, seccion.getDocente().getId());
          ps.setInt(4, seccion.getAula().getId());
          ps.setInt(5, seccion.getId());

          ps.executeUpdate();
        }

        sincronizarMatriculas(con, seccion);

        con.commit();

      } catch (Exception e) {
        con.rollback();
        throw e;
      } finally {
        con.setAutoCommit(true);
      }
    }
  }

  @Override
  public void eliminar(int id) throws Exception {
    try (Connection con = abrir()) {

      String sqlMatriculas =
          "SELECT COUNT(*) FROM matricula WHERE seccion_id=?";

      try (PreparedStatement ps =
          con.prepareStatement(sqlMatriculas)) {

        ps.setInt(1, id);

        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next() && rs.getInt(1) > 0) {
            throw new IllegalArgumentException(
                "No se puede eliminar la sección porque tiene estudiantes inscritos.");
          }
        }
      }

      try (PreparedStatement ps =
          con.prepareStatement(
              "DELETE FROM seccion WHERE id=?")) {

        ps.setInt(1, id);
        ps.executeUpdate();
      }
    }
  }

  @Override
  public Optional<Seccion> buscarPorId(int id) throws Exception {
    String sql =
        "SELECT "
            + "s.id AS seccion_id, "
            + "s.codigo, "
            + "s.nombre AS seccion_nombre, "
            + "e.id AS docente_id, "
            + "e.tipo AS docente_tipo, "
            + "e.nombre AS docente_nombre, "
            + "e.apellidos AS docente_apellidos, "
            + "e.email AS docente_email, "
            + "e.salario, "
            + "e.fecha_ingreso, "
            + "a.id AS aula_id, "
            + "a.numero, "
            + "a.capacidad, "
            + "a.tipo AS aula_tipo, "
            + "ed.id AS edificio_id, "
            + "ed.codigo AS edificio_codigo, "
            + "ed.nombre AS edificio_nombre "
            + "FROM seccion s "
            + "JOIN empleado e ON e.id = s.docente_id "
            + "JOIN aula a ON a.id = s.aula_id "
            + "JOIN edificio ed ON ed.id = a.edificio_id "
            + "WHERE s.id=?";

    try (Connection con = abrir();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Seccion seccion = mapearSeccion(rs);
          cargarEstudiantes(con, seccion);
          return Optional.of(seccion);
        }

        return Optional.empty();
      }
    }
  }

  @Override
  public List<Seccion> buscarTodos() throws Exception {
    List<Seccion> lista = new ArrayList<>();

    String sql =
        "SELECT "
            + "s.id AS seccion_id, "
            + "s.codigo, "
            + "s.nombre AS seccion_nombre, "
            + "e.id AS docente_id, "
            + "e.tipo AS docente_tipo, "
            + "e.nombre AS docente_nombre, "
            + "e.apellidos AS docente_apellidos, "
            + "e.email AS docente_email, "
            + "e.salario, "
            + "e.fecha_ingreso, "
            + "a.id AS aula_id, "
            + "a.numero, "
            + "a.capacidad, "
            + "a.tipo AS aula_tipo, "
            + "ed.id AS edificio_id, "
            + "ed.codigo AS edificio_codigo, "
            + "ed.nombre AS edificio_nombre "
            + "FROM seccion s "
            + "JOIN empleado e ON e.id = s.docente_id "
            + "JOIN aula a ON a.id = s.aula_id "
            + "JOIN edificio ed ON ed.id = a.edificio_id";

    try (Connection con = abrir();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        Seccion seccion = mapearSeccion(rs);
        cargarEstudiantes(con, seccion);
        lista.add(seccion);
      }
    }

    return lista;
  }

  private Seccion mapearSeccion(ResultSet rs)
      throws Exception {

    Empleado docente =
        new Empleado(
            rs.getInt("docente_id"),
            rs.getString("docente_nombre"),
            rs.getString("docente_apellidos"),
            rs.getString("docente_email"),
            rs.getDouble("salario"),
            rs.getDate("fecha_ingreso").toLocalDate(),
            TipoEmpleado.valueOf(
                rs.getString("docente_tipo")));

    Edificio edificio =
        new Edificio(
            rs.getInt("edificio_id"),
            rs.getString("edificio_codigo"),
            rs.getString("edificio_nombre"));

    Aula aula =
        new Aula(
            rs.getInt("aula_id"),
            rs.getString("numero"),
            rs.getInt("capacidad"),
            TipoAula.valueOf(
                rs.getString("aula_tipo")),
            edificio);

    return new Seccion(
        rs.getInt("seccion_id"),
        rs.getString("codigo"),
        rs.getString("seccion_nombre"),
        docente,
        aula);
  }

  private void cargarEstudiantes(
      Connection con,
      Seccion seccion)
      throws Exception {

    String sql =
        "SELECT e.* "
            + "FROM estudiante e "
            + "JOIN matricula m "
            + "ON m.estudiante_id = e.id "
            + "WHERE m.seccion_id=?";

    try (PreparedStatement ps =
        con.prepareStatement(sql)) {

      ps.setInt(1, seccion.getId());

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {

          Estudiante estudiante;

          if ("BECADO".equals(
              rs.getString("tipo"))) {

            estudiante =
                new EstudianteBecado(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellidos"),
                    rs.getString("email"),
                    rs.getString("carnet"),
                    rs.getDouble(
                        "porcentaje_beca"));

          } else {

            estudiante =
                new EstudianteRegular(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellidos"),
                    rs.getString("email"),
                    rs.getString("carnet"));
          }

          seccion.agregarEstudiante(estudiante);
        }
      }
    }
  }

  private void sincronizarMatriculas(
      Connection con,
      Seccion seccion)
      throws Exception {

    List<Integer> idsActuales =
        new ArrayList<>();

    String sqlBuscar =
        "SELECT estudiante_id "
            + "FROM matricula "
            + "WHERE seccion_id=?";

    try (PreparedStatement ps =
        con.prepareStatement(sqlBuscar)) {

      ps.setInt(1, seccion.getId());

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          idsActuales.add(
              rs.getInt("estudiante_id"));
        }
      }
    }

    for (Estudiante estudiante :
        seccion.getEstudiantes()) {

      if (!idsActuales.contains(
          estudiante.getId())) {

        String sqlInsertar =
            "INSERT INTO matricula "
                + "(estudiante_id, seccion_id) "
                + "VALUES (?, ?)";

        try (PreparedStatement ps =
            con.prepareStatement(sqlInsertar)) {

          ps.setInt(
              1,
              estudiante.getId());

          ps.setInt(
              2,
              seccion.getId());

          ps.executeUpdate();
        }
      }
    }

    for (Integer estudianteId :
        idsActuales) {

      boolean existe = false;

      for (Estudiante estudiante :
          seccion.getEstudiantes()) {

        if (estudiante.getId()
            == estudianteId) {

          existe = true;
          break;
        }
      }

      if (!existe) {

        String sqlEliminar =
            "DELETE FROM matricula "
                + "WHERE estudiante_id=? "
                + "AND seccion_id=?";

        try (PreparedStatement ps =
            con.prepareStatement(sqlEliminar)) {

          ps.setInt(1, estudianteId);
          ps.setInt(2, seccion.getId());

          ps.executeUpdate();
        }
      }
    }
  }
}