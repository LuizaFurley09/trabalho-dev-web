import { useQuery } from "@tanstack/react-query";
import useAPI from "../useAPI";
import type { Produto } from "../../interfaces/Produto";
import { URL_PRODUTOS } from "../../util/constantes";

const useRecuperarProdutos = () => {
  const { recuperar } = useAPI<Produto>(URL_PRODUTOS);

  return useQuery({
    queryKey: ["produtos"],
    queryFn: recuperar,
    staleTime: 10_000,
  });
};
export default useRecuperarProdutos;
