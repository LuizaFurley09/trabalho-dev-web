import dayjs from "dayjs";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import type { Produto } from "../interfaces/Produto";
import useProdutoStore from "../store/ProdutoStore";
import useRecuperarProdutoPorId from "../hooks/produto/useRecuperarProdutoPorId";
import useRemoverProduto from "../hooks/produto/useRemoverProduto";

const ProdutoPage = () => {
  const [removido, setRemovido] = useState(false);
  const mensagem = useProdutoStore((s) => s.mensagem);
  const setMensagem = useProdutoStore((s) => s.setMensagem);
  const setProdutoSelecionado = useProdutoStore((s) => s.setProdutoSelecionado);
  const navigate = useNavigate();
  
  const { id } = useParams();

  const {
    data: produto,
    isPending: recuperandoProduto,
    error: errorRecuperarProduto,
  } = useRecuperarProdutoPorId(+id!, removido);

  const tratarEdicao = (produto: Produto) => {
    setProdutoSelecionado(produto);
    navigate("/cadastrar-produto");
  };

  const tratarRemocao = (id: number) => {
    removerProduto(id);
    setRemovido(true);
    setMensagem("Produto removido com sucesso!");
  };

  const { mutate: removerProduto, 
          error: errorRemoverProduto } = useRemoverProduto();

  useEffect(() => {

    return () => {
      setMensagem("");
    }
  },[])

  if (errorRecuperarProduto) throw errorRecuperarProduto;
  if (errorRemoverProduto) throw errorRemoverProduto;
  if (recuperandoProduto)
    return <p className="text-lg">Recuperando produto...</p>;

  return (
    <>
      <h1 className="mb-1 text-xl font-semibold">Página de Produto</h1>
      <hr className="mb-4" />

      {mensagem && (
        <div className="mb-3 rounded border-2 border-green-600 bg-green-100 px-4 py-3 font-bold text-green-800">
          {mensagem}
        </div>
      )}

      {/* 
      Modos do Tailwindcss:
      sm: 640px
      md: 768px
      lg: 1024px
      xl: 1280px
      2xl: 1536px */}

      <div className="grid grid-cols-12">
        <div className="col-span-12 lg:col-span-4 xl:col-span-3">
          {/* Para chegar nessa página o url foi /produtos/:id */}
          {/* Sem a / abaixo seria enviada uma requisição para /produtos/abacate.png*/}
          <img className="lg:hidden" src={"/" + produto.imagem} width="170px" />
          <img
            className="hidden lg:block"
            src={"/" + produto.imagem}
            width="210px"
          />
        </div>
        <div className="col-span-12 mb-2 lg:col-span-8 xl:col-span-9">
          <div className="grid grid-cols-12">
            <div className="col-span-4 mb-1 font-bold lg:col-span-3 xl:col-span-2">
              Categoria
            </div>
            <div className="col-span-8 lg:col-span-9 xl:col-span-10">
              {produto.categoria.nome}
            </div>

            <div className="col-span-4 mb-1 font-bold lg:col-span-3 xl:col-span-2">
              Nome
            </div>
            <div className="col-span-8 lg:col-span-9 xl:col-span-10">
              {produto.nome} ({produto.descricao})
            </div>

            <div className="col-span-4 mb-1 font-bold lg:col-span-3 xl:col-span-2">
              Preço
            </div>
            <div className="col-span-8 lg:col-span-9 xl:col-span-10">
              {produto.preco!.toLocaleString("pt-BR", {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
                useGrouping: true,
              })}
            </div>

            <div className="col-span-4 mb-1 font-bold lg:col-span-3 xl:col-span-2">
              Estoque
            </div>
            <div className="col-span-8 lg:col-span-9 xl:col-span-10">
              {produto.qtdEstoque}
            </div>

            <div className="col-span-4 mb-1 font-bold lg:col-span-3 xl:col-span-2">
              Data Cadastro
            </div>
            <div className="col-span-8 lg:col-span-9 xl:col-span-10">
              {dayjs(produto.dataCadastro).format("DD/MM/YYYY")}
            </div>

            <div className="col-span-4 mb-1 font-bold lg:col-span-3 xl:col-span-2">
              Disponível
            </div>
            <div className="col-span-8 lg:col-span-9 xl:col-span-10">
              {produto.disponivel ? "Sim" : "Não"}
            </div>
          </div>
        </div>
        <div className="col-span-4 me-3 xl:col-span-3">
          <button
            onClick={() => tratarEdicao(produto)}
            disabled={removido}
            className="btn-success w-full py-1"
            type="button"
          >
            Editar
          </button>
        </div>
        <div className="col-span-4 me-3 xl:col-span-3">
          <button
            onClick={() => tratarRemocao(produto.id!)}
            disabled={removido}
            className="btn-danger w-full py-1"
            type="button"
          >
            Remover
          </button>
        </div>
      </div>
    </>
  );
};
export default ProdutoPage;
