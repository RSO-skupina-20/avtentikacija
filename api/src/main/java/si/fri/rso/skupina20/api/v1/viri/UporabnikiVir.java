package si.fri.rso.skupina20.api.v1.viri;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import com.kumuluz.ee.rest.beans.QueryParameters;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.fri.rso.skupina20.auth.GenerirajZeton;
import si.fri.rso.skupina20.dtos.UporabnikPrijavaDTO;
import si.fri.rso.skupina20.dtos.UporabnikRegistracijaDTO;
import si.fri.rso.skupina20.entitete.Uporabnik;
import si.fri.rso.skupina20.zrna.UporabnikZrno;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@ApplicationScoped
@Path("/uporabniki")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CrossOrigin(supportedMethods = "GET, POST, DELETE, PUT, HEAD, OPTIONS")
@Tag(name = "Uporabniki", description = "Upravljanje uporabnikov")

public class UporabnikiVir {
    @Context
    protected UriInfo uriInfo;

    @Inject
    private UporabnikZrno uporabnikZrno;

    @GET
    @Operation(summary = "Pridobi seznam vseh uporabnikov", description = "Vrne seznam vseh uporabnikov")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Seznam uporabnikov", content = @Content(schema = @Schema(implementation = Uporabnik.class)),
                    headers = @Header(name = "X-Total-Count", description = "Število vrnjenih uporabnikov", schema = @Schema(type = SchemaType.INTEGER))),
            @APIResponse(responseCode = "404", description = "Uporabnikov ni mogoče najti", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class, example = "{\"napaka\": \"Uporabnikov ni mogoče najti\"}"))),
    })
    @SecurityRequirement(name = "bearerAuth")
    public Response vrniUporabnike(@HeaderParam("Authorization") String jwt){
        // Dostopajo samo admini
        List<String> dovoli_dostop = List.of("ADMIN");

        // Preveri žeton
        if(jwt == null || !GenerirajZeton.verifyToken(jwt, dovoli_dostop)){
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"napaka\": \"Dostop zavrnjen\"}").build();
        }

        // Izvedi poizvedbo
        QueryParameters query = QueryParameters.query(uriInfo.getRequestUri().getQuery()).build();
        List<Uporabnik> uporabniki = uporabnikZrno.getUporabniki(query);
        if(uporabniki == null){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"napaka\": \"Uporabnikov ni mogoče najti\"}").build();
        }
        // Odstranjevanje polja "geslo" iz uporabnikov
        for(Uporabnik uporabnik : uporabniki){
            uporabnik.setGeslo(null);
            uporabnik.setSol(null);
        }
        Long count = uporabnikZrno.getUporabnikiCount(query);
        return Response.ok(uporabniki).header("X-Total-Count", count).build();
    }

    @GET
    @Operation(summary = "Pridobi uporabnika glede na id", description = "Vrne uporabnika glede na id")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Uporabnik", content = @Content(schema = @Schema(implementation = Uporabnik.class, example = "{\"id\": 1, \"ime\": \"Ime\", \"priimek\": \"Priimek\", \"email\": \"Email\", \"telefon\": \"Telefon\", \"tip_uporabnika\": \"Tip uporabnika\"}"))),
            @APIResponse(responseCode = "404", description = "Uporabnik ne obstaja", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class, example = "{\"napaka\": \"Uporabnik z id 1 ne obstaja\"}"))),
    })
    @SecurityRequirement(name = "bearerAuth")
    @Path("{id}")
    public Response vrniUporabnika(@PathParam("id") Integer id, @HeaderParam("Authorization") String jwt){
        // Dostopajo samo admini in uporabnik sam zase
        List<String> dovoli_dostop = List.of("ADMIN");

        // Preveri žeton
        if(jwt == null || !GenerirajZeton.verifyToken(jwt, dovoli_dostop)){
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"napaka\": \"Dostop zavrnjen\"}").build();
        }

        Uporabnik uporabnik = uporabnikZrno.getUporabnik(id);
        if(uporabnik == null){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"napaka\": \"Uporabnik z id " + id + " ne obstaja\"}").build();
        }
        return Response.ok(uporabnik).build();
    }

    // Registracija novega uporabnika (potreben DTO objekt da imamo 2 polji za geslo)
    @POST
    @Path("registracija")
    @Operation(
            summary = "Registracija uporabnika",
            description = "Omogoči registracijo novega uporabnika"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Uporabnik uspešno registriran",
                    content = @Content(schema = @Schema(implementation = Uporabnik.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Manjkajo obvezni podatki",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "{\"napaka\": \"Manjkajo obvezni podatki\"}")
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Gesli se ne ujemata",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "{\"napaka\": \"Gesli se ne ujemata\"}")
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Geslo mora vsebovati vsaj 8 znakov",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "{\"napaka\": \"Geslo mora vsebovati vsaj 8 znakov\"}")
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Napačen tip uporabnika",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "{\"napaka\": \"Napačen tip uporabnika\"}")
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Uporabnik s tem emailom že obstaja",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class, example = "{\"napaka\": \"Uporabnik s tem emailom že obstaja\"}")
                    )
            )
    })
    public Response registracijaUporabnika(@RequestBody(description = "DTO objekt za registracijo uporabnika", content = @Content(schema = @Schema(implementation = UporabnikRegistracijaDTO.class))) UporabnikRegistracijaDTO uporabnikRegistracijaDTO) {
        if (uporabnikRegistracijaDTO.getGeslo() == null || uporabnikRegistracijaDTO.getGeslo2() == null || uporabnikRegistracijaDTO.getGeslo().equals("") || uporabnikRegistracijaDTO.getGeslo2().equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"napaka\": \"Manjkajo obvezni podatki\"}").build();
        }
        if (uporabnikRegistracijaDTO.getIme() == null || uporabnikRegistracijaDTO.getPriimek() == null || uporabnikRegistracijaDTO.getEmail() == null || uporabnikRegistracijaDTO.getTelefon() == null || uporabnikRegistracijaDTO.getIme().equals("") || uporabnikRegistracijaDTO.getPriimek().equals("") || uporabnikRegistracijaDTO.getEmail().equals("") || uporabnikRegistracijaDTO.getTelefon().equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"napaka\": \"Manjkajo obvezni podatki\"}").build();
        }
        if (!uporabnikRegistracijaDTO.getGeslo().equals(uporabnikRegistracijaDTO.getGeslo2())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"napaka\": \"Gesli se ne ujemata\"}").build();
        }
        if (uporabnikRegistracijaDTO.getGeslo().length() < 8) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"napaka\": \"Geslo mora vsebovati vsaj 8 znakov\"}").build();
        }
        // Pretvorba UporabnikRegistracijaDTO v Uporabnik
        Uporabnik uporabnik = new Uporabnik();
        uporabnik.setIme(uporabnikRegistracijaDTO.getIme());
        uporabnik.setPriimek(uporabnikRegistracijaDTO.getPriimek());
        uporabnik.setEmail(uporabnikRegistracijaDTO.getEmail());
        uporabnik.setTelefon(uporabnikRegistracijaDTO.getTelefon());
        uporabnik.setGeslo(uporabnikRegistracijaDTO.getGeslo());
        // sol se generira v zrnu; zato neka privzeta vrednost
        uporabnik.setSol("sol");
        // pretvori uporabnikRegistracijaDTO.getTipUporabnika() v TipUporabnika
        Uporabnik.TipUporabnika tipUporabnika;
        try {
            tipUporabnika = Uporabnik.TipUporabnika.valueOf(uporabnikRegistracijaDTO.getTipUporabnika());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"napaka\": \"Napačen tip uporabnika\"}").build();
        }
        uporabnik.setTipUporabnika(tipUporabnika);
        String jwt = uporabnikZrno.addUporabnik(uporabnik);
        if (uporabnik == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"napaka\": \"Uporabnik s tem emailom že obstaja\"}").build();
        }
        JsonObject web_token = Json.createObjectBuilder().add("jwt", jwt).build();
        return Response.status(Response.Status.CREATED).entity(web_token).build();
    }

    @POST
    @Path("prijava")
    @Operation(
            summary = "Prijava uporabnika",
            description = "Omogoči prijavo uporabnika"
    )

    public Response prijavaUporabnika(@RequestBody(description = "DTO objekt za prijavo uporabnika", content = @Content(schema = @Schema(implementation = UporabnikPrijavaDTO.class))) UporabnikPrijavaDTO uporabnikPrijavaDTO) {
        if (uporabnikPrijavaDTO.getEmail() == null || uporabnikPrijavaDTO.getGeslo() == null || uporabnikPrijavaDTO.getEmail().equals("") || uporabnikPrijavaDTO.getGeslo().equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"napaka\": \"Manjkajo obvezni podatki\"}").build();
        }
        String jwt = uporabnikZrno.prijavaUporabnika(uporabnikPrijavaDTO);

        if (jwt == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"napaka\": \"Napačen email ali geslo\"}").build();
        }
        JsonObject web_token = Json.createObjectBuilder().add("jwt", jwt).build();
        return Response.ok(web_token).build();

    }






}
