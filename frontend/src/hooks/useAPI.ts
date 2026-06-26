import type { ResultadoPaginado } from "../interfaces/ResultadoPaginado";
import { URL_BASE } from "../util/constantes";
import useFetchWithAuth from "./useFetchWithAuth";

const useAPI = <T>(endpoint: string) => {
  const URL = `${URL_BASE}${endpoint}`;
  const { fetchWithAuth } = useFetchWithAuth();

  const handleResponseError = async (response: Response) => {
    if (!response.ok) {
      // Se ocorrer um erro 401 ou 403 então a linha abaixo com "return await response.json()"
      // dará erro pois não retornará json.
      const error: any = await response.json().catch(() => ({}));
      if (error) throw error;
      else
        throw new Error(
          "Erro desconhecido: " + " - Status code: " + response.status
        );
    }
  };

  const cadastrar = async (obj: T): Promise<T> => {
    const response = await fetchWithAuth(URL, {
      method: "POST",
      headers: {
        "Content-type": "application/json",
      },
      body: JSON.stringify(obj),
    });
    await handleResponseError(response);
    return await response.json();
  };

  const recuperarPorId = async (id: number): Promise<T> => {
    const response = await fetchWithAuth(`${URL}/${id}`);
    await handleResponseError(response);
    return await response.json();
  };

  const recuperar = async (): Promise<T[]> => {
    const response = await fetchWithAuth(URL);
    await handleResponseError(response);
    return await response.json();
  };

  const alterar = async (obj: T): Promise<T> => {
    const response = await fetchWithAuth(URL, {
      method: "PUT",
      headers: {
        "Content-type": "application/json",
      },
      body: JSON.stringify(obj),
    });
    await handleResponseError(response);
    return await response.json();
  };

  const recuperarComPaginacao = async (
    queryString: Record<string, string>
  ): Promise<ResultadoPaginado<T>> => {
    const response = await fetchWithAuth(
      `${URL}/paginacao?` + new URLSearchParams({ ...queryString })
    );
    await handleResponseError(response);
    return await response.json();
  };

  const removerPorId = async (id: number): Promise<void> => {
    const response = await fetchWithAuth(`${URL}/${id}`, {
      method: "DELETE",
    });
    await handleResponseError(response);
    // sem: return await response.json() pois o back-end retorna void.
  };

  return {
    cadastrar,
    recuperarPorId,
    recuperar,
    alterar,
    recuperarComPaginacao,
    removerPorId,
  };
};
export default useAPI;
