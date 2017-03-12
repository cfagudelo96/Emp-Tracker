package models;

import javax.persistence.*;

import com.avaje.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Constraints;
import play.data.format.Formats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Capacitacion extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Constraints.Required
    @Constraints.MaxLength(300)
    public String descripcion;

    @Constraints.Required
    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date fechaInicio;

    @Constraints.Required
    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date fechaFinal;

    @Constraints.Required
    public String area;

    @Constraints.Required
    @Constraints.MaxLength(300)
    public String lugar;

    @Constraints.Required
    @Constraints.MaxLength(140)
    public String facilitador;

    @Constraints.MaxLength(500)
    public String observaciones;

    @Constraints.Min(0)
    public double costo;

    @JsonIgnore
    @OneToMany(mappedBy = "capacitacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Asistencia> asistencias;

    public Capacitacion() {
        asistencias = new ArrayList<>();
    }

    public static Finder<Long, Capacitacion> find = new Finder<Long,Capacitacion>(Capacitacion.class);
}