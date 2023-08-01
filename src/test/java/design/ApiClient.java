package design;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;

public interface ApiClient {
	
	 // HTTP GET method
    Response get(String endpoint);

    // HTTP POST method
    Response post(String endpoint, Object requestBody);

    // HTTP PUT method
    Response put(String endpoint, Object requestBody);

    // HTTP DELETE method
    Response delete(String endpoint);

    // Set base URI for the API
    void setBaseURI(String baseURI);

    // Set authentication token or credentials, if required
    void setAuthToken(String authToken);

    // Set common headers for all requests, if required
    void setCommonHeaders(Map<String, String> headers);

    // Get the response body as a String
    String getResponseBodyAsString(Response response);

    // Validate the response status code
    void validateResponseStatusCode(Response response, int expectedStatusCode);

    // Validate the response body against a JSON schema, if required
    void validateResponseSchema(Response response, String schemaFilePath);


}
