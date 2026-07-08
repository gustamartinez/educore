package edu.uam.educore.model.infraestructura;

public class Aula {

    private int id;
    private String numero;
    private int capacidad;
    private TipoAula tipo;
    private Edificio edificio;

    public Aula(int id, String numero, int capacidad, TipoAula tipo, Edificio edificio) {

        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("El número del aula es obligatorio.");
        }

        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de aula es obligatorio.");
        }

        if (edificio == null) {
            throw new IllegalArgumentException("El edificio es obligatorio.");
        }

        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.tipo = tipo;
        this.edificio = edificio;
    }

    public int getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public TipoAula getTipo() {
        return tipo;
    }

    public Edificio getEdificio() {
        return edificio;
    }

    public void setNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("El número del aula es obligatorio.");
        }
        this.numero = numero;
    }

    public void setCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
        this.capacidad = capacidad;
    }

    public void setTipo(TipoAula tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de aula es obligatorio.");
        }
        this.tipo = tipo;
    }

    public String getInfo() {
        return String.format(
                "Aula %s | %s | Capacidad: %d",
                numero,
                tipo,
                capacidad);
    }
}