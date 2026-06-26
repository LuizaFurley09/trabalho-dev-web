import { isRouteErrorResponse, useRouteError } from "react-router-dom";
import NavBar from "../components/NavBar";
import isErrorResponse from "../util/isErrorResponse";

const ErrorPage = () => {
  const error = useRouteError();
  return (
    <>
      <NavBar />
      <div className="mx-3 md:mx-10 lg:mx-20">
        <h1 className="mb-1 text-xl font-semibold">Página de Erro</h1>
        <hr className="mb-4" />
        {isRouteErrorResponse(error) ? (
          "Página requisitada inválida"
        ) : error instanceof Error ? (
          error.message
        ) : isErrorResponse(error) ? (
          <div>
            <h6>Mensagem do servidor:</h6>
            <pre>{JSON.stringify(error, null, 2)}</pre>
          </div>
        ) : (
          "Erro desconhecido. Msg: " + error
        )}
      </div>
    </>
  );
};
export default ErrorPage;
