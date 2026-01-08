import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
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

// La pagina OrdersPage mostra gli ordini dell'utente attualmente loggato
const OrdersPage = () => {
  // Lista degli ordini dell'utente
  const [orders, setOrders] = useState([]);
  // Stato per mostrare il loader durante il fetch
  const [loading, setLoading] = useState(false);
  // Stato per eventuali errori
  const [error, setError] = useState(null);
  // Recupera l'utente loggato dallo store Redux
  const currentUser = useSelector((state) => state.user.currentUser);

  // useEffect viene eseguito al mount e ogni volta che cambia currentUser
  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        setError(null);
        if (!currentUser) {
          setError("You must be logged in to see your orders.");
          // Svuota eventuali ordini precedenti
          setOrders([]);
          return;
        }
        // Chiamata API filtrata per userId
        const res = await fetch(
          `http://localhost:3001/orders?userId=${currentUser.id}`
        );

        // Se la risposta HTTP non è OK
        if (!res.ok) {
          const text = await res.text();
          throw new Error(`HTTP ${res.status}: ${text}`);
        }

        // Converte la risposta in JSON
        const data = await res.json();
        
        // Salva gli ordini nello state
        setOrders(data);
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        // Disattiva il loader in ogni caso
        setLoading(false);
      }
    };
    fetchOrders();
  }, [currentUser]);

  // Rendering condizionale per il Loader
  if (loading) {
    return (
        <div className="loader-wrapper">
          <MoonLoader color="#36d7b7" size={100} />
        </div>
    );
  }

  // Mostra eventuali errori 
  if (error) {
    return (
      <div className="error">
        Errore nel caricamento degli ordini: {error}
      </div>
    );
  }

  //Se non ci sono ordini
  if (!orders || orders.length === 0) {
    return (
      <div className="info">
        <p>You haven't placed any orders yet.</p>
      </div>
    );
  }

  return (
    <div>
      <h2>Your Orders</h2>
      {/* Tabella Ordini */}
      <table className="orderTable">
        <thead>
          <tr className="orderHeader">
            <th>Order #</th>
            <th>Date</th>
            <th>Products</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order) => (
            // Riga per ogni ordine
            <tr key={order.id} className="orderTableContent">
              <td>{order.id}</td>
              <td>{formatDate(order.date)}</td>
              <td>
                <ul className="orderModelList">
                  {order.items?.map((item) => (
                    // Riga per ogni prodotto dell'ordine
                    <li key={item.id}>
                      {item.model} — €{item.price} x {item.qty}
                    </li>
                  ))}
                </ul>
              </td>
              <td>€{order.total}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default OrdersPage;
