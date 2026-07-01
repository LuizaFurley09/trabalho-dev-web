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
public class ProdutoCadastroE2ETest {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions action;

    private int tempoAguardar = 1500;

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
    void cadastrarProdutoComSucesso() {
        efetuarLogin();

        WebElement botao_cad = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='cad']"))
        );

        action.moveToElement(botao_cad).perform();
        aguardar(2*tempoAguardar);
        botao_cad.click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("form"))
        );

        aguardar(tempoAguardar);

        String nomeProduto = "Produto Teste";

        WebElement nome = driver.findElement(By.id("nome"));
        nome.sendKeys(nomeProduto);

        aguardar(tempoAguardar);

        WebElement descricao = driver.findElement(By.id("descricao"));
        descricao.sendKeys("Produto cadastrado pelo teste com Selenium");
        
        aguardar(tempoAguardar);

        WebElement categoria = driver.findElement(By.id("categoria"));
        categoria.click();

        aguardar(tempoAguardar);

        WebElement fruta = driver.findElement(By.cssSelector("#categoria option[value='1']"));
        fruta.click();

        aguardar(tempoAguardar);

        WebElement dataCadastro = driver.findElement(By.id("data_cadastro"));
        dataCadastro.sendKeys("28");
        aguardar(tempoAguardar);
        dataCadastro.sendKeys("06");
        aguardar(tempoAguardar);
        dataCadastro.sendKeys("2026");

        WebElement preco = driver.findElement(By.id("preco"));
        preco.sendKeys("12");
        aguardar(tempoAguardar);
        preco.sendKeys(".50");

        aguardar(tempoAguardar);

        WebElement estoque = driver.findElement(By.id("qtd_estoque"));
        estoque.sendKeys("20");

        aguardar(tempoAguardar);

        WebElement imagem = driver.findElement(By.id("imagem"));
        imagem.sendKeys("banana.png");

        WebElement disponivel = driver.findElement(By.id("disponivel"));
        if (!disponivel.isSelected()) {
            disponivel.click();
        }

        aguardar(tempoAguardar);

        WebElement salvar = driver.findElement(By.cssSelector("[data-testid='salvar-produto']"));
        salvar.click();

        wait.until(
                ExpectedConditions.and(
                        ExpectedConditions.urlContains("/produtos/"),
                        ExpectedConditions.textToBePresentInElementLocated(
                                By.tagName("body"),
                                "Produto cadastrado com sucesso."
                        ),
                        ExpectedConditions.textToBePresentInElementLocated(
                                By.tagName("body"),
                                nomeProduto
                        )
                )
        );

        aguardar(tempoAguardar);

        assertTrue(
                driver.getCurrentUrl().contains("/produtos/"),
                "Após cadastrar o produto, o usuário deveria ser redirecionado para a página do produto."
        );

        assertTrue(
                driver.getPageSource().contains("Produto cadastrado com sucesso."),
                "A mensagem de sucesso deveria ser exibida após o cadastro."
        );

        assertTrue(
                driver.getPageSource().contains(nomeProduto),
                "O produto cadastrado deveria aparecer na página de detalhe."
        );
    }

    @Test
    void cadastrarProdutoSemSucesso() {
        efetuarLogin();

        WebElement botao_cad = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='cad']"))
        );
        botao_cad.click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("form"))
        );

        String nomeProduto = "Produto Teste";

        WebElement nome = driver.findElement(By.id("nome"));
        nome.sendKeys(nomeProduto);

        aguardar(tempoAguardar);

        WebElement descricao = driver.findElement(By.id("descricao"));
        descricao.sendKeys("Produto cadastrado pelo teste com Selenium");

        aguardar(tempoAguardar);

        WebElement categoria = driver.findElement(By.id("categoria"));
        categoria.click();

        aguardar(tempoAguardar);

        WebElement fruta = driver.findElement(By.cssSelector("#categoria option[value='1']"));
        fruta.click();

        aguardar(tempoAguardar);

        WebElement dataCadastro = driver.findElement(By.id("data_cadastro"));
        dataCadastro.sendKeys("28");
        aguardar(tempoAguardar);
        dataCadastro.sendKeys("06");
        aguardar(tempoAguardar);
        dataCadastro.sendKeys("2026");

        WebElement preco = driver.findElement(By.id("preco"));
        preco.sendKeys("12");
        aguardar(tempoAguardar);
        preco.sendKeys(".50");

        WebElement estoque = driver.findElement(By.id("qtd_estoque"));
        estoque.sendKeys("20");

        aguardar(tempoAguardar);

        WebElement imagem = driver.findElement(By.id("imagem"));
        imagem.sendKeys("banana");

        WebElement disponivel = driver.findElement(By.id("disponivel"));
        if (!disponivel.isSelected()) {
            disponivel.click();
        }

        WebElement salvar = driver.findElement(By.cssSelector("[data-testid='salvar-produto']"));
        salvar.click();

        wait.until(
                ExpectedConditions.textToBePresentInElementLocated(
                        By.tagName("body"),
                        "Nome de imagem inválido."
                )
        );

        aguardar(2*tempoAguardar);

        assertTrue(
                driver.getCurrentUrl().contains("/cadastrar-produto"),
                "Após tentar cadastrar produto inválido, o usuário deveria permanecer na página de cadastro."
        );

        assertTrue(
                driver.getPageSource().contains("Nome de imagem inválido."),
                "A mensagem de validação da imagem deveria ser exibida."
        );
    }

    private void efetuarLogin() {
        driver.get(FRONTEND_URL + "/login");

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );

        aguardar(tempoAguardar);

        email.sendKeys("admin@mail.com");
        aguardar(tempoAguardar);

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

        aguardar(tempoAguardar);
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
