import { useState } from "react";

const API_URL = "http://localhost:3001/products";

// Liste con scelte definite
const COLLECTIONS = ["Autumn - Winter", "Spring - Summer"];

const TYPE_COLORS = ["Monocolor", "Bicolor", "Tricolor", "Multicolor"];

const BAG_TYPES = ["Quilted", "Tote", "Half-moon"];

const DIMENSIONS = ["Mini", "Medium", "Large"];

const YARN_TYPES = ["Cotton", "Fur", "Lanyard"];

const CLOSURES = ["Zipper", "Swivel/Magnetic closure", "Swivel", "Magnetic closure"];

const HANDLE_TYPES = ["Shoulder bag", "Hand bag", "Crossbody bag"];

const HANDLE_MATERIALS = ["Chain", "Cotton", "Cotton & Chain"];

const HANDLE_LENGTHS = ["Long", "Medium", "Small"];

const HANDLE_RINGS = ["Small", "Medium", "Large"];

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

export default function AdminNewProduct() {
  const [form, setForm] = useState(initialForm);
  const [status, setStatus] = useState({ loading: false, error: "", ok: "" });

  const setField = (name, value) => setForm((p) => ({ ...p, [name]: value }));

  const setDescField = (name, value) =>
    setForm((p) => ({
      ...p,
      description: { ...p.description, [name]: value }
    }));

  const setHandlesField = (name, value) =>
    setForm((p) => ({
      ...p,
      description: {
        ...p.description,
        handles: { ...p.description.handles, [name]: value }
      }
    }));


  const setColorsFromString = (value) => {
    const colors = value
      .split(",")
      .map((c) => c.trim())
      .filter(Boolean);

    setDescField("colors", colors);
  };

  // Id incrementale leggendo max id da json-server
  const getNextProductId = async () => {
    const res = await fetch(API_URL);
    if (!res.ok) throw new Error("Impossibile leggere /products");
    const products = await res.json();

    if (!Array.isArray(products) || products.length === 0) return "1";

    const numericIds = products
      .map((p) => Number(p.id))
      .filter((n) => Number.isFinite(n));

    const maxId = numericIds.length ? Math.max(...numericIds) : 0;
    return String(maxId + 1);
  };

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
    const yearNum = Number(form.year);
    if (Number.isNaN(yearNum) || yearNum < 1900 || yearNum > 2100)
      return "Invalid Year";

    const priceNum = Number(form.price);
    if (Number.isNaN(priceNum) || priceNum <= 0) return "Invalid Price";

    const stockNum = Number(form.stock);
    if (Number.isNaN(stockNum) || stockNum < 0) return "Invalid Stock";

    // Colori obbligatori e coerenza con typeColor
    const colors = form.description.colors;
    const typeColor = form.description.typeColor;

    if (!Array.isArray(colors) || colors.length === 0)
        return "Insert at least one color";

    const expected = expectedColorsCount(typeColor);

    if (expected !== null && colors.length !== expected) {
        return `${typeColor} requires exactly ${expected} color(s)`;
    }
    // Se il tipo di chiusura è una zipper allora non posso inserire una fibbia
    if (form.description.closure === "Zipper" && form.description.buckle) {
        return "If the closure is a zipper there can't also be a buckle";
    }

    return "";
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus({ loading: false, error: "", ok: "" });

    const err = validate();
    if (err) return setStatus({ loading: false, error: err, ok: "" });

    setStatus({ loading: true, error: "", ok: "" });

    try {
      const nextId = await getNextProductId();

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

      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error(`Errore HTTP ${res.status}`);

      setStatus({
        loading: false,
        error: "",
        ok: `Product Created (id: ${nextId})`
      });
      setForm(initialForm);
    } catch (error) {
      console.error(error);
      setStatus({
        loading: false,
        error:
          "Error saving",
        ok: ""
      });
    }
  };

  return (
    <div style={{ maxWidth: 900, margin: "0 auto", padding: "1.5rem" }}>
      <h2>Admin — New Product</h2>

      <form onSubmit={handleSubmit}>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <tbody>

            {/*BASE*/}
            <tr>
                <th colSpan="2" style={{ textAlign: "left", paddingTop: 16 }}>
                Base
                </th>
            </tr>

            <tr>
                <td>Year</td>
                <td>
                <input
                    type="number"
                    value={form.year}
                    onChange={(e) => setField("year", e.target.value)}
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
                    onChange={(e) => setField("collection", e.target.value)}
                >
                    <option value="">-- Select --</option>
                    {COLLECTIONS.map((c) => (
                    <option key={c} value={c}>{c}</option>
                    ))}
                </select>❗
                </td> 
            </tr>

            <tr>
                <td>Model</td>
                <td>
                <input
                    value={form.model}
                    onChange={(e) => setField("model", e.target.value)}
                    placeholder="Ex. Amelia"
                /> ❗
                </td>
            </tr>

            {/*DESCRIPTION*/}
            <tr>
                <th colSpan="2" style={{ textAlign: "left", paddingTop: 24 }}>
                Description
                </th>
            </tr>

            <tr>
                <td>Color Type</td>
                <td>
                <select
                    value={form.description.typeColor}
                    onChange={(e) => setDescField("typeColor", e.target.value)}
                >
                    <option value="">-- Select --</option>
                    {TYPE_COLORS.map((v) => (
                    <option key={v} value={v}>{v}</option>
                    ))}
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
                <div style={{ fontSize: 12, opacity: 0.7 }}>
                    {form.description.typeColor === "Monocolor" && "Enter 1 color"}
                    {form.description.typeColor === "Bicolor" && "Enter 2 colors"}
                    {form.description.typeColor === "Tricolor" && "Enter 3 colors"}
                </div>
                </td>
            </tr>

            <tr>
                <td>Type</td>
                <td>
                <select
                    value={form.description.type}
                    onChange={(e) => setDescField("type", e.target.value)}
                >
                    <option value="">-- Select --</option>
                    {BAG_TYPES.map((v) => (
                    <option key={v} value={v}>{v}</option>
                    ))}
                </select>❗
                </td>
            </tr>

            <tr>
                <td>Dimension</td>
                <td>
                <select
                    value={form.description.dimension}
                    onChange={(e) => setDescField("dimension", e.target.value)}
                >
                    <option value="">-- Select --</option>
                    {DIMENSIONS.map((v) => (
                    <option key={v} value={v}>{v}</option>
                    ))}
                </select>
                </td>
            </tr>

            <tr>
                <td>Dimension (cm)</td>
                <td>
                <input
                    value={form.description.dimensionCm}
                    onChange={(e) => setDescField("dimensionCm", e.target.value)}
                    placeholder="30 x 20 x 10"
                />
                </td>
            </tr>

            <tr>
                <td>Yarn type</td>
                <td>
                <select
                    value={form.description.yarnType}
                    onChange={(e) => setDescField("yarnType", e.target.value)}
                >
                    <option value="">-- Select --</option>
                    {YARN_TYPES.map((v) => (
                    <option key={v} value={v}>{v}</option>
                    ))}
                </select>
                </td>
            </tr>

            <tr>
                <td>Closure</td>
                <td>
                <select
                    value={form.description.closure}
                    onChange={(e) => {
                    const value = e.target.value;
                    setDescField("closure", value);
                    if (value === "Zipper") setDescField("buckle", false);
                    }}
                >
                    <option value="">-- Select --</option>
                    {CLOSURES.map((v) => (
                    <option key={v} value={v}>{v}</option>
                    ))}
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
                    onChange={(e) => setDescField("buckle", e.target.checked)}
                />
                </td>
            </tr>

            <tr>
                <td>Lining</td>
                <td>
                    <input
                    type="checkbox"
                    checked={form.description.lining}
                    onChange={(e) => setDescField("lining", e.target.checked)}
                    />
                </td>
            </tr>

            {/*HANDLES*/}
            <tr>
                <th colSpan="2" style={{ textAlign: "left", paddingTop: 24 }}>Handles</th>
            </tr>

            <tr>
                <td>Handles type</td>
                <td>
                    <select
                    value={form.description.handles.type}
                    onChange={(e) => setHandlesField("type", e.target.value)}
                    >
                    <option value="">-- Select --</option>
                    {HANDLE_TYPES.map((v) => (
                        <option key={v} value={v}>{v}</option>
                    ))}
                    </select>
                </td>
                </tr>

                <tr>
                <td>Handles material</td>
                <td>
                    <select
                    value={form.description.handles.material}
                    onChange={(e) => setHandlesField("material", e.target.value)}
                    >
                    <option value="">-- Select --</option>
                    {HANDLE_MATERIALS.map((v) => (
                        <option key={v} value={v}>{v}</option>
                    ))}
                    </select>
                </td>
                </tr>

                <tr>
                <td>Handles color</td>
                <td>
                    <input
                    value={form.description.handles.color}
                    onChange={(e) => setHandlesField("color", e.target.value)}
                    placeholder="Ex. Gold / Silver / Black & Silver"
                    />
                </td>
                </tr>

                <tr>
                <td>Handles length</td>
                <td>
                    <select
                    value={form.description.handles.length}
                    onChange={(e) => setHandlesField("length", e.target.value)}
                    >
                    <option value="">-- Select --</option>
                    {HANDLE_LENGTHS.map((v) => (
                        <option key={v} value={v}>{v}</option>
                    ))}
                    </select>
                </td>
                </tr>

                <tr>
                <td>Handles rings</td>
                <td>
                    <select
                    value={form.description.handles.rings}
                    onChange={(e) => setHandlesField("rings", e.target.value)}
                    >
                    <option value="">-- Select --</option>
                    {HANDLE_RINGS.map((v) => (
                        <option key={v} value={v}>{v}</option>
                    ))}
                    </select>
                </td>
                </tr>


            {/* ================= COMMERCE ================= */}
            <tr>
                <th colSpan="2" style={{ textAlign: "left", paddingTop: 24 }}>
                Commerce
                </th>
            </tr>

            <tr>
                <td>Price</td>
                <td>
                <input
                    value={form.price}
                    onChange={(e) => setField("price", e.target.value)}
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
                    onChange={(e) => setField("stock", e.target.value)}
                /> ❗
                </td>
            </tr>

            <tr>
                <td>Available</td>
                <td>
                <input
                    type="checkbox"
                    checked={form.available}
                    onChange={(e) => setField("available", e.target.checked)}
                /> ❗
                </td>
            </tr>

            <tr>
                <td>Image (URL)</td>
                <td>
                    <input
                    value={form.image}
                    onChange={(e) => setField("image", e.target.value)}
                    placeholder="https://raw.githubusercontent.com/..."
                    style={{ width: "100%" }}
                    />
                </td>
            </tr>


            {/*SUBMIT BUTTON*/}
            <tr>
                <td colSpan="2" style={{ paddingTop: 16 }}>
                    <button
                    type="submit"
                    disabled={status.loading}
                    style={{ width: "100%" }}
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