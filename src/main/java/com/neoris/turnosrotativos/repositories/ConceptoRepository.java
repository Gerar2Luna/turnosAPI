package com.neoris.turnosrotativos.repositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.neoris.turnosrotativos.entities.Concepto;
import java.util.Optional;


@Repository
public interface ConceptoRepository extends CrudRepository<Concepto,Integer> {
    public boolean existsById(Integer id);
    public Optional<Concepto> findById(Integer id);
}
