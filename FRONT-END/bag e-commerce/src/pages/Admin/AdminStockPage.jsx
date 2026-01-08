import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { MoonLoader } from "react-spinners";

const PRODUCTS_URL = "http://localhost:3001/products";

// La pagina AdminStockPagee permette di restockare i prodotti (ADMIN-ONLY)
const AdminStockPage = () => {

  // Utente attualmente loggato
  const currentUser = useSelector((state) => state.user.currentUser);

  // Si attiva quando cambia currentUser; controlla se l’utente non esiste oppure non ha ruolo admin
  useEffect(() => {
    if (!currentUser || currentUser.role !== "admin") {
      setProductsError("Access denied: admin only.");
    }
  }, [currentUser]);

  // Stati locali del componente
  const [products, setProducts] = useState([]);
  const [productsError, setProductsError] = useState(null);
  const [loading, setLoading] = useState(false);

  // Stato per il restock: mappa productId -> valore input
  const [restockValues, setRestockValues] = useState({});

  // Fetch prodotti:
  // attiva il loader, resetta gli errori, fetch da db.json, gestisce gli errori HTTP, salva i prodotti nello stato
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        setProductsError(null);

        const res = await fetch(PRODUCTS_URL);
        if (!res.ok) {
          const text = await res.text();
          throw new Error(`Products HTTP ${res.status}: ${text}`);
        }

        const data = await res.json();
        setProducts(data);
      } catch (err) {
        console.error(err);
        setProductsError(err.message);
      } finally {
        setLoading(false);
      }
    };

    // I prodotti vengono caricati solo se l’utente è admin
    if (currentUser && currentUser.role === "admin") {
      fetchProducts();
    }
  }, [currentUser]);

  // Se l'utente non è admin ritorna un messaggio di errore e il link di redirect
  if (!currentUser || currentUser.role !== "admin") {
    return (
      <div className="error">
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

  // Gestione del re-stock
  const handleRestock = async (bagId) => {
    const rawValue = restockValues[bagId];
    const newStock = Number(rawValue);

    // Validazione: non accetta Nan o valori negativi 
    if (Number.isNaN(newStock) || newStock < 0) {
      alert("Please insert a valid non-negative number for stock.");
      return;
    }

    // Request al db.json con METHOD PATCH per aggiornare il numero dello stock
    // e l'attributo Available quando lo stock è maggiore di 0
    try {
      const res = await fetch(`${PRODUCTS_URL}/${bagId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          stock: newStock,
          available: newStock > 0,
        }),
      });

      // Response della PATCH fatta sopra
      if (!res.ok) {
        const text = await res.text();
        console.error("PATCH failed:", res.status, text);
        alert("Error while updating stock.");
        return;
      }

      // Aggiornamento dello stato locale
      setProducts((prev) =>
        prev.map((p) =>
          String(p.id) === String(bagId)
            ? { ...p, stock: newStock, available: newStock > 0 }
            : p
        )
      );

      // Ripulisce l’input di restock dopo un aggiornamento riuscito e gestisce eventuali errori
      setRestockValues((prev) => ({
        ...prev,
        [bagId]: "",
      }));
    } catch (err) {
      console.error("Error updating product stock:", err);
      alert("Unexpected error while updating stock.");
    }
  };

  return (
    <div className="stock-container">
      <h2>Admin - Stock management</h2>

      {/* Se c'è un errore nel caricamento prodotti, lo mostra */}
      {productsError && (
        <p className="error">
          Error loading products: {productsError}
        </p>
      )}

      {/* Se non ci sono prodotti mostra un messaggio informativo, altrimenti mostra la tabella*/}
      {(!products || products.length === 0) ? (
        <p className= "info">No bags found.</p>
      ) : (
        <table className="stockTable">
          <thead>
            <tr className="stockHeader">
              <th>ID</th>
              <th>Model</th>
              <th>Collection</th>
              <th>Stock</th>
              <th>Available</th>
              <th>Restock</th>
            </tr>
          </thead>
          <tbody>
            {/* Per ogni prodotto creo una riga */}
            {products.map((bag) => (
              <tr key={bag.id}>
                <td>{bag.id}</td>
                <td>{bag.model}</td>
                <td>{bag.collection}</td>
                <td>{bag.stock}</td>
                <td style={{textAlign: "center"}}>{bag.available ? "Yes" : "No"}</td>
                <td>
                  {/* Input per inserire il nuovo stock */}
                  <input type="number" min="0" className="inputStock"
                    value={restockValues[bag.id] ?? ""}
                    onChange={(e) =>
                      // Aggiorna lo state restockValues mantenendo gli altri valori
                      setRestockValues((prev) => ({...prev, [bag.id]: e.target.value}))
                    }/>
                  <button onClick={() => handleRestock(bag.id)}>Update</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default AdminStockPage;
