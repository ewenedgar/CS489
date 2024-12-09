package edu.miu.horelo.service.impl;


import edu.miu.horelo.dto.request.AllergyRequest;
import edu.miu.horelo.dto.response.AllergyResponse;
import edu.miu.horelo.dto.response.AllergyUserResponse;
import edu.miu.horelo.model.Allergy;
import edu.miu.horelo.model.User;
import edu.miu.horelo.repository.AllergyRepository;
import edu.miu.horelo.repository.UserRepository;
import edu.miu.horelo.service.AllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class AllergyServiceImpl implements AllergyService {
    @Autowired
    private AllergyRepository allergyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AllergyResponse addNewAllergy(AllergyRequest newAllergy, Integer userId) {
        // Fetch the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Allergy allergy = new Allergy(null,newAllergy.name(),newAllergy.description(),
                newAllergy.scale(), user);

        Allergy savedAllergy = allergyRepository.save(allergy);

        // Return DTO response
        return new AllergyResponse(
                savedAllergy.getAllergyId(),
                savedAllergy.getName(),
                savedAllergy.getDescription(),
                savedAllergy.getScale()
        );
    }

    @Override
    public List<AllergyResponse> getAllAllergies() {
        return allergyRepository.findAll()
                .stream()
                .map(a -> new AllergyResponse(
                        a.getAllergyId(),
                        a.getName(),
                        a.getDescription(),
                        a.getScale()
                )).toList();
    }

    @Override
    public List<AllergyUserResponse> getAllergiesByUser(Integer userId) {
        return allergyRepository.findAllByUser_UserId(userId)
                .stream()
                .map(a -> new AllergyUserResponse(
                        a.getAllergyId(),
                        a.getName(),
                        a.getDescription(),
                        a.getScale()
                )).toList();
    }

    @Override
    public void deleteAllergy(Integer id, Integer userId) {
        allergyRepository.deleteAllByAllergyId(id);
    }

    @Override
    public Optional<AllergyResponse> getAllergyById(Integer id, Integer userId) {
        // Fetch the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the allergy by ID
        Allergy allergy = allergyRepository.findByAllergyId(id)
                .orElseThrow(() -> new RuntimeException("Allergy not found"));

        // Check if the user is associated with the allergy
        /*
        if (!allergy.getUsers().contains(user)) {
            throw new RuntimeException("User does not have permission to access this allergy");
        }
*/
        // Return the AllergyResponse if the user has access
        return Optional.of(new AllergyResponse(
                allergy.getAllergyId(),
                allergy.getName(),
                allergy.getDescription(),
                allergy.getScale()
        ));
    }

    @Override
    public AllergyResponse updateAllergy(Integer id, AllergyRequest allergy, Integer userId) {
        Optional<Allergy> existingAllergyOpt = allergyRepository.findByAllergyId(id);

        if (existingAllergyOpt.isPresent()) {
            Allergy existingAllergy = existingAllergyOpt.get();

            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            Allergy updateAllergy = new Allergy(id , allergy.name(), allergy.description(), allergy.scale(), user);
            Allergy updatedAllergy = allergyRepository.save(updateAllergy);

            return new AllergyResponse(
                    updatedAllergy.getAllergyId(),
                    updatedAllergy.getName(),
                    updatedAllergy.getDescription(),
                    updatedAllergy.getScale()
            );
        } else {
            throw new RuntimeException("Allergy not found");
        }
    }
}