import { useMutation } from "@tanstack/react-query";
import { queryClient } from "../../main";
import useAPI from "../useAPI";
import type { Produto } from "../../interfaces/Produto";

const useRemoverProduto = () => {
  const {removerPorId} = useAPI<Produto>("/produtos");

  return useMutation({
    mutationFn: (id: number) => removerPorId(id),
    onSuccess: async () => {
      // invalidateQueries retorna uma Promise, logo, para tornar esse método mais 
      // síncrono utilizamos async / await. A função definida em onSettled é 
      // executada após a execução de onSuccess. Sem async/await ao se clicar no 
      // botão remover (de um produto), a função onSettled (definida em 
      // ProdutosComPaginacaoPage) é executada imediatamente e o botão "Removendo..." 
      // com o spinner irá piscar muito rápido. Com async / await, a função onSettled 
      // só será executada quando onSuccess terminar.   
      await queryClient.invalidateQueries({
        queryKey: ["produtos"],
      });
    },
  });
};
export default useRemoverProduto;