package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.*;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import models.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CapacitacionController extends Controller {

    public Result buscarCapacitaciones() {
        JsonNode jsonNode = request().body().asJson();
        String atributo = jsonNode.findPath("atributo").asText();
        atributo = atributo.toLowerCase();
        String valor = jsonNode.findPath("valor").asText();
        List<Capacitacion> capacitaciones = Capacitacion.find.where().ilike(atributo,"%"+valor+"%").findList();
        return ok(Json.toJson(capacitaciones));
    }

    public Result buscarCapacitacionesFecha() {
        try {
            JsonNode jsonNode = request().body().asJson();
            String fechaInicioString = jsonNode.findPath("fechaInicio").asText();
            String fechaFinalString = jsonNode.findPath("fechaFinal").asText();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicio = format.parse(fechaInicioString);
            Date fechaFinal = format.parse(fechaFinalString);
            List<Capacitacion> capacitaciones = Capacitacion.find.where().and(Expr.between("fechaInicio",fechaInicio,fechaFinal),Expr.between("fechaFinal",fechaInicio,fechaFinal)).findList();
            return ok(Json.toJson(capacitaciones));
        } catch (Exception e) {
            ObjectNode error = Json.newObject();
            error.put("mensaje","Error al buscar las capacitaciones: "+e.getMessage());
            return badRequest(error);
        }
    }

    public Result darCapacitaciones() {
        List<Capacitacion> capacitaciones = Capacitacion.find.all();
        return ok(Json.toJson(capacitaciones));
    }

    public Result darCapacitacion(Long id) {
        Capacitacion capacitacion = Capacitacion.find.byId(id);
        return ok(Json.toJson(capacitacion));
    }

    public Result agregarCapacitacion() {
        Capacitacion capacitacion = Json.fromJson(request().body().asJson(), Capacitacion.class);
        try {
            capacitacion.save();
            return created(Json.toJson(capacitacion));
        } catch (Exception e) {
            ObjectNode error = Json.newObject();
            error.put("mensaje","Ha ocurrido un error al agregar la capacitación: "+e.getMessage());
            return badRequest(Json.toJson(error));
        }
    }

    public Result editarCapacitacion(Long id) {
        Capacitacion nuevaCapacitacion = Json.fromJson(request().body().asJson(), Capacitacion.class);
        try {
            Capacitacion capacitacion = Capacitacion.find.byId(id);
            capacitacion.descripcion = nuevaCapacitacion.descripcion;
            capacitacion.fechaInicio = nuevaCapacitacion.fechaInicio;
            capacitacion.fechaFinal = nuevaCapacitacion.fechaFinal;
            capacitacion.area = nuevaCapacitacion.area;
            capacitacion.lugar = nuevaCapacitacion.lugar;
            capacitacion.facilitador = nuevaCapacitacion.facilitador;
            capacitacion.observaciones = nuevaCapacitacion.observaciones;
            capacitacion.costo = nuevaCapacitacion.costo;
            capacitacion.update();
            return ok(Json.toJson(capacitacion));
        } catch (Exception e) {
            ObjectNode error = Json.newObject();
            error.put("mensaje","Ha ocurrido un error al editar la capacitación: "+e.getMessage());
            return badRequest(Json.toJson(error));
        }
    }

    public Result eliminarCapacitacion(Long id) {
        Capacitacion capacitacion = Capacitacion.find.byId(id);
        capacitacion.delete();
        return ok(Json.toJson(capacitacion));
    }

    public Result cargarArchivo() throws Exception {
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart excel = body.getFile("excel");
        if(excel!=null) {
            File file = (File)excel.getFile();
            Workbook wb = WorkbookFactory.create(file);
            Sheet sheet = wb.getSheetAt(1);
            Iterator<Row> rows = sheet.rowIterator();
            //Avanza el título
            rows.next();
            //Avanza la cabecera
            rows.next();
            int c = 0;
            while(rows.hasNext()) {
                c++;
                Row row = rows.next();
                Cell cell = row.getCell(1);
                if(cell==null||cell.getStringCellValue().equals("")) {
                    break;
                }
                else {
                    String error = "";
                    try {
                        error = "Se ha encontrado un error al cargar el área de la capacitación en el registro #"+c;
                        String area = cell.getStringCellValue();
                        cell = row.getCell(2);
                        error = "Se ha encontrado un error al cargar el nombre del empleado en el registro #"+c;
                        String nombre = cell.getStringCellValue();
                        cell = row.getCell(3);
                        error = "Se ha encontrado un error al cargar la cédula del empleado en el registro #"+c;
                        int cedula = (int)cell.getNumericCellValue();
                        cell = row.getCell(4);
                        error = "Se ha encontrado un error al cargar la fecha de inicio de la capacitación en el registro #"+c;
                        Date fechaInicio = cell.getDateCellValue();
                        cell = row.getCell(5);
                        error = "Se ha encontrado un error al cargar la fecha final de la capacitación en el registro #"+c;
                        Date fechaFinal = cell.getDateCellValue();
                        cell = row.getCell(6);
                        error = "Se ha encontrado un error al cargar la descripción de la capacitación en el registro #"+c;
                        String descripcion = cell.getStringCellValue();
                        cell = row.getCell(7);
                        error = "Se ha encontrado un error al cargar el lugar de la capacitación en el registro #"+c;
                        String lugar = cell.getStringCellValue();
                        cell = row.getCell(8);
                        error = "Se ha encontrado un error al cargar el número de horas en el registro #"+c;
                        double horas = cell.getNumericCellValue();
                        cell = row.getCell(9);
                        error = "Se ha encontrado un error al cargar el facilitador de la capacitación en el registro #"+c;
                        String facilitador = cell.getStringCellValue();
                        cell = row.getCell(10);
                        error = "Se ha encontrado un error al cargar el área de la capacitación en el registro #"+c;
                        double costo = cell.getNumericCellValue();
                        cell = row.getCell(11);
                        error = "Se ha encontrado un error al cargar las observaciones de la capacitación en el registro #"+c;
                        String observaciones = cell.getStringCellValue();
                        error = "Ha ocurrido un error al persistir la información del empleado en el registro #"+c;
                        Transaction transaction = Ebean.beginTransaction();
                        Empleado empleado = Empleado.find.where().eq("cedula",cedula).findUnique();
                        Empleado nuevoEmpleado = new Empleado();
                        if(empleado==null) {
                            nuevoEmpleado.nombre = nombre;
                            nuevoEmpleado.cedula = cedula;
                            nuevoEmpleado.save();
                        }
                        error = "Ha ocurrido un error al persistir la información de la capacitación en el registro #"+c;
                        List<Capacitacion> capacitaciones = Capacitacion.find.where().conjunction()
                                .add(Expr.ilike("descripcion",descripcion))
                                .add(Expr.eq("fechaInicio",fechaInicio))
                                .add(Expr.eq("fechaFinal",fechaFinal))
                                .findList();
                        Capacitacion nuevaCapacitacion = new Capacitacion();
                        if(capacitaciones.isEmpty()) {
                            nuevaCapacitacion.area = area;
                            nuevaCapacitacion.costo = costo;
                            nuevaCapacitacion.descripcion = descripcion;
                            nuevaCapacitacion.facilitador = facilitador;
                            nuevaCapacitacion.fechaInicio = fechaInicio;
                            nuevaCapacitacion.fechaFinal = fechaFinal;
                            nuevaCapacitacion.lugar = lugar;
                            nuevaCapacitacion.observaciones = observaciones;
                            nuevaCapacitacion.save();
                        }
                        error = "Ha ocurrido un error al persistir la información de la asistencia en el registro #"+c;;
                        Asistencia asistencia = new Asistencia();
                        asistencia.empleado = nuevoEmpleado;
                        asistencia.capacitacion = nuevaCapacitacion;
                        asistencia.horas = horas;
                        asistencia.save();
                        transaction.commit();
                    }
                    catch(Exception e) {
                        ObjectNode errorJson = Json.newObject();
                        errorJson.put("mensaje",error);
                        return badRequest(errorJson);
                    }
                }
            }
            return ok();
        }
        ObjectNode error = Json.newObject();
        error.put("mensaje","Error en el archivo enviado");
        return badRequest(error);
    }
}
