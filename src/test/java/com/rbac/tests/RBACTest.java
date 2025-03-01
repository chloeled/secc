package com.rbac.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class RBACTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setupTest() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testAdminAccess() {
        driver.get("http://localhost:8080/Login");

        // Connexion en tant qu'admin
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin123");
        driver.findElement(By.id("login-btn")).click();

        // Vérifier que l'admin voit le panneau d'administration
        try {
            WebElement adminPanel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("admin-panel")));
            Assert.assertTrue("L'admin peut accéder au panneau d'administration !", adminPanel.isDisplayed());
        } catch (TimeoutException e) {
            Assert.fail("L'admin ne peut pas accéder au panneau d'administration !");
        }
    }

    @Test
    public void testUserRestriction() {
        driver.get("http://localhost:8080/Login");

        // Connexion en tant qu'utilisateur standard
        driver.findElement(By.id("username")).sendKeys("user");
        driver.findElement(By.id("password")).sendKeys("user123");
        driver.findElement(By.id("login-btn")).click();

        // Vérifie que l'utilisateur standard ne voit pas le panneau admin
        boolean isAdminPanelVisible;
        try {
            driver.findElement(By.id("admin-panel"));
            isAdminPanelVisible = true;
        } catch (NoSuchElementException e) {
            isAdminPanelVisible = false;
        }

        Assert.assertFalse("Un utilisateur ne devrait pas voir le panneau admin !", isAdminPanelVisible);
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}