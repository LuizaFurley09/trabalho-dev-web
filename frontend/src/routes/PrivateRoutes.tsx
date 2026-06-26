import { Navigate, useLocation } from "react-router-dom";
import useTokenStore from "../store/TokenStore";
import Layout from "./Layout";

const PrivateRoutes = () => {
  const tokenResponse = useTokenStore((s) => s.tokenResponse);
  const location = useLocation();

  if (tokenResponse.idUsuario > 0) {
    return <Layout />
  }
  else {
    return <Navigate to="/login" state={{destino: location.pathname}} />
  }
}
export default PrivateRoutes