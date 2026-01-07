import NavBar from "./NavBar"
import { Outlet } from "react-router"

const Layout = () => {
    return <>
        <NavBar />
        {/* Componente speciale in React Router utilizato come placeholder (verrà rimpiazzato da Login, Home, Cart, ...) */}
        <Outlet />
    </>
}

export default Layout