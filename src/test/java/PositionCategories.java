import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class PositionCategories {
    Faker faker = new Faker();

    String positionCategoriesID;
    String positionCategoriesName;
    RequestSpecification recSpec;


    @BeforeClass
    public void Login() {

        baseURI = "https://test.mersys.io";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =

                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();

    }

    @Test
    public void createPositionCategories() {

        Map<String, String> positionCategories = new HashMap<>();

        positionCategoriesName = (faker.name().fullName());

        positionCategories.put("name", positionCategoriesName);

        positionCategoriesID =
                given()
                        .spec(recSpec)
                        .body(positionCategories)
                        //.log().body()

                        .when()
                        .post("/school-service/api/position-category")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;

    }

    @Test(dependsOnMethods = "createPositionCategories")
    public void createPositionCategoriesNegative() {

        Map<String, String> positionCategories = new HashMap<>();

        positionCategories.put("name", positionCategoriesName);


        given()
                .spec(recSpec)
                .body(positionCategories)
                //.log().body()

                .when()
                .post("/school-service/api/position-category")

                .then()
                //.log().all()
                .statusCode(400)
                .body("message", containsString("already"))

        ;
    }

    @Test(dependsOnMethods = "createPositionCategoriesNegative")
    public void updatePositionCategories() {
        Map<String, String> positionCategories = new HashMap<>();
        positionCategories.put("id", positionCategoriesID);

        positionCategoriesName = "Hello API 5e87";

        positionCategories.put("name", positionCategoriesName);

        given()
                .spec(recSpec)
                .body(positionCategories)
                //.log().body()

                .when()
                .put("/school-service/api/position-category")

                .then()
                //.log().all()
                .statusCode(200)
                .body("name", equalTo(positionCategoriesName))

        ;
    }

    @Test(dependsOnMethods = "updatePositionCategories")
    public void deletePositionCategories() {
        given()
                .spec(recSpec)
                .pathParam("positionCategoriesID", positionCategoriesID)
                //.log().uri()

                .when()
                .delete("/school-service/api/position-category/{positionCategoriesID}")

                .then()
                //.log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deletePositionCategories")
    public void deletePositionCategoriesNegative() {
        given()
                .spec(recSpec)
                .pathParam("positionCategoriesID", positionCategoriesID)
                //.log().uri()

                .when()
                .delete("/school-service/api/position-category/{positionCategoriesID}")

                .then()
                //.log().body()
                .statusCode(400)
                .body("message",containsString("not  found"))
        ;

    }
    
}



