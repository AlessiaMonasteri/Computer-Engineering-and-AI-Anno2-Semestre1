import { NavLink, useNavigate, useLocation } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";

const NavBar = () => {
  // useSelector legge lo stato globale Redux prendendo l’utente attualmente loggato: se è null o undefined → utente non autenticato.
  // Serve per mostrare/nascondere i link Login, Logout
  const currentUser = useSelector((state) => state.user.currentUser);

  // Prendo state.cart.items (array) e uso reduce per sommare le quantity: viene restituito il numero totale di articoli nel carrello.
  const cartCount = useSelector((state) => state.cart.items.reduce((sum, item) => sum + item.quantity, 0));

  // Invia le azioni a Redux
  const dispatch = useDispatch();

  // Cambia pagina
  const navigate = useNavigate();
  
  // Per sapere in che URL mi trovo
  const location = useLocation();

  // Invia l’azione LOGOUT a Redux, resetta lo stato dell’utente e reindirizza alla pagina /login
  const handleLogout = () => {dispatch({ type: "LOGOUT" }); navigate("/login") };

  // Se sono nella pagina di login, nascondo Home e Contact Us
  const hideLinks = location.pathname === "/login";

  return (
    <nav className="navbar">
      {/* SINISTRA */}
      <div className="navbar-left">
        {currentUser && <span>Hello, {currentUser.name}</span>}
      </div>

      {/* CENTRO */}
      <div className="navbar-center">
        {!hideLinks && <NavLink to="/">Home</NavLink>}

        {/* LINK ADMIN */}
        {currentUser?.role === "admin" && !hideLinks && (
          <>
            <NavLink to="/admin/orders">Orders</NavLink>
            <NavLink to="/admin/newproduct">New product</NavLink>
            <NavLink to="/admin/stock">Restock</NavLink>
          </>
        )}

        {/*LINK UTENTE NORMALE */}
        {currentUser && currentUser.role !== "admin" && !hideLinks && (
          <>
            <NavLink to="/orders">My Orders</NavLink>
            <NavLink to="/cart">🛒 {cartCount}</NavLink>
          </>
        )}

        {!hideLinks && <NavLink to="/contact">Contact Us</NavLink>}
      </div>

      {/* DESTRA */}
      <div className="navbar-right">
        <div>
          {currentUser && <button onClick={handleLogout}>Logout</button>}
        </div>
      </div>
    </nav>
  );
};

export default NavBar;
