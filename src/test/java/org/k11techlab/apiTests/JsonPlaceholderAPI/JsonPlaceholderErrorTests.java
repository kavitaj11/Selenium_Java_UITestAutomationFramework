package org.k11techlab.apiTests.JsonPlaceholderAPI;

import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.k11techlab.framework.apiHelper.ApiUtil;
import org.k11techlab.framework.apiHelper.Validator;

public class JsonPlaceholderErrorTests {
  @Test
  void testGetNonExistingPost() {
    String endpoint = ApiUtil.generateEndpoint("posts/999999"); // Assuming 999999 does not exist
    RequestSpecification request = ApiUtil.generateRequest();
    Response response = ApiUtil.sendRequest(request, "GET", endpoint);
    // Expecting a 404 Not Found
    Validator.validateStatusCode(404, ApiUtil.getStatusCode(response));
  }
  @Test
  void testPostWithInvalidData() {
    String endpoint = ApiUtil.generateEndpoint("posts");
    RequestSpecification request = ApiUtil.generateRequest();
    String invalidPostData = "{\"title\": \"\", \"body\": \"\", \"userId\": }"; // Incorrect JSON format
    Response response = ApiUtil.sendRequest(request, "POST", endpoint, invalidPostData);
    // Expecting a 400 Bad Request
    Validator.validateStatusCode(400, ApiUtil.getStatusCode(response));
  }
  @Test
  void testUpdateNonExistingPost() {
    String endpoint = ApiUtil.generateEndpoint("posts/9999999"); // Assuming 999999 does not exist
    RequestSpecification request = ApiUtil.generateRequest();
    String postData = "{\"title\": \"Updated\", \"body\": \"Updated body\", \"userId\": 1}";
    Response response = ApiUtil.sendRequest(request, "PUT", endpoint, postData);
    // Expecting a 404 Not Found
    Validator.validateStatusCode(404, ApiUtil.getStatusCode(response));
  }
  @Test
  void testPatchNonExistingPost() {
    String endpoint = ApiUtil.generateEndpoint("posts/9999999"); // Assuming 999999 does not exist
    RequestSpecification request = ApiUtil.generateRequest();
    String patchData = "{\"title\": \"Patched Title\"}";
    Response response = ApiUtil.sendRequest(request, "PATCH", endpoint, patchData);
    // Expecting a 404 Not Found
    Validator.validateStatusCode(404, ApiUtil.getStatusCode(response));
  }
  @Test
  void testDeleteNonExistingPost() {
    String endpoint = ApiUtil.generateEndpoint("posts/9999999"); // Assuming 999999 does not exist
    RequestSpecification request = ApiUtil.generateRequest();
    Response response = ApiUtil.sendRequest(request, "DELETE", endpoint);
    // Expecting a 404 Not Found
    Validator.validateStatusCode(404, ApiUtil.getStatusCode(response));
  }
}
