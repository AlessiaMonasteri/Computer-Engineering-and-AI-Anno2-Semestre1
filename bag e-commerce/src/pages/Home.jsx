import { useState, useEffect } from "react";
import Product from "../components/Product";
import { MoonLoader } from "react-spinners";
import ProductsFilters from "../components/ProductsFilters";
import { useSearchParams } from "react-router-dom";

// Pagina principale dei prodotti
const Home = () => {
  // Stato che serve a mostrare la lista prodotti (inizialmente vuota)
  const [products, setProducts] = useState([]);
  // Stato che serve a mostrare lo spinner (inizialmente false)
  const [isLoading, setIsLoading] = useState(false);
  // Stato per mostrare un eventuale messaggio di errore
  const [error, setError] = useState(null);
  // Hook per aggiornare i parametri dell'URL
  const [searchParams, setSearchParams] = useSearchParams();

  // URL params
  const limit = searchParams.get("limit") || "";
  const search = searchParams.get("search") || "";

  // Filtri avanzati
  const [filters, setFilters] = useState({
    collection: "",
    typeColor: "",
    bagType: "",
    dimension: "",
    handlesType: "",
    maxPrice: "",
  });

  const fetchProducts = async () => {
    try {
      setIsLoading(true);
      setError(null);

      // Per vedere il loader
      await new Promise((res) => setTimeout(res, 500));

      // Fetch di tutti i prodotti (limit applicato alla fine)
      const res = await fetch("http://localhost:3001/products");
      if (!res.ok) throw new Error("Errore durante il caricamento prodotti");

      let items = await res.json();

      // Filtro per SEARCH (model)
      // Se l'utente ha scritto qualcosa nella search la trimmo,
      if (search.trim()) {
        // la trasformo in lowercase
        const q = search.toLowerCase();
        // e tengo i prodotti per i quali ciò che ha inserito l'utente è contenuto nel model
        items = items.filter((p) => p.model?.toLowerCase().includes(q));
      }

      // Filtro per collection
      if (filters.collection) {
        items = items.filter((p) => p.collection === filters.collection);
      }

      // Filtro per tipo di colorazione
      if (filters.typeColor) {
        items = items.filter((p) => p.description?.typeColor === filters.typeColor
        );
      }

      // Filtro per tipo di borsa
      if (filters.bagType) {
        items = items.filter((p) => p.description?.type === filters.bagType);
      }

      // Filtro per dimensione
      if (filters.dimension) {
        items = items.filter((p) => p.description?.dimension === filters.dimension
        );
      }

      // Filtro per tipo di manico
      if (filters.handlesType) {
        items = items.filter((p) => p.description?.handles?.type === filters.handlesType
        );
      }

      //Filtro per prezzo minore o uguale
      if (filters.maxPrice) {
        const max = Number(filters.maxPrice);
        if (!Number.isNaN(max)) {
          items = items.filter((p) => p.price <= max);
        }
      }

      // Limite nel numero di articoli da visualizzare
      if (limit) {
        const n = Number(limit);
        if (!Number.isNaN(n) && n > 0) {
          items = items.slice(0, n);
        }
      }

      // Salvo i prodotti filtrati nello state che triggera un re-render
      setProducts(items);
    } catch (err) {
      console.error(err);
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  // Rieseguo la fetch quando cambia limit, search o uno dei filtri avanzati
  useEffect(() => {
    fetchProducts();
  }, [limit,
    search,
    filters.collection,
    filters.typeColor,
    filters.bagType,
    filters.dimension,
    filters.handlesType,
    filters.maxPrice,
  ]);

return (
  <div className="page-layout">

    {/* Filtri della sidebar */}
    <div className="sidebar">
      <ProductsFilters
        limit={limit}
        search={search}
        setSearchParams={setSearchParams}
        filters={filters}
        setFilters={setFilters}
      />
    </div>

    {/* Area prodotti */}
    <div className="products-area">
      {isLoading && (
        <div className="loader-wrapper">
          <MoonLoader color="#36d7b7" size={100} />
        </div>
      )}

      {error && <div className="error">Error: {error}</div>}

      {/* Se ci sono prodotti l'array products viene iterato così che per ogni elemento dell'array venga renderizzato un componente Product*/}
      {products.length > 0 ? (
        products.map((product) => (
          <Product key={product.id} {...product} product={product}/>
        ))
      ) : (
        !isLoading && <div className="info">No Products</div>
      )}
    </div>
  </div>
);

};

export default Home;
