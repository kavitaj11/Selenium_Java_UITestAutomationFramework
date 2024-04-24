package org.k11techlab.apiTests.JsonPlaceholderAPI;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class JsonPlaceholderAPITests {
  @BeforeAll
  static void setup() {
    RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
  }
  @Test
  void testGetUsers() {
    when().get("/users")
        .then().statusCode(200)
        .and().body("size()", greaterThan(0));
  }
  // More detailed tests for each resource type
  @Test
  void testSpecificUser() {
    given().pathParam("userId", 1)
        .when().get("/users/{userId}")
        .then().statusCode(200)
        .and().body("id", equalTo(1));
  }
  @Test
  void testTodosForUser() {
    given().queryParam("userId", 1)
        .when().get("/todos")
        .then().statusCode(200)
        .and().body("size()", greaterThan(0))
        .and().body("findAll { it.userId == 1 }.size()", greaterThan(0));
  }
  @Test
  void testPostsForUser() {
    given().queryParam("userId", 1)
        .when().get("/posts")
        .then().statusCode(200)
        .and().body("size()", greaterThan(0))
        .and().body("findAll { it.userId == 1 }.size()", greaterThan(0));
  }
  @Test
  void testAlbumsForUser() {
    given().queryParam("userId", 1)
        .when().get("/albums")
        .then().statusCode(200)
        .and().body("size()", greaterThan(0))
        .and().body("findAll { it.userId == 1 }.size()", greaterThan(0));
  }
  @Test
  void testPhotosInAlbum() {
    given().queryParam("albumId", 1)
        .when().get("/photos")
        .then().statusCode(200)
        .and().body("size()", greaterThan(0))
        .and().body("findAll { it.albumId == 1 }.size()", greaterThan(0));
  }
  @Test
  void testCommentsOnPost() {
    given().queryParam("postId", 1)
        .when().get("/comments")
        .then().statusCode(200)
        .and().body("size()", greaterThan(0))
        .and().body("findAll { it.postId == 1 }.size()", greaterThan(0));
  }




/*Test cases for each of the routes. These will cover all GET, POST, PUT, PATCH, and DELETE methods as applicable to the /posts resource */
  @Test
  void testGetAllPosts() {
    when().get("/posts")
        .then().statusCode(200)
        .and().body("size()", greaterThan(0));
  }
  @Test
  void testGetSpecificPost() {
    given().pathParam("postId", 1)
        .when().get("/posts/{postId}")
        .then().statusCode(200)
        .and().body("id", equalTo(1));
  }
  @Test
  void testGetCommentsByPostId() {
    given().pathParam("postId", 1)
        .when().get("/posts/{postId}/comments")
        .then().statusCode(200)
        .and().body("size()", greaterThan(0))
        .and().body("findAll { it.postId == 1 }.size()", greaterThan(0));
  }
  @Test
  void testGetCommentsByQueryParam() {
    given().queryParam("postId", 1)
        .when().get("/comments")
        .then().statusCode(200)
        .and().body("size()", greaterThan(0))
        .and().body("findAll { it.postId == 1 }.size()", greaterThan(0));
  }
  @Test
  void testCreatePost() {
    with().body("{\"title\": \"New Post\", \"body\": \"This is a new post\", \"userId\": 1}")
       .contentType("application/json")
       .when().post("/posts")
       .then().statusCode(201)
       .and().body("title", equalTo("New Post"));
  }
  @Test
  void testUpdatePost() {
    given().pathParam("postId", 1)
        .body("{\"title\": \"Updated Post\", \"body\": \"This is an updated post\", \"userId\": 1}")
        .contentType("application/json")
        .when().put("/posts/{postId}")
        .then().statusCode(200)
        .and().body("title", equalTo("Updated Post"));
  }
  @Test
  void testPatchPost() {
    given().pathParam("postId", 1)
        .body("{\"title\": \"Patched Title\"}")
        .contentType("application/json")
        .when().patch("/posts/{postId}")
        .then().statusCode(200)
        .and().body("title", equalTo("Patched Title"));
  }
  @Test
  void testDeletePost() {
    given().pathParam("postId", 1)
        .when().delete("/posts/{postId}")
        .then().statusCode(200);
  }
}
