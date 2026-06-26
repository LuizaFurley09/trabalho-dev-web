import { useMutation } from "@tanstack/react-query";
import type { Produto } from "../../interfaces/Produto";
import { queryClient } from "../../main";
import useAPI from "../useAPI";
import { URL_PRODUTOS } from "../../util/constantes";

const useCadastrarProduto = () => {
  const {cadastrar} = useAPI<Produto>(URL_PRODUTOS);
  
  return useMutation({
    mutationFn: (produto: Produto) => cadastrar(produto),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["produtos"],
        exact: false
      })      
    }
  });
};
export default useCadastrarProduto;
