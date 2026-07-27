
package edu.uam.educore.dao;

import edu.uam.educore.db.Conexion;
import edu.uam.educore.db.ConfiguracionBD;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.infraestructura.TipoAula;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EdificioRepoSql extends Repositorio<Edificio> {

  private final ConfiguracionBD config;

  public EdificioRepoSql(ConfiguracionBD config) {
    this.config = config;
  }

  private Connection abrir() throws Exception {
    return Conexion.getConnection(config.url(), config.usuario(), config.contrasena());
  }

  @Override
  public void guardar(Edificio edificio) throws Exception {
    String sql = "INSERT INTO edificio (id, codigo, nombre) VALUES (?, ?, ?)";

    try (Connection con = abrir();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, edificio.getId());
      ps.setString(2, edificio.getCodigo());
      ps.setString(3, edificio.getNombre());

      ps.executeUpdate();
    }
  }

  @Override
  public void actualizar(Edificio edificio) throws Exception {
    String sqlEdificio = "UPDATE edificio SET codigo=?, nombre=? WHERE id=?";

    try (Connection con = abrir()) {
      con.setAutoCommit(false);

      try {
        try (PreparedStatement ps = con.prepareStatement(sqlEdificio)) {
          ps.setString(1, edificio.getCodigo());
          ps.setString(2, edificio.getNombre());
          ps.setInt(3, edificio.getId());
          ps.executeUpdate();
        }

        sincronizarAulas(con, edificio);

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
    try (Connection con = abrir();
        PreparedStatement ps = con.prepareStatement("DELETE FROM edificio WHERE id=?")) {

      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }

  @Override
  public Optional<Edificio> buscarPorId(int id) throws Exception {
    String sql = "SELECT * FROM edificio WHERE id=?";

    try (Connection con = abrir();
        PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Edificio edificio = mapearEdificio(rs);
          cargarAulas(con, edificio);
          return Optional.of(edificio);
        }

        return Optional.empty();
      }
    }
  }

  @Override
  public List<Edificio> buscarTodos() throws Exception {
    List<Edificio> lista = new ArrayList<>();

    try (Connection con = abrir();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM edificio");
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        Edificio edificio = mapearEdificio(rs);
        cargarAulas(con, edificio);
        lista.add(edificio);
      }
    }

    return lista;
  }

  private Edificio mapearEdificio(ResultSet rs) throws Exception {
    int id = rs.getInt("id");
    String codigo = rs.getString("codigo");
    String nombre = rs.getString("nombre");

    return new Edificio(id, codigo, nombre);
  }

  private void cargarAulas(Connection con, Edificio edificio) throws Exception {
    String sql = "SELECT * FROM aula WHERE edificio_id=?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, edificio.getId());

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Aula aula =
              new Aula(
                  rs.getInt("id"),
                  rs.getString("numero"),
                  rs.getInt("capacidad"),
                  TipoAula.valueOf(rs.getString("tipo")),
                  edificio);

          edificio.agregarAula(aula);
        }
      }
    }
  }

  private void sincronizarAulas(Connection con, Edificio edificio) throws Exception {
    List<Integer> idsActuales = new ArrayList<>();

    String sqlBuscar = "SELECT id FROM aula WHERE edificio_id=?";

    try (PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
      ps.setInt(1, edificio.getId());

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          idsActuales.add(rs.getInt("id"));
        }
      }
    }

    for (Aula aula : edificio.getAulas()) {

      if (idsActuales.contains(aula.getId())) {
        actualizarAula(con, aula);
      } else {
        guardarAula(con, aula, edificio.getId());
      }
    }

    for (Integer id : idsActuales) {
      boolean existe = false;

      for (Aula aula : edificio.getAulas()) {
        if (aula.getId() == id) {
          existe = true;
          break;
        }
      }

      if (!existe) {
        eliminarAula(con, id);
      }
    }
  }

  private void guardarAula(Connection con, Aula aula, int edificioId) throws Exception {
    String sql =
        "INSERT INTO aula (id, numero, capacidad, tipo, edificio_id) VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, aula.getId());
      ps.setString(2, aula.getNumero());
      ps.setInt(3, aula.getCapacidad());
      ps.setString(4, aula.getTipo().name());
      ps.setInt(5, edificioId);

      ps.executeUpdate();
    }
  }

  private void actualizarAula(Connection con, Aula aula) throws Exception {
    String sql = "UPDATE aula SET numero=?, capacidad=?, tipo=? WHERE id=?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, aula.getNumero());
      ps.setInt(2, aula.getCapacidad());
      ps.setString(3, aula.getTipo().name());
      ps.setInt(4, aula.getId());

      ps.executeUpdate();
    }
  }

  private void eliminarAula(Connection con, int id) throws Exception {
    try (PreparedStatement ps = con.prepareStatement("DELETE FROM aula WHERE id=?")) {
      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }
}