package scenario;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import base.ApiBase;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class PaymentGatewayAPITest {

	ApiBase apiBase;
	String purchaseEndPoint ="api/v1/payment/card";
	//create similar endpoint for Refund, Reversal, Dispute
	Response response;
	String reponseStringBody ="";
	JsonPath jsonpath;
	Map<String,String> header= new HashMap<>(); 
	String authToken;

	@BeforeClass
	public void beforeClass() {
		
		header.put("", "");
		header.put("", "");
		
		apiBase = new ApiBase();
		apiBase.setBaseURI("http://localhost:5100/");
		apiBase.setCommonHeaders(header);
	}
	
	@Test//test written for local wiremock
	public void testcase01() {
		response = apiBase.get(purchaseEndPoint);	
		int statusCode = response.getStatusCode();
		System.out.println(statusCode);
		String responseBodyAsString = apiBase.getResponseBodyAsString(response);
		jsonpath = response.jsonPath();
		String object = jsonpath.get("data.data[0].customer_email");
		System.out.println(object);
		assertEquals("shalithax@gmail.com", object);
	}

	// Smoke Testing
	@Test(groups = { "Smoke" })
	public void testAPIAccessibility() {
		response = apiBase.get("assuming this the base endpoint with most priority");
		assertEquals(response.getStatusCode(), 200);
	}

	// Functional Testing - Purchase API
	@Test(groups = { "Functional" })
	public void testValidPurchaseRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("amount", 100);
		requestBody.put("item", "Product A");
		requestBody.put("currency", "USD");
		requestBody.put("paymentMethod", "Credit Card");

		//POST
		response = apiBase.post(purchaseEndPoint, requestBody);
		assertEquals(response.getStatusCode(), 201);

		String paymentDetails = response.jsonPath().getString("paymentDetails");
		String accountBalance = response.jsonPath().getString("accountBalance");

		//assert response
		assertEquals(paymentDetails, "Payment successful");
		assertEquals(accountBalance, "Updated balance amount");
	}

	@Test(groups = { "Functional" })
	public void testInvalidPurchaseRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("amount", 100);
		requestBody.put("item", "Product A");
		response = apiBase.post("/purchase", requestBody);
		assertEquals(response.getStatusCode(), 400);
		String errorMessage = response.jsonPath().getString("error");
		assertEquals(errorMessage, "Currency is required");
	}

	@Test(groups = { "Functional" })
	public void testValidRefundRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("transactionId", "123456789");
		requestBody.put("refundAmount", 50);
		response = apiBase.post("/refund", requestBody);
		assertEquals(response.getStatusCode(), 200);
		String refundedAmount = response.jsonPath().getString("refundedAmount");
		String accountBalance = response.jsonPath().getString("accountBalance");
		assertEquals(refundedAmount, "50");
		assertEquals(accountBalance, "Updated balance amount");

	}

	@Test(groups = { "Functional" })
	public void testInvalidRefundRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("transactionId", "non_existent_id");
		requestBody.put("refundAmount", 50);
		response = apiBase.post("/refund", requestBody);
		assertEquals(response.getStatusCode(), 404);
		String errorMessage = response.jsonPath().getString("error");
		assertEquals(errorMessage, "Transaction not found");
	}

	@Test(groups = { "Functional" })
	public void testValidReversalRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("transactionId", "123456789");
		response = apiBase.post("/reversal", requestBody);
		assertEquals(response.getStatusCode(), 200);
		String successMessage = response.jsonPath().getString("message");
		assertEquals(successMessage, "Transaction successfully reversed");
	}

	@Test(groups = { "Functional" })
	public void testInvalidReversalRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("transactionId", "settled_transaction_id");
		response = apiBase.post("/reversal", requestBody);
		assertEquals(response.getStatusCode(), 400);
		String errorMessage = response.jsonPath().getString("error");
		assertEquals(errorMessage, "Transaction is already settled and cannot be reversed");	
	}

	@Test(groups = { "Functional" })
	public void testValidDisputeRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("transactionId", "123456789");
		requestBody.put("disputeDetails", "Product not delivered");
		response = apiBase.post("/dispute", requestBody);
		assertEquals(response.getStatusCode(), 200);
		String disputeDetails = response.jsonPath().getString("disputeDetails");
		String disputeStatus = response.jsonPath().getString("status");
		assertEquals(disputeDetails, "Product not delivered");
		assertEquals(disputeStatus, "Open");
	}

	@Test(groups = { "Functional" })
	public void testInvalidDisputeRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("transactionId", "123456789");
		response = apiBase.post("/dispute", requestBody);
		assertEquals(response.getStatusCode(), 400);
		String errorMessage = response.jsonPath().getString("error");
		assertEquals(errorMessage, "Dispute details are required");
	}
}



