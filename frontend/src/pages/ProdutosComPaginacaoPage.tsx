import Paginacao from "../components/Paginacao";
import Pesquisa from "../components/Pesquisa";
import TabelaDeProdutosPessimista from "../components/TabelaDeProdutosPessimista";

const ProdutosComPaginacaoPage = () => {
  return (
    <>
      <h1 className="mb-1 text-xl font-semibold">Lista de Produtos</h1>
      <hr className="mb-4" />

      <Pesquisa />
      <TabelaDeProdutosPessimista />
      <Paginacao />
    </>
  );
};
export default ProdutosComPaginacaoPage;
