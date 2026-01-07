import { useEffect } from 'react'
import { Link } from 'react-router'
import { addToCart } from '../redux/cart/cartActions'
import { useDispatch } from 'react-redux'
import { useSelector } from 'react-redux'

// Home passa le props id, image, model, price, collection, product
function Product({id, image, model, price, collection, product}) {
    // Per inviare azioni Redux
    const dispatch = useDispatch()
    // Utente attualmente loggato dallo store Redux
    const currentUser = useSelector((state) => state.user.currentUser) 
    // True se l'utente loggato è admin (per nascondere Add to Cart)
    const isAdmin = currentUser?.role === "admin"
    // Legge la lista items del carrello dallo store Redux
    const cartItems = useSelector(state => state.cart.items);
    // Cerca nel carrello se il prodotto è già presente
    // Converte id a stringa per evitare mismatch numero/stringa
    // Se non esiste, quantity diventa 0
    const currentInCart = cartItems.find((i) => String(i.id) === String(id))?.quantity ?? 0;
    // maxReached = true quando la quantità nel carrello ha raggiunto lo stock massimo disponibile
    const maxReached = product ? currentInCart >= product.stock : false;
    // isOutOfStock = true quando product.available è false oppure stock = 0
    const isOutOfStock = !product.available || product.stock === 0;

    useEffect(()=>{console.log(`Product ${model} rendered!`)})

    return (
        <div className="product-card">
            {/* Card con le caratteristiche principali dei prodotti */}
            <h2>{model}</h2>
            <p><strong>Collection: {collection}</strong></p>

            {/* Se il prodotto ha Stock = 0 l'immagine sarà più trasparente con una linea diagonale rossa sopra a significare che il prodotto non è disponibile*/}
            <div className={`product-image-wrapper ${isOutOfStock ? "out-of-stock" : ""}`}>
                <img src={image} alt={model} className="product-image"/>
            </div>

            <p><strong>Price: € {price}</strong></p>

            <div>
            {/* Se sono un utente admin il button "Add To Cart" è disabilitato */}
            {/* Se sono un utente normale e il prodotto ha stock = 0 allora il button "Add To Cart" è disabilitato */}
            {!isAdmin && (
                    <button 
                    className="addToCartButton"
                    disabled={!product.available || product.stock === 0 || maxReached}
                    onClick={(e) => {e.stopPropagation();
                                    if (!maxReached) {
                                    dispatch(addToCart(product));
                                    }
                    }}>Add to Cart</button>
            )}

            {/* Button Product detail visibile sia per utenti admin che per utenti classici */}
            <Link to={`/products/${id}`}>
                <button>Product Detail</button>
            </Link>
            </div>
        </div>
    )
}

export default Product