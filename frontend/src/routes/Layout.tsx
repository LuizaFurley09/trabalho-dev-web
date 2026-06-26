import { Outlet } from "react-router-dom"
import NavBar from "../components/NavBar"

const Layout = () => {
  return (
    <>
      <NavBar />
      <div className="mx-3 md:mx-10 lg:mx-20">
        <Outlet />
      </div>
    </>
  )
}
export default Layout