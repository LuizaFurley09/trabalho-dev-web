package com.carlosribeiro.apirestful.controller;

import com.carlosribeiro.apirestful.dto.ProdutoRequest;
import com.carlosribeiro.apirestful.dto.ProdutoResponse;
import com.carlosribeiro.apirestful.dto.ResultadoPaginado;
import com.carlosribeiro.apirestful.service.ProdutoService;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // http://localhost:8080/produtos
    @GetMapping
    public List<ProdutoResponse> recuperarProdutos() {
        return produtoService.recuperarProdutos();
    }

    // http://localhost:8080/produtos/1
    @GetMapping("{idProduto}")
    public ProdutoResponse recuperarProdutoPorId(@PathVariable("idProduto") long id) {
        return produtoService.recuperarProdutoPorId(id);
    }

//    @GetMapping("{idProduto}")
//    public ResponseEntity<?> recuperarProdutoPorId(@PathVariable("idProduto") long id) {
//        try {
//            Produto produto = produtoService.recuperarProdutoPorId(id);
//            return new ResponseEntity(produto, HttpStatus.OK);
//        } catch(EntidadeNaoEncontradaException e) {
//            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }

    // @Validated({Default.class, ProdutoRequest.OnCreate.class}) diz ao Spring para validar esse objeto
    // antes de entrar no método. O ponto importante é que ele manda rodar dois grupos de validação:
    // Default: aqui entram as constraints comuns como @NotEmpty, @NotNull, @Min, @DecimalMin.
    // ProdutoRequest.OnCreate: aqui entram as regras específicas de cadastro, como o id precisar ser nulo
    // no POST.
    // Sem o Default, o Spring irá validar apenas o grupo OnCreate e as anotações de validação para nome,
    // descrição, imagem etc. serão ignoradas.

    // Diferença entre @Valid e @Validated
    //
    // @Valid
    // Usa o Bean Validation no grupo padrão Default. É o mais comum para validar DTOs em @RequestBody.
    // Funciona bem quando você não precisa separar regras de criação e alteração.
    //
    // @Validated
    // É uma extensão do Spring sobre @Valid. A principal vantagem é permitir grupos de validação, como fizemos
    // com OnCreate e OnUpdate. Serve quando queremos regras diferentes para POST e PUT, por exemplo.
    //
    // @NotEmpty String nome pertence ao grupo Default
    // @Null(groups = OnCreate.class) Long id pertence ao grupo OnCreate
    // @NotNull(groups = OnUpdate.class) Long id pertence ao grupo OnUpdate
    // Se utilizarmos apenas @Valid, o @NotEmpty(nome) roda, mas OnCreate e OnUpdate não.
    // Se utilizarmos apenas @Validated(OnCreate.class), o @Null(id) roda, mas @NotEmpty(nome) não.
    // Se utilizarmos @Validated({Default.class, OnCreate.class}), os dois rodam.

    @PostMapping
    public ProdutoResponse cadastrarProduto(@RequestBody @Validated({Default.class, ProdutoRequest.OnCreate.class}) ProdutoRequest produtoRequest) {
        return produtoService.cadastrarProduto(produtoRequest);
    }

    @PutMapping
    public ProdutoResponse alterarProduto(@RequestBody @Validated({Default.class, ProdutoRequest.OnUpdate.class}) ProdutoRequest produtoRequest) {
        return produtoService.alterarProduto(produtoRequest);
    }

    // http://localhost:8080/produtos/1
    @DeleteMapping("{idProduto}")
    public void removerProdutoPotId(@PathVariable("idProduto") long id) {
        produtoService.removerProdutoPorId(id);
    }

    // http://localhost:8080/produtos/paginacao?pagina=0&tamanho=3
    @GetMapping("paginacao")
    public ResultadoPaginado<ProdutoResponse> recuperarProdutosComPaginacao(
        @RequestParam(name = "pagina", defaultValue = "0") int pagina,
        @RequestParam(name = "tamanho", defaultValue = "5") int tamanho,
        @RequestParam(name = "nome", defaultValue = "") String nome
    ) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<ProdutoResponse> page = produtoService.recuperarProdutosComPaginacao(pageRequest, nome);
        return new ResultadoPaginado<>(
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getContent());
    }
}
