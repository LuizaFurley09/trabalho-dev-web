import dayjs from "dayjs";
import { Link } from "react-router-dom";
import databaseDelete from '../assets/skin/database_delete.png';
import useRecuperarProdutosComPaginacao from "../hooks/produto/useRecuperarProdutosComPaginacao";
import useRemoverProduto from "../hooks/produto/useRemoverProduto";
import useProdutoStore from "../store/ProdutoStore";

const TabelaDeProdutosPessimista = () => {
  const pagina = useProdutoStore((s) => s.pagina);
  const tamanho = useProdutoStore((s) => s.tamanho);
  const nome = useProdutoStore((s) => s.nome);
  const idRemovendo = useProdutoStore((s) => s.idRemovendo);

  const setPagina = useProdutoStore((s) => s.setPagina);
  const setIdRemovendo = useProdutoStore((s) => s.setIdRemovendo);
  
  const tratarRemocao = (id: number) => {
    removerProduto(id, {
      onSettled: () => {
        setIdRemovendo(null);
        setPagina(0);
      }
    });
    setIdRemovendo(id);
  }

  const {
    mutate: removerProduto,
    error: errorRemoverProduto} = useRemoverProduto();

  const {
    data: resultadoPaginado,
    isPending: recuperandoProdutos,
    // isFetching: atualizandoProdutos,
    error: errorRecuperarProdutos,
  } = useRecuperarProdutosComPaginacao({
    pagina: pagina.toString(),
    tamanho: tamanho.toString(),
    nome: nome
  });

  if (errorRecuperarProdutos) throw errorRecuperarProdutos;
  if (errorRemoverProduto) throw errorRemoverProduto;
  if (recuperandoProdutos) return <p className="text-lg">Recuperando produtos...</p>;

  const produtos = resultadoPaginado.itens;

  return (
    <div className="overflow-x-auto mb-3">
      <table className="w-full border-2 border-gray-400">
        <thead>
          <tr className="border-2 border-gray-400 bg-gray-300">
            <th className="border-r border-r-gray-200 p-1.5 font-semibold">Id</th>
            <th className="border-r border-r-gray-200 p-1.5 font-semibold">Imagem</th>
            <th className="border-r border-r-gray-200 p-1.5 font-semibold">Categoria</th>
            <th className="border-r border-r-gray-200 p-1.5 font-semibold">Nome</th>
            <th className="border-r border-r-gray-200 p-1.5 font-semibold">Disponível</th>
            <th className="border-r border-r-gray-200 p-1.5 font-semibold">Data de Cadastro</th>
            <th className="border-r border-r-gray-200 p-1.5 font-semibold">Preço</th>
            <th className="border-r border-r-gray-200 p-1.5 font-semibold">Ação</th>
          </tr>
        </thead>
        <tbody>
          {produtos.map((produto, index) => (
            <tr key={produto.id} className={"border border-gray-200 " + (index % 2 === 0 ? "bg-white" : "bg-gray-100")}>
              <td className="border-r border-r-gray-200 text-center py-1 w-[8%]">{produto.id}</td>
              <td className="border-r border-r-gray-200 text-center py-1 w-[10%]">
                <div className="flex justify-center">
                  <img src={produto.imagem} width="40px" />
                </div>
              </td>
              <td className="border-r border-r-gray-200 text-center py-1 w-[13%]">{produto.categoria.nome}</td>
              <td className="border-r border-r-gray-200 ps-2 py-1 w-[20%]">
                <Link className="font-bold text-green-700" to={"/produtos/" + produto.id}>{produto.nome}</Link> 
              </td>
              <td className="border-r border-r-gray-200 text-center py-1 w-[13%]">{produto.disponivel ? "Sim" : "Não"}</td>
              <td className="border-r border-r-gray-200 text-center py-1 w-[13%]">{dayjs(produto.dataCadastro).format("DD/MM/YYYY")}</td>
              <td className="border-r border-r-gray-200 text-end pe-2 py-1 w-[10%]">{produto.preco!.toLocaleString("pt-BR", {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
                useGrouping: true
              })}</td>
              <td className="border-r border-r-gray-200 text-center py-1 w-[13%]">
                <button onClick={() => tratarRemocao(produto.id!)} className="btn-danger px-4 py-1" type="button">
                  <div className="flex items-center">
                    {idRemovendo === produto.id ? 
                    <>
                      <span className="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent me-2" />
                      Removendo...
                    </> : 
                    <>
                      <img className="me-1" src={databaseDelete} />
                      Remover
                    </>}
                   </div>
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
export default TabelaDeProdutosPessimista;
