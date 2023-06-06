
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


public class Positions {

    Faker faker = new Faker();

    String positionsID;

    String positionsName;

    String positionsShortName;

    String tenantID;

    RequestSpecification recSpec;


    @BeforeClass
    public void Setup() {
        baseURI = "https://test.mersys.io/";
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
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();

    }

    @Test
    public void createPositions() {

        Map<String, String> positions = new HashMap<>();

        positionsName = (faker.name().firstName());
        positionsShortName = (faker.name().nameWithMiddle());
        tenantID = "6390ef53f697997914ec20c2";

        positions.put("name", positionsName);
        positions.put("shortName", positionsShortName);
        positions.put("tenantId", tenantID);

        positionsID =
                given()
                        .spec(recSpec)
                        .body(positions)
                        .log().body()

                        .when()
                        .post("/school-service/api/employee-position")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("positionsID = " + positionsID);

    }

    @Test(dependsOnMethods = "createPositions")
    public void createPositionsNegative() {

        Map<String, String> positions = new HashMap<>();

        positions.put("name", positionsName);
        positions.put("shortName", positionsShortName);
        positions.put("tenantId", tenantID);

        given()
                .spec(recSpec)
                .body(positions)
                .log().body()

                .when()
                .post("/school-service/api/employee-position")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))

        ;
    }

    @Test(dependsOnMethods = "createPositionsNegative")
    public void updatePositions() {
        Map<String, String> positions = new HashMap<>();
        positions.put("id", positionsID);

        positionsName = "Computer programming" +faker.number().digits(5);

        positions.put("name", positionsName);
        positions.put("shortName", positionsShortName);
        positions.put("tenantId", tenantID);

        given()
                .spec(recSpec)
                .body(positions)
                .log().body()

                .when()
                .put("/school-service/api/employee-position")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(positionsName))
        ;
    }

    @Test(dependsOnMethods = "updatePositions")
    public void deletePositions() {

        given()
                .spec(recSpec)
                .pathParam("positionsID", positionsID)
                .log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionsID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deletePositions")
    public void deletePositionsNegative() {

        given()
                .spec(recSpec)
                .pathParam("positionsID", positionsID)
                //.log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionsID}")

                .then()
                .log().body()
                .statusCode(204)

        ;

    }

}
