import { useMutation } from "@tanstack/react-query";
import type { UsuarioLogin } from "../../interfaces/UsuarioLogin";
import useAPIAutenticacao from "./useAPIAutenticacao";

const useEfetuarLogin = () => {
  const { login } = useAPIAutenticacao();
  
  return useMutation({
    mutationFn: (usuarioLogin: UsuarioLogin) => login(usuarioLogin),
  });
};
export default useEfetuarLogin;
