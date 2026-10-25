package ut4.persistencia.lab2;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.util.List;

public class Lab2 {

    public static void main(String[] args) {
        // Inicializar SessionFactory
        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Long planetaIdGenerado = null;

        // --- PRIMERA TRANSACCIÓN: PERSISTENCIA ---
        System.out.println("--- INICIANDO FASE DE REGISTRO ---");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            // 1. Instanciar un nuevo planeta
            Planeta astillero = new Planeta("Astillero de Corellia", "Mundos del Núcleo");

            // 2. Crear dos naves en estado transitorio
            Nave nave1 = new Nave("Caza TIE Básico");
            Nave nave2 = new Nave("TIE Bomber");

            // 3. Utilizar el método helper para vincular ambas naves
            astillero.agregarNave(nave1);
            astillero.agregarNave(nave2);

            // 4. Persistir el planeta (la cascada guardará las naves automáticamente)
            session.persist(astillero);
            
            tx.commit();
            planetaIdGenerado = astillero.getId();
            System.out.println("Planeta y naves persistidos con éxito. ID del Astillero: " + planetaIdGenerado);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- SEGUNDA TRANSACCIÓN: RECUPERACIÓN ---
        System.out.println("\n--- INICIANDO FASE DE LECTURA (Sesión Limpia) ---");
        try (Session session = sessionFactory.openSession()) {
            // 5. Recuperar el planeta por su ID y recorrer el manifiesto
            Planeta planetaRecuperado = session.get(Planeta.class, planetaIdGenerado);
            
            if (planetaRecuperado != null) {
                System.out.println("Planeta encontrado: " + planetaRecuperado.getNombrePlaneta() + 
                                   " | Sector: " + planetaRecuperado.getSectorGalactico());
                
                System.out.println("Manifiesto de naves amarradas:");
                List<Nave> flota = planetaRecuperado.getNaves();
                
                for (Nave nave : flota) {
                    System.out.println(" -> " + nave.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionFactory.close();
        }
    }
}
