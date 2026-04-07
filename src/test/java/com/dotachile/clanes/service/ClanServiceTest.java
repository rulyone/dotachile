package com.dotachile.clanes.service;

import com.dotachile.auth.entity.Usuario;
import com.dotachile.auth.facade.UsuarioFacade;
import com.dotachile.clanes.entity.Clan;
import com.dotachile.clanes.entity.ClanBan;
import com.dotachile.clanes.facade.ClanBanFacade;
import com.dotachile.clanes.facade.ClanFacade;
import com.dotachile.media.Movimiento;
import com.dotachile.media.MovimientoFacade;
import com.dotachile.media.TipoMovimiento;
import com.dotachile.shared.BusinessLogicException;
import com.dotachile.torneos.entity.FaseTorneo;
import com.dotachile.torneos.entity.Torneo;
import com.dotachile.torneos.facade.ModificacionFacade;
import com.dotachile.torneos.facade.TemporadaModificacionFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ejb.SessionContext;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClanServiceTest {

    private ClanService service;
    private ClanFacade clanFac;
    private UsuarioFacade userFac;
    private TemporadaModificacionFacade tempFac;
    private ModificacionFacade modFac;
    private ClanBanFacade clanBanFac;
    private MovimientoFacade movFac;
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
        service = new ClanService();
        clanFac = mock(ClanFacade.class);
        userFac = mock(UsuarioFacade.class);
        tempFac = mock(TemporadaModificacionFacade.class);
        modFac = mock(ModificacionFacade.class);
        clanBanFac = mock(ClanBanFacade.class);
        movFac = mock(MovimientoFacade.class);
        ctx = mock(SessionContext.class);

        nextClanId = 1L;

        inject("clanFac", clanFac);
        inject("userFac", userFac);
        inject("tempFac", tempFac);
        inject("modFac", modFac);
        // confirmacionFac is only used by confirmar(), which is not a target method
        inject("clanBanFac", clanBanFac);
        inject("movFac", movFac);
        inject("ctx", ctx);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field f = ClanService.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(service, value);
    }

    /** Build a minimal Usuario with the given username and no clan. */
    private Usuario usuarioSinClan(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setInvitacionesDeClan(new ArrayList<>());
        u.setMovimientos(new ArrayList<>());
        return u;
    }

    /** Build a Clan with a unique id and the given tag, with the user as chieftain. */
    private Clan clanConChieftain(String tag, Usuario chieftain) {
        Clan clan = new Clan();
        clan.setId(nextClanId++);
        clan.setTag(tag);
        clan.setNombre("Nombre_" + tag);
        clan.setChieftain(chieftain);
        List<Usuario> integrantes = new ArrayList<>();
        integrantes.add(chieftain);
        clan.setIntegrantes(integrantes);
        clan.setShamanes(new ArrayList<>());
        clan.setGrunts(new ArrayList<>());
        clan.setPeones(new ArrayList<>());
        clan.setInvitaciones(new ArrayList<>());
        clan.setMovimientos(new ArrayList<>());
        clan.setTorneos(new ArrayList<>());
        return clan;
    }

    /** Stub ctx so the logged-in principal resolves to the given Usuario. */
    private void stubCallerAs(Usuario user) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getUsername());
        when(ctx.getCallerPrincipal()).thenReturn(principal);
        when(userFac.findByUsername(user.getUsername())).thenReturn(user);
    }

    // ========== crearClan ==========

    @Test
    void crearClanThrowsWhenUserAlreadyHasAClan() {
        Usuario user = usuarioSinClan("jugador1");
        Clan existingClan = new Clan();
        existingClan.setId(nextClanId++);
        user.setClan(existingClan);
        stubCallerAs(user);

        when(clanFac.findByNombre("NuevoClan")).thenReturn(null);
        when(clanFac.findByTag("NCLN")).thenReturn(null);

        assertThatThrownBy(() -> service.crearClan("NuevoClan", "NCLN"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("ningun clan");

        verify(clanFac, never()).create(any(Clan.class));
    }

    @Test
    void crearClanPersistsClanWithInitialEloOneThousand() throws Exception {
        Usuario user = usuarioSinClan("jugador1");
        stubCallerAs(user);

        when(clanFac.findByNombre("AlphaClan")).thenReturn(null);
        when(clanFac.findByTag("ALPH")).thenReturn(null);

        service.crearClan("AlphaClan", "ALPH");

        ArgumentCaptor<Clan> captor = ArgumentCaptor.forClass(Clan.class);
        verify(clanFac).create(captor.capture());
        assertThat(captor.getValue().getElo()).isEqualTo(1000);
        assertThat(captor.getValue().getNombre()).isEqualTo("AlphaClan");
        assertThat(captor.getValue().getTag()).isEqualTo("ALPH");
    }

    @Test
    void crearClanWritesMovimientoCreoClan() throws Exception {
        Usuario user = usuarioSinClan("jugador1");
        stubCallerAs(user);

        when(clanFac.findByNombre("BetaClan")).thenReturn(null);
        when(clanFac.findByTag("BETA")).thenReturn(null);

        service.crearClan("BetaClan", "BETA");

        ArgumentCaptor<Movimiento> captor = ArgumentCaptor.forClass(Movimiento.class);
        verify(movFac).create(captor.capture());
        assertThat(captor.getValue().getTipoMovimiento()).isEqualTo(TipoMovimiento.CREO_CLAN);
        assertThat(captor.getValue().getUsuario()).isEqualTo(user);
    }

    // ========== revivirClan ==========

    @Test
    void revivirClanThrowsWhenUserIsNotLastChieftain() {
        // chieftain is someone else
        Usuario chieftain = usuarioSinClan("chieftain1");
        Clan clan = clanConChieftain("RVVR", chieftain);
        clan.setIntegrantes(new ArrayList<>()); // disbanded — no members

        Usuario otro = usuarioSinClan("otro1");
        stubCallerAs(otro);

        when(clanFac.findByTag("RVVR")).thenReturn(clan);

        assertThatThrownBy(() -> service.revivirClan("RVVR"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("chieftain");
    }

    @Test
    void revivirClanThrowsWhenUserAlreadyHasAClan() {
        Usuario chieftain = usuarioSinClan("chief2");
        Clan clan = clanConChieftain("RVVR", chieftain);
        clan.setIntegrantes(new ArrayList<>()); // disbanded

        // chieftain tries to revive but already joined another clan
        Clan otroClan = new Clan();
        otroClan.setId(nextClanId++);
        chieftain.setClan(otroClan);
        stubCallerAs(chieftain);

        when(clanFac.findByTag("RVVR")).thenReturn(clan);

        assertThatThrownBy(() -> service.revivirClan("RVVR"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("ningun clan");
    }

    // ========== invitarPlayer ==========

    @Test
    void invitarPlayerThrowsWhenInviterIsNotChieftainOrShaman() {
        Usuario peon = usuarioSinClan("peon1");
        Clan clan = clanConChieftain("ALPH", usuarioSinClan("chief3"));
        // peon belongs to clan but is neither chieftain nor shaman
        peon.setClan(clan);
        clan.getIntegrantes().add(peon);

        stubCallerAs(peon);

        assertThatThrownBy(() -> service.invitarPlayer("candidato1"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("chieftain o shaman");

        verify(clanFac, never()).edit(any(Clan.class));
    }

    @Test
    void invitarPlayerThrowsWhenInviterClanIsBanned() {
        Usuario chief = usuarioSinClan("chief4");
        Clan clan = clanConChieftain("BAND", chief);
        chief.setClan(clan);

        stubCallerAs(chief);

        ClanBan ban = new ClanBan();
        ban.setRazon("Incumplimiento de normas");
        when(clanBanFac.findByTag("BAND")).thenReturn(ban);

        assertThatThrownBy(() -> service.invitarPlayer("candidato2"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("baneado");

        verify(clanFac, never()).edit(any(Clan.class));
    }

    // ========== kickearPlayer ==========

    @Test
    void kickearPlayerAllowsShamanToKickPeon() throws Exception {
        Usuario chief = usuarioSinClan("chief5");
        Clan clan = clanConChieftain("KICK", chief);
        chief.setClan(clan);

        Usuario shaman = usuarioSinClan("shaman1");
        shaman.setClan(clan);
        clan.getIntegrantes().add(shaman);
        clan.getShamanes().add(shaman);

        Usuario peon = usuarioSinClan("peon2");
        peon.setClan(clan);
        clan.getIntegrantes().add(peon);
        clan.getPeones().add(peon);

        stubCallerAs(shaman);
        when(userFac.findByUsername("peon2")).thenReturn(peon);
        when(clanBanFac.findByTag("KICK")).thenReturn(null);

        service.kickearPlayer("peon2");

        assertThat(clan.getIntegrantes()).doesNotContain(peon);
        assertThat(peon.getClan()).isNull();
    }

    @Test
    void kickearPlayerForbidsShamanFromKickingChieftain() {
        Usuario chief = usuarioSinClan("chief6");
        Clan clan = clanConChieftain("KICK", chief);
        chief.setClan(clan);

        Usuario shaman = usuarioSinClan("shaman2");
        shaman.setClan(clan);
        clan.getIntegrantes().add(shaman);
        clan.getShamanes().add(shaman);

        stubCallerAs(shaman);
        when(userFac.findByUsername("chief6")).thenReturn(chief);
        when(clanBanFac.findByTag("KICK")).thenReturn(null);

        assertThatThrownBy(() -> service.kickearPlayer("chief6"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("mayor rango");
    }

    // ========== desarmarClan ==========

    @Test
    void desarmarClanThrowsWhenClanInRegistrationTournament() {
        Usuario chief = usuarioSinClan("chief7");
        Clan clan = clanConChieftain("DSMR", chief);
        chief.setClan(clan);
        // clan has only the chieftain — size check passes

        Torneo torneo = new Torneo();
        torneo.setNombre("Copa Apertura");
        torneo.setId(1L);
        torneo.setFaseTorneo(FaseTorneo.REGISTRATION);
        clan.getTorneos().add(torneo);

        stubCallerAs(chief);
        when(clanBanFac.findByTag("DSMR")).thenReturn(null);

        assertThatThrownBy(() -> service.desarmarClan())
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("desinscribelo");
    }
}
