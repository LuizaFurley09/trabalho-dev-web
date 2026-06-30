import useRecuperarProdutosComPaginacao from "../hooks/produto/useRecuperarProdutosComPaginacao";
import useProdutoStore from "../store/ProdutoStore";

const Paginacao = () => {
  const pagina = useProdutoStore((s) => s.pagina);
  const tamanho = useProdutoStore((s) => s.tamanho);
  const nome = useProdutoStore((s) => s.nome);
  const idRemovendo = useProdutoStore((s) => s.idRemovendo);

  const setPagina = useProdutoStore((s) => s.setPagina);

  const {
    data: resultadoPaginado,
    isPending: recuperandoProdutos,
    isFetching: atualizandoProdutos,
    error: errorRecuperarProdutos,
  } = useRecuperarProdutosComPaginacao({
    pagina: pagina.toString(),
    tamanho: tamanho.toString(),
    nome: nome
  });

  const tratarPaginacao = (pagina: number) => {
    setPagina(pagina);
  };

  if (errorRecuperarProdutos) throw errorRecuperarProdutos;
  if (recuperandoProdutos) return;

  const totalDePaginas = resultadoPaginado.totalDePaginas;

  if (totalDePaginas < 2) return;

  const pages = Array.from({ length: totalDePaginas }).map((_, index) => index);

  return (
    <div className="flex">
      <div className="flex flex-col items-center gap-2">
        <nav data-testid="paginacao" aria-label="Paginação">
          <ul className="flex">
            <li>
              <button
                data-testid="pagina-anterior"
                type="button"
                disabled={pagina === 0}
                onClick={() => tratarPaginacao(pagina - 1)}
                className={
                  "rounded-l-lg border border-gray-300 px-4 py-2 font-semibold hover:bg-gray-200 " +
                  (pagina === 0
                    ? "cursor-not-allowed bg-gray-300 opacity-50"
                    : "cursor-pointer bg-white text-green-700")
                }
              >
                Anterior
              </button>
            </li>

            {pages.map((page) => (
              <li key={page}>
                <button
                  data-testid={`pagina-${page + 1}`}
                  aria-current={pagina === page ? "page" : undefined}
                  type="button"
                  onClick={() => tratarPaginacao(page)}
                  className={
                    "cursor-pointer border px-4 py-2 font-semibold " +
                    (pagina === page
                      ? "border-green-800 bg-green-700 text-white"
                      : "border-gray-300 bg-white text-green-600 hover:bg-gray-100")
                  }
                >
                  {page + 1}
                </button>
              </li>
            ))}

            <li>
              <button
                data-testid="pagina-proxima"
                type="button"
                disabled={pagina === totalDePaginas - 1}
                onClick={() => tratarPaginacao(pagina + 1)}
                className={
                  "rounded-r-lg border border-gray-300 px-4 py-2 font-semibold hover:bg-gray-200 " +
                  (pagina === totalDePaginas - 1
                    ? "cursor-not-allowed bg-gray-300 opacity-50"
                    : "cursor-pointer bg-white text-green-700")
                }
              >
                Próxima
              </button>
            </li>
          </ul>
        </nav>
        {atualizandoProdutos && idRemovendo === null && (
          <span data-testid="paginacao-atualizando" className="text-sm text-green-700">
            Atualizando...
          </span>
        )}
      </div>
    </div>
  );
};
export default Paginacao;
