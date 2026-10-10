package org.acme.repositories;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;

import org.acme.models.Producto;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
@ApplicationScoped
public class ProductoRepository {

    @Inject
    DataSource dataSource;

    @Retry(maxRetries = 3, delay = 500)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "unFallbackFindByName")
    public Optional<Producto> findByName(String nombre) throws Exception {

        // Simula fallo si se pide un nombre que empieza por "error"
        if (nombre.startsWith("error")) {
            throw new RuntimeException("Fallo simulado en la base de datos");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT id, nombre, precio FROM products WHERE nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Producto(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio")
                    ));
                }
                return Optional.empty();
            }
        }
    }
    // Este es el Fallback: devuelve un producto por defecto cuando todo falla
    public Optional<Producto> unFallbackFindByName(String nombre) {
        return Optional.of(new Producto(-1L, "Producto no disponible", 0.0));
    }
}
