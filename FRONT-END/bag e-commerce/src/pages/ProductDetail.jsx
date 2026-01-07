import { useEffect } from "react";
import { useState } from "react";
import { useParams } from "react-router";
import { useDispatch } from "react-redux";
import { addToCart } from "../redux/cart/cartActions";
import { useSelector } from "react-redux";
import { MoonLoader } from "react-spinners";

// Mostra i dettagli del prodotto
const ProductDetail = () => {
  // Stato locale: contiene il prodotto caricato dal server (inizialmente null)
  const [product, setProduct] = useState(null);
  // Stato locale: per mostrare il loading
  const [Loading, setIsLoading] = useState(false);
  // Stato locale: eventuale errore durante fetch
  const [error, setError] = useState(null);
  // Legge l'id dall'URL
  const { id } = useParams();
  // Per inviare azioni Redux
  const dispatch = useDispatch();
  // Legge l'utente loggato dallo store Redux
  const currentUser = useSelector((state) => state.user.currentUser);
  // True se l'utente è admin (per bloccare il button "Add to cart")
  const isAdmin = currentUser?.role === "admin";
  // Legge gli items del carrello dallo store Redux
  const cartItems = useSelector((state) => state.cart.items);
  // Trova nel carrello l'item con lo stesso id del prodotto corrente
  // Converte gli id a stringa per evitare mismatch numero/stringa
  // Se non lo trova, quantity è 0
  const currentInCart = cartItems.find((i) => String(i.id) === String(id))?.quantity ?? 0;

  // maxReached = true se la quantità nel carrello ha già raggiunto lo stock disponibile
  const maxReached = product ? currentInCart >= product.stock : false;

  // Esegue il fetch quando cambia l'id nella URL
  useEffect(() => {
    const fetchProductDetail = async () => {
      try {
        setIsLoading(true);
        // Chiamata HTTP al json-server per prendere il prodotto singolo
        const response = await fetch(`http://localhost:3001/products/${id}`);
        // Converte la risposta in JSON
        const product = await response.json();
        // Salva il prodotto nello stato triggerando un re-render
        setProduct(product);
      } catch (error) {
        setError(error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchProductDetail();
  }, [id]);

  if (Loading) return         
        <div className="loader-wrapper">
          <MoonLoader color="#36d7b7" size={100} />
        </div>;

  if (error) return <h2>{error.message}</h2>;

  // Se product è null, non renderizzo il dettaglio
  if (!product) return null;

  const desc = product.description;
  
  const isOutOfStock = !product.available || product.stock === 0;

  return (
    <div className="detail-page-layout">
      {/* COLONNA SINISTRA */}
      <div className="detail-left-page">
        <div
          className={`product-image-wrapper ${
            isOutOfStock ? "out-of-stock" : ""
          }`}
        >
          <img
            src={product.image}
            alt={product.model}
            className="product-detail-image"
          />
        </div>

        <h3>Price: €{product.price}</h3>

        <h3>Available: {product.available ? "Yes" : "No"}</h3>

        <h3>Stock: {product.stock}</h3>

        {!isAdmin && (
        <button
          disabled={isOutOfStock || maxReached}
          onClick={(e) => {
            e.stopPropagation();
            if (!maxReached && !isOutOfStock) {
              dispatch(addToCart(product));
            }
          }}
          className="addToCartButton"
        >Add to Cart</button>

        )}
      </div>

      {/* COLONNA DESTRA */}
      <div className="detail-right-page">
        <h2>{product.model}</h2>
        <h3>Description</h3>
        <ul className="detail-list">
          <li><strong>Type color:</strong> {desc?.typeColor}</li>
          <li><strong>Colors:</strong> {desc?.colors?.join(", ")}</li>
          <li><strong>Type:</strong> {desc?.type}</li>
          <li><strong>Dimension:</strong> {desc?.dimension}</li>
          <li><strong>Width x Length x Depth (cm):</strong> {desc?.dimensionCm || "N/A"}</li>
          <li><strong>Embroidery:</strong> {desc?.embroidery}</li>
          <li><strong>Yarn type:</strong> {desc?.yarnType}</li>
          <li><strong>Buckle:</strong> {desc?.buckle ? "Yes" : "No"}</li>
          <li><strong>Closure:</strong> {desc?.closure}</li>
          <li><strong>Internal lining:</strong> {desc?.lining ? "Yes" : "No"}</li>
          <li><strong>Handles:</strong></li>
            <ul className="detail-sublist">
              <li>Type: {desc?.handles?.type}</li>
              <li>Material: {desc?.handles?.material}</li>
              <li>Color: {desc?.handles?.color}</li>
              <li>Length: {desc?.handles?.length}</li>
              <li>Rings: {desc?.handles?.rings || "N/A"}</li>
            </ul>
        </ul>
      </div>
    </div>
  );
};

export default ProductDetail;
