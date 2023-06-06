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

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Discount {

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

    String des;
    String id;

    @Test
    public void createDiscount() {

        Map<String, String> doc = new HashMap<>();
        doc.put("description", faker.name().username());
        doc.put("code", faker.number().digits(2));
        doc.put("priority", faker.number().digits(5));


        Response response =
                given()
                        .spec(requestSpec)
                        .body(doc)
                        .when()
                        .post("/school-service/api/discounts")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().response();

        id = response.path("id");
        des = response.path("description");



    }

    @Test(dependsOnMethods = "createDiscount")
    public void createDiscountNegative() {

        Map<String, String> doc = new HashMap<>();
        doc.put("description",des);
        doc.put("code", faker.number().digits(2));
        doc.put("priority", faker.number().digits(5));

                given()
                        .spec(requestSpec)
                        .body(doc)
                        .when()
                        .post("/school-service/api/discounts")
                        .then()
                        .log().body()
                        .statusCode(400)
                        ;
    }


    @Test(dependsOnMethods = "createDiscountNegative")
    public void updateDiscount() {
        Map<String, String> doc = new HashMap<>();
        doc.put("description",faker.name().fullName());
        doc.put("code", faker.number().digits(2));
        doc.put("id", id);

        given()
                .spec(requestSpec)
                .body(doc)
                .when()
                .put("/school-service/api/discounts")
                .then()
                .log().body()
                .statusCode(200)
                ;
    }

    @Test(dependsOnMethods = "updateDiscount")
    public void deleteDiscount() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/discounts/"+id)
                .then()
                .log().body()
                .statusCode(200)
                ;
    }


    @Test(dependsOnMethods = "deleteDiscount")
    public void deleteDiscountNegative() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/discounts/"+id)
                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Discount not found"))
        ;
    }


}
