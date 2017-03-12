package controllers;

import com.avaje.ebean.*;
import com.avaje.ebeaninternal.server.query.RawSqlQueryPlanKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.*;

import models.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmpleadoController extends Controller {

    public Result agregarEmpleado() {
        Empleado nuevoEmpleado = Json.fromJson(request().body().asJson(), Empleado.class);
        try {
            nuevoEmpleado.save();
            return created(Json.toJson(nuevoEmpleado));
        } catch (Exception e) {
            ObjectNode error = Json.newObject();
            error.put("mensaje","Ha ocurrido un error al agregar el empleado: "+e.getMessage());
            return badRequest(Json.toJson(error));
        }
    }

    public Result editarEmpleado(Long id) {
        Empleado nuevoEmpleado = Json.fromJson(request().body().asJson(), Empleado.class);
        try {
            Empleado empleado = Empleado.find.byId(id);
            empleado.nombre = nuevoEmpleado.nombre;
            empleado.cedula = nuevoEmpleado.cedula;
            empleado.activo = nuevoEmpleado.activo;
            empleado.update();
            return ok(Json.toJson(empleado));
        } catch (Exception e) {
            ObjectNode error = Json.newObject();
            error.put("mensaje","Ha ocurrido un error al editar el empleado: "+e.getMessage());
            return badRequest(Json.toJson(error));
        }
    }

    public Result eliminarEmpleado(Long id) {
        Empleado empleado = Empleado.find.byId(id);
        empleado.delete();
        return ok(Json.toJson(empleado));
    }

    public Result buscarEmpleados() {
        JsonNode jsonNode = request().body().asJson();
        String atributo = jsonNode.findPath("atributo").asText();
        atributo = atributo.toLowerCase();
        String valor = jsonNode.findPath("valor").asText();
        boolean activo = jsonNode.findPath("activo").asBoolean();
        if(activo) {
            List<Empleado> empleados = Empleado.find.where().and(Expr.ilike(atributo,"%"+valor+"%"),Expr.eq("activo",true)).findList();
            return ok(Json.toJson(empleados));
        } else {
            List<Empleado> empleados = Empleado.find.where().ilike(atributo,"%"+valor+"%").findList();
            return ok(Json.toJson(empleados));
        }
    }

    public Result darEmpleados() {
        List<Empleado> empleados = Empleado.find.all();
        return ok(Json.toJson(empleados));
    }

    public Result darEmpleadosActivos() {
        List<Empleado> empleados = Empleado.find.where().eq("activo",true).findList();
        return ok(Json.toJson(empleados));
    }

    public Result darEmpleadosSinCapacitaciones() {
        String query = "find empleado where asistencias.id is null";
        List<Empleado> empleados = Ebean.createQuery(Empleado.class,query).findList();
        return ok(Json.toJson(empleados));
    }

    public Result darEmpleadosSinCapacitacionesFecha() {
        JsonNode jsonNode = request().body().asJson();
        String fechaInicio = jsonNode.findPath("fechaInicio").asText();
        String fechaFinal = jsonNode.findPath("fechaFinal").asText();
        String sql = "SELECT id FROM empleado MINUS (SELECT e.id FROM empleado e INNER JOIN " +
                "asistencia a ON e.id=a.empleado_id " +
                "INNER JOIN capacitacion c ON a.capacitacion_id=c.id " +
                "WHERE c.fecha_inicio >= '"+fechaInicio+"' AND c.fecha_inicio <= '"+fechaFinal+"' " +
                "AND c.fecha_final >= '"+fechaInicio+"' AND c.fecha_final <= '"+fechaFinal+"')";
        List<SqlRow> sqlQuery = Ebean.createSqlQuery(sql).findList();
        List<Empleado> empleados = new ArrayList<>();
        for(SqlRow row : sqlQuery) {
            empleados.add(Empleado.find.byId(row.getLong("id")));
        }
        return ok(Json.toJson(empleados));
    }

    public Result darEmpleado(Long id) {
        Empleado empleado = Empleado.find.byId(id);
        return ok(Json.toJson(empleado));
    }
}