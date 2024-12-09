package edu.miu.horelo.controller;

import edu.miu.horelo.dto.response.AddressResponse2;
import edu.miu.horelo.service.impl.AddressServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/address"})
public class AddressController {

    private AddressServiceImpl addressService;

    public AddressController(AddressServiceImpl addressService) {
        this.addressService = addressService;
    }

    @GetMapping(value = {"/list"})
    public ResponseEntity<List<AddressResponse2>> listAddress() {
        return ResponseEntity.ok(addressService.getAddresses());
    }
}
