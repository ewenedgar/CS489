package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.AllergyRequest;
import edu.miu.horelo.dto.response.AllergyResponse;
import edu.miu.horelo.dto.response.AllergyUserResponse;

import java.util.List;
import java.util.Optional;

public interface AllergyService {
    AllergyResponse addNewAllergy(AllergyRequest newAllergy, Integer userId);

    List<AllergyResponse> getAllAllergies();

    List<AllergyUserResponse> getAllergiesByUser(Integer userId);

    void deleteAllergy(Integer id, Integer userId);

    Optional<AllergyResponse> getAllergyById(Integer id,Integer userId);

    AllergyResponse updateAllergy(Integer id, AllergyRequest allergy, Integer userId);
}
