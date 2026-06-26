import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { Produto } from "../../interfaces/Produto";
import useAPI from "../useAPI";

const useRecuperarProdutosComPaginacao = (queryString: Record<string, string>) => {
  const {recuperarComPaginacao} = useAPI<Produto>("/produtos");
  
  return useQuery({
    queryKey: ["produtos", "paginacao", queryString],
    queryFn: () => recuperarComPaginacao(queryString),
    placeholderData: keepPreviousData
  });
};
export default useRecuperarProdutosComPaginacao;
