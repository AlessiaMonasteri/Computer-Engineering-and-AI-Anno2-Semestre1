const initialState = {items: [], 
                      total: 0};
// Reducer Redux: riceve lo stato corrente e un'azione, e ritorna il nuovo stato
export const cartReducer = (state = initialState, action) => {
  // switch per gestire i vari tipi di azioni sul carrello
  switch (action.type) {
    case "cart/addToCart": {
      // Verifico se il prodotto è già nel carrello (normalizzo id a stringa)
      const payloadId = String(action.payload.id);

      // Trova l'indice dell'elemento nel carrello con lo stesso id
      // findIndex restituisce indice >= 0 se trovato o -1 se non trovato
      const index = state.items.findIndex(
        (item) => String(item.id) === payloadId
      );

      // Array che conterrà la nuova lista items aggiornata
      let updatedItems = [];

      // Se l'indice è >=0 il prodotto è già nel carrello
      if (index >= 0) {
        // Se il prodotto esiste incremento la quantità SOLO del prodotto con id = payloadId
        updatedItems = state.items.map((item) =>
          String(item.id) === payloadId
            ? { ...item, quantity: item.quantity + 1 }
            // Gli altri items rimangono uguali
            : item
        );
      } else {
        // Se non esiste lo aggiungo con quantità 1
        updatedItems = [
          ...state.items,
          { ...action.payload, id: payloadId, quantity: 1 },
        ];
      }

      // Ricalcolo il totale del carrello sommando price x quantity per ogni item
      const updatedTotal = updatedItems.reduce(
        (total, item) => total + item.price * item.quantity,
        0
      );

      // Ritorna un nuovo stato con items e total aggiornati
      return { ...state, items: updatedItems, total: updatedTotal };
    }

    // Rimozione di un prodotto dal carrello
    case "cart/removeFromCart": {
      // Normalizzo l'id ricevuto come payload
      const id = String(action.payload);
      // Filtro gli items tenendo solo quelli con id diverso da quello da rimuovere
      const filteredItems = state.items.filter(
        (item) => String(item.id) !== id
      );

      // Ricalcolo il totale in base ai prodotti rimasti
      const updatedTotal = filteredItems.reduce(
        (total, item) => total + item.price * item.quantity,
        0
      );
      // Ritorna un nuovo stato con items e total aggiornati
      return { ...state, items: filteredItems, total: updatedTotal };
    }

    // Aumenta la quantità di un prodotto già presente nel carrello
    case "cart/increaseQuantity": {
      // Normalizzo l'id
      const id = String(action.payload);

      // Mappo l'array e incremento il quantity solo dell'item con id corrispondente
      const updatedItems = state.items.map((item) =>
        String(item.id) === id
          ? { ...item, quantity: item.quantity + 1 }
          : item
      );

      // Ricalcolo il totale
      const updatedTotal = updatedItems.reduce(
        (total, item) => total + item.price * item.quantity,
        0
      );
      // Ritorno un nuovo stato con items e total aggiornati
      return { ...state, items: updatedItems, total: updatedTotal };
    }
    // Diminuisce la quantità di un prodotto
    case "cart/decreaseQuantity": {
      // Normalizzo l'id
      const id = String(action.payload);

      // Decremento la quantity per l'item corretto e filtro gli item che sono arrivati a quantity <= 0
      const updatedItems = state.items
        .map((item) =>
          String(item.id) === id
            ? { ...item, quantity: item.quantity - 1 }
            : item
        )
        // Rimuovo quelli arrivati a 0 o negativi
        .filter((item) => item.quantity > 0);

      // Ricalcolo il totale
      const updatedTotal = updatedItems.reduce(
        (total, item) => total + item.price * item.quantity,
        0
      );

      // Ritorno un nuovo stato con items e total aggiornati
      return { ...state, items: updatedItems, total: updatedTotal };
    }

    // Svuota il carrello ritornando lo stato a ome era in origine
    case "cart/clearCart": {
      return { ...state, items: [], total: 0 };
    }

    default:
      return state;
  }
};

export default cartReducer;
