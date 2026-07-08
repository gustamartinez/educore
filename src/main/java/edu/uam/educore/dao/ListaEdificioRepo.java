package edu.uam.educore.dao;

import edu.uam.educore.model.infraestructura.Edificio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListaEdificioRepo extends Repositorio<Edificio> {

    private final List<Edificio> lista = new ArrayList<>();

    @Override
    public void guardar(Edificio edificio) {
        lista.add(edificio);
    }

    @Override
    public void actualizar(Edificio edificio) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == edificio.getId()) {
                lista.set(i, edificio);
                return;
            }
        }
    }

    @Override
    public void eliminar(int id) {
        lista.removeIf(edificio -> edificio.getId() == id);
    }

    @Override
    public Optional<Edificio> buscarPorId(int id) {
        return lista.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    @Override
    public List<Edificio> buscarTodos() {
        return new ArrayList<>(lista);
    }
}
