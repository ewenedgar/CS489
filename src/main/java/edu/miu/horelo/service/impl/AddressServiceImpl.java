package edu.miu.horelo.service.impl;

import edu.miu.horelo.dto.response.AddressResponse2;
import edu.miu.horelo.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl {

    private AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<AddressResponse2> getAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(a -> new AddressResponse2(
                        a.getAddressId(),
                        a.getStreet(), a.getCity(), a.getState(),
                        a.getZipCode()
                )).toList();
    }

}
