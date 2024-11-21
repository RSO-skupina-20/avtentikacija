package si.fri.rso.skupina20.entitete;

import javax.persistence.*;

@Entity(name = "uporabnik")
@NamedQueries(value = {
        @NamedQuery(name = "Uporabnik.getAll", query = "SELECT p FROM uporabnik p"),
        @NamedQuery(name = "Uporabnik.getUporabnik", query = "SELECT p FROM uporabnik p WHERE p.id = :id"),
        @NamedQuery(name = "Uporabnik.updateUporabnik", query = "UPDATE uporabnik p SET p.ime = :ime, p.priimek = :priimek, p.email = :email, p.telefon = :telefon WHERE p.id = :id"),
        @NamedQuery(name = "Uporabnik.deleteUporabnik", query = "DELETE FROM uporabnik p WHERE p.id = :id"),
        @NamedQuery(name = "Uporabnik.getUporabnikByEmail", query = "SELECT p FROM uporabnik p WHERE p.email = :email")
})
public class Uporabnik{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ime", nullable = false)
    private String ime;

    @Column(name = "priimek", nullable = false)
    private String priimek;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telefon")
    private String telefon;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip_uporabnika", nullable = false)
    private TipUporabnika tipUporabnika;

    public enum TipUporabnika {
        LASTNIK,
        UPORABNIK,
        ADMIN
    }

    @Column(name="geslo")
    private String geslo;

    @Column(name="sol")
    private String sol;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPriimek() {
        return priimek;
    }

    public void setPriimek(String priimek) {
        this.priimek = priimek;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }


    public TipUporabnika getTipUporabnika() {
        return tipUporabnika;
    }

    public void setTipUporabnika(TipUporabnika tipUporabnika) {
        this.tipUporabnika = tipUporabnika;
    }

    public String getGeslo() {
        return geslo;
    }

    public void setGeslo(String geslo) {
        this.geslo = geslo;
    }

    public String getSol() {
        return sol;
    }

    public void setSol(String sol) {
        this.sol = sol;
    }
}
