package com.dotachile.torneos.service;

import com.dotachile.auth.entity.Usuario;
import com.dotachile.auth.facade.UsuarioFacade;
import com.dotachile.clanes.entity.Clan;
import com.dotachile.clanes.entity.ClanBan;
import com.dotachile.clanes.facade.ClanBanFacade;
import com.dotachile.clanes.facade.ClanFacade;
import com.dotachile.comentarios.ComentarioFacade;
import com.dotachile.media.ReplayFacade;
import com.dotachile.shared.BusinessLogicException;
import com.dotachile.torneos.entity.FactorK;
import com.dotachile.torneos.entity.FaseTorneo;
import com.dotachile.torneos.entity.Game;
import com.dotachile.torneos.entity.GameMatch;
import com.dotachile.torneos.entity.Resultado;
import com.dotachile.torneos.entity.Ronda;
import com.dotachile.torneos.entity.TipoTorneo;
import com.dotachile.torneos.entity.Torneo;
import com.dotachile.torneos.facade.GameFacade;
import com.dotachile.torneos.facade.GameMatchFacade;
import com.dotachile.torneos.facade.ModificacionFacade;
import com.dotachile.torneos.facade.RondaFacade;
import com.dotachile.torneos.facade.TemporadaModificacionFacade;
import com.dotachile.torneos.facade.TorneoFacade;
import com.dotachile.torneos.helper.Standing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ejb.SessionContext;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TorneoServiceTest {

    private TorneoService service;
    private TorneoFacade torneoFac;
    private UsuarioFacade userFac;
    private ClanFacade clanFac;
    private RondaFacade rondaFac;
    private GameMatchFacade matchFac;
    private GameFacade gameFac;
    private TemporadaModificacionFacade tempFac;
    private ModificacionFacade modFac;
    private ReplayFacade replayFac;
    private ClanBanFacade clanBanFac;
    private ComentarioFacade comFac;
    private SessionContext ctx;

    /**
     * Source of unique ids for synthetic Clans built in tests.
     * Clan.equals() compares by id (a long that defaults to 0), so two
     * synthetic clans both at id=0 collide and break self-reference guards
     * in production code. Bumping a counter per built clan keeps each
     * test entity distinct.
     */
    private long nextClanId = 1L;

    @BeforeEach
    void setUp() throws Exception {
        service = new TorneoService();
        torneoFac = mock(TorneoFacade.class);
        userFac = mock(UsuarioFacade.class);
        clanFac = mock(ClanFacade.class);
        rondaFac = mock(RondaFacade.class);
        matchFac = mock(GameMatchFacade.class);
        gameFac = mock(GameFacade.class);
        tempFac = mock(TemporadaModificacionFacade.class);
        modFac = mock(ModificacionFacade.class);
        replayFac = mock(ReplayFacade.class);
        clanBanFac = mock(ClanBanFacade.class);
        comFac = mock(ComentarioFacade.class);
        ctx = mock(SessionContext.class);

        nextClanId = 1L;

        inject("torneoFac", torneoFac);
        inject("userFac", userFac);
        inject("clanFac", clanFac);
        inject("rondaFac", rondaFac);
        inject("matchFac", matchFac);
        inject("gameFac", gameFac);
        inject("tempFac", tempFac);
        inject("modFac", modFac);
        inject("replayFac", replayFac);
        inject("clanBanFac", clanBanFac);
        inject("comFac", comFac);
        inject("ctx", ctx);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field f = TorneoService.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(service, value);
    }

    /** Build a minimal Clan with a unique id and the given tag. */
    private Clan clanWith(String tag) {
        Clan clan = new Clan();
        clan.setId(nextClanId++);
        clan.setTag(tag);
        clan.setIntegrantes(new ArrayList<>());
        clan.setShamanes(new ArrayList<>());
        clan.setTorneos(new ArrayList<>());

        Usuario chief = new Usuario();
        chief.setUsername("chief_" + tag);
        chief.setClan(clan);
        clan.setChieftain(chief);
        clan.getIntegrantes().add(chief);

        return clan;
    }

    /** Stub ctx so the caller is an ADMIN_DOTA (bypasses encargado checks). */
    private void stubCallerAsAdminDota(String username) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);
        when(ctx.getCallerPrincipal()).thenReturn(principal);
        Usuario admin = new Usuario();
        admin.setUsername(username);
        when(userFac.findByUsername(username)).thenReturn(admin);
        when(ctx.isCallerInRole("ADMIN_DOTA")).thenReturn(true);
        when(ctx.isCallerInRole("ADMIN_ROOT")).thenReturn(false);
    }

    /** Build a minimal valid Torneo in REGISTRATION phase. */
    private Torneo torneoRegistration(int min, int max, List<Clan> enrolled) {
        Torneo torneo = new Torneo();
        torneo.setId(1L);
        torneo.setNombre("TestTorneo");
        torneo.setFaseTorneo(FaseTorneo.REGISTRATION);
        torneo.setMinCantidadClanes(min);
        torneo.setMaxCantidadClanes(max);
        torneo.setFactorK(FactorK.DIEZ);
        torneo.setTipoTorneo(TipoTorneo.values()[0]);
        torneo.setClanesInscritos(new ArrayList<>(enrolled));
        torneo.setRondas(new ArrayList<>());

        Usuario encargado = new Usuario();
        encargado.setUsername("encargado");
        torneo.setEncargado(encargado);
        return torneo;
    }

    /** Build a minimal valid Torneo in STARTED phase. */
    private Torneo torneoStarted(List<Clan> enrolled) {
        Torneo torneo = torneoRegistration(2, 8, enrolled);
        torneo.setFaseTorneo(FaseTorneo.STARTED);
        return torneo;
    }

    // ========== Group A: crearTorneo guards ==========

    @Test
    void crearTorneoThrowsWhenNombreIsNull() {
        assertThatThrownBy(() -> service.crearTorneo(null, "info", TipoTorneo.values()[0], FactorK.values()[0], 4, 2))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void crearTorneoThrowsWhenMaxClanesBelowTwo() {
        assertThatThrownBy(() -> service.crearTorneo("T1", "info", TipoTorneo.values()[0], FactorK.values()[0], 1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Max cantidad");
    }

    @Test
    void crearTorneoThrowsWhenMinClanesBelowTwo() {
        assertThatThrownBy(() -> service.crearTorneo("T1", "info", TipoTorneo.values()[0], FactorK.values()[0], 4, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Min cantidad");
    }

    @Test
    void crearTorneoThrowsWhenMinExceedsMax() {
        assertThatThrownBy(() -> service.crearTorneo("T1", "info", TipoTorneo.values()[0], FactorK.values()[0], 4, 8))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Max cantidad");
    }

    @Test
    void crearTorneoPersistsTorneoOnHappyPath() throws Exception {
        when(torneoFac.findByNombre("T1")).thenReturn(null);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("admin");
        when(ctx.getCallerPrincipal()).thenReturn(principal);
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        when(userFac.findByUsername("admin")).thenReturn(admin);

        service.crearTorneo("T1", "info", TipoTorneo.values()[0], FactorK.values()[0], 8, 4);

        ArgumentCaptor<Torneo> captor = ArgumentCaptor.forClass(Torneo.class);
        verify(torneoFac).create(captor.capture());
        Torneo created = captor.getValue();
        assertThat(created.getNombre()).isEqualTo("T1");
        assertThat(created.getFaseTorneo()).isEqualTo(FaseTorneo.REGISTRATION);
    }

    // ========== Group B: startTorneo ==========

    @Test
    void startTorneoThrowsWhenEnrolledCountBelowMin() {
        List<Clan> enrolled = Arrays.asList(clanWith("AAA"), clanWith("BBB"), clanWith("CCC"));
        Torneo torneo = torneoRegistration(4, 8, enrolled);
        when(torneoFac.find(1L)).thenReturn(torneo);
        when(torneoFac.cantidadClanesInscritos(1L)).thenReturn(3);

        assertThatThrownBy(() -> service.startTorneo(1L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("minimo");
    }

    @Test
    void startTorneoThrowsWhenAnEnrolledClanIsBanned() {
        Clan banned = clanWith("BAN");
        Clan ok = clanWith("OK1");
        List<Clan> enrolled = Arrays.asList(banned, ok);
        Torneo torneo = torneoRegistration(2, 8, enrolled);
        when(torneoFac.find(1L)).thenReturn(torneo);
        when(torneoFac.cantidadClanesInscritos(1L)).thenReturn(2);
        when(clanBanFac.findByTag("BAN")).thenReturn(new ClanBan());
        when(clanBanFac.findByTag("OK1")).thenReturn(null);

        assertThatThrownBy(() -> service.startTorneo(1L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("baneado");
    }

    @Test
    void startTorneoTransitionsToStartedAndCreatesFirstRonda() throws Exception {
        Clan c1 = clanWith("AAA");
        Clan c2 = clanWith("BBB");
        List<Clan> enrolled = Arrays.asList(c1, c2);
        Torneo torneo = torneoRegistration(2, 8, enrolled);
        when(torneoFac.find(1L)).thenReturn(torneo);
        when(torneoFac.cantidadClanesInscritos(1L)).thenReturn(2);
        when(clanBanFac.findByTag("AAA")).thenReturn(null);
        when(clanBanFac.findByTag("BBB")).thenReturn(null);
        stubCallerAsAdminDota("admin");

        service.startTorneo(1L);

        assertThat(torneo.getFaseTorneo()).isEqualTo(FaseTorneo.STARTED);
        verify(rondaFac).create(any(Ronda.class));
    }

    // ========== Group C: agregarPareo ==========

    @Test
    void agregarPareoThrowsWhenTorneoNotInStartedPhase() {
        Clan c1 = clanWith("AAA");
        Clan c2 = clanWith("BBB");
        Torneo torneo = torneoRegistration(2, 8, Arrays.asList(c1, c2));

        Ronda ronda = new Ronda();
        ronda.setId(10L);
        ronda.setTorneo(torneo);
        ronda.setMatches(new ArrayList<>());

        when(clanFac.findByTag("AAA")).thenReturn(c1);
        when(clanFac.findByTag("BBB")).thenReturn(c2);
        when(rondaFac.find(10L)).thenReturn(ronda);

        // Date 1 day in the future to pass the date guard
        Date futureDate = new Date(System.currentTimeMillis() + 86400_000L);

        assertThatThrownBy(() -> service.agregarPareo("AAA", "BBB", 10L, 1, null, futureDate, false))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("no ha comenzado");
    }

    @Test
    void agregarPareoThrowsWhenSameClanAppearsTwice() {
        Clan c1 = clanWith("AAA");
        Torneo torneo = torneoStarted(Arrays.asList(c1));

        Ronda ronda = new Ronda();
        ronda.setId(10L);
        ronda.setTorneo(torneo);
        ronda.setMatches(new ArrayList<>());

        when(clanFac.findByTag("AAA")).thenReturn(c1);

        Date futureDate = new Date(System.currentTimeMillis() + 86400_000L);

        assertThatThrownBy(() -> service.agregarPareo("AAA", "AAA", 10L, 1, null, futureDate, false))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("distintos");
    }

    @Test
    void agregarPareoThrowsWhenBestOfIsEvenOrZero() {
        Clan c1 = clanWith("AAA");
        Clan c2 = clanWith("BBB");

        Date futureDate = new Date(System.currentTimeMillis() + 86400_000L);

        // bestOf = 2 (even)
        assertThatThrownBy(() -> service.agregarPareo("AAA", "BBB", 10L, 2, null, futureDate, false))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Best Of");

        // bestOf = 0
        assertThatThrownBy(() -> service.agregarPareo("AAA", "BBB", 10L, 0, null, futureDate, false))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Best Of");
    }

    // ========== Group D: inscribirClanTorneo ==========
    // inscribirClanTorneo calls FacesContext.getCurrentInstance() before the phase/duplicate check,
    // making it untestable without a servlet container. Deferred to Phase 2.

    // ========== Group E: standings ==========

    @Test
    void standingsAreOrderedByMatchesWonThenGamesWonThenTag() {
        // Three clans: AAA wins 2 matches, BBB wins 1, CCC wins 0.
        Clan clanA = clanWith("AAA");
        Clan clanB = clanWith("BBB");
        Clan clanC = clanWith("CCC");

        Torneo torneo = new Torneo();
        torneo.setId(99L);
        torneo.setNombre("StandingsTest");
        torneo.setClanesInscritos(Arrays.asList(clanA, clanB, clanC));

        // Build ronda with confirmed matches:
        // match1: AAA(sentinel) wins vs CCC(scourge) — BO1, WIN_SENTINEL
        // match2: AAA(sentinel) wins vs BBB(scourge) — BO1, WIN_SENTINEL
        // match3: BBB(sentinel) wins vs CCC(scourge) — BO1, WIN_SENTINEL

        GameMatch match1 = confirmedMatch(clanA, clanC, 1, Resultado.WIN_SENTINEL, clanA, clanC);
        GameMatch match2 = confirmedMatch(clanA, clanB, 1, Resultado.WIN_SENTINEL, clanA, clanB);
        GameMatch match3 = confirmedMatch(clanB, clanC, 1, Resultado.WIN_SENTINEL, clanB, clanC);

        Ronda ronda = new Ronda();
        ronda.setId(1L);
        ronda.setTorneo(torneo);
        ronda.setMatches(Arrays.asList(match1, match2, match3));

        torneo.setRondas(Arrays.asList(ronda));

        List<Standing> standings = torneo.getStandings();

        assertThat(standings).hasSize(3);
        // AAA: 2 wins, BBB: 1 win, CCC: 0 wins
        assertThat(standings.get(0).getTagClan()).isEqualTo("AAA");
        assertThat(standings.get(1).getTagClan()).isEqualTo("BBB");
        assertThat(standings.get(2).getTagClan()).isEqualTo("CCC");

        // Verify win counts
        assertThat(standings.get(0).getPartidosGanados()).isEqualTo(2);
        assertThat(standings.get(1).getPartidosGanados()).isEqualTo(1);
        assertThat(standings.get(2).getPartidosGanados()).isEqualTo(0);
    }

    /**
     * Build a confirmed BO-N GameMatch where clan1 is sentinel and clan2 is scourge.
     * Adds exactly one Game with the given resultado.
     */
    private GameMatch confirmedMatch(Clan clan1, Clan clan2, int bestOf, Resultado resultado,
                                     Clan sentinel, Clan scourge) {
        Game game = new Game();
        game.setSentinel(sentinel);
        game.setScourge(scourge);
        game.setResultado(resultado);

        GameMatch match = new GameMatch();
        match.setClan1(clan1);
        match.setClan2(clan2);
        match.setBestOf(bestOf);
        match.setResultadoConfirmado(true);
        match.setGames(new ArrayList<>(Arrays.asList(game)));
        return match;
    }

    // ========== Group F: confirmarMatch and finalizarTorneo ==========

    @Test
    void confirmarMatchAppliesEloDeltaPerGameForNonWoResults() throws Exception {
        Clan c1 = clanWith("AAA");
        Clan c2 = clanWith("BBB");
        c1.setElo(1000);
        c2.setElo(1000);

        // BO3, clan1 wins game1 (WIN_SENTINEL, c1=sentinel) and game2 (WIN_SENTINEL, c1=sentinel)
        Game game1 = new Game();
        game1.setSentinel(c1);
        game1.setScourge(c2);
        game1.setResultado(Resultado.WIN_SENTINEL);

        Game game2 = new Game();
        game2.setSentinel(c1);
        game2.setScourge(c2);
        game2.setResultado(Resultado.WIN_SENTINEL);

        Torneo torneo = torneoStarted(Arrays.asList(c1, c2));

        Ronda ronda = new Ronda();
        ronda.setId(5L);
        ronda.setTorneo(torneo);
        ronda.setMatches(new ArrayList<>());

        GameMatch match = new GameMatch();
        match.setId(20L);
        match.setClan1(c1);
        match.setClan2(c2);
        match.setBestOf(3);
        match.setResultadoConfirmado(false);
        match.setGames(new ArrayList<>(Arrays.asList(game1, game2)));
        match.setRonda(ronda);

        when(matchFac.find(20L)).thenReturn(match);

        // Confirm as ADMIN_DOTA (bypasses loser-must-confirm check)
        stubCallerAsAdminDota("admin");

        int eloC1Before = c1.getElo();
        int eloC2Before = c2.getElo();

        service.confirmarMatch(20L);

        assertThat(match.isResultadoConfirmado()).isTrue();

        // Winner (c1) should have gained ELO; loser (c2) should have lost ELO.
        // Production code applies ELO per-game in sequence, so ELO state at game2 differs from game1.
        // We assert direction and reciprocity rather than exact values.
        assertThat(c1.getElo()).isGreaterThan(eloC1Before);
        assertThat(c2.getElo()).isLessThan(eloC2Before);
        // Zero-sum: what winner gains equals what loser loses
        assertThat(c1.getElo() + c2.getElo()).isEqualTo(eloC1Before + eloC2Before);
    }

    @Test
    void finalizarTorneoSetsChampionAndTransitionsToFinished() throws Exception {
        Clan campeon = clanWith("WIN");
        Clan runner = clanWith("RUN");
        List<Clan> enrolled = Arrays.asList(campeon, runner);

        Torneo torneo = torneoStarted(enrolled);

        // One confirmed match in the ronda
        GameMatch match = new GameMatch();
        match.setResultadoConfirmado(true);

        Ronda ronda = new Ronda();
        ronda.setId(7L);
        ronda.setTorneo(torneo);
        ronda.setMatches(Arrays.asList(match));
        torneo.getRondas().add(ronda);

        when(torneoFac.find(1L)).thenReturn(torneo);
        when(clanFac.findByTag("WIN")).thenReturn(campeon);
        stubCallerAsAdminDota("admin");

        service.finalizarTorneo(1L, "WIN");

        assertThat(torneo.getFaseTorneo()).isEqualTo(FaseTorneo.FINISHED);
        assertThat(torneo.getClanCampeon()).isEqualTo(campeon);
    }
}
