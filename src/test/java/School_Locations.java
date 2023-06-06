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

public class School_Locations {
    Map<String, String> SchoolLocations = new HashMap<>();

    Faker faker = new Faker();
    
    String SchoolLocationsName;

    String SchoolLocationsShortName;

    String locationID;
    String capacity;

    RequestSpecification recSpec;


    @BeforeClass
    public void Login() {
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
    public void createSchoolLocations() {
        SchoolLocationsName = (faker.name().firstName());
        SchoolLocationsShortName = faker.name().lastName();
        capacity = faker.number().digit();

        SchoolLocations.put("name", SchoolLocationsName);
        SchoolLocations.put("shortName", SchoolLocationsShortName);
        SchoolLocations.put("capacity", capacity);
        SchoolLocations.put("school", "6390f3207a3bcb6a7ac977f9");
        SchoolLocations.put("type", "CLASS");
        locationID=
        given()
                .spec(recSpec)
                .body(SchoolLocations)
                .log().body()

                .when()
                .post("school-service/api/location")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "createSchoolLocations")
    public void createSchoolLocationsNegative() {


        SchoolLocations.put("name", SchoolLocationsName);
        SchoolLocations.put("shortName", SchoolLocationsShortName);
        SchoolLocations.put("capacity", capacity);
        SchoolLocations.put("school", "6390f3207a3bcb6a7ac977f9");
        SchoolLocations.put("type", "CLASS");

        given()
                .spec(recSpec)
                .body(SchoolLocations)
                .log().body()
                .when()
                .post("school-service/api/location")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))

        ;
    }

    @Test(dependsOnMethods = "createSchoolLocationsNegative")
    public void updateSchoolLocations() {

        SchoolLocationsName = (faker.name().firstName());
        SchoolLocations.put("name", SchoolLocationsName);
        SchoolLocations.put("shortName", SchoolLocationsShortName);
        SchoolLocations.put("id", locationID);
        SchoolLocations.put("capacity", capacity);
        SchoolLocations.put("school", "6390f3207a3bcb6a7ac977f9");
        SchoolLocations.put("type", "CLASS");

        given()
                .spec(recSpec)
                .body(SchoolLocations)
                .log().body()

                .when()
                .put("/school-service/api/location")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(SchoolLocationsName))
        ;
    }

    @Test(dependsOnMethods = "updateSchoolLocations")
    public void deleteSchoolLocations() {

        given()
                .spec(recSpec)
                .pathParam("locationID", locationID)
                .log().uri()

                .when()
                .delete("school-service/api/location/{locationID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteSchoolLocations")
    public void deleteSchoolLocationsNegative() {

        given()
                .spec(recSpec)
                .pathParam("locationID", locationID)
                //.log().uri()

                .when()
                .delete("school-service/api/location/{locationID}")

                .then()
                .log().body()
                .statusCode(400)

        ;

    }

}

