
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
public class Document_Types {

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

    String id;
    String schoolId = "6390f3207a3bcb6a7ac977f9";
    String[] attachmentStages = {"CONTRACT"};
    @Test
    public void createDocumentTypes() {

        Map<String, Object> doc = new HashMap<>();
        doc.put("name", faker.name().username());
        doc.put("attachmentStages", attachmentStages);
        doc.put("schoolId", schoolId);

        Response response =
                given()
                        .spec(requestSpec)
                        .body(doc)
                        .when()
                        .post("/school-service/api/attachments/create")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().response();
        id = response.path("id");
    }


    @Test(dependsOnMethods = "createDocumentTypes")
    public void updateDocumentTypes() {

        Map<String, Object> doc = new HashMap<>();
        doc.put("name", faker.funnyName().name());
        doc.put("schoolId", schoolId);
        doc.put("attachmentStages", attachmentStages);
        doc.put("id", id);

        given()
                .spec(requestSpec)
                .body(doc)
                .when()
                .put("/school-service/api/attachments")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "updateDocumentTypes")
    public void deleteDocumentTypes() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/attachments/"+id)
                .then()
                .log().body()
                .statusCode(200);
    }


    @Test(dependsOnMethods = "deleteDocumentTypes")
    public void deleteDocumentTypesNegative() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/attachments/"+id)
                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Attachment Type not found"));
    }


}
