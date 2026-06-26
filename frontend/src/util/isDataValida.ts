import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";

// É preciso utilizar o plugin customParseFormat de dayjs.
dayjs.extend(customParseFormat);

const isDataValida = (umaData: string) => {
  // Enquanto os 3 valores (ano, mes e dia) não são digitados não entra aqui.
  console.log("umaData = ", umaData);  // YYYY-MM-DD - 0001-01-01
  // O true ativa validação estrita. Por exemplo, não irá aceitar 2025/06/12.
  // Para validar datas com formatos personalizados como YYYY-MM-DD, é preciso importar
  // e estender customParseFormat.
  const resultado: boolean = dayjs(umaData, "YYYY-MM-DD", true).isValid();
  console.log(resultado);
  return resultado;
};
export default isDataValida;
