AM BagECommerce — Spring Boot API

Il Backend Spring Boot creato per l'esame di Back-End Programming è un e-commerce dedicato a borse e accessori
che segue una logica simile al Front-End sviluppato in React per l'esame di Front-End Programming.
L’app espone API REST per gestione prodotti, carrello, ordini, utenti e funzionalità di supporto 
come upload immagini (Cloudinary) e invio email (Mailgun).
Autenticazione e autorizzazione sono gestite tramite JWT con ruoli: USER, ADMIN, SUPERADMIN.

Funzionalità principali

-   UTENTI E AUTENTICAZIONE

* Registrazione e login
* Autenticazione via JWT
* Autorizzazioni per ruoli (USER, ADMIN, SUPERADMIN):
  - USER: può usare funzioni self-service (/me) e consultare il catalogo con l'ausilio dei filtri
  - ADMIN: gestione parziale del catalogo e dello stock, visualizzazione ordini
  - SUPERADMIN: accesso a statistiche, messaggi utenti e operazioni più sensibili sugli utenti,
              compreso l'aggiornamento dei ruoli e l'eliminazione degli utenti;
              può effettuare l'aggiornamento e l'eliminazione di un prodotto
              e può accedere allo storico dei movimenti di stock effettuati dagli admins

-   CATALOGO PRODOTTI

* Lettura prodotti, ricerca e filtri
* Sottotipi: BagProduct, AccessoryProduct
* Gestione prodotti (ADMIN, SUPERADMIN)

-   CARRELLO (Ogni tipo di utente può accedere solo al proprio carrello)

* Visualizzazione carrello dell’utente loggato
* Aggiunta/rimozione articoli
* Update quantità
* Clear carrello

-   ORDINI E DETTAGLIO ORDINI

* Creazione e gestione ordini
* Calcolo totale ordine
* Consultazione righe ordine con controllo sull'ownership (USER vede solo i propri ordini)

-   MOVIMENTAZIONE DELLO STOCK

* Registrazione movimenti stock
* Riepiloghi (per tipo / date / prodotto)

-   SERVIZI ESTERNI

* Cloudinary per l'upload delle immagini dei prodotti
* Mailgun per notifiche email (Registrazione e ordine)

-   TECNOLOGIE USATE

* Java + Spring Boot
* Spring Web (REST)
* Spring Security + JWT
* Spring Data JPA + Hibernate
* PostgreSQL
* Cloudinary
* Mailgun

-   SETUP

Installazioni necessarie:
* Java 17+ (o la versione richiesta dal progetto)
* PostgreSQL
* Maven

-   VARIABILI

Il progetto usa spring.config.import=file:env.properties

Creare un file env.properties nella root del progetto con questo contenuto:

PG_DB_NAME=bagecommerce

PG_USERNAME=postgres

PG_PASSWORD=your_password

CLOUDINARY_NAME=your_cloud_name

CLOUDINARY_API_KEY=your_api_key

CLOUDINARY_SECRET=your_secret

MAILGUN_DOMAIN=your_domain

MAILGUN_APIKEY=your_api_key

MAILGUN_SENDER=your_sender_email

MAILGUN_TEST_RECEIVER=your_test_receiver_email

JWT_SECRET=your_jwt_secret

app.seed.superadmin.email=your_superadmin_email

app.seed.superadmin.password=your_superadmin_password

app.seed.superadmin.name=your_superadmin_name

app.seed.superadmin.surname=your_superadmin_surname

-   DATABASE POSTGRESQL

Creare il database (nome coerente con PG_DB_NAME), esempio: "bagecommerce"

Lo schema ERD e l'export della POSTMAN collection si trovano nella root del progetto, sotto la folder utilities.

Settare l'environment (button in alto a destra) su Postman inserendo come chiave 'baseUrl' e come valore 'http://localhost:3001'.


