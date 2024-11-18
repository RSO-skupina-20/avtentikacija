package si.fri.rso.skupina20;

import si.fri.rso.skupina20.entitete.Uporabnik;
import si.fri.rso.skupina20.zrna.UporabnikZrno;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/servlet")
public class ServletTest extends HttpServlet {
    @Inject
    private UporabnikZrno uporabnikZrno;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Uporabnik> uporabniki = uporabnikZrno.getUporabniki();

        PrintWriter writer = resp.getWriter();

        for(Uporabnik uporabnik : uporabniki) {
            writer.write("Uporabnik: " + uporabnik.getIme() + " " + uporabnik.getPriimek() + " " + uporabnik.getEmail() + " " + uporabnik.getTelefon() + "\n");
        }

    }
}