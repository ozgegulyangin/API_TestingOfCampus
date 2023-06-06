import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
public class Nationalities {

    Faker faker = new Faker();
    RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {

        baseURI = "https://test.mersys.io";

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", "turkeyts");
        userInfo.put("password", "TechnoStudy123");
        userInfo.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(userInfo)
                        .when()
                        .post("/auth/login")
                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();


    }

    String name;
    String id;

    @Test
    public void createNationalities() {

        Map<String, String> doc = new HashMap<>();
        doc.put("name", faker.name().name());

        Response response =
                given()
                        .spec(requestSpec)
                        .body(doc)
                        .when()
                        .post("/school-service/api/nationality")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().response();

        id = response.path("id");
        name = response.path("name");



    }

    @Test(dependsOnMethods = "createNationalities")
    public void createNationalitiesNegative() {

        Map<String, String> doc = new HashMap<>();
        doc.put("name", name);


        given()
                .spec(requestSpec)
                .body(doc)
                .when()
                .post("/school-service/api/nationality")
                .then()
                .log().body()
                .statusCode(400)
        ;
    }


    @Test(dependsOnMethods = "createNationalitiesNegative")
    public void updateNationalities() {
        Map<String, String> doc = new HashMap<>();
        doc.put("name",faker.name().fullName());
        doc.put("id", id);

        given()
                .spec(requestSpec)
                .body(doc)
                .when()
                .put("/school-service/api/nationality")
                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "updateNationalities")
    public void deleteNationalities() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/nationality/"+id)
                .then()
                .log().body()
                .statusCode(200)
        ;
    }


    @Test(dependsOnMethods = "deleteNationalities")
    public void deleteNationalitiesNegative() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/nationality/"+id)
                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Nationality not  found"))
        ;
    }


}
