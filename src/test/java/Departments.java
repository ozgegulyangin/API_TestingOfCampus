import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class Departments {
    Faker faker = new Faker();

    String departmentsID;
    String departmentsName;
    String departmentsCode;
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
    public void createDepartments() {

        Map<String, String> departments = new HashMap<>();

        departmentsName = (faker.lorem().word() + faker.number().digits(2));
        departmentsCode = (faker.number().digits(4));

        departments.put("name", departmentsName);
        departments.put("code", departmentsCode);
        departments.put("school", "6390f3207a3bcb6a7ac977f9");
        departmentsID =
                given()
                        .spec(recSpec)
                        .body(departments)
                        //.log().body()

                        .when()
                        .post("/school-service/api/department")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;

    }

    @Test(dependsOnMethods = "createDepartments")
    public void createDepartmentsNegative() {

        Map<String, String> departments = new HashMap<>();

        departments.put("name", departmentsName);
        departments.put("code", departmentsCode);
        departments.put("school", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(recSpec)
                .body(departments)
                //.log().body()

                .when()
                .post("/school-service/api/department")

                .then()
                //.log().body()
                .statusCode(400)
                .body("message", containsString("already"))

        ;
    }

    @Test(dependsOnMethods = "createDepartmentsNegative")
    public void updateDepartments() {
        Map<String, String> departments = new HashMap<>();
        departments.put("id", departmentsID);

        departmentsName = "Hello API" + faker.number().digits(7);
        departments.put("name", departmentsName);
        departments.put("code", departmentsCode);

        departments.put("school", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(recSpec)
                .body(departments)
                //.log().all()

                .when()
                .put("/school-service/api/department")

                .then()
                //.log().all()
                .statusCode(200)
                .body("name", equalTo(departmentsName))

        ;
    }

    @Test(dependsOnMethods = "updateDepartments")
    public void deleteDepartments() {
        given()
                .spec(recSpec)
                .pathParam("departmentsID", departmentsID)
                //.log().uri()

                .when()
                .delete("/school-service/api/department/{departmentsID}")

                .then()
                //.log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deleteDepartments")
    public void deleteDepartmentsNegative() {
        given()
                .spec(recSpec)
                .pathParam("departmentsID", departmentsID)
                //.log().uri()

                .when()
                .delete("/school-service/api/department/{departmentsID}")

                .then()
                .log().all()
                .statusCode(204)
        ;

    }
}
