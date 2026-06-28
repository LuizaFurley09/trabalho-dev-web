import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import z from "zod";
import type { TokenResponse } from "../interfaces/TokenResponse";
import type { UsuarioLogin } from "../interfaces/UsuarioLogin";
import useLoginStore from "../store/LoginStore";
import useTokenStore from "../store/TokenStore";
import isErrorResponse from "../util/isErrorResponse";
import useEfetuarLogin from "../hooks/autenticacao/useEfetuarLogin";

const schema = z.object({
  email: z
    .email("Informe um email válido."),
  senha: z
    .string()
    .nonempty("Informe a senha.")
});

type FormLogin = z.infer<typeof schema>;

const LoginPage = () => {
  const setTokenResponse = useTokenStore((s) => s.setTokenResponse);
  const loginInvalido = useLoginStore((s) => s.loginInvalido);
  const setLoginInvalido = useLoginStore((s) => s.setLoginInvalido);
  const setMsg = useLoginStore((s) => s.setMsg);
  const msg = useLoginStore((s) => s.msg);

  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    setTokenResponse({ idUsuario: 0, token: "", nome: "", role: "" }); // Logout
    return () => {
      setLoginInvalido(false);
      setMsg("");
    };
  }, []);

  const { register, handleSubmit, formState: {errors} } = useForm<FormLogin>({resolver: zodResolver(schema)});
  const { mutate: efetuarLogin } = useEfetuarLogin();

  const submit = ({ email, senha }: FormLogin) => {
    const usuarioLogin: UsuarioLogin = { email, senha };
    efetuarLogin(usuarioLogin, {
      onSuccess: (tokenResp: TokenResponse) => {
        console.log("tokenResp = ", tokenResp);

        setTokenResponse({
          idUsuario: tokenResp.idUsuario,
          token: tokenResp.token,
          nome: tokenResp.nome,
          role: tokenResp.role,
        });
        if (location.state?.destino) {
          navigate(location.state.destino);
        } else {
          navigate("/");
        }
      },
      onError: (error: any) => {
        if (isErrorResponse(error)) {
          setLoginInvalido(true);
          setMsg("Login inválido");
        } else {
          console.log("deu erro", error);
          // Aqui nunca irá ocorrer o erro 403 pois todos os usuários podem 
          // tentar efetuar login. Um erro 403 só ocorrerá quando um usuário
          // estive logado e tentar fazer algo sem possuir o respectivo Role.
          // *****************************************************************
          // *   Aqui estamos capturando o erro lançado em useEfetuarLogin   *
          // *****************************************************************
          if (error.message.includes("401")) {
            setLoginInvalido(true);
            setMsg("Email ou senha inválidos.");
          } else {
            setLoginInvalido(true);
            setMsg(
              "Não foi possível efetuar o login. Por favor, tente mais tarde."
            );
          }
        }
      },
    });
  };

  // if (errorEfetuarLogin) throw errorEfetuarLogin;

  return (
    <>
      {/* Container queries - Como adaptar o tamanho dos filhos com base no tamanho dos pais.
          sm ≥ 640px
          md ≥ 768px
          lg ≥ 1024px
          xl ≥ 1280px
          2xl ≥ 1536px
          3xl (se customizado) ≥ 1600px (meu monitor não chega nem a 1500px, logo, não consigo testar) */}

      {/* pseudo elements */}
      <div className="mt-12 flex justify-center bg-white">
        {/* justify-center, centraliza a div abaixo no eixo principal (horizontal). 
            items-center, centraliza a div abaixo no eixo cruzado (vertical). */}
        <div className="w-full max-w-md space-y-6 rounded-2xl bg-white p-8 shadow-2xl duration-300">
          {/* w-full: largura 100% do container. Sem w-full, a div fica com a largura 
          do conteudo. Com w-full, ela ocupa 100% do container ate o limite de max-w-md.
          max-w-md: limita a largura máxima (md ≈ 28rem / 448px).
          space-y-6: coloca espacamento vertical entre os filhos diretos (gap).
          rounded-2xl: cantos bem arredondados.
          bg-white: fundo branco.
          p-8: padding interno (2rem ou 32px).
          shadow-2xl: sombra forte.
          duration-300: transicoes duram 300ms (quando houver hover, focus, etc.). */}
          <h2 className="text-center text-2xl font-bold text-gray-800">
            Informe seu Email e Senha
          </h2>
          {loginInvalido && (
            <div 
              data-testid="login-error"
              className="mb-3 rounded border-2 border-red-600 bg-red-100 px-4 py-3 font-bold text-red-800"
            >
              {msg}
            </div>
          )}	
          <form onSubmit={handleSubmit(submit)} className="space-y-4">
            <div>
              <label
                htmlFor="email"
                className="mb-1 block text-sm font-medium text-gray-700"
                // Sem block, o <label> é inline.
                // Se você colocar ambos (label e input) na mesma linha sem quebra (ou colocar
                // display: inline no input), o label e o input ficam lado a lado.

                // block - block faz o <label> virar elemento de bloco, então ele ocupa a linha
                // inteira e quebra linha antes/depois. Isso ajuda a manter o rótulo acima do input,
                // em vez de ficar na mesma linha.
              >
                Email
              </label>
              <input
                {...register("email")} // Adiciona ao input os aributos: onChange, onBlur, name e ref
                id="email"
                data-testid="login-email"
                type="text"
                placeholder="Informe seu email"
                // id="email"
                className="w-full rounded-md border-2 border-gray-300 bg-white px-4 py-2 text-gray-900 outline-none hover:border-gray-500"
              />
              {errors.email && <p style={{color: "red", 
                                          fontSize: "14px", 
                                          marginTop: "2px", 
                                          marginBottom: "0px"}}>{errors.email.message}</p>}
            </div>
            <div>
              <label
                htmlFor="senha"
                // Usando hmlFor="senha" no label e id="senha" no input, ao passar o mouse sobre o label Senha
                // o input abaixo recebe uma borda. Não estou usando isso.
                className="mb-1 block text-sm font-medium text-gray-700"
              >
                Senha
              </label>
              <input
                {...register("senha")}
                id="senha"
                data-testid="login-senha"
                type="password"
                placeholder="Informe sua senha"
                // id="senha"
                className="w-full rounded-md border-2 border-gray-300 bg-white px-4 py-2 text-gray-900 outline-none hover:border-gray-500"
              />
              {errors.senha && <p style={{color: "red", 
                                          fontSize: "14px", 
                                          marginTop: "2px", 
                                          marginBottom: "0px"}}>{errors.senha.message}</p>}
            </div>
            <div className="flex items-center justify-end">
              <a tabIndex={-1} href="#" className="text-green-600 hover:underline">
                Esqueceu a senha?
              </a>
            </div>
            <button
              type="submit"
              data-testid="login-submit"
              className="w-full cursor-pointer rounded-md bg-green-600 py-2 font-semibold text-white duration-200 hover:bg-green-700"
            >
              Entrar
            </button>
          </form>
          <p className="text-center text-sm text-gray-500">
            <span className="me-1">Não tem conta?</span>
            {/* A âncora tenta ficar na mesma linha - é um inline element */}
            <a href="#" className="text-green-600 hover:underline">
              Cadastre-se
            </a>
          </p>
        </div>
      </div>
    </>
  );
};
export default LoginPage;
