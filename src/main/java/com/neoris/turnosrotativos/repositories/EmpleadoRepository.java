package com.neoris.turnosrotativos.repositories;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.neoris.turnosrotativos.entities.Empleado;


@Repository
public interface EmpleadoRepository extends CrudRepository<Empleado, Integer> {
    
    Optional<Empleado> findByNroDocumento(Integer nroDocumento);

    Optional<Empleado> findByEmail(String email);
}
