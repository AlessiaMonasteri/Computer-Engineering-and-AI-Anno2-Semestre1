// Stato iniziale del reducer orders
const initialState = {
  list: []
};

// Reducer che riceve lo stato corrente e un'azione
export const orderReducer = (state = initialState, action) => {
  switch (action.type) {

    // Aggiunta di un ordine
    case "orders/addOrder":
      return {...state, list: [...state.list, action.payload]};

    // Se l'azione non è riconosciuta, ritorna lo stato invariato
    default:
      return state;
  }
};

export default orderReducer;
