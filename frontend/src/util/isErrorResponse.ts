// Essa é uma "função de tipo de guarda (type guard)" em TypeScript. Ela serve para verificar se um
// objeto é de um tipo específico—neste caso, ErrorResponse — e, se a verificação for verdadeira, o
// TypeScript consegue inferir o tipo dentro de blocos condicionais.

// Se a condição no return for verdadeira, a função retorna true, e graças à assinatura de guarda de
// tipo, TypeScript passa a entender que, dentro do bloco condicional onde a função foi chamada,
// error é do tipo ErrorResponse.

import type { ErrorResponse } from "../interfaces/ErrorResponse";

function isErrorResponse(error: any): error is ErrorResponse {
  return error && typeof error.requestUri === "string";
}
export default isErrorResponse;
