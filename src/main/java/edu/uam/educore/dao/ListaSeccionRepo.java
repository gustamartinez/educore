package edu.uam.educore.dao;

import edu.uam.educore.model.academico.Seccion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListaSeccionRepo extends Repositorio<Seccion> {

    private final List<Seccion> lista = new ArrayList<>();

    @Override
    public void guardar(Seccion seccion) {
        lista.add(seccion);
    }

    @Override
    public void actualizar(Seccion seccion) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == seccion.getId()) {
                lista.set(i, seccion);
                return;
            }
        }
    }

    @Override
    public void eliminar(int id) {
        lista.removeIf(seccion -> seccion.getId() == id);
    }

    @Override
    public Optional<Seccion> buscarPorId(int id) {
        return lista.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }

    @Override
    public List<Seccion> buscarTodos() {
        return new ArrayList<>(lista);
    }
}
