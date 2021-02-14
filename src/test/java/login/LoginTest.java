package login;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;
import pages.SecureAreaPage;

@Epic("Sign In & Sign Up")
@Feature("Sign In")
public class LoginTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(description = "Verify the successful login scenario.")
    public void verifySuccessfulLogin() {

        HomePage objHomePage;
        LoginPage objLoginPage;
        SecureAreaPage objSecureAreaPage;

        objHomePage = new HomePage(driver);
        objLoginPage = objHomePage.clickFormAuthentication();
        objLoginPage.setUsername("tomsmith");
        objLoginPage.setPassword("SuperSecretPassword!");
        objSecureAreaPage = objLoginPage.clickLoginButton();
        Assert.assertTrue(objSecureAreaPage.getAlertText().contains("You logged into a secure area!"), "Alert text is incorrect.");
    }

}
