package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.*;

import models.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AsistenciaController extends Controller {

    public Result agregarAsistencias() {
        Ebean.beginTransaction();
        ObjectNode error = Json.newObject();
        try {
            JsonNode jsonNode = request().body().asJson();
            double horas = jsonNode.get("horas").asDouble();
            JsonNode jsonCapacitacion = jsonNode.get("capacitacion");
            Capacitacion capacitacion = Json.fromJson(jsonCapacitacion, Capacitacion.class);
            for(JsonNode jsonEmpleado:jsonNode.get("empleados")) {
                Empleado empleado = Json.fromJson(jsonEmpleado, Empleado.class);
                Asistencia asistencia = new Asistencia();
                asistencia.empleado = empleado;
                asistencia.capacitacion = capacitacion;
                asistencia.horas = horas;
                asistencia.save();
            }
            Ebean.commitTransaction();
            return created();
        } catch (Exception e) {
            error.put("mensaje","Ha ocurrido un error al agregar las asistencias: "+e.getMessage());
            Ebean.rollbackTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return badRequest(error);
    }

    public Result darAsistencias() {
        List<Asistencia> asistencias = Asistencia.find.all();
        return ok(Json.toJson(asistencias));
    }

    public Result obtenerAsistenciasEmpleado(Long id) {
        List<Asistencia> asistencias = Asistencia.find.where().eq("empleado.id",id).findList();
        return ok(Json.toJson(asistencias));
    }

    public Result obtenerAsistenciasEmpleadoFechas(Long id) {
        try {
            JsonNode jsonNode = request().body().asJson();
            String fechaInicioString = jsonNode.findPath("fechaInicio").asText();
            String fechaFinalString = jsonNode.findPath("fechaFinal").asText();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicio = format.parse(fechaInicioString);
            Date fechaFinal = format.parse(fechaFinalString);
            List<Asistencia> asistencias = Asistencia.find.where().and(Expr.eq("empleado.id",id),Expr.and(Expr.between("capacitacion.fechaInicio",fechaInicio,fechaFinal),Expr.between("capacitacion.fechaFinal",fechaInicio,fechaFinal))).findList();
            return ok(Json.toJson(asistencias));
        } catch (Exception e) {
            ObjectNode error = Json.newObject();
            error.put("mensaje","Error al buscar las capacitaciones: "+e.getMessage());
            return badRequest(error);
        }
    }

    public Result obtenerAsistenciasEmpleadoCapacitacion(Long id) {
        JsonNode jsonNode = request().body().asJson();
        String atributo = jsonNode.findPath("atributo").asText();
        atributo = atributo.toLowerCase();
        String valor = jsonNode.findPath("valor").asText();
        List<Asistencia> asistencias = Asistencia.find.where().and(Expr.ilike("capacitacion."+atributo,"%"+valor+"%"),Expr.eq("empleado.id",id)).findList();
        return ok(Json.toJson(asistencias));
    }

    public Result obtenerAsistenciasCapacitacionEmpleado(Long id) {
        JsonNode jsonNode = request().body().asJson();
        String atributo = jsonNode.findPath("atributo").asText();
        atributo = atributo.toLowerCase();
        String valor = jsonNode.findPath("valor").asText();
        List<Asistencia> asistencias = Asistencia.find.where().and(Expr.ilike("empleado." + atributo, "%" + valor + "%"), Expr.eq("capacitacion.id", id)).findList();
        return ok(Json.toJson(asistencias));
    }

    public Result obtenerAsistenciasCapacitacion(Long id) {
        List<Asistencia> asistencias = Asistencia.find.where().eq("capacitacion.id",id).findList();
        return ok(Json.toJson(asistencias));
    }
}