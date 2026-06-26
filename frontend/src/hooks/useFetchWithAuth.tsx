import { useNavigate } from "react-router-dom";
import useLoginStore from "../store/LoginStore";
import useTokenStore from "../store/TokenStore";

const useFetchWithAuth = () => {
  const setLoginInvalido = useLoginStore((s) => s.setLoginInvalido);
  const setMsg = useLoginStore((s) => s.setMsg);
  
  const tokenResponse = useTokenStore((s) => s.tokenResponse);
  const setTokenResponse = useTokenStore((s) => s.setTokenResponse);
  const navigate = useNavigate();

  const fetchWithAuth = async (url: string, options?: any) => {

    console.log("Entrou em fetchWithAuth - url = ", url);
    console.log("Entrou em fetchWithAuth - options = ", options);  // options pode ser method, headers ou body

    const token = tokenResponse.token;

    let newHeaders: Record<string, string> = {}; // definindo headers como um Record, isto é, como objeto que contém
                                                 // pares de chave e valor onde ambos são do tipo string.
    if (options && options.headers) {
      newHeaders = { ...options.headers };
    }

    if (token != "") {
      newHeaders["Authorization"] = `Bearer ${token}`; // Essa linha de código acrescenta ao objeto
                                                       // newHeaders mais um par de chave e valor.
    }

    options = { ...(options || {}), headers: newHeaders }; // se options tiver headers, fica valendo o último (newHeaders)

    console.log("url da requisição = ", url);
    console.log("options da requisição = ", options);
    
    const response = await fetch(url, { ...options });

    if (!response.ok) {
      console.log("Ocorreu um erro com status = ", response.status);

      if (response.status === 401) {
        setLoginInvalido(true);
        setMsg("Efetue login para acessar este recurso.");
        setTokenResponse({ token: "", idUsuario: 0,  nome: "", role: "" });
        navigate("/login");
      } else if (response.status === 403) {
        setLoginInvalido(true);
        setMsg("Sem permissão para acessar este recurso.");
        setTokenResponse({ token: "", idUsuario: 0,  nome: "", role: "" });
        navigate("/login");
      } else {
        // Se o conteúdo retornado for json não dará erro, caso contrário irá retornar um objeto vazio.
        const error: any = await response.json().catch(() => ({}));
        if (error) throw error;
        else throw new Error(("Erro desconhecido: ") + " - Status code: " + response.status);
      }
    }
    return response;
  };

  return { fetchWithAuth };
};
export default useFetchWithAuth;

