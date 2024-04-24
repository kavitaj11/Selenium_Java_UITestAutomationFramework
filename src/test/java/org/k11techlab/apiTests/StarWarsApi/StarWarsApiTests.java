package org.k11techlab.apiTests.StarWarsApi;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class StarWarsApiTests {

    static {
        baseURI = "https://swapi.dev/api";
    }

    @Test
    void testPeopleEndpointSuccess() {
        get("/people/")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    void testPeopleHeightOver200() {
        get("/people/")
                .then()
                .assertThat()
                .body("results.findAll { it.height.toInteger() > 200 }.name.flatten()",
                        hasItems("Darth Vader", "Chewbacca", "Roos Tarpals", "Rugor Nass", "Yarael Poof", "Lama Su", "Taun We", "Grievous", "Tarfful", "Tion Medon"));
    }

    @Test
    void testTotalPeopleCount() {
        get("/people/")
                .then()
                .assertThat()
                .body("count", equalTo(82));
    }
}
