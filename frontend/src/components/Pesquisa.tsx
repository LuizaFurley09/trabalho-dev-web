import _ from "lodash";
import useProdutoStore from "../store/ProdutoStore";

const Pesquisa = () => {
  const setNome = useProdutoStore((s) => s.setNome);
  const setPagina = useProdutoStore((s) => s.setPagina);
  
  const tratarPesquisa = (nome: string) => {
    setNome(nome);
    setPagina(0);
  };

  const debouncedFunction = _.debounce((nome: string) => {
    tratarPesquisa(nome);
  }, 1000);

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    debouncedFunction(event.target.value);
  };

  return (
    <input
      onChange={handleChange}
      type="text"
      className="input mb-3"
      placeholder="Informe o nome do produto desejado..."
    />
  );
};
export default Pesquisa;
