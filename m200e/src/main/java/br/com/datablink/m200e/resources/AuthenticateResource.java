package br.com.datablink.m200e.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.datablink.m200e.authenticators.CheckTOTP;

@Path("/authenticate")
public class AuthenticateResource {

	@Inject
	CheckTOTP a;
	
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/totp")
	public Response authenticateTOTP(@QueryParam("serial_number") String serial_number,
								 @QueryParam("otp") String otp){
		String entity=a.authenticateTOTP(serial_number, otp);
		return Response.status(200).entity(entity).build();
	}
}