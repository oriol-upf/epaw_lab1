# 🎓 Guia de Desenvolupament Web (Lab 1)
Benvinguts a la primera pràctica d'EPAW. Aquesta guia t'ajudarà a entendre com funciona l'entorn de treball, com programar el teu primer backend amb Java Servlets, com connectar-ho amb una base de dades SQLite i com col·laborar amb el teu equip mitjançant Git.

---

## 🛠️ 1. Requisits Previs
Abans de començar, assegura't que tens instal·lat el següent programari:

- **Visual Studio Code**: El nostre editor oficial.
- **Java 21 (JDK)**: És la versió que fem servir per compilar el projecte (podeu baixar la versió d'OpenJDK).
- **Maven**: L'eina que gestiona les llibreries i arrenca el servidor.
- **Extensions de VS Code**: Busca i instal·la el **"Extension Pack for Java"** de Microsoft. Això t'activarà l'autocompletat de codi, la depuració i el suport de Maven.

> [!TIP]
> Per comprovar si tot està bé, obre una terminal i escriu `java -version` i `mvn -version`. Hauries de veure la versió 21 i la versió de Maven respectivament.

---

## 📂 2. Estructura del Projecte
És fonamental saber on va cada cosa per mantenir el codi net i organitzat. El projecte segueix l'estàndard de **Maven**:

- **`src/main/java/`**: Aquí és on escriuràs tot el codi **Java**.
  - `epaw.lab1/`: Paquet principal.
  - `util/DBManager.java`: Classe auxiliar per connectar amb la base de dades.
- **`src/main/webapp/`**: Aquí van els fitxers **estàtics** (HTML, CSS, imatges).
  - *Consell*: Tot el que hi posis aquí serà accessible directament des del navegador (ex: `localhost:8080/index.html`).
- **`pom.xml`**: El fitxer de configuració de **Maven**. 

### Què és Maven i el `pom.xml`?
**Maven** és l'eina que gestiona tot el cicle de vida del teu projecte. Pensa en el fitxer `pom.xml` (Project Object Model) com el **"llibre de receptes"** del teu programa:

1.  **Identitat**: Defineix qui ets (`groupId`, `artifactId`, `version`).
2.  **Dependències**: En lloc de descarregar fitxers `.jar` a mà i copiar-los a carpetes, simplement li dius a Maven: "necessito la llibreria de SQLite versió X". Maven la baixarà d'Internet automàticament i la posarà a punt per fer-la servir.
3.  **Plugins**: Són funcionalitats extra. Per exemple, el plugin de **Jetty** que fem servir és el que permet que, amb una sola comanda (`mvn jetty:run`), s'aixequi un servidor web complet.

- **`lab1.db`**: El fitxer de la base de dades **SQLite**. No l'has d'editar a mà, ho farà el teu codi!
- **`DB.txt`**: Conté les sentències SQL per crear les taules inicials si la base de dades no existeix.
- **`.vscode/`**: Configuració específica per a l'editor, incloent els **Snippets** de codi.

---

## ⚡ 3. Entorn de Treball i Avantatges
Aquesta plantilla està dissenyada per ser àgil i evitar pèrdues de temps re-arrencant el servidor.

### Per què fem servir Jetty?
**Jetty** és un "Servlet Container" (com Tomcat) però molt més **lleuger i ràpid**. Les seves bondats per al nostre curs són:
- **Integració total amb Maven**: No cal que t'instal·lis res a part; el servidor ja ve inclòs al projecte.
- **Hot-Reloading**: Quan facis un canvi en un Servlet i el desis (`Ctrl + S`), Jetty detectarà el canvi automàticament i tornarà a compilar el fitxer en pocs segons. No cal aturar i tornar a llançar `mvn jetty:run` cada vegada!
- **Baix consum**: Ideal per a ordinadors portàtils o màquines virtuals.

> [!TIP]
> Si el servidor no detecta un canvi, revisa la terminal per si hi ha algun error de sintaxi que hagi impedit la compilació.

### Snippets de VS Code
Hem configurat dreceres per no haver d'escriure estructures repetitives. Prova-ho:
1. Crea un fitxer `.html`.
2. Escriu `html5` i prem `Tab`. Es generarà tot l'esquelet a l'instant.
3. El mateix val per a Servlets si escrius `servlet`.

### Funcionalitat "Run on Server" (Obrir al Navegador ràpidament)
Quan estiguis editant un fitxer HTML, JSP o un Servlet (Java), pots fer que el teu navegador s'obri automàticament a l'adreça correcta sense haver de teclejar l'URL al navegador manualment, igual que faries a l'Eclipse!

Només has de prémer la següent drecera de teclat mentre tens el fitxer obert:
👉 **`Ctrl + Shift + B`** *(si prefereixes el ratolí, ves a Terminal > Run Task i tria "Obre al navegador")*.

Un script intern identificarà la ruta correctament (tot i que tinguis un `@WebServlet`) i obrirà el teu navegador web! T'estalviarà molts tràmits a l'hora de provar el codi fets els canvis.

---

## ☕ 4. Java Servlets (El Backend)
Un Servlet és una classe Java que rep peticions del navegador i retorna una resposta (normalment HTML).

### Com crear un Servlet
Tots els teus servlets han de:
1. Estendre de la classe `HttpServlet`.
2. Tenir l'anotació `@WebServlet("/ruta")` a sobre de la classe.
3. Implementar el mètode `doGet` (per rebre dades) o `doPost` (per enviar formularis).

Exemple de Servlet bàsic:
```java
@WebServlet("/hola")
public class HolaServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println("<h1>Hola des del Servlet!</h1>");
    }
}
```

> [!IMPORTANT]
> En aquesta pràctica, generarem l'HTML directament des del Servlet fent servir `response.getWriter().println()`. 
---

## 💾 5. Bases de Dades (SQLite)
Per connectar-nos a la base de dades sense embolics, fem servir la classe `DBManager.java`.

### Quins avantatges té SQLite?
A diferència de MySQL o Oracle, **SQLite** no és un servidor de base de dades, sinó una **base de dades de fitxer**.
- **Zero Configuració**: No cal instal·lar ni configurar cap servidor extern. Tot el que necessites ja és dins del fitxer `lab1.db`.
- **Portabilitat**: Si canvies d'ordinador, només cal que t'emportis la carpeta del projecte i la teva base de dades anirà amb tu.
- **Transaccional**: Tot i ser senzilla, compleix els estàndards profesionals (ACID), el que la fa perfecta per aprendre SQL.

### 🔍 Com veure i editar les dades (Fàcil)
Tot i que pots editar la base de dades mitjançant codi Java, de vegades voldràs veure les taules directament o afegir dades de prova ràpidament. Per això farem servir l'extensió **Database Client**:

1.  Busca la icona del **Database Client** a la barra lateral de VS Code (un quadrat amb 4 punts).
2.  Clica a **"New Connection" (+)** i tria **SQLite**.
3.  A l'apartat **File**, tria el fitxer `lab1.db` del teu projecte.
4.  Un cop connectat, fes **doble clic** sobre qualsevol taula per veure'n el contingut en una graella (com un Excel).
5.  Pots afegir files noves directament des de la graella fent clic al botó **"+" (Insert)**.

> [!TIP]
> Si l'extensió et diu que no troba SQLite instal·lat al sistema:
> - **Linux**: Executa `sudo apt install sqlite3`.
> - **macOS**: Normalment ja ve instal·lat, però pots fer `brew install sqlite`.
> - **Windows**: Descarrega el "sqlite-tools" de la web oficial [sqlite.org](https://www.sqlite.org/download.html) i afegeix-lo al PATH, o simplement reinstal·la l'extensió.

### Com s'inicialitza?
Quan el teu codi fa `new DBManager()`, la classe busca el fitxer `lab1.db`. Si no existeix, el crea i executa les instruccions que hi hagi a `DB.txt`. Pots afegir les teves taules allà mateix!

### Exemple de consulta (SELECT)
Fes servir un `PreparedStatement` per evitar atacs d'injecció SQL:
```java
try (DBManager db = new DBManager()) {
    String query = "SELECT name, description FROM users WHERE id = ?";
    PreparedStatement stmt = db.prepareStatement(query);
    stmt.setInt(1, 1); // Posem el valor del primer '?'
    
    ResultSet rs = stmt.executeQuery();
    while (rs.next()) {
        String nom = rs.getString("name");
        String desc = rs.getString("description");
        out.println("<p>Usuari: " + nom + " (" + desc + ")</p>");
    }
} catch (Exception e) {
    e.printStackTrace();
}
```

### Exemple d'inserció (INSERT)
```java
try (DBManager db = new DBManager()) {
    String sql = "INSERT INTO users (name, description) VALUES (?, ?)";
    PreparedStatement stmt = db.prepareStatement(sql);
    stmt.setString(1, "Joan");
    stmt.setString(2, "Estudiant de 3er");
    stmt.executeUpdate(); // Fa el canvi a la base de dades
}
```

---

## 🌐 6. HTML5 i Integració
Perquè l'usuari pugui interactuar amb el teu Servlet, pots fer servir **formularis**:

```html
<form action="/teu-servlet" method="POST">
    <input type="text" name="usuari" placeholder="Nom d'usuari">
    <button type="submit">Enviar</button>
</form>
```

Al Servlet, llegiràs el nom d'usuari així:
```java
String usuari = request.getParameter("usuari");
```

---

## 🐙 7. Git i GitHub (Guia per a Novells)
Si és el primer cop que fas servir Git, pensa-hi com una **"màquina del temps"** per al teu codi que, a més, permet que dues o més persones treballin en el mateix projecte sense esborrar-se la feina mútuament.

### Conceptes Bàsics
- **Repository (Repo)**: La carpeta del teu projecte controlada per Git.
- **Local vs Remoty**: El teu ordinador (**Local**) vs el servidor de GitHub (**Remot**).
- **Commit**: Una "foto" de l'estat actual del teu codi amb un missatge explicatiu.
- **Branch (Branca)**: Una línia de treball separada. La branca principal es diu `main`.

### 1. Crea el teu compte i el teu Repositori

> [!IMPORTANT]
> **Tots** els membres del grup han de tenir el seu propi compte a GitHub, però **només un** membre de l'equip ha de crear el repositori del projecte.

1.  **Crea un compte**: Tots els membres del grup us heu de registrar a [github.com](https://github.com/).
2.  **Crea el Repositori (Només un membre)**:
    - Fes clic al botó **"+"** (a dalt a la dreta) i tria **"New repository"**.
    - Posa-li un nom (ex: `GrupX-Lab1`).
    - **IMPORTANT**: No marquis cap casella de "README" ni ".gitignore".
3.  **Convida l'equip**:
    - El membre que ha creat el repo ha d'anar a **Settings > Collaborators** i afegir els correus de la resta de companys.
4.  **Vincula el projecte**:
    ```bash
    git remote add origin https://github.com/el-teu-usuari/el-teu-repo.git
    git branch -M main
    git push -u origin main
    ```

### 2. La resta de l'equip (Clonar)
Si un company ja ha creat el repositori i t'ha convidat, tu no has de fer els passos anteriors. Simplement:
1.  Ves a la URL del repositori a GitHub.
2.  Copia l'enllaç de **"Code"**.
3.  Obre la terminal al teu ordinador i executa:
    ```bash
    git clone URL_DEL_REPOSITORI
    ```
    *Així ja tindràs tot el projecte a punt per començar!*

### 2. Configuració inicial (Només el primer cop)
Si mai has fet servir Git en aquest ordinador, identifica't perquè els teus companys sàpiguen qui ha fet cada canvi:
```bash
git config --global user.name "El Teu Nom"
git config --global user.email "el-teu-correu@estudiant.upf.edu"
```

### 3. El flux de treball en equip
Mai treballis directament sobre la branca `main`. Imagina que `main` és el codi sagrat que sempre funciona.

#### Pas A: Crear la teva parcel·la (Branca)
Abans de començar una nova funció, crea una branca:
```bash
git checkout -b feature-nom-funció
```

#### Pas B: Desar la feina (Commit)
Quan hagis fet uns quants canvis, guarda'ls. Primer mira què has tocat:
```bash
git status
git add .
git commit -m "Explicació del que he fet (ex: Creada la taula d'usuaris)"
```

#### Pas C: Compartir amb l'equip (Push)
Perquè els teus companys vegin la teva branca a GitHub:
```bash
git push origin feature-nom-funció
```

### 3. Com ajuntar la feina (Pull Request)
Un cop has pujat la teva branca:
1. Ves a la web del vostre repositori a GitHub.
2. Veuràs un botó groc que diu **"Compare & pull request"**. Clica'l.
3. Això permet que els teus companys revisin el codi. Si tot està bé, algú prem **"Merge"** i el teu codi passa a la branca `main`.

### 4. Mantenir-se al dia (Pull)
Mentre tu treballaves, un company pot haver pujat canvis a `main`. Per baixar-te'ls:
```bash
git checkout main
git pull origin main
```

---

## ⚠️ 8. Auxili! Tinc un Conflicte
Un conflicte passa quan tu i un company heu tocat **la mateixa línia** del mateix fitxer. Git no sap quina prefereixes i t'ho preguntarà així dins del fitxer:

```text
<<<<<<< HEAD
El meu codi (el que estic fent ara)
=======
El codi del meu company (el que ja estava a GitHub)
>>>>>>> main
```

**Com resoldre-ho?**
1. Obre el fitxer a **VS Code**.
2. Veuràs uns botons sobre el conflicte: "Accept Current Change", "Accept Incoming Change", etc.
3. Tria la que vulguis (o edita el text per barrejar-les).
4. **IMPORTANT**: Quan acabis, has de fer `git add .` i `git commit` per confirmar que has arreglat el desordre.


---

## 🎯 10. Objectiu Final de la Pràctica
L'objectiu d'aquest laboratori és que siguis capaç de crear una aplicació que no només llegeixi dades, sinó que també permeti interactuar a l'usuari. El flux complet que has d'aconseguir és:

1.  **Llistar**: Mostrar la taula d'usuaris extreta de SQLite (aixó ja està fet a la plantilla).
2.  **Interactuar**: Afegir un formulari HTML al final de la pàgina per crear-ne de nous (ho has de fer al Servlet dins del mètode `doGet`).
3.  **Processar**: Rebre les dades al mètode `doPost`, guardar-les a la base de dades i redirigir correctament la petició.
4.  **Decorar**: Fer servir CSS bàsic per millorar l'aparença de la taula i el formulari.


---

## ❓ 9. Problemes Freqüents (FAQ)

### "El port 8080 ja està en ús"
Si en llançar `mvn jetty:run` obtens un error de port ocupat, pot ser que tinguis una altra sessió del servidor oberta.
- **Solució**: Troba el terminal on s'està executant el servidor i prem `Ctrl + C`. Si no el trobes, reinicia el teu entorn o tanca els processos Java que s'estiguin executant al sistema.

### "He canviat el codi però no veig el canvi al navegador"
A vegades el Hot-Reload pot fallar si hi ha errors de sintaxi greus.
- **Solució**: Revisa els missatges de la terminal. Si veus línies en vermell (errors de compilació), corregeix-les al VS Code i torna a desar. Quan veguis el missatge `Scanning for changes...` i després `Re-started Jetty`, ja podràs refrescar el navegador.

### "Error 404: Pàgina no trobada"
Això vol dir que la URL que has posat al navegador no coincideix amb cap `@WebServlet`.
- **Solució**: Comprova que a `@WebServlet("/la-teva-ruta")` hagis posat la mateixa ruta que al navegador (`http://localhost:8080/la-teva-ruta`). Recorda que les rutes de Java Servlets **discriminen entre majúscules i minúscules**.

### "GitHub no em deixa fer `push` (Rejected)"
Passa quan un company ha pujat canvis a GitHub i tu no els tens al teu ordinador.
- **Solució**: Fes primer un `git pull origin main` per baixar la feina dels companys, arregla possibles conflictes si n'hi ha, i després ja podràs fer el teu `push`.

---

## 🚀 Conclusió
Aquesta plantilla és el teu camp de proves. Experimenta, llegeix els errors de la terminal i, sobretot, diverteix-te creant la teva primera aplicació web!
