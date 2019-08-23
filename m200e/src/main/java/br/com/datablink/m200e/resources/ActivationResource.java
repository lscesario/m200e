package br.com.datablink.m200e.resources;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.datablink.m200e.activator.ActivationControl;
import br.com.datablink.m200e.models.Token;

@Path("/activate")
public class ActivationResource {

	@Inject
	ActivationControl ac;
		
	//TODO: IMPLEMENTAR ASSOCIAÇÃO POR ID DE USUÁRIO
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/step1")
	public Response activateStep1(Token token){
		
		try {

			System.out.println("ActivationResource - activateStep1 - Iniciando WS de ativação passo 1");
			System.out.println("ActivationResource - activateStep1 - "+token);
			String entity=ac.activateStep1(token);
			entity = Json.createObjectBuilder().add("data",entity).build().toString(); 
			return Response.status(200).entity(entity).build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return Response.status(401).build();
	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/step2")
	public Response activateStep2(Token token){
		try{
			System.out.println("ActivationResource - activateStep2 - Iniciando WS de ativação passo 2");
			System.out.println("ActivationResource - activateStep2 - "+token);
			String entity=ac.activateStep2(token);
			entity = Json.createObjectBuilder().add("data",entity).build().toString(); 
			return Response.status(200).entity(entity).build();
		}catch(Exception e){
			// TODO: handle exception
			//TODO: Testing Github comm protocol
			e.printStackTrace();
		}
		return Response.status(401).build();
	}
}
