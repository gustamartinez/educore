package edu.uam.educore.controller;

import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.infraestructura.TipoAula;
import java.util.List;
import java.util.Optional;

public class EdificioController {

    private final Repositorio<Edificio> repo;
    private int proximoEdificioId = 1;
    private int proximoAulaId = 1;

    public EdificioController(Repositorio<Edificio> repo) {
        if (repo == null) {
            throw new IllegalArgumentException("El repositorio de edificios es obligatorio.");
        }
        this.repo = repo;
    }

    public Edificio registrarEdificio(String codigo, String nombre) throws Exception {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Código y nombre del edificio son obligatorios.");
        }

        for (Edificio e : repo.buscarTodos()) {
            if (e.getCodigo().equalsIgnoreCase(codigo)) {
                throw new IllegalArgumentException("Ya existe un edificio con el código " + codigo + ".");
            }
        }

        Edificio edificio = new Edificio(proximoEdificioId, codigo, nombre);
        repo.guardar(edificio);
        proximoEdificioId++;

        return edificio;
    }

    public List<Edificio> listarEdificios() throws Exception {
        return repo.buscarTodos();
    }

    public Edificio buscarEdificioPorId(int id) throws Exception {
        Optional<Edificio> resultado = repo.buscarPorId(id);
        return resultado.orElse(null);
    }

    public Aula agregarAula(int edificioId, String numero, int capacidad, TipoAula tipo)
            throws Exception {

        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("El número del aula es obligatorio.");
        }

        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de aula es obligatorio.");
        }

        Edificio edificio = buscarEdificioPorId(edificioId);

        if (edificio == null) {
            throw new IllegalArgumentException("No existe edificio con ID " + edificioId + ".");
        }

        for (Aula a : edificio.getAulas()) {
            if (a.getNumero().equalsIgnoreCase(numero)) {
                throw new IllegalArgumentException("Ya existe un aula con el número " + numero + " en este edificio.");
            }
        }

        Aula aula = new Aula(proximoAulaId, numero, capacidad, tipo, edificio);
        edificio.agregarAula(aula);
        repo.actualizar(edificio);
        proximoAulaId++;

        return aula;
    }

    public List<Aula> listarAulas(int edificioId) throws Exception {
        Edificio edificio = buscarEdificioPorId(edificioId);

        if (edificio == null) {
            throw new IllegalArgumentException("No existe edificio con ID " + edificioId + ".");
        }

        return edificio.getAulas();
    }

    public void eliminarAula(int edificioId, int aulaId) throws Exception {
        Edificio edificio = buscarEdificioPorId(edificioId);

        if (edificio == null) {
            throw new IllegalArgumentException("No existe edificio con ID " + edificioId + ".");
        }

        boolean eliminado = edificio.eliminarAula(aulaId);

        if (!eliminado) {
            throw new IllegalArgumentException("No existe aula con ID " + aulaId + ".");
        }

        repo.actualizar(edificio);
    }

    public void eliminarEdificio(int id) throws Exception {
        Edificio edificio = buscarEdificioPorId(id);

        if (edificio == null) {
            throw new IllegalArgumentException("No existe edificio con ID " + id + ".");
        }

        if (!edificio.getAulas().isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el edificio porque todavía tiene aulas.");
        }

        repo.eliminar(id);
    }
}