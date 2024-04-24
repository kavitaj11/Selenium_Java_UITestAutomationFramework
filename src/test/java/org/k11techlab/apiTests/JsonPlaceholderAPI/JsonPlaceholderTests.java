package org.k11techlab.apiTests.JsonPlaceholderAPI;

import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.k11techlab.framework.apiHelper.ApiUtil;
import org.k11techlab.framework.apiHelper.Validator;
import java.util.Map;
import java.util.HashMap;

public class JsonPlaceholderTests {


    @Test
    void testGetAllPosts() {
        String endpoint = ApiUtil.generateEndpoint("posts");
        RequestSpecification request = ApiUtil.generateRequest();
        Response response = ApiUtil.sendRequest(request, "GET", endpoint, null);


        Validator.validateStatusCode(200, ApiUtil.getStatusCode(response));
        Validator.validateResponseBodyContains("\"userId\":", ApiUtil.getResponseBody(response));
    }


    @Test
    void testCreatePost() {
        String endpoint = ApiUtil.generateEndpoint("posts");
        RequestSpecification request = ApiUtil.generateRequest();
        String postData = "{\"title\": \"New Post\", \"body\": \"This is a new post\", \"userId\": 1}";
        Response response = ApiUtil.sendRequest(request, "POST", endpoint, postData);


        Validator.validateStatusCode(201, ApiUtil.getStatusCode(response));
        Validator.validateResponseBodyContains("New Post",       ApiUtil.getResponseBody(response));
    }




    @Test
    void testCreatePostWithCustomHeaders() {
        String endpoint = ApiUtil.generateEndpoint("posts");
        RequestSpecification request = ApiUtil.generateRequest();


        // Adding custom headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Custom-Header", "Value123");
        request = ApiUtil.addHeaders(request, headers);


        // Generating data for the request
        String contentType = "application/json";
        String postData = "{\"title\": \"New Post\", \"body\": \"This is a new post\", \"userId\": 1}";
        request = ApiUtil.generateData(request, contentType, postData);


        // Making the HTTP request
        Response response = ApiUtil.sendRequest(request, "POST", endpoint);


        // Validate response
        Validator.validateStatusCode(201, ApiUtil.getStatusCode(response));
        Validator.validateResponseBodyContains("New Post",   ApiUtil.getResponseBody(response));
    }


}
