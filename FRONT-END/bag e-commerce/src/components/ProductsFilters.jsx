import { useState, useEffect } from "react";

// Props del componente ProductsFilters
const ProductsFilters = ({limit, search, setSearchParams, filters, setFilters,}) => {

  // Stato locale per il filtro Search
  const [searchText, setSearchText] = useState(search);
  
  // Sincronizzazione con l'hook useEffect in modo che l'effetto si attivi quando cambia search e che aggiorni searchText
  useEffect(() => {setSearchText(search);}, [search]);

  // Funzione per modificare i query params dell'URL (usata per search e limit)
  const updateParamLimitOrSearch = (key, value) => {setSearchParams((prev) => {const params = new URLSearchParams(prev);
                                                                              // Se value è vuoto rimuove il parametro
                                                                              if (!value) params.delete(key);
                                                                              // Altrimenti lo aggiorna
                                                                              else params.set(key, value);
                                                                              return params;
                                                                            });
  };

  // Quando viene premuto il button SEARCH blocca il refresh della pagina e aggiorna il parametro search nell’URL
  const handleSearchSubmit = (e) => {e.preventDefault(); updateParamLimitOrSearch("search", searchText.trim())};

  // Quando cambia la select aggiorna il parametro limit nell’URL
  const handleLimitChange = (e) => {const value = e.target.value; updateParamLimitOrSearch("limit", value)};

  // Aggiorna un singolo filtro: se l’utente sceglie "ALL" salva la stringa vuota e mantiene gli altri filtri invariati (...prev)
  const handleFilterChange = (key, value) => {setFilters((prev) => ({...prev, [key]: value === "ALL" ? "" : value}));};

  return (
    // FORM DEI FILTRI
    <form className="filters-container" onSubmit={handleSearchSubmit}>

      {/* Search */}
      <div className="filter-item">
        <label>Search:</label>
        <input
          type="text"
          placeholder="Search Products"
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
        />
              <button type="submit">SEARCH</button>
      </div>

      {/* Products per page */}
      <div className="filter-item">
        <label>Products per page:</label>
        <select value={limit || ""} onChange={handleLimitChange}>
          <option value="">All</option>
          <option value="1">1</option>
          <option value="5">5</option>
          <option value="10">10</option>
          <option value="15">15</option>
          <option value="20">20</option>
        </select>
      </div>

      {/* Collection */}
      <div className="filter-item">
        <label>Collection:</label>
        <select
          value={filters.collection || "ALL"}
          onChange={(e) => handleFilterChange("collection", e.target.value)}
        >
          <option value="ALL">All</option>
          <option value="Autumn - Winter">Autumn - Winter</option>
          <option value="Spring - Summer">Spring - Summer</option>
        </select>
      </div>

      {/* Color type */}
      <div className="filter-item">
        <label>Color type:</label>
        <select
          value={filters.typeColor || "ALL"}
          onChange={(e) => handleFilterChange("typeColor", e.target.value)}
        >
          <option value="ALL">All</option>
          <option value="Monocolor">Monocolor</option>
          <option value="Bicolor">Bicolor</option>
          <option value="Tricolor">Tricolor</option>
        </select>
      </div>

      {/* Bag type */}
      <div className="filter-item">
        <label>Type:</label>
        <select
          value={filters.bagType || "ALL"}
          onChange={(e) => handleFilterChange("bagType", e.target.value)}
        >
          <option value="ALL">All</option>
          <option value="Quilted">Quilted</option>
          <option value="Half-moon">Half-moon</option>
          <option value="Tote">Tote</option>
        </select>
      </div>

      {/* Dimension */}
      <div className="filter-item">
        <label>Dimension:</label>
        <select
          value={filters.dimension || "ALL"}
          onChange={(e) => handleFilterChange("dimension", e.target.value)}
        >
          <option value="ALL">All</option>
          <option value="Mini">Mini</option>
          <option value="Medium">Medium</option>
          <option value="Large">Large</option>
        </select>
      </div>

      {/* Handles type */}
      <div className="filter-item">
        <label>Handles type:</label>
        <select
          value={filters.handlesType || "ALL"}
          onChange={(e) => handleFilterChange("handlesType", e.target.value)}
        >
          <option value="ALL">All</option>
          <option value="Shoulder bag">Shoulder bag</option>
          <option value="Crossbody bag">Crossbody bag</option>
          <option value="Hand bag">Hand bag</option>
        </select>
      </div>

      {/* Price */}
      <div className="filter-item">
        <label>Price:</label>
        <select
          value={filters.maxPrice || "ALL"}
          onChange={(e) => handleFilterChange("maxPrice", e.target.value)}
        >
          <option value="ALL">All</option>
          <option value="50">≤ 50 €</option>
          <option value="70">≤ 70 €</option>
          <option value="90">≤ 90 €</option>
        </select>
      </div>
    </form>
  );
};

export default ProductsFilters;
