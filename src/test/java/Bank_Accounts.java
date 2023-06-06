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


public class Bank_Accounts {

    Faker faker=new Faker();

    String bankAccountsID;

    String bankAccountsName;

    String bankAccountsIban;

    String bankAccountscurrency;

    String bankAccountsIntegrationCode;

    String shcoolID;

    RequestSpecification recSpec;

    @BeforeClass
    public void Setup() {
        baseURI="https://test.mersys.io";

        Map<String,String> userCredential=new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");

        Cookies cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;

        recSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createBankAccounts() {

        Map<String,String> bankAccounts=new HashMap<>();

        bankAccountsName = (faker.name().fullName());
        bankAccountsIban = (faker.number().digits(11));
        bankAccountscurrency ="USD";
        bankAccountsIntegrationCode = (faker.number().digits(3));
        shcoolID = "6390f3207a3bcb6a7ac977f9";

        bankAccounts.put("name", bankAccountsName);
        bankAccounts.put("iban", bankAccountsIban);
        bankAccounts.put("currency", bankAccountscurrency);
        bankAccounts.put("integrationCode", bankAccountsIntegrationCode);
        bankAccounts.put("schoolId", shcoolID);


        bankAccountsID =
                given()
                        .spec(recSpec)
                        .body(bankAccounts)
                        // .log().body()

                        .when()
                        .post("/school-service/api/bank-accounts")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;


    }

    @Test(dependsOnMethods = "createBankAccounts")
    public void createBankAccountsNegative() {

        Map<String, String> bankAccounts= new HashMap<>();

        bankAccounts.put("name", bankAccountsName);
        bankAccounts.put("iban", bankAccountsIban);
        bankAccounts.put("currency", bankAccountscurrency);
        bankAccounts.put("integrationCode", bankAccountsIntegrationCode);
        bankAccounts.put("schoolId", shcoolID);

        given()
                .spec(recSpec)
                .body(bankAccounts)
                .log().body()

                .when()
                .post("/school-service/api/bank-accounts")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }
    @Test(dependsOnMethods = "createBankAccounts")
    public void updateBankAccounts() {
        Map<String, String> bankAccounts = new HashMap<>();
        bankAccounts.put("id", bankAccountsID);

        bankAccountsName = "TD Bank (US)" +faker.number().digits(6);

        bankAccounts.put("name", bankAccountsName);
        bankAccounts.put("iban", bankAccountsIban);
        bankAccounts.put("currency", bankAccountscurrency);
        bankAccounts.put("integrationCode", bankAccountsIntegrationCode);
        bankAccounts.put("schoolId", shcoolID);

        given()
                .spec(recSpec)
                .body(bankAccounts)
                .log().body()

                .when()
                .put("/school-service/api/bank-accounts")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(bankAccountsName))

        ;
    }

    @Test(dependsOnMethods = "updateBankAccounts")
    public void deleteBankAccounts() {

        given()
                .spec(recSpec)
                .pathParam("bankAccountsID", bankAccountsID)
                .log().uri()

                .when()
                .delete("/school-service/api/bank-accounts/{bankAccountsID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteBankAccounts")
    public void deleteBankAccountsNegative() {
        given()
                .spec(recSpec)
                .pathParam("bankAccountsID", bankAccountsID)
                .log().uri()

                .when()
                .delete("/school-service/api/bank-accounts/{bankAccountsID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("Please, bank account must be exist"))

        ;
    }
}
