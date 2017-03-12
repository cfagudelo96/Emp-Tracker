package models;

import javax.persistence.*;

import com.avaje.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Constraints;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Empleado extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Constraints.Required
    @Constraints.MaxLength(140)
    public String nombre;

    @Column(unique=true)
    @Constraints.Required
    public int cedula;

    @Constraints.Required
    public boolean activo;

    @JsonIgnore
    @OneToMany(mappedBy = "empleado", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Asistencia> asistencias;

    public Empleado() {
        asistencias = new ArrayList<>();
        this.activo = true;
    }

    public static Finder<Long, Empleado> find = new Finder<Long,Empleado>(Empleado.class);
}