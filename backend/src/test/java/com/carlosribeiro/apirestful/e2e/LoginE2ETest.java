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

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "RUN_E2E", matches = "true")
public class LoginE2ETest {
    private WebDriver driver;
    private WebDriverWait wait;
    private int tempoAguardar = 5000;

    private static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:5173");

    @BeforeEach
    void setUp(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1280, 800");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void efetuarLoginValido() {
        driver.get(FRONTEND_URL + "/login");

        WebElement email = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );
        
        aguardar(tempoAguardar);

        email.sendKeys("admin@mail.com");

        WebElement senha = driver.findElement(By.id("senha"));
        senha.sendKeys("desweb");

        aguardar(tempoAguardar);

        WebElement entrar = driver.findElement(By.cssSelector("[data-testid='login-submit']"));
        entrar.click();

        wait.until(
            ExpectedConditions.or(
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Sair")
            )
        );

        aguardar(tempoAguardar*2);

        assertTrue(
            driver.getCurrentUrl().contains("/home") || driver.getPageSource().contains("Sair"),
            "Após login válido, o usuário deveria ser redirecionado para a pagina /home"
        );
    }

    @Test
    void efetuarLoginInvalido() {
        driver.get(FRONTEND_URL + "/login");

        WebElement email = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );
        
        aguardar(tempoAguardar);

        email.sendKeys("invalido@mail.com");

        WebElement senha = driver.findElement(By.id("senha"));
        senha.sendKeys("invalido");

        aguardar(tempoAguardar);

        WebElement entrar = driver.findElement(By.cssSelector("[data-testid='login-submit']"));
        entrar.click();

        WebElement msgErro = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='login-error']"))
        );

        aguardar(tempoAguardar*2);

        assertTrue(
            driver.getCurrentUrl().contains("/login") && msgErro.isDisplayed(),
            "Após login inválido, o usuário deveria permanecer em /login e a mensagem de erro deveria ser exibida."
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
