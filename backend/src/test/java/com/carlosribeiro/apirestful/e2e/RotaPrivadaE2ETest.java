package com.carlosribeiro.apirestful.e2e;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named="RUN_E2E", matches="true")
public class RotaPrivadaE2ETest {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions action;

    private int tempoAguardar = 5000;

    private static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:5173");
    
    @BeforeEach
    void setUp(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1280, 800");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        action = new Actions(driver);
    }

    @AfterEach
    void tearDown(){
        if (driver != null){
            driver.quit();
        }
    }

    @Test
    void acessarFavoritosSemLogin() {
        driver.get(FRONTEND_URL + "/home");

        aguardar(tempoAguardar);

        WebElement botao_fav1 = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='fav']"))
        );

        action.moveToElement(botao_fav1).perform();
        aguardar(tempoAguardar);
        botao_fav1.click();

        wait.until(
            ExpectedConditions.urlContains("/login")
        );

        assertTrue(
            driver.getCurrentUrl().contains("/login"),
            "Após tentar acessar favoritos sem estar logado, usuário deveria ser redirecionado para "+ FRONTEND_URL + "/login."
        );

        WebElement email = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );

        email.sendKeys("admin@mail.com");

        WebElement senha = driver.findElement(By.id("senha"));
        senha.sendKeys("desweb");

        WebElement entrar = driver.findElement(By.cssSelector("[data-testid='login-submit']"));
        entrar.click();

        aguardar(tempoAguardar);

        wait.until(
            ExpectedConditions.and(
                ExpectedConditions.urlContains("/favoritos"),
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "FavoritosPage")
            )
        );

        assertTrue(
            driver.getCurrentUrl().contains("/favoritos"),
            "Após acessar favoritos estando logado, usuário deveria ser redirecionado para " + FRONTEND_URL + "/favoritos"
        );
    }

    private void aguardar(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Pausa visual do teste foi interrompida.", exception);
        }
    }
}
