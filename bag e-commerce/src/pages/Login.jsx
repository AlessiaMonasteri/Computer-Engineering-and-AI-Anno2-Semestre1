import { useState } from "react";
import { useDispatch } from "react-redux";
import { login } from "../redux/users/usersAction";

//Pagina di Login
const Login = () => {
  // Stato locale per l'email inserita dall'utente (inizialmente stringa vuota)
  const [email, setEmail] = useState("");
  // Stato locale per la password inserita dall'utente (inizialmente stringa vuota)
  const [password, setPassword] = useState("");
  // Per inviare azioni Redux
  const dispatch = useDispatch();

  // Submit del form di Login
  const handleSubmit = (e) => {e.preventDefault();
    // Controlla che email e password non siano vuote
    if (email && password) {
      // Invia l'azione login allo store Redux con email e password come parametri
      dispatch(login(email, password));
    }
  };

  return (
    <div
      className="login-page">
      <div className="login-card">
        <h2>E-COMMERCE BAG LOGIN</h2>
        <form onSubmit={handleSubmit}>
          <table>
            <tbody>
              <tr>
                <td className="login-label">
                  <label>Email</label>
                </td>
                <td>
                  <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} className = "login-input"/>
                </td>
              </tr>
              <tr>
                <td className="login-label">
                  <label>Password</label>
                </td>
                <td>
                  <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} className = "login-input"/>
                </td>
              </tr>
              <tr>
                <td colSpan="2" style={{ textAlign: "center", paddingTop: "1rem" }}>
                  <button type="submit" className="button">LOGIN</button>
                </td>
              </tr>
            </tbody>
          </table>
        </form>
      </div>
    </div>
  );
};

export default Login;
