import { useState } from "react";

const API_URL = "http://localhost:3001/products";

const initialForm = {
  year: new Date().getFullYear(),
  collection: "",
  model: "",
  description: {
    typeColor: "",
    colors: [],
    type: "",
    dimension: "",
    dimensionCm: "",
    embroidery: "",
    yarnType: "",
    buckle: false,
    closure: "",
    lining: false,
    handles: {
      type: "",
      material: "",
      color: "",
      length: "",
      rings: ""
    }
  },
  price: "",
  image: "",
  stock: 1,
  available: true
};

// Per aggiungere un nuovo prodotto al catalogo
export default function AdminNewProduct() {
  // Stato del form, inizializzato con initialForm (struttura vuota)
  const [form, setForm] = useState(initialForm);
  // Stato per gestire loading, errore e messaggio di successo
  const [status, setStatus] = useState({ loading: false, error: "", ok: "" });

  // Converte una stringa "Indigo, White" in un array ["Indigo","White"] e lo salva in description.colors
  const setColorsFromString = (value) => {
    const colors = value
        .split(",")
        .map(c => c.trim())
        .filter(Boolean);

    setForm(prev => ({
        ...prev,
        description: {
        ...prev.description,
        colors
        }
    }));
    };

  // Id incrementale leggendo max id da json-server
  const getNextProductId = async () => {
    const res = await fetch(API_URL);
    if (!res.ok) throw new Error("Unable to read /products");
    const products = await res.json();

    if (!Array.isArray(products) || products.length === 0) return "1";

    // Converto gli id in numeri
    const numericIds = products
      .map((p) => Number(p.id))
      .filter((n) => Number.isFinite(n));

    // Trovo il maxId (se non ce ne sono, 0)
    const maxId = numericIds.length ? Math.max(...numericIds) : 0;
    // Ritorno maxId+1 come stringa
    return String(maxId + 1);
  };
    // Numero di colori attesi
    const expectedColorsCount = (typeColor) => {
        switch (typeColor) {
            case "Monocolor":
            return 1;
            case "Bicolor":
            return 2;
            case "Tricolor":
            return 3;
            default:
            return null;
        }
        };

  const validate = () => {
    // Campi Obbligatori
    if (!String(form.year).trim()) return "Year is a mandatory field";
    if (!form.collection.trim()) return "Collection is a mandatory field";
    if (!form.model.trim()) return "Model is a mandatory field";
    if (!String(form.price).trim()) return "Price is a mandatory field";
    if (!form.description.type.trim()) return "Type is a mandatory field";

    // Controlli numerici
    // Anno
    const yearNum = Number(form.year);
    if (Number.isNaN(yearNum) || yearNum < 1900 || yearNum > 2100)
      return "Invalid Year";

    // Prezzo
    const priceNum = Number(form.price);
    if (Number.isNaN(priceNum) || priceNum <= 0) return "Invalid Price";

    // Stock
    const stockNum = Number(form.stock);
    if (Number.isNaN(stockNum) || stockNum < 0) return "Invalid Stock";

    // Colors

    // Coerenza con typeColor
    const colors = form.description.colors;
    const typeColor = form.description.typeColor;

    // Colors obbligatorio
    if (!Array.isArray(colors) || colors.length === 0)
        return "Insert at least one color";

    // Calcolo quanti colori sono richiesti per il typeColor selezionato
    const expected = expectedColorsCount(typeColor);

    // Se expected non è null (quindi mono/bi/tri), la lunghezza deve combaciare
    if (expected !== null && colors.length !== expected) {
        return `${typeColor} requires exactly ${expected} color(s)`;
    }

    // Closure
    // Se il tipo di chiusura è una zipper allora non posso inserire una fibbia
    if (form.description.closure === "Zipper" && form.description.buckle) {
        return "If the closure is a zipper there can't also be a buckle";
    }
    // Fine validazione 
    return "";
  };

  // Submit del form
  const handleSubmit = async (e) => {
    e.preventDefault();
    // Reset dei messaggi
    setStatus({ loading: false, error: "", ok: "" });

    // Eseguo la validazione e se c'è un errore esco
    const err = validate();
    if (err) return setStatus({ loading: false, error: err, ok: "" });

    // Loading true
    setStatus({ loading: true, error: "", ok: "" });

    try {
      // Calcolo id incrementale leggendo max id
      const nextId = await getNextProductId();

      // Preparazione Payload
      const payload = {
        id: nextId,
        year: Number(form.year),
        collection: form.collection.trim(),
        model: form.model.trim(),
        description: {
          typeColor: form.description.typeColor,
          colors: form.description.colors,
          type: form.description.type,
          dimension: form.description.dimension,
          dimensionCm: form.description.dimensionCm,
          embroidery: form.description.embroidery,
          yarnType: form.description.yarnType,
          buckle: Boolean(form.description.buckle),
          closure: form.description.closure,
          lining: Boolean(form.description.lining),
          handles: {
            type: form.description.handles.type,
            material: form.description.handles.material,
            color: form.description.handles.color,
            length: form.description.handles.length,
            rings: form.description.handles.rings
          }
        },
        price: Number(form.price),
        image: form.image.trim(),
        stock: Number(form.stock),
        available: Boolean(form.available)
      };

      // POST
      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error(`HTTP Error ${res.status}`);

      // Messaggio successo con id creato
      setStatus({
        loading: false,
        error: "",
        ok: `Product Created (id: ${nextId})`
      });
      // Reset del form ai valori iniziali
      setForm(initialForm);
    } catch (error) {
      console.error(error);
      // Aggiornamento stato con messaggio d'errore
      setStatus({
        loading: false,
        error:
          "Error saving",
        ok: ""
      });
    }
  };

  return (
    <div className = "new-product-container">
      <h2>Admin — New Product</h2>

      <form onSubmit={handleSubmit}>
        <table className = "new-product-table">
            <tbody>

            {/* BASE */}
            <tr>
                <th colSpan="2" className="new-product-titles">Base</th>
            </tr>

            <tr>
                <td>Year</td>
                <td>
                <input
                    type="number"
                    value={form.year}
                    //setForm aggiorna lo stato copiando tutto con lo Spread Operator
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, year: e.target.value}}))}
                    min={1900}
                    max={2100}
                /> ❗
                </td>
            </tr>

            <tr>
                <td>Collection</td>
                <td>
                    <select 
                    value={form.collection}
                    // React passa l’evento onChange alla funzione
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, model: e.target.value}}))}>
                    <option>Autumn - Winter</option>
                    <option>Spring - Summer</option>
                    </select>❗
                </td> 
            </tr>

            <tr>
                <td>Model</td>
                <td>
                <input
                    value={form.model}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, model: e.target.value}}))}
                    placeholder="Ex. Amelia"
                /> ❗
                </td>
            </tr>

            {/* DESCRIPTION */}
            <tr>
                <th colSpan="2" className="new-product-titles">Description</th>
            </tr>

            <tr>
                <td>Color Type</td>
                <td>
                    <select
                    value={form.description.typeColor}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, typeColor: e.target.value}}))}>
                    <option value="Monocolor">Monocolor</option>
                    <option value="Bicolor">Bicolor</option>
                    <option value="Tricolor">Tricolor</option>
                    </select>❗
                </td> 
            </tr>

            <tr>
                <td>Colors</td>
                <td>
                <input
                    value={form.description.colors.join(", ")}
                    onChange={(e) => setColorsFromString(e.target.value)}
                    placeholder="Indigo, White"
                /> ❗
                    {form.description.typeColor === "Monocolor" && "Enter 1 color"}
                    {form.description.typeColor === "Bicolor" && "Enter 2 colors"}
                    {form.description.typeColor === "Tricolor" && "Enter 3 colors"}
                </td>
            </tr>

            <tr>
                <td>Type</td>
                <td>
                <select
                    value={form.description.typeColor}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, typeColor: e.target.value}
                    }))}>
                    <option value="Quilted">Quilted</option>
                    <option value="Tote">Tote</option>
                    <option value="Half-moon">Half-moon</option>
                    </select>❗
                </td>
            </tr>

            <tr>
                <td>Dimension</td>
                <td>
                <select
                    value={form.description.dimension}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, dimension: e.target.value}
                    }))}>
                    <option value="Mini">Mini</option>
                    <option value="Medium">Medium</option>
                    <option value="Large">Large</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td>Dimension (cm)</td>
                <td>
                <input
                    value={form.description.dimensionCm}
                    onChange={(e) => setForm(prev => ({...prev, description: {...prev.description, dimensionCm: e.target.value}}))}
                    placeholder="30 x 20 x 10"
                />
                </td>
            </tr>

            <tr>
                <td>Yarn type</td>
                <td>
                    <select
                    value={form.description.yarnType}
                    onChange={(e) => setForm(prev => ({...prev, description: {...prev.description, yarnType: e.target.value}}))}>
                    <option value="Cotton">Cotton</option>
                    <option value="Fur">Fur</option>
                    <option value="Lanyard">Lanyard</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td>Closure</td>
                <td>
                    <select
                    value={form.description.closure}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, closure: e.target.value}}))}>
                    if (value === "Zipper") setDescField("buckle", false);
                    <option value="Zipper">Zipper</option>
                    <option value="Swivel">Swivel</option>
                    <option value="Swivel/Magnetic closure">Swivel/Magnetic closure</option>
                    <option value="Magnetic closure">Magnetic closure</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td>Buckle</td>
                <td>
                    <input
                        type="checkbox"
                        checked={form.description.buckle}
                        disabled={form.description.closure === "Zipper"}
                        onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, buckle: e.target.checked}}))}
                    />
                </td>
            </tr>

            <tr>
                <td>Lining</td>
                <td>
                    <input
                    type="checkbox"
                    checked={form.description.lining}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, lining: e.target.checked}}))}
                    />
                </td>
            </tr>

            {/* HANDLES */}
            <tr>
                <th colSpan="2" className="new-product-titles">Handles</th>
            </tr>

            <tr>
                <td>Handles type</td>
                <td>
                    <select
                    value={form.description.type}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, type: e.target.value}}))}>
                    <option value="Shoulder bag">Shoulder bag</option>
                    <option value="Hand bag">Hand bag</option>
                    <option value="Crossbody bag">Crossbody bag</option>
                    </select>
                </td>
                </tr>

                <tr>
                <td>Handles material</td>
                <td>
                    <select
                    value={form.description.material}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, material: e.target.value}}))}>
                    <option value="Chain">Chain</option>
                    <option value="Cotton">Cotton</option>
                    <option value="Cotton & Chain">Cotton & Chain</option>
                    </select>
                </td>
                </tr>

                <tr>
                <td>Handles color</td>
                <td>
                    <input
                    value={form.description.handles.color}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, color: e.target.value}}))}
                    placeholder="Ex. Silver / Black & Silver"
                    />
                </td>
                </tr>

                <tr>
                <td>Handles length</td>
                <td>
                    <select
                    value={form.description.length}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, length: e.target.value}}))}>
                    <option value="Long">Long</option>
                    <option value="Medium">Medium</option>
                    <option value="Small">Small</option>
                    </select>
                </td>
                </tr>

                <tr>
                <td>Handles rings</td>
                <td>
                    <select
                    value={form.description.rings}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, rings: e.target.value}}))}>
                    <option value="Large">Large</option>
                    <option value="Medium">Medium</option>
                    <option value="Small">Small</option>
                    </select>
                </td>
                </tr>


            {/* COMMERCE */}
            <tr>
                <th colSpan="2" className="new-product-titles">Commerce</th>
            </tr>

            <tr>
                <td>Price</td>
                <td>
                    <input
                        value={form.price}
                        onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, rings: e.target.value}}))}
                        placeholder="Ex. 70"
                    /> ❗
                </td>
            </tr>

            <tr>
                <td>Stock</td>
                <td>
                    <input
                        type="number"
                        value={form.stock}
                        onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, stock: e.target.value}}))}
                    /> ❗
                </td>
            </tr>

            <tr>
                <td>Available</td>
                <td>
                <input
                    type="checkbox"
                    checked={form.available}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, available: e.target.checked}}))}
                />❗
                </td>
            </tr>

            <tr>
                <td>Image (URL)</td>
                <td>
                    <input
                    value={form.image}
                    onChange={(e) =>setForm(prev => ({...prev, description: {...prev.description, image: e.target.checked}}))}
                    placeholder="https://raw.githubusercontent.com/..."
                    className="new-product-URL"
                    />
                </td>
            </tr>


            {/* SUBMIT BUTTON */}
            <tr>
                <td colSpan="2" className="new-product-submit-button">
                    <button
                    type="submit"
                    disabled={status.loading}
                    className="new-product-submit-button"
                    >
                    {status.loading ? "Saving..." : "Create Product"}
                    </button>
                </td>
            </tr>

            </tbody>
        </table>
        </form>

    {/*ERROR AND SUCCESS MESSAGES*/}
    {status.error && (
        <p className="error">{status.error}</p>
    )}

    {status.ok && (
        <div className="create-success">{status.ok}</div>
    )}
    </div>
  );
}