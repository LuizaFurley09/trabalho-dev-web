import { useMutation } from "@tanstack/react-query";
import type { Produto } from "../../interfaces/Produto";
import { queryClient } from "../../main";
import useAPI from "../useAPI";
import { URL_PRODUTOS } from "../../util/constantes";

const useAlterarProduto = () => {
  const {alterar} = useAPI<Produto>(URL_PRODUTOS);
  
  return useMutation({
    mutationFn: (produto: Produto) => alterar(produto),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["produtos"],
        exact: false
      })      
    }
  });
};
export default useAlterarProduto;
