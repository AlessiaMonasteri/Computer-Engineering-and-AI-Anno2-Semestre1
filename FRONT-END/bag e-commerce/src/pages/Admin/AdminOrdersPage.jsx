import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { MoonLoader } from "react-spinners";

// Utility per avere la data in formato DD-MM-YYYY HH:mm
const formatDate = (isoString) => {
  const date = new Date(isoString);
  const day = String(date.getDate()).padStart(2, "0");
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const year = date.getFullYear();
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  return `${day}-${month}-${year} ${hours}:${minutes}`;
};

// La pagina AdminOrdersPage mostra tutti gli ordini di tutti gli utenti
const AdminOrdersPage = () => {

  // Utente attualmente loggato
  const currentUser = useSelector((state) => state.user.currentUser);

  // Si attiva quando cambia currentUser; controlla se l’utente non esiste oppure non ha ruolo admin
  useEffect(() => {
    if (!currentUser || currentUser.role !== "admin") {
      setError("Access denied: admin only.");
    }
  }, [currentUser]);

  // Stati locali del componente
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Fetch ordini:
  // attiva il loader, resetta gli errori, fetch da db.json, gestisce gli errori HTTP, salva gli ordini nello stato
  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        setError(null);
        // Recupera tutti gli ordini dal db.json
        const res = await fetch("http://localhost:3001/orders");
        if (!res.ok) {
          const text = await res.text();
          throw new Error(`HTTP ${res.status}: ${text}`);
        }

        // Converte la risposta in json
        const data = await res.json();
        // salvando gli ordini nello stato
        setOrders(data);

      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    // Gli ordini vengono caricati solo se l’utente è admin
    if (currentUser && currentUser.role === "admin") {
      fetchOrders();
    }
  }, [currentUser]);

  // Se l'utente non è admin ritorna un messaggio di errore e il link di redirect
  if (!currentUser || currentUser.role !== "admin") {
    return (
      <div>
        <h2>Access Denied</h2>
        <p>This page is for administrators only.</p>
        <p>
          <Link to="/">Go Back to Home Page</Link>
        </p>
      </div>
    );
  }

  // Loader
  if (loading) {
    return (
        <div className="loader-wrapper">
          <MoonLoader color="#36d7b7" size={100} />
        </div>
    );
  }

  // Errore diverso dall'autorizzazione
  if (error && error !== "Access denied: admin only.") {
    return (
      <div className="error">
        Error loading orders: {error}
      </div>
    );
  }

  // Messaggio informativo di assenza di ordini
  if (!orders || orders.length === 0) {
    return (
      <div className="info">
        <p>No order present.</p>
      </div>
    );
  }

  return (
    <div className="admin-orders">
      {/* Tabella con tutti gli ordini di tutti gli utenti*/}
      <h2>Admin - All orders</h2>
      <table className = "orderTable">
        <thead>
          <tr className="orderHeader">
            <th>Order #</th>
            <th>User ID</th>
            <th>Email</th>
            <th>Date</th>
            <th>Products</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {/*Iterazione sull’array orders*/}
          {orders.map((order) => (
            <tr key={order.id} className = "orderTableContent">
              <td>{order.id}</td>
              <td>{order.userId ?? "-"}</td>
              <td>{order.userEmail ?? "-"}</td>
              <td>{formatDate(order.date)}</td>
              <td>
                <ul className="orderModelList">
                  {/*Iterazione sui prodotti dell’ordine*/}
                  {order.items?.map((item) => (
                    <li key={item.id}>
                      {item.model} — €{item.price} x {item.qty}
                    </li>
                  ))}
                </ul>
              </td>
              <td className="orderTableContent">€{order.total}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminOrdersPage;
