package base;

import java.util.HashMap;
import java.util.Map;

import design.ApiClient;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiBase implements ApiClient {
    private ThreadLocal<String> baseURI = new ThreadLocal<>();
    private ThreadLocal<String> authToken = new ThreadLocal<>();
    //private ThreadLocal<ObjectMapper> objectMapper = ThreadLocal.withInitial(ObjectMapper::new);
    private ThreadLocal<Map<String, String>> headers = ThreadLocal.withInitial(HashMap::new);

    public void setBaseURI(String newBaseURI) {
        baseURI.set(newBaseURI);
    }

    public void setAuthToken(String authToken) {
        this.authToken.set(authToken);
    }

    public void setCommonHeaders(Map<String, String> commonHeaders) {
        headers.set(commonHeaders);
    }

    public Response get(String endpoint) {
        return RestAssured.given().headers(getCommonHeaders()).when().get(baseURI.get() + endpoint);
    }

    public Response post(String endpoint, Object requestBody) {
        return RestAssured.given().headers(getCommonHeaders()).body(requestBody).when().post(baseURI.get() + endpoint);
    }

    public Response put(String endpoint, Object requestBody) {
        return RestAssured.given().headers(getCommonHeaders()).body(requestBody).when().put(baseURI.get() + endpoint);
    }

    public Response delete(String endpoint) {
        return RestAssured.given().headers(getCommonHeaders()).when().delete(baseURI.get() + endpoint);
    }

    private Map<String, String> getCommonHeaders() {
        Map<String, String> commonHeaders = headers.get();
        if (commonHeaders == null) {
            commonHeaders = new HashMap<>();
            headers.set(commonHeaders);
        }
        if (authToken.get() != null) {
            commonHeaders.put("Authorization", "Bearer " + authToken.get());
        }
        return commonHeaders;
    }

    public String getResponseBodyAsString(Response response) {
        return response.getBody().asString();
    }

    public void validateResponseStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    public void validateResponseSchema(Response response, String schemaFilePath) {
        // Implement JSON schema validation using your preferred library or method.
    }

//    public ObjectMapper getObjectMapper() {
//        return objectMapper.get();
//    }
}
