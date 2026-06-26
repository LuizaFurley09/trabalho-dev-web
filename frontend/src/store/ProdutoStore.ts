import { create } from "zustand";
import type { Produto } from "../interfaces/Produto";

interface ProdutoStore {
    pagina: number;
    tamanho: number;
    nome: string;   
    idRemovendo: number | null;
    mensagem: string;
    produtoSelecionado: Produto;

    setPagina: (novaPagina: number) => void;
    setNome: (novoNome: string) => void;
    setIdRemovendo: (novoIdRemovendo: number | null) => void;
    setMensagem: (novaMensagem: string) => void;
    setProdutoSelecionado: (novoProdutoSelecionado: Produto) => void;
}

const useProdutoStore = create<ProdutoStore>((set) => ({
    pagina: 0,
    tamanho: 5,
    nome: "",
    idRemovendo: null,
    mensagem: "",
    produtoSelecionado: {} as Produto,
    
    setPagina: (novaPagina: number) => set((s) => ({pagina: novaPagina, tamanho: s.tamanho, nome: s.nome})),
    setNome:  (novoNome: string) => set(() => ({nome: novoNome})),
    setIdRemovendo:  (novoIdRemovendo: number | null) => set(() => ({idRemovendo: novoIdRemovendo})),
    setMensagem:  (novaMensagem: string) => set(() => ({mensagem: novaMensagem})),
    setProdutoSelecionado: (novoProdutoSelecionado: Produto) => 
        set(() => ({produtoSelecionado: novoProdutoSelecionado}))
}))
export default useProdutoStore