package com.rsr.rest;
 
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.rsr.entity.TstClaimHeaderEntityColumn;
 
@Path("/tstclaimheader")
public class TstClaimHeaderController {
	
	private static final String TABLE_NAME = "TST_CLAIM_HEADER"; 
	private static final String PRIMARY_KEY = "CLAIM_ID";
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEntity() throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		
		DBEntity entity = new TstClaimHeaderEntity();
		JSONArray jsonEntity = entity.getEntity(TABLE_NAME);
		return Response.status(200).entity(jsonEntity).build();
	}
	
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postEntity(JSONObject entity) throws JSONException {
		
		validateEntity(entity);
	//	removePrimaryKey(entity);
		
		DBEntity claimHeaderEntity = new TstClaimHeaderEntity();
		claimHeaderEntity.postEntity(TABLE_NAME, PRIMARY_KEY, entity);
		
		return Response.status(200).entity(entity).build();
	}
	
	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEntity(@PathParam("id") String id) throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		
		DBEntity entity = new TstClaimHeaderEntity();
		JSONArray jsonEntity = entity.deleteEntity(TABLE_NAME, id);
		return Response.status(200).entity(jsonEntity).build();
	}
	
	

//
//	private void removePrimaryKey(JSONObject entity) {
//		entity.remove(PRIMARY_KEY);
//	}


	private void validateEntity(JSONObject entity) throws JSONException {
		
		for (TstClaimHeaderEntityColumn tstClaimHeaderEntityColumn : TstClaimHeaderEntityColumn.values()) {
			if(PRIMARY_KEY.equalsIgnoreCase(tstClaimHeaderEntityColumn.name())) {
				continue;
			}
			Object columnValue = entity.get(tstClaimHeaderEntityColumn.name());
			if(columnValue == null) {
				throw new RuntimeException("cannot find " + tstClaimHeaderEntityColumn.name());
			}
		}
		if(TstClaimHeaderEntityColumn.values().length != entity.length() && TstClaimHeaderEntityColumn.values().length - 1 == entity.length()) {
			throw new RuntimeException("More then enough parameter found ");
		}
	}
	
	
 
}