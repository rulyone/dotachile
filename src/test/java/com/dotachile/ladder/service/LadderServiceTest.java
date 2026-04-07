package com.dotachile.ladder.service;

import com.dotachile.auth.entity.Usuario;
import com.dotachile.auth.facade.UsuarioFacade;
import com.dotachile.clanes.entity.Clan;
import com.dotachile.clanes.entity.ClanBan;
import com.dotachile.clanes.facade.ClanBanFacade;
import com.dotachile.clanes.facade.ClanFacade;
import com.dotachile.ladder.entity.Desafio;
import com.dotachile.ladder.entity.FaseLadder;
import com.dotachile.ladder.entity.Ladder;
import com.dotachile.ladder.facade.DesafioFacade;
import com.dotachile.ladder.facade.LadderFacade;
import com.dotachile.media.ReplayFacade;
import com.dotachile.shared.BusinessLogicException;
import com.dotachile.torneos.entity.FactorK;
import com.dotachile.torneos.facade.GameFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ejb.SessionContext;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LadderServiceTest {

    private LadderService service;
    private UsuarioFacade userFac;
    private ClanFacade clanFac;
    private DesafioFacade desafioFac;
    private LadderFacade ladderFac;
    private GameFacade gameFac;
    private ReplayFacade replayFac;
    private ClanBanFacade clanBanFac;
    private SessionContext ctx;

    @BeforeEach
    void setUp() throws Exception {
        service = new LadderService();
        userFac = mock(UsuarioFacade.class);
        clanFac = mock(ClanFacade.class);
        desafioFac = mock(DesafioFacade.class);
        ladderFac = mock(LadderFacade.class);
        gameFac = mock(GameFacade.class);
        replayFac = mock(ReplayFacade.class);
        clanBanFac = mock(ClanBanFacade.class);
        ctx = mock(SessionContext.class);

        inject(service, "userFac", userFac);
        inject(service, "clanFac", clanFac);
        inject(service, "desafioFac", desafioFac);
        inject(service, "ladderFac", ladderFac);
        inject(service, "gameFac", gameFac);
        inject(service, "replayFac", replayFac);
        inject(service, "clanBanFac", clanBanFac);
        inject(service, "ctx", ctx);
    }

    /** Reflection-set a private field on the service instance. */
    private static void inject(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    /** Returns any valid FactorK enum value. */
    private FactorK anyFactorK() {
        return FactorK.values()[0];
    }

    /**
     * Source of unique ids for synthetic Clans built in tests.
     *
     * Clan.equals() compares by id (a long that defaults to 0). If we let two
     * different Clans both keep id=0, production guards like the self-challenge
     * check (if (desafiador.equals(rival)) throw ...) fire spuriously because
     * the two synthetic clans look equal. Bumping a counter per built clan
     * keeps each test entity distinct.
     */
    private long nextClanId = 1L;

    /**
     * Build a Clan with a unique id, the given tag, a chieftain, and integranteCount
     * integrantes (the chieftain is always the first integrante).
     */
    private Clan clanWith(String tag, int integranteCount) {
        Clan clan = new Clan();
        clan.setId(nextClanId++);
        clan.setTag(tag);

        Usuario chief = new Usuario();
        chief.setUsername("chief_" + tag);
        chief.setClan(clan);
        clan.setChieftain(chief);

        List<Usuario> shamanes = new ArrayList<>();
        clan.setShamanes(shamanes);

        List<Usuario> integrantes = new ArrayList<>();
        integrantes.add(chief);
        for (int i = 1; i < integranteCount; i++) {
            Usuario u = new Usuario();
            u.setUsername("member_" + tag + "_" + i);
            u.setClan(clan);
            integrantes.add(u);
        }
        clan.setIntegrantes(integrantes);

        return clan;
    }

    /** Stub ctx so that the logged-in user is the given Usuario. */
    private void stubCallerAs(Usuario user) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getUsername());
        when(ctx.getCallerPrincipal()).thenReturn(principal);
        when(userFac.findByUsername(user.getUsername())).thenReturn(user);
    }

    // ========== crearLadderYComenzarlo ==========

    @Test
    void crearLadderYComenzarloThrowsWhenNombreIsNull() {
        when(ladderFac.count()).thenReturn(0);
        assertThatThrownBy(() -> service.crearLadderYComenzarlo(null, "info", anyFactorK()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("requerido");
    }

    @Test
    void crearLadderYComenzarloThrowsWhenALadderAlreadyExists() {
        when(ladderFac.count()).thenReturn(1);
        assertThatThrownBy(() -> service.crearLadderYComenzarlo("L1", "info", anyFactorK()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("solo se puede tener 1 ladder");
    }

    @Test
    void crearLadderYComenzarloPersistsLadderInStartedPhase() throws Exception {
        when(ladderFac.count()).thenReturn(0);

        service.crearLadderYComenzarlo("L1", "info", anyFactorK());

        ArgumentCaptor<Ladder> captor = ArgumentCaptor.forClass(Ladder.class);
        verify(ladderFac).create(captor.capture());
        Ladder created = captor.getValue();
        assertThat(created.getNombre()).isEqualTo("L1");
        assertThat(created.getInformacion()).isEqualTo("info");
        assertThat(created.getFaseLadder()).isEqualTo(FaseLadder.STARTED);
    }

    // ========== desafiarClan ==========

    @Test
    void desafiarClanThrowsWhenDesafiadorClanIsBanned() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 5);
        Clan clanRival = clanWith("RIVA", 5);

        Usuario chief = clanDesafiador.getChieftain();
        stubCallerAs(chief);

        when(clanFac.findByTag("RIVA")).thenReturn(clanRival);
        when(clanBanFac.findByTag("DESA")).thenReturn(new ClanBan());
        when(clanBanFac.findByTag("RIVA")).thenReturn(null);

        assertThatThrownBy(() -> service.desafiarClan("RIVA"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("baneado");

        verify(desafioFac, never()).create(any(Desafio.class));
    }

    @Test
    void desafiarClanThrowsWhenRivalClanIsBanned() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 5);
        Clan clanRival = clanWith("RIVA", 5);

        Usuario chief = clanDesafiador.getChieftain();
        stubCallerAs(chief);

        when(clanFac.findByTag("RIVA")).thenReturn(clanRival);
        when(clanBanFac.findByTag("DESA")).thenReturn(null);
        when(clanBanFac.findByTag("RIVA")).thenReturn(new ClanBan());

        assertThatThrownBy(() -> service.desafiarClan("RIVA"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("baneado");

        verify(desafioFac, never()).create(any(Desafio.class));
    }

    @Test
    void desafiarClanThrowsWhenDesafiadorIsNotChieftainOrShaman() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 5);
        Clan clanRival = clanWith("RIVA", 5);

        // Create a peon — not chieftain, not shaman
        Usuario peon = new Usuario();
        peon.setUsername("peon_desa");
        peon.setClan(clanDesafiador);
        clanDesafiador.getIntegrantes().add(peon);

        stubCallerAs(peon);

        when(clanFac.findByTag("RIVA")).thenReturn(clanRival);

        assertThatThrownBy(() -> service.desafiarClan("RIVA"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageMatching(".*chieftain.*|.*shaman.*");
    }

    @Test
    void desafiarClanThrowsWhenDesafiadorHasFewerThanFiveIntegrantes() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 4);
        Clan clanRival = clanWith("RIVA", 5);

        Usuario chief = clanDesafiador.getChieftain();
        stubCallerAs(chief);

        when(clanFac.findByTag("RIVA")).thenReturn(clanRival);
        when(clanBanFac.findByTag("DESA")).thenReturn(null);
        when(clanBanFac.findByTag("RIVA")).thenReturn(null);
        when(desafioFac.desafiosPendientes("DESA")).thenReturn(Collections.emptyList());
        when(desafioFac.desafiosPendientes("RIVA")).thenReturn(Collections.emptyList());
        when(ladderFac.find(1L)).thenReturn(new Ladder());

        assertThatThrownBy(() -> service.desafiarClan("RIVA"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("5");
    }

    @Test
    void desafiarClanThrowsWhenRivalHasFewerThanFiveIntegrantes() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 5);
        Clan clanRival = clanWith("RIVA", 4);

        Usuario chief = clanDesafiador.getChieftain();
        stubCallerAs(chief);

        when(clanFac.findByTag("RIVA")).thenReturn(clanRival);
        when(clanBanFac.findByTag("DESA")).thenReturn(null);
        when(clanBanFac.findByTag("RIVA")).thenReturn(null);
        when(desafioFac.desafiosPendientes("DESA")).thenReturn(Collections.emptyList());
        when(desafioFac.desafiosPendientes("RIVA")).thenReturn(Collections.emptyList());
        when(ladderFac.find(1L)).thenReturn(new Ladder());

        assertThatThrownBy(() -> service.desafiarClan("RIVA"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("5");
    }

    @Test
    void desafiarClanThrowsWhenDesafiadorAlreadyHasTenPendingChallenges() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 5);
        Clan clanRival = clanWith("RIVA", 5);

        Usuario chief = clanDesafiador.getChieftain();
        stubCallerAs(chief);

        when(clanFac.findByTag("RIVA")).thenReturn(clanRival);
        when(clanBanFac.findByTag("DESA")).thenReturn(null);
        when(clanBanFac.findByTag("RIVA")).thenReturn(null);

        List<Desafio> tenPending = new ArrayList<>();
        for (int i = 0; i < 10; i++) tenPending.add(new Desafio());
        when(desafioFac.desafiosPendientes("DESA")).thenReturn(tenPending);
        when(desafioFac.desafiosPendientes("RIVA")).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.desafiarClan("RIVA"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("10");
    }

    @Test
    void desafiarClanThrowsWhenRivalAlreadyHasTenPendingChallenges() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 5);
        Clan clanRival = clanWith("RIVA", 5);

        Usuario chief = clanDesafiador.getChieftain();
        stubCallerAs(chief);

        when(clanFac.findByTag("RIVA")).thenReturn(clanRival);
        when(clanBanFac.findByTag("DESA")).thenReturn(null);
        when(clanBanFac.findByTag("RIVA")).thenReturn(null);

        List<Desafio> tenPending = new ArrayList<>();
        for (int i = 0; i < 10; i++) tenPending.add(new Desafio());
        when(desafioFac.desafiosPendientes("DESA")).thenReturn(Collections.emptyList());
        when(desafioFac.desafiosPendientes("RIVA")).thenReturn(tenPending);

        assertThatThrownBy(() -> service.desafiarClan("RIVA"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("10");
    }

    @Test
    void desafiarClanPersistsDesafioOnHappyPath() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 5);
        Clan clanRival = clanWith("RIVA", 5);

        Ladder ladder = new Ladder();
        ladder.setDesafios(new ArrayList<>());

        Usuario chief = clanDesafiador.getChieftain();
        stubCallerAs(chief);

        when(clanFac.findByTag("RIVA")).thenReturn(clanRival);
        when(clanBanFac.findByTag("DESA")).thenReturn(null);
        when(clanBanFac.findByTag("RIVA")).thenReturn(null);
        when(desafioFac.desafiosPendientes("DESA")).thenReturn(Collections.emptyList());
        when(desafioFac.desafiosPendientes("RIVA")).thenReturn(Collections.emptyList());
        when(ladderFac.find(1L)).thenReturn(ladder);

        service.desafiarClan("RIVA");

        ArgumentCaptor<Desafio> captor = ArgumentCaptor.forClass(Desafio.class);
        verify(desafioFac).create(captor.capture());
        Desafio created = captor.getValue();
        assertThat(created.getDesafiador()).isEqualTo(clanDesafiador);
        assertThat(created.getRival()).isEqualTo(clanRival);
        assertThat(created.isDesafioAceptado()).isFalse();
        assertThat(created.isResultadoConfirmado()).isFalse();
    }

    // ========== aceptarDesafio ==========

    @Test
    void aceptarDesafioThrowsWhenCallerIsNotRivalChieftainOrShaman() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 5);
        Clan clanRival = clanWith("RIVA", 5);

        // A peon of the rival clan
        Usuario peon = new Usuario();
        peon.setUsername("peon_rival");
        peon.setClan(clanRival);
        clanRival.getIntegrantes().add(peon);

        stubCallerAs(peon);

        Desafio desafio = new Desafio();
        desafio.setDesafiador(clanDesafiador);
        desafio.setRival(clanRival);
        desafio.setDesafioAceptado(false);
        desafio.setResultadoConfirmado(false);

        when(desafioFac.find(42L)).thenReturn(desafio);

        assertThatThrownBy(() -> service.aceptarDesafio(42L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageMatching(".*chieftain.*|.*shaman.*");
    }

    @Test
    void aceptarDesafioFlipsAceptadoFlagOnHappyPath() throws Exception {
        Clan clanDesafiador = clanWith("DESA", 5);
        Clan clanRival = clanWith("RIVA", 5);

        Usuario chief = clanRival.getChieftain();
        stubCallerAs(chief);

        Desafio desafio = new Desafio();
        desafio.setDesafiador(clanDesafiador);
        desafio.setRival(clanRival);
        desafio.setDesafioAceptado(false);
        desafio.setResultadoConfirmado(false);

        when(desafioFac.find(10L)).thenReturn(desafio);
        when(clanBanFac.findByTag("DESA")).thenReturn(null);
        when(clanBanFac.findByTag("RIVA")).thenReturn(null);

        service.aceptarDesafio(10L);

        ArgumentCaptor<Desafio> captor = ArgumentCaptor.forClass(Desafio.class);
        verify(desafioFac).edit(captor.capture());
        assertThat(captor.getValue().isDesafioAceptado()).isTrue();
    }

    // ========== pausarLadder / despausarLadder ==========

    @Test
    void pausarLadderTransitionsToPaused() throws Exception {
        Ladder ladder = new Ladder();
        ladder.setId(1L);
        ladder.setFaseLadder(FaseLadder.STARTED);

        when(ladderFac.find(1L)).thenReturn(ladder);

        service.pausarLadder(1L);

        ArgumentCaptor<Ladder> captor = ArgumentCaptor.forClass(Ladder.class);
        verify(ladderFac).edit(captor.capture());
        assertThat(captor.getValue().getFaseLadder()).isEqualTo(FaseLadder.PAUSED);
    }

    @Test
    void despausarLadderTransitionsToStarted() throws Exception {
        Ladder ladder = new Ladder();
        ladder.setId(1L);
        ladder.setFaseLadder(FaseLadder.PAUSED);

        when(ladderFac.find(1L)).thenReturn(ladder);

        service.despausarLadder(1L);

        ArgumentCaptor<Ladder> captor = ArgumentCaptor.forClass(Ladder.class);
        verify(ladderFac).edit(captor.capture());
        assertThat(captor.getValue().getFaseLadder()).isEqualTo(FaseLadder.STARTED);
    }
}
