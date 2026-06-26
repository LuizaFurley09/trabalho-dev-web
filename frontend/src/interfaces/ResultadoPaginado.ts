export interface ResultadoPaginado<T> {
    totalDeItens: number;
    totalDePaginas: number;
    paginaCorrente: number;
    itens: T[];
}