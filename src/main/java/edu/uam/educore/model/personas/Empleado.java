package edu.uam.educore.model.personas;

import java.time.LocalDate;

public class Empleado extends Persona {

    private double salario;
    private LocalDate fechaIngreso;
    private TipoEmpleado tipo;

    public Empleado(int id, String nombre, String apellidos, String email,
            double salario, LocalDate fechaIngreso, TipoEmpleado tipo) {

        super(id, nombre, apellidos, email);
        this.salario = salario;
        this.fechaIngreso = fechaIngreso;
        this.tipo = tipo;
    }

    public double getSalario() {
        return salario;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public TipoEmpleado getTipoEmpleado() {
        return tipo;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public void setTipoEmpleado(TipoEmpleado tipo) {
        this.tipo = tipo;
    }

    @Override
    public String getInfo() {
        return String.format(
                "%s: %s %s | Salario: ₡%.2f",
                tipo,
                getNombre(),
                getApellidos(),
                salario
        );
    }

    @Override
    public String getTipo() {
        return tipo.name();
    }
}