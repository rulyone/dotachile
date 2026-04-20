# Coding Conventions

**Analysis Date:** 2026-04-19

## Naming Patterns

**Files:**
- Java classes: PascalCase (e.g., `Usuario.java`, `ClanService.java`, `CrearClanMB.java`)
- Test files: TestClass suffix with `Test` (e.g., `ClanServiceTest.java`, `UtilTest.java`)
- All identifiers (packages, classes, methods, fields) are in **Spanish** per project rule in CLAUDE.md

**Functions/Methods:**
- camelCase starting with lowercase verb (e.g., `crearClan`, `aceptarDesafio`, `cambiarSlashes`, `hashPassword`)
- Getters: `get` prefix (e.g., `getUsername`, `getNombre`, `getChieftain`)
- Setters: `set` prefix (e.g., `setNombre`, `setTag`, `setClan`)
- Predicates: `is` or boolean verb (e.g., `passwordsMatch`, `caracteresValidosPvpgn`, `isBanned`)
- Static utility methods: all camelCase verbs (e.g., `dateSinMillis`, `addErrorMessage`, `getUsuarioLogeado`)
- Service methods: domain-driven names (e.g., `crearClan`, `invitarPlayer`, `promover`, `demotear`, `traspasarChieftain`, `desafiarClan`)

**Variables:**
- camelCase (e.g., `nombre`, `tag`, `chieftain`, `usuario`, `clanBanFac`)
- Field injections: camelCase ending in facade pattern (e.g., `clanFac`, `userFac`, `desafioFac`, `ladderFac`, `movFac`)
- Loop variables: single lowercase letters (e.g., `i`, `j`) or descriptive (e.g., `clan`, `usuario`)
- Boolean fields/variables: explicit naming (e.g., `desafioAceptado`, `resultadoConfirmado`, `high`)

**Types/Classes:**
- Entity classes: PascalCase, Spanish (e.g., `Usuario`, `Clan`, `Desafio`, `Ladder`, `Torneo`)
- Service classes: `*Service` suffix (e.g., `ClanService`, `LadderService`, `ComentariosService`)
- Facade classes (DAOs): `*Facade` suffix (e.g., `UsuarioFacade`, `ClanFacade`, `DesafioFacade`)
- Controller/ManagedBean classes: `*MB` suffix (e.g., `CrearClanMB`, `VerClanMB`, `LoginMB`)
- Converter classes: `*Converter` suffix (e.g., `UsuarioConverter`, `ClanConverter`)
- Helper/DTO classes: plain names (e.g., `ComparacionClanes`, `AbstractFacade`)
- Enums: PascalCase (e.g., `FaseLadder`, `FaseTorneo`, `TipoMovimiento`)

## Code Style

**Formatting:**
- No automated formatter detected (no `.editorconfig`, Checkstyle, or SpotBugs config)
- Standard Java conventions observed: 4-space indentation (implicit from source)
- Braces on same line (e.g., `} catch (Exception ex) {`)

**Linting:**
- No linting tool configured (pom.xml has no Maven Checkstyle or PMD plugins)
- Code style enforced by code review, not automated tooling

**Comments:**
- Javadoc class-level comments present on many classes (auto-generated template style)
- Author attribution: `@author Pablo`
- Method-level comments sparse but used for complex logic (e.g., `ClanService` has a lengthy block comment explaining clan lifecycle rules)
- Post-condition comments in tests document expected behavior (e.g., "gain = (int)(32 * (1 - 0.76)) = 7")

## Import Organization

**Order:**
1. `java.*` and `javax.*` standard library
2. `java.util.*` collections
3. `java.security.*`, `java.io.*` if needed
4. `javax.ejb.*`, `javax.persistence.*`, `javax.faces.*` (EJB/JPA/JSF framework)
5. `javax.annotation.*` (for `@Resource`, `@RolesAllowed`)
6. Project imports from `com.dotachile.*`

**Path Aliases:**
- No path aliases configured; full package names used throughout (e.g., `com.dotachile.clanes.service.ClanService`)

**Example:** `ClanServiceTest.java` imports:
```java
import com.dotachile.auth.entity.Usuario;
import com.dotachile.auth.facade.UsuarioFacade;
import com.dotachile.clanes.entity.Clan;
import com.dotachile.clanes.entity.ClanBan;
import com.dotachile.clanes.facade.ClanBanFacade;
import com.dotachile.clanes.facade.ClanFacade;
// ... more domain imports ...
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
```

## EJB & Service Layer Patterns

**@Stateless Services:**
- One per feature domain (e.g., `ClanService`, `LadderService`, `ComentariosService`)
- Location: `src/java/com/dotachile/{feature}/service/`
- Constructor: no-arg public constructor (EJB requirement)
- Dependencies: injected via `@EJB` fields (e.g., `@EJB private ClanFacade clanFac;`)
- SessionContext: injected via `@Resource SessionContext ctx;` to call `ctx.getCallerPrincipal()`
- Scope annotations: `@Stateless` (never Stateful)
- Role annotations: `@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA", ...})` at class level; `@RolesAllowed(...)` at class or method level

**Example:** `LadderService.java`
```java
@Stateless
@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA", "ADMIN_TORNEO"})
@RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})  // class-level default
public class LadderService {
    @Resource SessionContext ctx;
    @EJB private UsuarioFacade userFac;
    @EJB private ClanFacade clanFac;
    // ... method overrides use @PermitAll to allow broader access
}
```

**Facade (DAO) Pattern:**
- All facades extend `model.entities.AbstractFacade<T>` (generic CRUD base)
- Location: `src/java/com/dotachile/{feature}/facade/`
- Annotation: `@Stateless`
- Injection: `@PersistenceContext(unitName = "DotaCLPU") private EntityManager em;`
- Named queries defined on entity: `@NamedQuery(name="Entity.findByX", query="SELECT ... WHERE ...")` used in facade methods
- Custom finder methods: return single entities or lists (e.g., `findByUsername`, `findByEmail`, `findByTag`)

**Example:** `UsuarioFacade.java`
```java
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public Usuario findByUsername(String username) {
        List<Usuario> list = em.createNamedQuery("Usuario.findByUsername", Usuario.class)
                .setParameter("username", username)
                .getResultList();
        return list.size()==1? list.get(0):null;
    }
}
```

**AbstractFacade Methods:**
- `create(T entity)` — persist
- `edit(T entity)` — merge
- `remove(T entity)` — delete
- `find(Object id)` — fetch by PK
- `findAll()` — all rows
- `findLimit(int first, int size)` — pagination
- `count()` — row count
- `flush()` — explicit EntityManager flush

## JSF ManagedBean Patterns

**Naming & Scope:**
- Location: `src/java/com/dotachile/{feature}/controller/`
- Annotation: `@ManagedBean(name="beanName")` with lowerCamelCase bean name
- Scope: `@RequestScoped`, `@ViewScoped`, or `@SessionScoped`
- Convention: name `*MB` suffix (e.g., `CrearClanMB`, `LoginMB`)

**Example:** `CrearClanMB.java`
```java
@ManagedBean(name="crearClanMB")
@ViewScoped
public class CrearClanMB implements Serializable {
    @EJB
    private ClanService clanService;

    private String nombre;
    private String tag;

    public CrearClanMB() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Action method returning navigation outcome
    public String crearClan() {
        try {
            clanService.crearClan(nombre, tag);
            Util.addInfoMessage("Clan creado satisfactoriamente", null);
            return "/web/clanes/VerClan.xhtml?faces-redirect=true&amp;tag=" + tag;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al crear clan", ex.getMessage());
        }
        return null;  // stay on current view
    }
}
```

**Action Methods:**
- Return `String` (navigation outcome) or `void`
- Called from XHTML forms/buttons: `action="#{beanName.methodName}"`
- On success: return redirect path (e.g., `"/web/clanes/VerClan.xhtml?faces-redirect=true"`)
- On validation error: return `null` to re-render current view; error messages added via `Util.add*Message`

## Error Handling

**BusinessLogicException:**
- Located: `src/java/com/dotachile/shared/BusinessLogicException.java`
- Annotated: `@ApplicationException(rollback = true, inherited = true)` for EJB transaction rollback
- Usage: thrown from Service layer for domain/validation errors
- Constructor: takes optional `String msg` detail message
- Caught in Controller (MB) layer and converted to user-facing `FacesMessage` via `Util.add*Message`

**Example Flow:** Service → ManagedBean
```java
// Service (ClanService.java)
public void crearClan(String nombre, String tag) throws BusinessLogicException {
    if(nombre == null || tag == null)
        throw new BusinessLogicException("Valor requerido.");
    if(clanFac.findByNombre(nombre) != null)
        throw new BusinessLogicException("Nombre ya existe.");
    // ... proceed to create
}

// Controller (CrearClanMB.java)
public String crearClan() {
    try {
        clanService.crearClan(nombre, tag);
        Util.addInfoMessage("Clan creado satisfactoriamente", null);
        return "/web/clanes/VerClan.xhtml?faces-redirect=true";
    } catch (BusinessLogicException ex) {
        Util.addErrorMessage("Error al crear clan", ex.getMessage());
        // returns null, re-renders form with error
    }
    return null;
}
```

**Logging:**
- Uses `java.util.logging.Logger` and `Level` enum
- Typical pattern: `Logger.getLogger(ClassName.class.getName()).log(Level.SEVERE, null, ex);`
- Logs used for exceptions that should not propagate to user (e.g., `NamingException` in `Util.getUsuarioLogeado()`)

## Security Annotations

**Role-Based Access Control:**
- Roles defined in `web/WEB-INF/web.xml` security-role: `ADMIN_ROOT`, `ADMIN_DOTA`, `ADMIN_TORNEO`, `ESCRITOR`, `MODERADOR`, `BANEADO`, `ADMIN_LADDER`
- Service methods use: `@DeclareRoles({...})` and `@RolesAllowed({...})`
- Some methods override class-level `@RolesAllowed` with `@PermitAll` to allow broader access (e.g., public challenge methods in `LadderService`)

**Example:** `AdminService` (all methods require ADMIN_ROOT)
```java
@Stateless
@RolesAllowed({"ADMIN_ROOT"})
public class AdminService {
    // All methods here require ADMIN_ROOT by default
    
    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA", "MODERADOR"})  // override for banUser
    public void banUser(Usuario usuario, String razonBan) throws BusinessLogicException { ... }
}
```

## Entity Patterns

**JPA Entities:**
- Located: `src/java/com/dotachile/{feature}/entity/`
- Annotated: `@Entity`, `@Table(name="spanish_name")`
- Primary keys: `@Id` with column constraints
- Relationships: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany` with cascade and fetch settings
- Named queries: `@NamedQueries({@NamedQuery(...), ...})` at class level

**Example:** `Usuario.java`
```java
@Entity
@Table(name = "usuario")
@NamedQueries({
    @NamedQuery(name="Usuario.findByUsername", query="SELECT u FROM Usuario u WHERE u.username = :username"),
    @NamedQuery(name="Usuario.findByEmail", query="SELECT u FROM Usuario u WHERE u.email = :email"),
})
public class Usuario implements Serializable, HttpSessionBindingListener {
    @Id
    @Column(nullable = false, unique = true, length = 20)
    private String username;
    
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Clan clan;
    
    @ManyToMany
    @JoinTable(
        joinColumns = { @JoinColumn(name="USERNAME") },
        inverseJoinColumns = { @JoinColumn(name="GROUPNAME") }
    )
    private List<Grupo> grupos;
}
```

## Module/Package Organization

**Package structure:**
- `src/java/com/dotachile/{feature}/` for each feature domain
- Within each feature: `entity/`, `facade/`, `service/`, `controller/`, `converter/` sub-packages
- `src/java/com/dotachile/shared/` for cross-cutting utilities (`Util`, `BusinessLogicException`, `EloSystem`, `PvpgnHash`)
- `src/java/com/dotachile/infrastructure/web/` for filters, servlets, exception handlers

**Cross-feature imports:**
- Services may import from multiple features' facades (e.g., `ClanService` imports `UsuarioFacade`, `TemporadaModificacionFacade`)
- Controllers import their own Service and shared `Util`
- Entities import only JPA and serialization APIs, no service imports

---

*Convention analysis: 2026-04-19*
