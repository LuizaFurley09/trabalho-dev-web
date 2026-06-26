import TabelaDeProdutosOtimista from "../components/TabelaDeProdutosOtimista";
import useRecuperarProdutos from "../hooks/produto/useRecuperarProdutos";
import useRemoverProdutoOtimista from "../hooks/produto/useRemoverProdutoOtimista";

const ProdutosPage = () => {
  const {
    data: produtos,
    isPending: recuperandoProdutos,
    error: errorRecuperarProdutos,
  } = useRecuperarProdutos();

  const tratarRemocao = (id: number) => {
    removerProduto(id);
  };

  const { mutate: removerProduto,
          isPending: removendoProduto
       // error: errorRemoverProduto  <== Isso não pode existir com remoção otimista
       // caso contrário a página de erro será exibida. Veja abaixo.
  } = useRemoverProdutoOtimista();

  if (errorRecuperarProdutos) throw errorRecuperarProdutos;

  // Ao utiizar a remoção otimista errorRemoverProduto não deve ser utilizado para que a 
  // página de erro não seja exibida.
  // if (errorRemoverProduto) throw errorRemoverProduto;

  if (recuperandoProdutos) return <p className="text-lg">Recuperando produtos...</p>;

  return (
    <>
      <h1 className="mb-1 text-xl font-semibold">Lista de Produtos</h1>
      <hr className="mb-4" />
      <TabelaDeProdutosOtimista produtos={produtos} tratarRemocao={tratarRemocao} removendoProduto={removendoProduto} />
    </>
  );
};
export default ProdutosPage;