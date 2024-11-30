import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import si.fri.rso.skupina20.entitete.Uporabnik;
import si.fri.rso.skupina20.zrna.UporabnikZrno;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // To je potreben za integracijo Mockito z JUnit 5
public class UporabnikZrnoTest {

    @InjectMocks
    private UporabnikZrno uporabnikZrno;

    @Mock
    private EntityManager em;


    // Testiranje metode getUporabnik, če uporabnik ne obstaja
    @Test
    public void testGetUporabnikNotFound() {
        // Preverimo, da se metoda pravilno obnaša, ko uporabnik ni najden
        TypedQuery<Uporabnik> mockQuery = mock(TypedQuery.class); // Uporabi TypedQuery
        when(em.createNamedQuery("Uporabnik.getUporabnik", Uporabnik.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter("id", 1)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        Uporabnik uporabnik = uporabnikZrno.getUporabnik(1);
        assertNull(uporabnik);
    }

    // Testiranje metode getUporabnik, če uporabnik obstaja
    @Test
    public void testGetUporabnikFound() {
        // Mockanje TypedQuery
        TypedQuery<Uporabnik> mockQuery = mock(TypedQuery.class);

        // Priprava testnega uporabnika
        Uporabnik mockUporabnik = new Uporabnik();
        mockUporabnik.setId(1);
        mockUporabnik.setEmail("Testni@gmail.com");
        mockUporabnik.setIme("Testni");
        mockUporabnik.setPriimek("Testni");
        mockUporabnik.setGeslo("Testni");
        mockUporabnik.setSol("Testni");
        mockUporabnik.setTelefon("Testni");
        mockUporabnik.setTipUporabnika(Uporabnik.TipUporabnika.ADMIN);

        // Mockanje poizvedbe
        when(em.createNamedQuery("Uporabnik.getUporabnik", Uporabnik.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter("id", 1)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(mockUporabnik);

        // Klic metode in preverjanje rezultatov
        Uporabnik uporabnik = uporabnikZrno.getUporabnik(1);
        assertNotNull(uporabnik);
        assertEquals(1, uporabnik.getId());
        assertEquals("Testni@gmail.com", uporabnik.getEmail());
        assertEquals("Testni", uporabnik.getIme());
        assertEquals("Testni", uporabnik.getPriimek());
    }



    // Testiranje, če uporabnik že obstaja
    @Test
    public void testAddUporabnikAlreadyExists() {
        // Nastavimo testne podatke
        Uporabnik uporabnik = new Uporabnik();

        uporabnik.setEmail("obstoječiUporabnik@gmail.com");
        uporabnik.setGeslo("testnaGeslo");
        uporabnik.setIme("Maja");
        uporabnik.setPriimek("Kralj");

        // Mockanje TypedQuery, ki bo vrnjen iz createNamedQuery
        TypedQuery<Uporabnik> mockQuery = mock(TypedQuery.class);

        // Mocker za preverjanje, da uporabnik že obstaja
        when(em.createNamedQuery("Uporabnik.getUporabnikByEmail", Uporabnik.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter("email", uporabnik.getEmail())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(uporabnik); // Uporabnik že obstaja

        // Testiramo metodo
        String jwt = uporabnikZrno.addUporabnik(uporabnik);

        // Preverimo, da metoda vrne null, ker uporabnik že obstaja
        assertNull(jwt);
    }

    // Testiranje izbrisa uporanika uspešno
    @Test
    public void testDeleteUporabnikSuccess() {
        // Priprava testnega uporabnika
        Uporabnik uporabnik = new Uporabnik();
        uporabnik.setId(1);
        uporabnik.setEmail("test@uporabnik.com");

        // Mockanje EntityManagerja in rezultatov
        EntityTransaction mockTransaction = mock(EntityTransaction.class);

        // Mockanje EntityManager za vrnitev transakcije in iskanje uporabnika
        when(em.find(Uporabnik.class, 1)).thenReturn(uporabnik);
        when(em.getTransaction()).thenReturn(mockTransaction);

        // Preverimo, da transakcija začne in konča
        doNothing().when(mockTransaction).begin();
        doNothing().when(mockTransaction).commit();
        doNothing().when(em).remove(uporabnik);

        // Testiranje metode
        boolean result = uporabnikZrno.deleteUporabnik(1);

        // Preverjanje rezultatov
        assertTrue(result);
        verify(em).remove(uporabnik);
        verify(mockTransaction).begin();
        verify(mockTransaction).commit();
    }

    // neuspešen izbris uporabnika
    @Test
    public void testDeleteUporabnikNotFound() {
        // Mockanje, da uporabnik z ID-jem 1 ne obstaja
        when(em.find(Uporabnik.class, 1)).thenReturn(null);

        // Testiranje metode
        boolean result = uporabnikZrno.deleteUporabnik(1);

        // Preverjanje rezultatov
        assertFalse(result);
        verify(em, never()).remove(any(Uporabnik.class));
    }


}
