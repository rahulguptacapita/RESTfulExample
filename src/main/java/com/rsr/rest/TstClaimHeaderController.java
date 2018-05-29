package com.rsr.rest;
 
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.rsr.entity.DBEntity;
import com.rsr.entity.TstClaimHeaderEntity;
 
@Path("/tstclaimheader")
public class TstClaimHeaderController {
	
	private static final String TABLE_NAME = "TST_CLAIM_HEADER"; 
	private static final String PRIMARY_KEY = "CLAIM_ID";
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEntities() throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		
		DBEntity entity = new TstClaimHeaderEntity();
		JSONArray jsonEntity = entity.getEntities(TABLE_NAME);
		return Response.status(200).entity(jsonEntity).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEntity(@PathParam("id") String id) throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		
		DBEntity entity = new TstClaimHeaderEntity();
		JSONObject jsonEntity = entity.getEntity(TABLE_NAME, id);
		return Response.status(200).entity(jsonEntity).build();
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postEntity(JSONObject entity) throws JSONException {
		
		DBEntity dbEntity = new TstClaimHeaderEntity();
		dbEntity.validateEntity(entity, PRIMARY_KEY);
		dbEntity.postEntity(TABLE_NAME, PRIMARY_KEY, entity);
		return Response.status(200).entity(entity).build();
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEntity(@PathParam("id") String id) throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		
		DBEntity entity = new TstClaimHeaderEntity();
		JSONObject jsonEntity = entity.deleteEntity(TABLE_NAME, id);
		return Response.status(200).entity(jsonEntity).build();
	}
	
	
	
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putEntity(@PathParam("id") String id, JSONObject entity) throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		
		DBEntity dbEntity = new TstClaimHeaderEntity();
		dbEntity.validateEntity(entity, PRIMARY_KEY);

		JSONObject getResponseEntity = dbEntity.getEntity(TABLE_NAME, id);
		if(getResponseEntity.has("message")) {
			String s = (String) getResponseEntity.get("message");
			if(s.equalsIgnoreCase("no recored found")) {
				return Response.status(200).entity(getResponseEntity).build();
			}
		}
		dbEntity.putEntity(TABLE_NAME, PRIMARY_KEY, entity, id);
		return Response.status(200).entity(getResponseEntity).build();
	}
	
	
	
	
}