import { create } from "zustand";
import type { TokenResponse } from "../interfaces/TokenResponse";

interface TokenStore{
    tokenResponse: TokenResponse;
    setTokenResponse: (novoTokenResponse: TokenResponse) => void;
}

const useTokenStore = create<TokenStore>((set) => ({
    tokenResponse: {token: "", idUsuario: 0, nome: "", role: ""},
    setTokenResponse: (novoTokenResponse: TokenResponse) => set(() => ({tokenResponse: novoTokenResponse})),
}))
export default useTokenStore;
