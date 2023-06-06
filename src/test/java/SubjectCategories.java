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

public class SubjectCategories {
    Faker faker = new Faker();
    RequestSpecification reqSpec;

    String SubjectCategoriesName;
    String code;
    String SubjectCategoriesID;

    Map<String, String> SubjectCategories = new HashMap<>();
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

        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createSubjectCategories() {

        SubjectCategoriesName = faker.name().fullName() + "zz";
        SubjectCategories.put("name", SubjectCategoriesName);
        code = faker.code().ean8();
        SubjectCategories.put("code", code);


        SubjectCategoriesID =
                given()
                        .spec(reqSpec)
                        .contentType(ContentType.JSON)
                        .body(SubjectCategories)

                        .log().body()
                        .when()
                        .post("school-service/api/subject-categories")
                        .then()

                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
        ;
        System.out.println("SubjectCategoriesID = " + SubjectCategoriesID);

    }

    @Test(dependsOnMethods = "createSubjectCategories")
    public void createSubjectCategoriesNegative() {

        SubjectCategories.put("name", SubjectCategoriesName);
        SubjectCategories.put("code", code);
        given()
                .spec(reqSpec)
                .body(SubjectCategories)
                .log().body()
                .when()
                .post("school-service/api/subject-categories")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;

    }


    @Test(dependsOnMethods = "createSubjectCategoriesNegative")
    public void updateSubjectCategories() {

        SubjectCategoriesName = faker.name().fullName() + "xx";
        SubjectCategories.put("name", SubjectCategoriesName);
        code = faker.code().ean8();
        SubjectCategories.put("code", code);
        SubjectCategories.put("id",SubjectCategoriesID);
        given()
                .spec(reqSpec)
                .body(SubjectCategories)
                .log().body()
                .when()
                .put("school-service/api/subject-categories")
                .then()
                .statusCode(200)
                .body("name", equalTo(SubjectCategoriesName))
        ;


    }

    @Test(dependsOnMethods = "updateSubjectCategories")
    public void deleteSubjectCategories() {

        given()
                .spec(reqSpec)
                .pathParam("SubjectCategoriesID", SubjectCategoriesID)
                .when()
                .delete("school-service/api/subject-categories/{SubjectCategoriesID}")
                .then()
                .log().body()
                .statusCode(200)
        ;

    }

    @Test(dependsOnMethods = "deleteSubjectCategories")
    public void deleteSubjectCategoriesNegative() {
        given()
                .spec(reqSpec)
                .pathParam("SubjectCategoriesID", SubjectCategoriesID)
                .when()
                .delete("school-service/api/subject-categories/{SubjectCategoriesID}")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("SubjectCategory not  found"))
        ;
    }
}
