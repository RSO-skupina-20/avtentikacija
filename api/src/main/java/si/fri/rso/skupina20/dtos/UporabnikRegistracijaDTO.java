package si.fri.rso.skupina20.dtos;

public class UporabnikRegistracijaDTO {
    private String ime;
    private String priimek;
    private String email;
    private String telefon;
    private String geslo;
    private String geslo2;
    private String tipUporabnika;

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

    public String getGeslo() {
        return geslo;
    }

    public void setGeslo(String geslo) {
        this.geslo = geslo;
    }

    public String getGeslo2() {
        return geslo2;
    }

    public void setGeslo2(String geslo2) {
        this.geslo2 = geslo2;
    }

    public String getTipUporabnika() {
        return tipUporabnika;
    }

    public void setTipUporabnika(String tipUporabnika) {
        this.tipUporabnika = tipUporabnika;
    }
}
