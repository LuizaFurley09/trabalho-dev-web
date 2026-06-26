import { create } from "zustand";

interface LoginStore{
    loginInvalido: boolean;
    msg: string;
    
    setLoginInvalido: (novoValorLoginInvalido: boolean) => void;
    setMsg: (novaMsg: string) => void;
}

const useLoginStore = create<LoginStore>((set) => ({
    loginInvalido: false,
    msg: "",
    
    setLoginInvalido: (novoValoLoginInvalido: boolean) => set(() => ({loginInvalido: novoValoLoginInvalido})),
    setMsg: (novaMsg: string) => set(() => ({msg: novaMsg}))
}))
export default useLoginStore;
