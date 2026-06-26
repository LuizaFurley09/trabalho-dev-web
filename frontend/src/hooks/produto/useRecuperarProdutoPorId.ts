import { useQuery } from "@tanstack/react-query";
import type { Produto } from "../../interfaces/Produto";
import useAPI from "../useAPI";

const useRecuperarProdutoPorId = (id: number, removido: boolean = false) => {
  const {recuperarPorId} = useAPI<Produto>("/produtos");

  return useQuery({
    queryKey: ["produtos", id],
    queryFn: () => recuperarPorId(id),
    enabled: !removido
    // staleTime: 10_000,
  });
};
export default useRecuperarProdutoPorId;
