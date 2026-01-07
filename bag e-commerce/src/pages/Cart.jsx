import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { increaseQuantity, decreaseQuantity, removeFromCart, clearCart } from "../redux/cart/cartActions";
import { submitOrder } from "../redux/orders/orderActions";

const BASE_URL = "http://localhost:3001";

const Cart = () => {
  // Lettura degli stati Redux
  const cartItems = useSelector((state) => state.cart.items);
  const total = useSelector((state) => state.cart.total);
  const currentUser = useSelector((state) => state.user.currentUser);
  const dispatch = useDispatch();
  // Stato locale per mostrare il messaggio di Order Submitted
  const [orderSuccess, setOrderSuccess] = useState(false);

  const handleSubmit = async () => {
    // Controllo Login
    if (!currentUser) {
      alert("You must be logged in.");
      return;
    }

    // Se il carrello è vuoto avvisa l'utente che non è possibile submittare l'ordine 
    if (cartItems.length === 0) {
      alert("Your cart is empty.\nAdd something to your cart to place your order.");
      return;
    }

    // Creazione dell'ordine
    const orderData = {
      id: Date.now(),
      date: new Date().toISOString(),
      items: cartItems.map((item) => ({
        id: item.id,
        model: item.model,
        price: item.price,
        qty: item.quantity,
      })),
      total: cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0),
      userId: currentUser.id,
      userEmail: currentUser.email,
    };

    // Invio dell'ordine
    await dispatch(submitOrder(orderData));

    // Aggiorna lo stock e l'attributo available su db.json per ogni prodotto
    for (const item of cartItems) {
      try {
        // Leggo il prodotto da db.json
        const resGet = await fetch(`${BASE_URL}/products/${item.id}`);
        if (!resGet.ok) {
          console.error("GET product failed:", resGet.status);
          continue;
        }
        
        // Calcolo del nuovo stock
        const productData = await resGet.json();
        const newStock = Math.max((productData.stock ?? 0) - item.quantity, 0);
        // Se c'è almeno 1 pezzo è Available
        const newAvailable = newStock > 0;

        // Aggiornamento dei campi stock e available
        const resPatch = await fetch(`${BASE_URL}/products/${item.id}`, {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            stock: newStock,
            available: newAvailable,
          }),
        });

        if (!resPatch.ok) {
          console.error("PATCH product failed:", resPatch.status);
        }
      } catch (err) {
        console.error("Error updating product", item.id, err);
      }
    }

    // Svuota il carrello
    dispatch(clearCart());

    // Mostra il messaggio per 5 secondi
    setOrderSuccess(true);
    setTimeout(() => {
      setOrderSuccess(false);
    }, 5000);
  };

  return (
<div className="cart-page">
  <div className="cart-container">
    <h2>Your Cart</h2>

    {orderSuccess && (
      <p className="order-success">Order Submitted!</p>
    )}

    {cartItems.length === 0 ? (
      orderSuccess ? null : <p><strong>Your cart is empty!</strong></p>
    ) : (
      <ul className="cart-list">
        {cartItems.map((item) => (
          <li key={item.id} className="cart-item">
            <img src={item.image} alt={item.title} />

            <div>
              <strong>{item.title}</strong>
              <div>Price: €{item.price}</div>
              <div><strong>Quantity: {item.quantity}</strong></div>
              <button disabled={item.stock !== undefined && item.quantity >= item.stock} onClick={() => dispatch(increaseQuantity(item.id))}>+ 1</button>
              <button onClick={() => dispatch(decreaseQuantity(item.id))}> - 1</button>
              <button onClick={() => dispatch(removeFromCart(item.id))}>🗑️</button>
            </div>

          </li>
        ))}
      </ul>
    )}

    <strong>Cart Total: €{total}</strong>

    <div className="cart-actions">
      <button onClick={handleSubmit}>Submit Order</button>
      <button onClick={() => dispatch(clearCart())}>Clear Cart</button>
    </div>

  </div>
</div>
  );
};

export default Cart;
