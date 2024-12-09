package edu.miu.horelo.repository;

import edu.miu.horelo.model.Address;
import org.springframework.data.repository.ListCrudRepository;

public interface AddressRepository extends ListCrudRepository<Address, Integer> {
}
