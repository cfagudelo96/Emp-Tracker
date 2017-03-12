package models;

import javax.persistence.*;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

@Entity
public class Asistencia extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne(fetch=FetchType.EAGER)
    @Constraints.Required
    public Empleado empleado;

    @ManyToOne(fetch=FetchType.EAGER)
    @Constraints.Required
    public Capacitacion capacitacion;

    @Constraints.Required
    @Constraints.Min(0)
    public double horas;

    public Asistencia() {
    }

    public static Finder<Long, Asistencia> find = new Finder<Long,Asistencia>(Asistencia.class);
}