package com.rbac.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class RBACTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public static void setupClass() {
        // Setup WebDriverManager pour le gestionnaire de WebDriver
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setupTest() {
        // Initialisation du WebDriver et maximisation de la fenêtre
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void testAdminAccess() {
        driver.get("http://localhost:8080/Login.html");

        // Vérification du DOM (utile pour le débogage, peut être retiré en production)
        System.out.println("Page source :\n" + driver.getPageSource());

        // Vérification des erreurs JS dans la console
        System.out.println("Logs du navigateur : " + driver.manage().logs().get("browser").getAll());

        // Connexion en tant qu'admin
        login("admin", "0df8d6f7f798464cb026d1abe2a8bb8f");

        // Vérifier que l'admin voit le panneau d'administration
        WebElement adminPanel = waitForElementVisible(By.id("admin-panel"));
        Assert.assertTrue("L'admin peut accéder au panneau d'administration !", adminPanel.isDisplayed());
    }

    @Test
    public void testUserRestriction() {
        driver.get("http://localhost:8080/job/ism/");

        // Connexion en tant qu'utilisateur standard
        login("user", "userpassword"); // Remplacer par un mot de passe utilisateur valide

        // Vérifier que l'utilisateur standard ne voit pas le panneau d'administration
        boolean isAdminPanelVisible = isElementPresent(By.id("admin-panel"));
        Assert.assertFalse("Un utilisateur ne devrait pas voir le panneau admin !", isAdminPanelVisible);
    }

    private void login(String utilisateur, String motDePasse) {
        // Gestion des iframes s'il y en a
        switchToFirstIframeIfPresent();

        // Remplir le champ utilisateur
        WebElement utilisateurField = waitForElementVisible(By.id("utilisateur"));
        utilisateurField.sendKeys(utilisateur);

        // Remplir le champ mot de passe
        WebElement motDePasseField = waitForElementVisible(By.id("mot-de-passe"));
        motDePasseField.sendKeys(motDePasse);

        // Cliquer sur le bouton de connexion
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-btn")));
        loginButton.click();

        // Revenir au contexte principal après la connexion
        driver.switchTo().defaultContent();
    }

    private void switchToFirstIframeIfPresent() {
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        if (!iframes.isEmpty()) {
            driver.switchTo().frame(0); // Basculer vers la première iframe si nécessaire
            System.out.println("Basculé dans la première iframe.");
        }
    }

    private boolean isElementPresent(By by) {
        // Méthode pour vérifier si un élément est présent sur la page
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private WebElement waitForElementVisible(By locator) {
        // Méthode générique pour attendre qu'un élément soit visible
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @After
    public void teardown() {
        // Fermeture du driver après chaque test
        if (driver != null) {
            driver.quit();
        }
    }
}