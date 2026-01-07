// Leggo l'utente salvato nel localStorage se c'è
let storedUser = null;
try {
  // Recupero la stringa associata alla chiave "currentUser"
  const raw = localStorage.getItem("currentUser");

  // Se esiste, la converte da JSON a oggetto JavaScript
  // Altrimenti assegna null
  storedUser = raw ? JSON.parse(raw) : null;

} catch {
  storedUser = null;
}

// Stato iniziale del reducer users
const initialState = {
  // Se ho qualcosa nel localStorage parto già loggato
  currentUser: storedUser,
  error: null,
};

// Reducer Redux che riceve lo stato corrente e un'azione per la gestione dell'autenticazione utente
const usersReducer = (state = initialState, action) => {
  // Switch sul tipo di azione dispatchata
  switch (action.type) {
    case "LOGIN_SUCCESS": {
      // Salvo anche nel localStorage
      localStorage.setItem("currentUser", JSON.stringify(action.payload));
      return {
        ...state,
        currentUser: action.payload,
        error: null,
      };
    }

    case "LOGIN_FAILURE": {
      // Pulisco eventuale utente salvato
      localStorage.removeItem("currentUser");
      return {
        ...state,
        currentUser: null,
        error: "Invalid credentials",
      };
    }

    case "LOGOUT": {
      // Pulisco storage e stato
      localStorage.removeItem("currentUser");
      return {
        ...state,
        currentUser: null,
        error: null,
      };
    }

    default:
      return state;
  }
};

export default usersReducer;
