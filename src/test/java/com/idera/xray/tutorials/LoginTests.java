package com.idera.xray.tutorials;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.Duration;

import app.getxray.xray.junit.customjunitxml.XrayTestReporter;
import app.getxray.xray.junit.customjunitxml.XrayTestReporterParameterResolver;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import io.github.bonigarcia.wdm.WebDriverManager;

@ExtendWith(XrayTestReporterParameterResolver.class)
public class LoginTests {
    WebDriver driver;
    RepositoryParser repo;

    @BeforeEach
    public void setUp() throws Exception {
    	WebDriverManager.firefoxdriver().setup();
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("intl.accept_languages", "en-GB");
		FirefoxOptions options = new FirefoxOptions();
		options.setProfile(profile);
		driver = new FirefoxDriver(options);
        ///ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox"); // Bypass OS security model, to run in Docker
        options.addArguments("--headless");
        //driver = new ChromeDriver(options);
        repo = new RepositoryParser("./src/configs/object.properties");
    }

    @AfterEach
    public void tearDown() throws Exception {
        driver.quit();
        driver = null;
        repo = null;
    }
    
    @Test
    /*@XrayTest(key = "XT-307")
    @Requirement("XT-10")
    @XrayTest(key = "XPS-24")
    @Requirement("XPS-10")*/
    public void successLogin()
    {
        LoginPage loginPage = new LoginPage(driver).open();
        assertTrue(loginPage.isVisible());
        LoginResultsPage loginResultsPage = loginPage.login("demo", "mode");
        //explicit wait
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(5));
        //expected condition
        w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"container\"]/h1[contains(.,'Welcome Page')]")));//span[contains(.,'EDIT')]
		System.out.println(repo.getBy("expected.login.title"));
		System.out.println(loginResultsPage.getTitle());
        assertEquals(loginResultsPage.getTitle(), repo.getBy("expected.login.title"));
        assertTrue(loginResultsPage.contains(repo.getBy("expected.login.success")));
    }

    @Test
   // @XrayTest(summary = "invalid login test", description = "login attempt with invalid credentials")
    public void nosuccessLogin(XrayTestReporter xrayReporter)
    {
        LoginPage loginPage = new LoginPage(driver).open();
        assertTrue(loginPage.isVisible());
        LoginResultsPage loginResultsPage = loginPage.login("demo", "invalid");
        TakesScreenshot screenshotTaker =((TakesScreenshot)driver);
        File screenshot = screenshotTaker.getScreenshotAs(OutputType.FILE);
        xrayReporter.addTestRunEvidence(screenshot.getAbsolutePath());
        xrayReporter.addComment("auth should have failed");
        //explicit wait
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(5));
        //expected condition
        w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"container\"]/h1[contains(.,'Error Page')]")));
		System.out.println(repo.getBy("expected.error.title"));
		System.out.println(loginResultsPage.getTitle());

		System.out.println(repo.getBy("expected.login.failed"));
        assertEquals(loginResultsPage.getTitle(), repo.getBy("expected.error.title"));
        assertTrue(loginResultsPage.contains(repo.getBy("expected.login.failed")));
    }

}
