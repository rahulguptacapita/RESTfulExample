package com.rsr;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.http.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class TstClaimHeaderEntityTests {

	private static final String ID = "/0000000003";
	private static final String RESTPATH = "tstclaimheader";
	private static final String BASEURL = "http://localhost:8080/RESTfulExample/rest/";

	
	@Test
	public void getTest() {
		
		RestAssured.baseURI = BASEURL + RESTPATH;
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.request(Method.GET,"");
		
		String responseBody = response.getBody().asString();
		System.out.println("GET Response Body is =>  " + responseBody);
	}
	
	
	@Test
	public void getByIdTest() {
		
		RestAssured.baseURI =  BASEURL + RESTPATH + ID;
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.request(Method.GET, "");
		
		String responseBody = response.getBody().asString();
		System.out.println("GET by id Response Body is =>  " + responseBody);
	}
	
	@Test
	public void deleteByIdTest() {
		
		RestAssured.baseURI =  BASEURL + RESTPATH + ID;
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.request(Method.DELETE, "");
		
		String responseBody = response.getBody().asString();
		System.out.println("Delete by id Response Body is =>  " + responseBody);
	}
	
	
	@Test
	public void postTest() throws JSONException {
		
		
		RestAssured.baseURI =  BASEURL + RESTPATH;
		RequestSpecification httpRequest = RestAssured.given();
		
		JSONObject requestParams = new JSONObject();
		
		requestParams.put("FirstName", "Virender"); // Cast
		requestParams.put("LastName", "Singh");
		requestParams.put("UserName", "sdimpleuser2dd2011");
		requestParams.put("Password", "password1");
		requestParams.put("Email",  "sample2ee26d9@gmail.com");
		
		httpRequest.body(requestParams.toString());
		Response response = httpRequest.request(Method.POST);
		
		String responseBody = response.getBody().asString();
		System.out.println("Post Response Body is =>  " + responseBody);
		
	}
	
	
	
	@Test
	public void putTest() throws JSONException {
		
		
		RestAssured.baseURI =  BASEURL + RESTPATH + ID;
		RequestSpecification httpRequest = RestAssured.given();
		
		JSONObject requestParams = new JSONObject();
		
		requestParams.put("FirstName", "Virender"); // Cast
		requestParams.put("LastName", "Singh");
		requestParams.put("UserName", "sdimpleuser2dd2011");
		requestParams.put("Password", "password1");
		requestParams.put("Email",  "sample2ee26d9@gmail.com");
		
		httpRequest.body(requestParams.toString());
		Response response = httpRequest.request(Method.PUT);
		
		String responseBody = response.getBody().asString();
		System.out.println("Put Response Body is =>  " + responseBody);
		
	}
	
	
	
	

}
