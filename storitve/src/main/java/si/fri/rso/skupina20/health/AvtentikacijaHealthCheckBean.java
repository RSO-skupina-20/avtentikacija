package si.fri.rso.skupina20.health;

import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import si.fri.rso.skupina20.auth.GenerirajZeton;
import si.fri.rso.skupina20.entitete.Uporabnik;
import si.fri.rso.skupina20.zrna.UporabnikZrno;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

@Readiness
@ApplicationScoped
// Preverjanje ali registracija in izbris uporabnika delujejo
public class AvtentikacijaHealthCheckBean implements HealthCheck{

    private static final Logger LOG = Logger.getLogger(AvtentikacijaHealthCheckBean.class.getName());

    @Inject
    private UporabnikZrno uporabnikZrno;

    @Override
    public HealthCheckResponse call() {
        String description = "Preverjanje ali registracija in izbris uporabnika delujejo";
        try {
            Uporabnik uporabnik = new Uporabnik();
            uporabnik.setEmail("testniUporabnik123@testni-uporabni.si");
            uporabnik.setIme("Testni");
            uporabnik.setPriimek("Uporabnik");
            uporabnik.setGeslo("testniUporabnik123");
            uporabnik.setTipUporabnika(Uporabnik.TipUporabnika.UPORABNIK);
            uporabnik.setTelefon("040123456");


            String jwt = uporabnikZrno.addUporabnik(uporabnik);

            Integer id = GenerirajZeton.getUserId(jwt);

            if (id == -1) {
                return HealthCheckResponse.named(AvtentikacijaHealthCheckBean.class.getSimpleName())
                        .down()
                        .withData("description", description)
                        .withData("error", "Napaka pri pridobivanju id uporabnika iz žetona")
                        .build();
            }

            uporabnikZrno.deleteUporabnik(id);

        } catch (Exception e) {
            LOG.severe("Napaka pri preverjanju zdravja: " + e.getMessage());
            return HealthCheckResponse.named(AvtentikacijaHealthCheckBean.class.getSimpleName())
                    .down()
                    .withData("description", description)
                    .withData("error", e.getMessage())
                    .build();
        }
        return HealthCheckResponse.named(AvtentikacijaHealthCheckBean.class.getSimpleName())
                .up()
                .withData("description", description)
                .build();

    }
}
