package br.com.datablink.m200e.resources;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.datablink.m200e.activator.UsuarioControl;
import br.com.datablink.m200e.models.Usuario;

@Path("/users")
public class UserResource {
	
	@Inject
	UsuarioControl uc;
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTest(){
		String response = "User service v 1.0 - Alive";
		return Response.status(200).entity(response).build();
	}
	
	
	@POST 
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/create")
	public Response createUser(Usuario user){
		if (user == null) throw new BadRequestException();
		uc.createUser(user);
		String result="User created: "+user;
		return Response.status(201).entity(result).build();
	}

}
