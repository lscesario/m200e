package br.com.datablink.m200e.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.datablink.m200e.util.TokenImporter;

@Path("/util")
public class UtilResource {
	@Inject
	TokenImporter ti;
	
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/import")
	public Response importTokens(@QueryParam("file_path") String file_path,
								 @QueryParam("transport_key") String transport_key){
		String entity=ti.importTokens(file_path, transport_key);
		return Response.status(200).entity(entity).build();
	}
}
