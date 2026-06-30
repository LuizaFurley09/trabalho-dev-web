import "bootstrap-icons/font/bootstrap-icons.min.css";
import { useState } from "react";
import { NavLink } from "react-router-dom";
import hortifruti from "../assets/hortifruti.png";
import type { Produto } from "../interfaces/Produto";
import useProdutoStore from "../store/ProdutoStore";
import useTokenStore from "../store/TokenStore";

// Modos do Tailwindcss:
// sm: 640px
// md: 768px
// lg: 1024px
// xl: 1280px
// 2xl: 1536px

const NavBar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const tokenResponse = useTokenStore((s) => s.tokenResponse);
  const setProdutoSelecionado = useProdutoStore((s) => s.setProdutoSelecionado);

  return (
    <nav className="mb-6 bg-gray-100 py-4">
      <div className="mx-3 md:mx-10 lg:mx-20">
        <div className="flex justify-between">
          <div className="flex items-center space-x-4">
            <NavLink to="/" onClick={() => setIsOpen(false)}>
              <img src={hortifruti} width="45px" />
            </NavLink>
            <NavLink
              className="hidden text-gray-700 hover:text-black md:block"
              aria-current="page"
              to="/"
            >
              <i className="bi bi-house me-1"></i>
              Home
            </NavLink>
            <NavLink
              className="hidden text-gray-700 hover:text-black md:block"
              to="/carrinho"
            >
              <i className="bi bi-cart3 me-1"></i>
              Carrinho
            </NavLink>
            <NavLink
              data-testid="fav"
              className="hidden text-gray-700 hover:text-black md:block"
              to="/favoritos"
            >
              <i className="bi bi-heart me-1"></i>
              Favoritos
            </NavLink>
          </div>
          <div className="hidden items-center space-x-4 md:flex">
            <NavLink
              className="text-gray-700 hover:text-black"
              to="/produtos-sem-paginacao"
            >
              <i className="bi bi-card-list me-1"></i>
              Produtos sem Paginação
            </NavLink>
            <NavLink
              data-testid="com-pag"
              className="text-gray-700 hover:text-black"
              to="/produtos-com-paginacao"
            >
              <i className="bi bi-card-list me-1"></i>
              Produtos com Paginação
            </NavLink>
            <NavLink
              onClick={() => setProdutoSelecionado({} as Produto)}
              data-testid="cad"
              className="text-gray-700 hover:text-black"
              to="/cadastrar-produto"
            >
              <i className="bi bi-database-add me-1"></i>
              Cad. Produto
            </NavLink>
            <NavLink className="text-gray-700 hover:text-black" to="/login">
              {tokenResponse.idUsuario > 0 ? 
                <>
                  <i className="bi bi-box-arrow-left me-1"></i>
                  Sair
                </> : 
                <>
                  <i className="bi bi-box-arrow-in-right me-1"></i>
                  Entrar
                </>
              }
            </NavLink>
          </div>

          <button
            onClick={() => setIsOpen(!isOpen)}
            className={
              "rounded bg-gray-400 p-2 text-white md:hidden " +
              (isOpen ? "border-2 border-gray-800" : "border border-gray-400")
            }
          >
            {/* Use um ícone de hambúrguer aqui */}
            <svg
              className="h-6 w-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M4 6h16M4 12h16m-7 6h7"
              ></path>
            </svg>
          </button>
        </div>
        {isOpen && (
          <div className="mt-4 flex flex-col space-y-2 md:hidden">
            <NavLink
              className="text-gray-700 hover:text-black"
              aria-current="page"
              to="/"
              onClick={() => setIsOpen(false)}
            >
              <i className="bi bi-house me-1"></i>
              Home
            </NavLink>
            <NavLink
              className="text-gray-700 hover:text-black"
              to="/carrinho"
              onClick={() => setIsOpen(false)}
            >
              <i className="bi bi-cart3 me-1"></i>
              Carrinho
            </NavLink>
            <NavLink
              className="text-gray-700 hover:text-black"
              to="/favoritos"
              onClick={() => setIsOpen(false)}
            >
              <i className="bi bi-heart me-1"></i>
              Favoritos
            </NavLink>
            <NavLink
              className="text-gray-700 hover:text-black"
              to="/produtos-sem-paginacao"
              onClick={() => setIsOpen(false)}
            >
              <i className="bi bi-card-list me-1"></i>
              Produtos sem Paginação
            </NavLink>
            <NavLink
              className="text-gray-700 hover:text-black"
              to="/produtos-com-paginacao"
              onClick={() => setIsOpen(false)}
            >
              <i className="bi bi-card-list me-1"></i>
              Produtos com Paginação
            </NavLink>
            <NavLink
              className="text-gray-700 hover:text-black"
              to="/cadastrar-produto"
              onClick={() => {
                setIsOpen(false);
                setProdutoSelecionado({} as Produto);
              }}
            >
              <i className="bi bi-database-add me-1"></i>
              Cad. Produto
            </NavLink>
            <NavLink
              className="text-gray-700 hover:text-black"
              to="/login"
              onClick={() => setIsOpen(false)}
            >
              {tokenResponse.idUsuario > 0 ? 
                <>
                  <i className="bi bi-box-arrow-left me-1"></i>
                  Sair
                </> : 
                <>
                  <i className="bi bi-box-arrow-in-right me-1"></i>
                  Entrar
                </>
              }
            </NavLink>
          </div>
        )}
      </div>
    </nav>
  );
};
export default NavBar;
