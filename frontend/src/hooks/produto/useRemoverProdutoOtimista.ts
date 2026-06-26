import { useMutation } from "@tanstack/react-query";
import { queryClient } from "../../main";
import type { Produto } from "../../interfaces/Produto";
import useAPI from "../useAPI";

const useRemoverProdutoOtimista = () => {
  const {removerPorId} = useAPI<Produto>("/produtos");
  
  return useMutation({
    mutationFn: (id: number) => removerPorId(id),

    // A função definida em onMutate é executada antes da mutation function ser executada.
    // Ela atualiza o cache, removendo dele o produto que será removido pela mutation function
    onMutate: (id: number) => {
      // Salva em produtosAntesDaRemocao os produtos que serão uilizados para restabelecer 
      // o cache "produtos" caso a remoção falhe.
      const produtosAntesDaRemocao = queryClient.getQueryData<Produto[]>(["produtos"]);
      console.log("produtosAntesDaRemocao = ", produtosAntesDaRemocao);

      // Atualiza o cache removendo dele o produto que será removido.
      // O cache será atualizado pela lista de produtos retornada função filter que retorna 
      // uma nova lista contendo todos os produtos que possuem um id com valor diferente do 
      // parâmetro id. Veja o parâmetro acima.
      queryClient.setQueryData<Produto[]>(["produtos"], (produtos) => {
        return produtos?.filter((produto) => produto.id !== id);
      })

      // Os produtos retornados em produtosAntesDaRemocao estarão disponíveis em context da 
      // função onError abaixo. Sem o return abaixo, context não terá produtosAntesDaRemocao.
      return {produtosAntesDaRemocao};
    },
    onError: (_, id, context) => {
      console.log("id = ", id);
      // Ao ocorrer um erro na remoção de um produto, é preciso restaurar o cache ("produtos") 
      // para o seu estado antes da remoção do produto. 
      queryClient.setQueryData(["produtos"], context?.produtosAntesDaRemocao);
    },
  });
};
export default useRemoverProdutoOtimista;
