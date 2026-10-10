package org.acme.repositories;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;

import org.acme.models.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
@ApplicationScoped
public class ProductoRepository {

    @Inject
    DataSource dataSource;

    
    public Optional<Producto> findByName(String nombre) throws Exception {

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

}
