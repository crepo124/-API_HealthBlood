/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.lasalle.controller.ControllerMedida;
import org.lasalle.controller.ControllerUsuario;
import org.lasalle.model.Medida;
import org.lasalle.model.Usuario;

/**
 *
 * @author vdjun
 */
@Path("glucosa")
public class RestGlucosa {
    @Path("Saludar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar(){
        String respuesta = """
                           {"response": "Hola desde Java"}
                           """;
        return Response.ok(respuesta).build();
    }
    @Path("SaludarPersonalizado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludarPersonalizado(
            @QueryParam("nombre") String nombre
    ){
        String respuesta = """
                           {"response" : "Hola %s desde java}
                           """;
        respuesta = String.format(respuesta, nombre);
        return Response.ok(respuesta).build();
    }
    @Path("Numero")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response cuadradoNumero(
            @QueryParam("numero") int numero
    ){
        String respuesta = """
                           {"response" : "De este numero %s su cuadrado %s}
                           """;
        respuesta = String.format(respuesta,numero, numero*numero);
        return Response.ok(respuesta).build();
    }
    
    @Path("getAllUsuarios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsuario(){
        ControllerUsuario cu = new ControllerUsuario();
        List<Usuario> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = cu.getUsuarios();
            out = gson.toJson(lista);
        }catch (Exception e) {
            e.printStackTrace();
            out = """
                  {"result": "Lista vacia"
                  """;
        }
        return Response.ok(out).build();
    }

    @Path("saveUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveUsuario(
            @FormParam("nombre") String nombre,
            @FormParam("primerApellido") String primerApellido,
            @FormParam("segundoApellido") String segundoApellido,
            @FormParam("email") String email
    ){
        Gson gson = new Gson();
        if (nombre == null || nombre.isBlank() || email == null || email.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(Map.of("result", "nombre y email son obligatorios")))
                    .build();
        }
        try{
            Usuario user = new Usuario(0, nombre, primerApellido, segundoApellido, email);
            Usuario creado = new ControllerUsuario().insertUsuario(user);
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(creado))
                    .build();
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(Map.of("result", "Error al guardar el usuario")))
                    .build();
        }
    }

    @Path("getAllMedidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMedidas(){
        Gson gson = new Gson();
        try{
            List<Medida> lista = new ControllerMedida().getMedidas();
            return Response.ok(gson.toJson(lista)).build();
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(Map.of("result", "Error al obtener las medidas")))
                    .build();
        }
    }

    @Path("getMedidasByUsuario/{idUsuario}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedidasByUsuario(
            @PathParam("idUsuario") int idUsuario
    ){
        Gson gson = new Gson();
        if (idUsuario <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(Map.of("result", "idUsuario debe ser mayor que 0")))
                    .build();
        }
        try{
            List<Medida> lista = new ControllerMedida().getMedidasByUsuario(idUsuario);
            if (lista.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(gson.toJson(Map.of("result", "No hay medidas para este usuario")))
                        .build();
            }
            return Response.ok(gson.toJson(lista)).build();
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(Map.of("result", "Error al obtener las medidas")))
                    .build();
        }
    }

    @Path("saveMedida")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveMedida(
            @FormParam("idUsuario") Integer idUsuario,
            @FormParam("glucosa") Integer glucosa,
            @FormParam("contexto") String contexto,
            @FormParam("notas") String notas
    ){
        Gson gson = new Gson();
        if (idUsuario == null || idUsuario <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(Map.of("result", "idUsuario es obligatorio y debe ser mayor que 0")))
                    .build();
        }
        if (glucosa == null || glucosa <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(Map.of("result", "glucosa es obligatoria y debe ser mayor que 0")))
                    .build();
        }
        if (contexto != null && contexto.length() > 40) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(Map.of("result", "contexto no puede superar los 40 caracteres")))
                    .build();
        }
        if (notas != null && notas.length() > 100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(Map.of("result", "notas no puede superar los 100 caracteres")))
                    .build();
        }
        try{
            Medida medida = new Medida(0, idUsuario, glucosa, null, contexto, notas);
            Medida creada = new ControllerMedida().insertMedida(medida);
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(creada))
                    .build();
        }catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            return Response.status(Response.Status.CONFLICT)
                    .entity(gson.toJson(Map.of("result", "El usuario indicado no existe")))
                    .build();
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(Map.of("result", "Error al guardar la medida")))
                    .build();
        }
    }
}
