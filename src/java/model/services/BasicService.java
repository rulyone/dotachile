/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.services;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import javax.sql.DataSource;
import model.entities.base.Perfil;
import model.entities.base.PreRegistro;
import model.entities.base.Usuario;
import model.entities.base.facades.PerfilFacade;
import model.entities.base.facades.PreRegistroFacade;
import model.entities.base.facades.UsuarioFacade;
//import model.entities.inhouse.PlayerIH;
//import model.entities.inhouse.facades.PlayerIHFacade;
import model.exceptions.BusinessLogicException;
import utils.PvpgnHash;
import utils.Util;

/**
 *
 * @author Pablo
 */
@Stateless
public class BasicService {
    
    @Resource(name = "pvpgnDatasourceRefName")
    private DataSource pvpgnDatasource;

    @Resource SessionContext ctx;

    //@Resource(name = "mail/mySession")
    @Resource(name="mail/dotachileSession")
    private Session mailmySession;

    @EJB private UsuarioFacade userFac;
    @EJB private PerfilFacade perfilFac;
    @EJB private PreRegistroFacade prFac;
    //@EJB private PlayerIHFacade playerFac;
    
    public void register(String username, String password, String email) throws BusinessLogicException {
        if(username == null || password == null || email == null)
            throw new BusinessLogicException("Valor requerido.");

        if(userFac.findByUsername(username) != null)
            throw new BusinessLogicException("Username ya está en uso.");

        if(prFac.findByUsername(username) != null)
            throw new BusinessLogicException("Username ya está en uso, sólo falta activar la cuenta (revisar mail).");

        if(userFac.findByEmail(email) != null)
            throw new BusinessLogicException("Email ya está en uso.");
        
        if(prFac.findByEmail(email) != null)
            throw new BusinessLogicException("Email ya está en uso, sólo falta activar la cuenta (revisar mail).");

        if(password.length() < 6)
            throw new BusinessLogicException("Password debe contener al menos 6 caracteres.");

        if(!email.toLowerCase().endsWith("@live.cl") && !email.toLowerCase().endsWith("@gmail.com") && !email.toLowerCase().endsWith("@hotmail.com") && !email.toLowerCase().endsWith("@hotmail.cl") && !email.toLowerCase().endsWith("@yahoo.es") && !email.toLowerCase().endsWith("@yahoo.com")) {
            throw new BusinessLogicException("Por el momento sólo se aceptan correos terminados en '@gmail.com', '@hotmail.com', '@hotmail.cl', '@yahoo.com', '@yahoo.es' y '@live.cl'.");
        }

        if(username.contains("&"))
            throw new BusinessLogicException("El carácter '&' está prohibido en los nicks.");

        PreRegistro pr = new PreRegistro();
        UUID code = UUID.randomUUID();
        pr.setCodigoActivacion(code.toString());
        pr.setEmail(email);
        pr.setFechaRegistro(new Date());
        pr.setUsername(username);
        pr.setPassword(Util.hashPassword(password, username));

        String subject = "Cuenta www.dotachile.com";
        String body = "Para activar tu cuenta, sólo debes copiar la siguiente dirección en tu navegador: \n\n";
        body += "www.dotachile.com/DotaCL/web/registro/ActivarCuenta.jsf?";
        body += "username=" + username + "&codigo=" + pr.getCodigoActivacion();
        
        body += "\n\nBienvenido a http://www.dotachile.com";

        try {
            this.sendMail(email, subject, body);
        } catch (NamingException ex) {
            Logger.getLogger(BasicService.class.getName()).log(Level.SEVERE, null, ex);
            throw new BusinessLogicException("No se pudo enviar el mail, asegurate que está bien escrito.");
        } catch (MessagingException ex) {
            Logger.getLogger(BasicService.class.getName()).log(Level.SEVERE, null, ex);
            throw new BusinessLogicException("No se pudo enviar el mail, asegurate que está bien escrito.");
        }

        prFac.create(pr);

    }

    public void activarCuenta(String username, String codigoActivacion) throws BusinessLogicException {
        if(username == null || codigoActivacion == null)
            throw new BusinessLogicException("Valor requerido.");

        if(userFac.findByUsername(username) != null) {
            throw new BusinessLogicException("La cuenta ya fue activada...");
        }

        PreRegistro pr = prFac.findByUsername(username);
        if(pr == null)
            throw new BusinessLogicException("Usuario incorrecto, o ya está activada la cuenta.");

        if(!pr.getCodigoActivacion().equals(codigoActivacion))
            throw new BusinessLogicException("Codigo de activacion incorrecto.");

        Usuario user = new Usuario();
        user.setEmail(pr.getEmail());
        user.setFechaRegistro(pr.getFechaRegistro());
        user.setLastLogin(new Date());
        user.setPassword(pr.getPassword());
        user.setUsername(pr.getUsername());

        userFac.create(user);

        Perfil perfil = new Perfil();
        perfil.setUsuario(user);

        perfilFac.create(perfil);
        
//        PlayerIH player = new PlayerIH();
//        player.setRoot(false);
//        player.setStaff(false);
//        player.setVoucher(false);
//        player.setUsuario(user);
//        
//        playerFac.create(player);
        
        user.setPerfil(perfil);
        //user.setPlayerIH(player);
        userFac.edit(user);

        prFac.remove(pr);
    }

    public void cambiarPassword(String username, String oldpassword, String newpassword) throws BusinessLogicException {
        if(username == null || oldpassword == null || newpassword == null)
            throw new BusinessLogicException("Valor requerido.");

        Usuario user = userFac.findByUsername(username);
        if(user == null)
            throw new BusinessLogicException("Nombre de usuario incorrecto.");
        if(newpassword.length() < 6)
            throw new BusinessLogicException("Password debe contener al menos 6 caracteres.");

        String oldhash = Util.hashPassword(oldpassword, user.getUsername());
        String newhash = Util.hashPassword(newpassword, user.getUsername());

        if(!oldhash.equals(user.getPassword()))
            throw new BusinessLogicException("Password incorrecto.");

        user.setPassword(newhash);

        userFac.edit(user);
    }

    public void cambiarEmail(String oldmail, String newmail, String password) throws BusinessLogicException {
        if(oldmail == null || newmail == null || password == null)
            throw new BusinessLogicException("Valor requerido.");

        Usuario user = userFac.findByEmail(oldmail);
        if(user == null)
            throw new BusinessLogicException("Email incorrecto.");

        String passhash = Util.hashPassword(password, user.getUsername());
        if(!passhash.equals(user.getPassword()))
            throw new BusinessLogicException("Password incorrecto.");

        user.setEmail(newmail);
        userFac.edit(user);
    }

    public void modificarNickW3(String username, String nickw3) throws BusinessLogicException {

        if(username == null || nickw3 == null)
            throw new BusinessLogicException("Valor requerido.");
        Perfil perfil = perfilFac.findByUsername(username);
        if(perfil == null)
            throw new BusinessLogicException("Usuario no encontrado.");

        Principal principal = ctx.getCallerPrincipal();
        if(principal == null)
            throw new BusinessLogicException("Debes estar logeado para modificar tu nick.");
        if(!principal.getName().equalsIgnoreCase(perfil.getUsuario().getUsername()))
            throw new BusinessLogicException("No puedes modificar el nick de w3 de otro usuario.");
        //TODO: Quizas permitir a los admins cambiar el nickw3.

        if(perfil.getNickw3() != null) {
            if(!perfil.getNickw3().equals(""))
                throw new BusinessLogicException("Nick de w3 ya fue creado, imposible actualizar.");
        }
        if(nickw3.length() > 30)
            throw new BusinessLogicException("Máximo 30 caracteres.");

        perfil.setNickw3(nickw3);
        perfilFac.edit(perfil);

    }

    public void resetPassword(String username, String email) throws BusinessLogicException {
        if(email == null)
            throw new BusinessLogicException("Valor requerido.");

        Usuario user = userFac.findByEmail(email);
        if(user == null)
            throw new BusinessLogicException("El email no está linkeado a ninguna de las cuentas registradas.");

        if(!user.getUsername().equalsIgnoreCase(username))
            throw new BusinessLogicException("El email y el username no coinciden.");

        //todo ok, sabia el username y el email de registro, le reseteamos la password y se la enviamos por email
        UUID code = UUID.randomUUID();
        String newPassword = code.toString();
        String hashedPassword = Util.hashPassword(newPassword, user.getUsername());
        user.setPassword(hashedPassword);

        //ENVIAMOS EMAIL CON NUEVO PASSWORD
        String subject = "RESET DE PASSWORD EN DOTACHILE.COM";
        String body = "SOLICITUD DE RESET DE PASSWORD EN DOTACHILE.COM: \n\n";
        body += "\tusername: " + user.getUsername() + "\n";
        body += "\tpassword: " + newPassword + "\n\n";
        body += "Debes logearte con estos datos en www.dotachile.com y una vez logeado, ";
        body += "clickeas en tu nick (al lado de 'Bienvenido' arriba a la izquierda) y ";
        body += "elegir la opción 'CAMBIAR PASSWORD' por la que desees.\n\n";
        body += "Saludos.";

        try {
            this.sendMail(email, subject, body);
        } catch (NamingException ex) {
            Logger.getLogger(BasicService.class.getName()).log(Level.SEVERE, null, ex);
            throw new BusinessLogicException("No se pudo enviar el mail, asegurate que está bien escrito.");
        } catch (MessagingException ex) {
            Logger.getLogger(BasicService.class.getName()).log(Level.SEVERE, null, ex);
            throw new BusinessLogicException("No se pudo enviar el mail, asegurate que está bien escrito.");
        }
        userFac.edit(user);
    }

    private void sendMail(String email, String subject, String body) throws NamingException, MessagingException {
        MimeMessage message = new MimeMessage(mailmySession);
        message.setSubject(subject);
        message.addRecipient(RecipientType.TO, new InternetAddress(email));
        try {
            message.setFrom(new InternetAddress("no-responder@dotachile.com", "Dotachile.com"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BasicService.class.getName()).log(Level.SEVERE, null, ex);
        }
        message.setText(body);
        Transport.send(message);
    }

    /**
     * Pasos a seguir para setear un NICK de W3 en pvpgn...
     * 
     * Existen 3 casos que pueden darse actualmente:
     * 
     * 1° NICK_W3 == NULL && UID == NULL
     * -En este caso, el usuario debe especificar un NICK_W3, 
     * revisar que este NICK_W3 no exista en la tabla 'perfil' por otro usuario, 
     * y luego revisar (sanity check) que no exista en la tabla BNET.
     * 
     * 2° NICK_W3 != NULL && UID == NULL
     * -En este caso, el usuario ya tenía un NICK_W3 anteriormente, por ende
     * simplemente se le crea la cuenta en pvpgn y además se le setea UID.
     * 
     * 3° NICK_W3 != NULL && UID != NULL
     * -En este caso, el NICK_W3 ya está satisfactoriamente seteado. No hay más que hacer.
     * 
     * En el 1° y 2° caso, la password debe ser seteada por el usuario, si bien 
     * no se guardará la password en la DB, si se guardará en el servidor pvpgn con la función
     * de hash que utiliza bnet.
     * 
     * Sólo porsiacaso, se debe revisar que el NICK_W3 no contenga caracteres extraños, y
     * debe satisfacer el largo minimo y maximo permitido por pvpgn...
     */
    public void crearCuentaW3(String w3username, String w3password) throws BusinessLogicException {
        //primero obtenemos al usuario que invoca este método...
        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        
        //w3username debe ser entre 3 y 15 caracteres, permitir letras y numeros y solo los sgtes
        //signos 'extraños': -_[]^. (regex: ^[a-z0-9_-\[\]\^\.]{3,15}$ )
        if (w3username.length() < 3 || w3username.length() > 15) {
            throw new BusinessLogicException("El largo del NICK W3 debe ser entre 3 y 15 caracteres.");
        }
        if (!Util.caracteresValidosPvpgn(w3username)) {
            throw new BusinessLogicException("El NICK W3 sólo permite letras, números, _ , - , [ , ] , ^ y . (punto).");
        }      
        //password entre 4 y 32 caracteres... todo permitido.
        if (w3password.length() < 4 || w3password.length() > 32) {
            throw new BusinessLogicException("Password debe contener entre 4 y 32 caracteres.");
        }
                
        String oldw3username = user.getPerfil().getNickw3();
        if (oldw3username == null) {
            //1° caso, nick_w3 null y uid se asume null también...
            //checkeamos que el nuevo w3username no esté ocupado en la table perfil
            Perfil perfil = perfilFac.findByNickW3(w3username);
            if (perfil != null) {
                throw new BusinessLogicException("El NICK W3 ya está en uso.");
            }
            //checkeamos que no lo esté usando un botw3
            perfil = perfilFac.findByBotW3(w3username);
            if (perfil != null) {
                throw new BusinessLogicException("El NICK W3 ya está en uso (por un bot).");
            }
            //ahora checkeamos en la tabla pvpgn... sanity check
            boolean existe = checkPvpgnUsername(w3username);
            if (existe) {
                throw new BusinessLogicException("Ya existe ese NICK W3 en la DB de PVPGN.");
            }            
            //tamos ready!
            Integer uid = crearCuentaW3Bnet(w3username, w3password);
            perfil = user.getPerfil();
            perfil.setUid(uid);
            perfil.setNickw3(w3username);
            perfilFac.edit(perfil);
        }else if(oldw3username != null && user.getPerfil().getUid() == null) {
            //2° caso
            /* 2° NICK_W3 != NULL && UID == NULL
             * -En este caso, el usuario ya tenía un NICK_W3 anteriormente, por ende
             * simplemente se le crea la cuenta en pvpgn y además se le setea UID.
             */
            //checkeamos que el nuevo w3username no esté ocupado en la table perfil (a menos q sea el mio)
            Perfil perfil = perfilFac.findByNickW3(w3username);
            if (perfil != null && !perfil.getNickw3().equals(oldw3username)) {
                throw new BusinessLogicException("El NICK W3 ya está en uso.");
            }
            //checkeamos que no lo esté usando un botw3
            perfil = perfilFac.findByBotW3(w3username);
            if (perfil != null) {
                throw new BusinessLogicException("El NICK W3 ya está en uso (por un bot).");
            }
            //ahora checkeamos en la tabla pvpgn... sanity check
            boolean existe = checkPvpgnUsername(w3username);
            if (existe) {
                throw new BusinessLogicException("Ya existe ese NICK W3 en la DB de PVPGN.");
            }       
            Integer uid = crearCuentaW3Bnet(w3username, w3password);
            perfil = user.getPerfil();
            perfil.setUid(uid);
            perfil.setNickw3(w3username);
            perfilFac.edit(perfil);
        }else if(oldw3username != null && user.getPerfil().getUid() != null) {
            //3° caso, mostrar mensaje de error
            throw new BusinessLogicException("Ya tienes creada tu cuenta en el server (" + user.getPerfil().getNickw3() + "). No hay vuelta atrás, si crees que es un error contacta un admin (aunque lo más seguro que no sea un error ;).");
        }else{
            throw new BusinessLogicException("Error de sistema al intentar setear nick de w3, porfavor contacta un admin.");
        }
    }
    
    private Integer crearCuentaW3Bnet(String w3username,String w3password) throws BusinessLogicException {
        
        Integer uid = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "INSERT INTO BNET (acct_username, username, acct_passhash1) VALUES (?, ?, ?)";
        try {
            conn = this.pvpgnDatasource.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, w3username);
            ps.setString(2, w3username.toLowerCase());
            ps.setString(3, PvpgnHash.GetHash(w3password));
            ps.executeUpdate();
            
            query = "SELECT LAST_INSERT_ID()";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                uid = rs.getInt(1);
                query = "UPDATE BNET SET acct_userid = ? WHERE uid = ?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, uid);
                ps.setInt(2, uid);
                ps.executeUpdate();
            }
            
        } catch (SQLException ex) {
            throw new EJBException(ex);
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException ex) {} }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { } }
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { } }
        }
        return uid;
    }
    
    public void crearCuentaW3BOT(String w3username, String w3password) throws BusinessLogicException {
        //primero obtenemos al usuario que invoca este método...
        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        
        //w3username debe ser entre 3 y 15 caracteres, permitir letras y numeros y solo los sgtes
        //signos 'extraños': -_[]^. (regex: ^[a-z0-9_-\[\]\^\.]{3,15}$ )
        if (w3username.length() < 3 || w3username.length() > 15) {
            throw new BusinessLogicException("El largo del NICK W3 debe ser entre 3 y 15 caracteres.");
        }
        if (!Util.caracteresValidosPvpgn(w3username)) {
            throw new BusinessLogicException("El NICK W3 sólo permite letras, números, _ , - , [ , ] , ^ y . (punto).");
        }      
        //password entre 4 y 32 caracteres... todo permitido.
        if (w3password.length() < 4 || w3password.length() > 32) {
            throw new BusinessLogicException("Password debe contener entre 4 y 32 caracteres.");
        }
        
        //checkeamos que el w3username no esté usado enla tabla perfil
        Perfil perfil = perfilFac.findByNickW3(w3username);
        if (perfil != null) {
            throw new BusinessLogicException("El NICK W3 ya está en uso.");
        }
        //checkeamos que no lo esté usando un botw3
        perfil = perfilFac.findByBotW3(w3username);
        if (perfil != null) {
            throw new BusinessLogicException("El NICK W3 ya está en uso (por un bot).");
        }
        //ahora checkeamos en la tabla pvpgn... sanity check
        boolean existe = checkPvpgnUsername(w3username);
        if (existe) {
            throw new BusinessLogicException("Ya existe ese NICK W3 en la DB de PVPGN.");
        }            
        //tamos ready!
        Integer uid = crearCuentaW3Bnet(w3username, w3password);
        perfil = user.getPerfil();
        perfil.setUidBot(uid);
        perfil.setBotw3(w3username);
        perfilFac.edit(perfil);
        
    }
    
    public void cambiarPasswordCuentaW3(String password) throws BusinessLogicException {
        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        
        if (password == null || password.length() < 4 || password.length() > 32) {
            throw new BusinessLogicException("Password debe contener entre 4 y 32 caracteres.");
        }
        
        if (user.getPerfil().getUid() == null || user.getPerfil().getNickw3() == null) {
            throw new BusinessLogicException("No tienes asociada una cuenta W3, imposible cambiar password.");
        }
        
        //ok... cambiar password
        this.cambiarPasswordPvpgn(user.getPerfil().getNickw3(), password);
    }
    
    public void cambiarPasswordCuentaBotW3(String password) throws BusinessLogicException {
        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        
        if (password == null || password.length() < 4 || password.length() > 32) {
            throw new BusinessLogicException("Password debe contener entre 4 y 32 caracteres.");
        }
        
        if (user.getPerfil().getUidBot() == null || user.getPerfil().getBotw3() == null) {
            throw new BusinessLogicException("No tienes asociada una cuenta BOT W3, imposible cambiar password.");
        }
        
        //ok... cambiar password
        this.cambiarPasswordPvpgn(user.getPerfil().getBotw3(), password);
        
    }
    
    private void cambiarPasswordPvpgn(String w3username, String w3password) throws BusinessLogicException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "UPDATE BNET SET acct_passhash1 = ? WHERE username = ?";
        try {
            conn = this.pvpgnDatasource.getConnection();
            ps = conn.prepareStatement(query);
            
            ps.setString(1, PvpgnHash.GetHash(w3password));
            ps.setString(2, w3username);
            ps.executeUpdate();                        
        } catch (SQLException ex) {
            throw new EJBException(ex.getMessage());
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException ex) {} }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { } }
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { } }
        }
    }
    
    private boolean checkPvpgnUsername(String w3username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT uid FROM BNET WHERE username = ?";
        try {
            conn = this.pvpgnDatasource.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, w3username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            throw new EJBException(ex);
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException ex) {} }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { } }
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { } }
        }
        return false;
    }
    
    
    public Usuario getByUsername(String username) {
        return this.userFac.findByUsername(username);
    }

    public void setLastLogin(String username, Date date) {
        Usuario usuario = userFac.findByUsername(username);
        usuario.setLastLogin(date);
        userFac.edit(usuario);
    }
    
    public List<String> searchUsernames(String username, int maxResults) {
        return userFac.searchUsernames(username, maxResults);
    }

    
    
}
