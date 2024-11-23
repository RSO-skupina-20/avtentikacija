package si.fri.rso.skupina20.zrna;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.hibernate.annotations.common.util.impl.Log;
import si.fri.rso.skupina20.auth.GenerirajZeton;
import si.fri.rso.skupina20.dtos.UporabnikPrijavaDTO;
import si.fri.rso.skupina20.entitete.Uporabnik;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

import static si.fri.rso.skupina20.auth.PreverjanjeGesla.*;

@ApplicationScoped
public class UporabnikZrno {

    @PersistenceContext(unitName = "avtentikacija-jpa")
    private EntityManager em;

    private Logger log = Logger.getLogger(UporabnikZrno.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija zrna " + UporabnikZrno.class.getSimpleName());
    }

    @PreDestroy
    private void destroy() {
        log.info("Deinicializacija zrna " + UporabnikZrno.class.getSimpleName());
    }

    public List<Uporabnik> getUporabniki() {
        Query q = em.createNamedQuery("Uporabnik.getAll", Uporabnik.class);
        return q.getResultList();
    }

    public Uporabnik getUporabnik(int id) {
        Query q = em.createNamedQuery("Uporabnik.getUporabnik", Uporabnik.class);
        q.setParameter("id", id);
        try {
            Uporabnik uporabnik = (Uporabnik) q.getSingleResult();
            uporabnik.setGeslo(null);
            uporabnik.setSol(null);
            return uporabnik;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Uporabnik> getUporabniki(QueryParameters query) {
        return JPAUtils.queryEntities(em, Uporabnik.class, query);
    }

    public Long getUporabnikiCount(QueryParameters query) {
        return JPAUtils.queryEntitiesCount(em, Uporabnik.class, query);
    }

    // Registracija novega uporabnika (preveri, če uporabnik s tem emailom že obstaja)
    @Transactional
    public String addUporabnik(Uporabnik uporabnik){
        try{
            Query q = em.createNamedQuery("Uporabnik.getUporabnikByEmail", Uporabnik.class);
            q.setParameter("email", uporabnik.getEmail());
            try {
                // Uporabnik s tem emailom že obstaja
                q.getSingleResult();
                return null;
            } catch(NoResultException e){

                // Uporabnik s tem emailom še ne obstaja; generiraj sol in hashiraj geslo
                uporabnik.setSol(generirajSol());
                String hash = generirajHash(uporabnik.getGeslo(), uporabnik.getSol());
                String geslo = uporabnik.getGeslo();
                uporabnik.setGeslo(hash);

                em.persist(uporabnik);

                log.info("Uporabnik uspešno dodan");

                // Ustvari žeton
                String jwt = GenerirajZeton.createToken(uporabnik);
                return jwt;
            }
        } catch(Exception e){
            log.info("Napaka pri dodajanju uporabnika: " + e.getMessage());
            return null;
        }
    }

    // Prijava uporabnika
    public String prijavaUporabnika(UporabnikPrijavaDTO uporabnik){
try{
            Query q = em.createNamedQuery("Uporabnik.getUporabnikByEmail", Uporabnik.class);
            q.setParameter("email", uporabnik.getEmail());
            Uporabnik uporabnikIzBaze = (Uporabnik) q.getSingleResult();


            // Preveri geslo
            if(preveriGeslo(uporabnik.getGeslo(), uporabnikIzBaze.getSol(), uporabnikIzBaze.getGeslo())){
                // Geslo je pravilno
                String jwt = GenerirajZeton.createToken(uporabnikIzBaze);
                return jwt;
            } else {
                // Geslo ni pravilno
                log.info("Napačno geslo");
                return null;
            }
        } catch(NoResultException e){
            // Uporabnik s tem emailom ne obstaja
            log.info("Uporabnik s tem emailom ne obstaja");
            return null;
        } catch(Exception e){
            log.info("Napaka pri prijavi uporabnika: " + e.getMessage());
            return null;
        }
    }

    // Posodobi uporabnika
    @Transactional
    public Uporabnik updateUporabnik(int id, Uporabnik uporabnik) {
        Uporabnik u = em.find(Uporabnik.class, id);
        if (u == null) {
            return null;
        }
        log.info("Posodabljam uporabnika: " + uporabnik.getEmail() + " " + u.getEmail());
        if (uporabnik.getEmail() != u.getEmail()) {
            Query q = em.createNamedQuery("Uporabnik.getUporabnikByEmail", Uporabnik.class);
            q.setParameter("email", uporabnik.getEmail());
            try {
                // Uporabnik s tem emailom že obstaja
                q.getSingleResult();
                return null;
            } catch (NoResultException e) {
                u.setEmail(uporabnik.getEmail());
            }
        }
        u.setIme(uporabnik.getIme());
        u.setPriimek(uporabnik.getPriimek());
        u.setTelefon(uporabnik.getTelefon());

        log.info("Uporabnik uspešno posodobljen");
        log.info("Uporabnik: " + u.toString());
        em.getTransaction().begin();
        u = em.merge(u);
        em.getTransaction().commit();
        return u;
    }

    // Odstrani uporabnika
    @Transactional
    public boolean deleteUporabnik(int id) {
        // Tu se kliče še api za prostore/dogodke, da se odstranijo vsi podatki, ki so povezani s tem uporabnikom
        Uporabnik uporabnik = em.find(Uporabnik.class, id);
        if (uporabnik != null) {
            em.getTransaction().begin();
            em.remove(uporabnik);
            em.getTransaction().commit();
            return true;
        }
        return false;
    }



}


