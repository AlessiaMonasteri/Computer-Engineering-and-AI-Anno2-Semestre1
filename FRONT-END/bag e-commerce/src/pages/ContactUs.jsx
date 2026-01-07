import { useState, useEffect } from "react";
import { useSelector } from "react-redux";

const ContactUs = () => {
  // Utente loggato
  const currentUser = useSelector((state) => state.user.currentUser);
  // Stati locali
  const [message, setMessage] = useState("");
  const [status, setStatus] = useState("");
  const [messages, setMessages] = useState([]);

  // Solo per admin: recupera messaggi
  useEffect(() => {
    if (currentUser?.role === "admin") {
      fetch("http://localhost:3001/contacts")
        .then((res) => res.json())
        .then((data) => setMessages(data))
        .catch((err) => console.error(err));
    }
  }, [currentUser]);

  //Blocca il refresh del form
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Invia una POST a /contacts
    try {
      const res = await fetch("http://localhost:3001/contacts", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          // Dati dell'utente loggato
          userId: currentUser.id,
          userName: currentUser.name,
          userEmail: currentUser.email,
          message,
          createdAt: new Date().toISOString(),
        }),
      });

      if (res.ok) {
        setStatus("Message sent successfully!");
        setMessage("");

        setTimeout(() => {
          setStatus("");
        }, 5000);
      } else {
        setStatus("Error sending");
      }
      } catch (err) { setStatus("Network error: " + err.message); }
  }

  // Rendering Condizionale
  // Se l'utente è admin, mostra la lista di tutti i messaggi
  if (currentUser?.role === "admin") {
    return (
      <div>
        <h2>Messages received</h2>
        {messages.length === 0 ? (
          <p className="info">No message received.</p>
        ) : (
          <ul>
            {messages.map((msg) => (
              <li
                key={msg.id}
                className = "message">
                <strong>{msg.userName || msg.name}</strong>{" "}({msg.userEmail || msg.email})<br />
                <small>{msg.createdAt ? new Date(msg.createdAt).toLocaleString() : ""}</small><br />
                {msg.message}
              </li>
            ))}
          </ul>
        )}
      </div>
    );
  }

  //Utente normale loggato: mostra solo il form per inserire il messaggio
  return (
    <div className="contact-container">
      <h2>Contact Us</h2>

      <p className="labelMessage">Sending as <strong>{currentUser.name}</strong> ({currentUser.email})</p>

      <form onSubmit={handleSubmit} className="contact-form">
        <textarea
          placeholder="Message"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          required
          className="textarea"
        />

        <button type="submit" className="button">Send</button>
      </form>

     {status && <p className="message-sent">{status}</p>}

    </div>
  );

};

export default ContactUs;
