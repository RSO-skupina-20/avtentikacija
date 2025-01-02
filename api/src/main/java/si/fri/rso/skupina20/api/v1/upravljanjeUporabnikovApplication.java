package si.fri.rso.skupina20.api.v1;



import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title = "Upravljanje uporabnikov API",
                version = "v1",
                description = "Upravljanje uporabnikov API je namenjen upravljanju uporabnikov v sistemu."
        ),
        servers = {
                @Server(url = "http://172.212.44.19/avtentikacija"),  // External server URL
                @Server(url = "http://localhost:8081")     // Local development server URL
        }
)
@SecurityScheme(
        securitySchemeName = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")

@ApplicationPath("v1")
public class upravljanjeUporabnikovApplication extends Application {

}
