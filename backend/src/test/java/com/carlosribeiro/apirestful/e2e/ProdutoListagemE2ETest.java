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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named="RUN_E2E", matches="true")
public class ProdutoListagemE2ETest {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions action;

    private int tempoAguardar = 2000;

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
    void acessarListagemPesquisarEPaginar() {
        driver.get(FRONTEND_URL + "/home");

        WebElement botao_com_pag = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='com-pag']"))
        );
        action.moveToElement(botao_com_pag).perform();
        aguardar(tempoAguardar*2);
        botao_com_pag.click();

        wait.until(ExpectedConditions.urlContains("/produtos-com-paginacao"));

        assertTrue(
            driver.getCurrentUrl().contains("/produtos-com-paginacao"),
            "Ao selecionar Produtos Com Paginacao, usuário deveria ser redirecionado para "+ FRONTEND_URL + "/produtos-com-paginacao."
        );

        aguardarTabelaRenderizada();
        aguardar(tempoAguardar);

        testarPaginacaoSeExistir();

        testarPesquisa();

        aguardar(tempoAguardar);
    }

    private void testarPaginacaoSeExistir() {
        List<WebElement> botoesProxima = driver.findElements(
                By.cssSelector("[data-testid='pagina-proxima']")
        );

        if (botoesProxima.isEmpty()) {
            System.out.println("Paginação não testada: existe apenas uma página ou a paginação não foi renderizada.");
            return;
        }

        WebElement botaoProxima = botoesProxima.get(0);

        if (!botaoProxima.isEnabled()) {
            System.out.println("Paginação não testada: botão Próxima está desabilitado.");
            return;
        }

        List<String> idsPagina1 = idsProdutosVisiveis();

        action.moveToElement(botaoProxima).perform();
        aguardar(tempoAguardar*2);
        botaoProxima.click();

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("[data-testid='pagina-2'][aria-current='page']")
                )
        );

        aguardar(tempoAguardar);

        wait.until(webDriver -> !idsProdutosVisiveis().equals(idsPagina1));

        List<String> idsPagina2 = idsProdutosVisiveis();

        assertNotEquals(
                idsPagina1,
                idsPagina2,
                "Ao avançar a paginação, os produtos exibidos deveriam mudar."
        );

        WebElement botaoAnterior = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='pagina-anterior']"))
        );
        action.moveToElement(botaoAnterior).perform();
        aguardar(tempoAguardar*2);
        botaoAnterior.click();

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("[data-testid='pagina-1'][aria-current='page']")
                )
        );

        wait.until(webDriver -> idsProdutosVisiveis().equals(idsPagina1));

        aguardar(tempoAguardar);

        List<String> idsDepoisDeVoltar = idsProdutosVisiveis();

        assertEquals(
                idsPagina1,
                idsDepoisDeVoltar,
                "Ao voltar para a página 1, os produtos exibidos deveriam ser os mesmos do início."
        );
    }

    private void testarPesquisa() {
        List<String> nomesAntesDaPesquisa = nomesProdutosVisiveis();

        if (nomesAntesDaPesquisa.isEmpty()) {
            System.out.println("Pesquisa não testada: não há produtos visíveis para usar como termo de busca.");
            return;
        }

        String termoPesquisa = nomesAntesDaPesquisa.get(0);

        WebElement campoPesquisa = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='pesquisa']"))
        );

        campoPesquisa.clear();
        campoPesquisa.sendKeys(termoPesquisa);

        wait.until(webDriver -> {
            List<String> nomesDepoisDaPesquisa = nomesProdutosVisiveis();

            return !nomesDepoisDaPesquisa.isEmpty()
                    && nomesDepoisDaPesquisa.get(0).equals(termoPesquisa);
        });

        List<String> nomesDepoisDaPesquisa = nomesProdutosVisiveis();

        aguardar(2*tempoAguardar);

        assertEquals(
                termoPesquisa,
                nomesDepoisDaPesquisa.get(0),
                "O primeiro produto exibido após a pesquisa deveria corresponder ao termo pesquisado."
        );
    }

    private void aguardarTabelaRenderizada() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-testid='tabela-produtos']")
                )
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("[data-testid='produtos-corpo']")
                )
        );
    }

    private List<String> idsProdutosVisiveis() {
        return driver.findElements(By.cssSelector("[data-testid='produto-linha']"))
                .stream()
                .map(elemento -> elemento.getAttribute("data-produto-id"))
                .toList();
    }

    private List<String> nomesProdutosVisiveis() {
        return driver.findElements(By.cssSelector("[data-testid='produto-nome']"))
                .stream()
                .map(WebElement::getText)
                .toList();
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
