// VITE_USERS_JSON contiene una stringa JSON con gli utenti di test
import.meta.env.VITE_USERS_JSON

// Riceve email e password inserite dall'utente
export const login = (email, password) => {
  // Ritorna una funzione invece di un oggetto (redux-thunk)
  return (dispatch) => {
    // Legge la variabile d'ambiente VITE_USERS_JSON; se non esiste, usa "[]"
    const USERS = JSON.parse(import.meta.env.VITE_USERS_JSON || "[]");
    // Cerca un utente che abbia email e password corrispondenti
    const user = USERS.find(
      (u) => u.email === email && u.password === password
    );

    if (user) {
      dispatch({
        type: "LOGIN_SUCCESS",
        payload: {
          id: user.id,
          email: user.email,
          name: user.name,
          role: user.role,
        },
      });
    } else {
      alert("Invalid credentials");
      dispatch({ type: "LOGIN_FAILURE" });
    }
  }
}