package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.FileDTO;
import edu.miu.horelo.dto.SavedFileDTO;
import edu.miu.horelo.dto.request.*;
import edu.miu.horelo.dto.response.*;
import edu.miu.horelo.model.*;
import edu.miu.horelo.repository.EstoreRepository;
import edu.miu.horelo.repository.RoleRepository;
import edu.miu.horelo.repository.UserRepository;
import edu.miu.horelo.service.EstoreService;
import edu.miu.horelo.service.FileManagerService;
import edu.miu.horelo.service.UserEstoreRoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EstoreServiceImpl implements EstoreService {
    private final EstoreRepository estoreRepository;
    private final UserRepository userRepository;
    private final FileManagerService fileManagerService;
    private final RoleRepository roleRepository;
    private final UserEstoreRoleService userEstoreRoleService;

        @Override
        public Optional<EstoreVerticalNavBarResponse> getStoreById(Long id) {
            return Optional.of(
                    getEstoreVerticalNavBarResponse(
                            estoreRepository.findByEstoreId(id)
                                    .orElseThrow(()->
                                            new ResourceNotFoundException("Estore not found"))));
    }


    @Override
    public List<EstoreResponse> getStoreByOwner(String email) {

        // Fetch the user by userId
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        // Fetch the stores by user (editor)
        List<Estore> stores = estoreRepository.findByCreator(user);

        // If no stores found, throw an exception
        if (stores.isEmpty()) {
            throw new ResourceNotFoundException("No stores found for user with email " + email);
        }

        return stores.stream()
                .map(this::getEstoreResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EstoreDTORes> getStoreByUser(int userId) {
        // Fetch the user by userId
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + userId));

        // Fetch the UserEstoreRole entities for this user
        List<UserEstoreRole> userEstoreRoles = userEstoreRoleService.getRolesByUser(userId);

        // If no roles found, throw an exception
        if (userEstoreRoles.isEmpty()) {
            throw new ResourceNotFoundException("No roles found for user with ID " + userId);
        }

        // Transform the roles to EstoreResponse
        return userEstoreRoles.stream()
                .map(role -> new EstoreDTORes(
                        role.getEstore().getEstoreId(),
                        role.getEstore().getName(),
                        role.getEstore().getEmail(),
                        role.getEstore().getLogo(),
                        role.getRole().getRoleName() // Assuming the role name is what you want to include
                ))
                .collect(Collectors.toList());
    }



    @Override
    public EstoreResponse addNewStore(Integer userId, EstoreRequest estoreRequest) throws BadRequestException {
        // Fetch the creator and editor users
        User creator = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found with id " + userId));

        // Create a new Estore object
        Estore newStore = new Estore();
        newStore.setCreator(creator);
        newStore.setEditor(creator);
        newStore.setName(estoreRequest.name());
        newStore.setEmail(estoreRequest.email());
        newStore.setPhoneNumber(estoreRequest.phoneNumber());

        // Save the new store
        Estore savedStore = estoreRepository.save(newStore);

        // Assign roles to the creator and editor
        // Fetch the role dynamically
        Role ownerRole = roleRepository.findByRoleName("ROLE_OWNER")
                .orElseThrow(() -> new ResourceNotFoundException("Role 'owner' not found"));

        // Assign 'owner' role to the creator and editor
        userEstoreRoleService.assignRoleToUser(userId,savedStore.getEstoreId(),ownerRole.getRoleId(), creator);

        // Create and return the response
        return getEstoreResponse(savedStore);
    }

    @Override
    public EstoreResponse updateStore(Long storeId,Integer userId, EstoreFullRequest estoreFullRequest) {
        // Fetch the store by storeId

        Estore store = estoreRepository.findByEstoreId(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id " + storeId));

        // Fetch the user by userId
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        // Validate that the user has permission to edit the store
        /*if (!store.getEditor().getUserId().equals(userId)) {
            throw new UnauthorizedException("User does not have permission to edit this store");
        }

         */

        // Update the store details
        store.setPhoneNumber(estoreFullRequest.phoneNumber());
        store.setEditor(user);
        store.setName(estoreFullRequest.name());
        store.setEmail(estoreFullRequest.email());
        store.setLogo(estoreFullRequest.logo());
        store.setVisibility(estoreFullRequest.visibility());

        // Update the primary address
        Address address = new Address();
        AddressRequest addressRequest = estoreFullRequest.primaryAddress();
        address.setStreet(addressRequest.street());
        address.setCity(addressRequest.city());
        address.setState(addressRequest.state());
        address.setCountry(address.getCountry());
        address.setZipCode(addressRequest.zipCode());
        store.setPrimaryAddress(address);

        // Update the editor (user id)
//        store.setEditor(user);
//        store.setEditorId(userId);



        // Save the updated store
        return getEstoreResponse(store);

    }
//EstoreVerticalNavBarResponse
private EstoreVerticalNavBarResponse getEstoreVerticalNavBarResponse(Estore store) {
    var savedEdstore = estoreRepository.save(store);
    return new EstoreVerticalNavBarResponse(
            savedEdstore.getEstoreId(),
            savedEdstore.getName(),
            savedEdstore.getEmail(),
            savedEdstore.getLogo(),
            savedEdstore.getPhoneNumber(),
            savedEdstore.getFoodSafetyMessage(),
            savedEdstore.getVisibility(),
            savedEdstore.getLastUpdate(),
            savedEdstore.getOrderPolicy(),
            savedEdstore.getOpenDaysAndHours(),
            savedEdstore.getPrimaryAddress() != null
                    ? new AddressResponse(
                    savedEdstore.getPrimaryAddress().getAddressId(),
                    savedEdstore.getPrimaryAddress().getStreet(),
                    savedEdstore.getPrimaryAddress().getCity(),
                    savedEdstore.getPrimaryAddress().getState(),
                    savedEdstore.getPrimaryAddress().getZipCode(),
                    savedEdstore.getPrimaryAddress().getCountry()
            )
                    : null

//                savedEdstore.getEditorId(),
//                savedEdstore.getCreatorId()
    );
}
    private EstoreResponse getEstoreResponse(Estore store) {
        var savedEdstore = estoreRepository.save(store);
        return new EstoreResponse(
                savedEdstore.getEstoreId(),
                savedEdstore.getName(),
                savedEdstore.getEmail(),
                savedEdstore.getLogo(),
                savedEdstore.getFoodSafetyMessage(),
                savedEdstore.getPhoneNumber(),
                savedEdstore.getVisibility(),
                savedEdstore.getLastUpdate(),
                savedEdstore.getOrderPolicy(),
                savedEdstore.getPrimaryAddress() != null ? new AddressResponse(
                        savedEdstore.getPrimaryAddress().getAddressId(),
                        savedEdstore.getPrimaryAddress().getStreet(),
                        savedEdstore.getPrimaryAddress().getCity(),
                        savedEdstore.getPrimaryAddress().getState(),
                        savedEdstore.getPrimaryAddress().getZipCode(),
                        savedEdstore.getPrimaryAddress().getCountry()
                ) : null,
                savedEdstore.getOpenDaysAndHours(),
                savedEdstore.getEditor()

//                savedEdstore.getEditorId(),
//                savedEdstore.getCreatorId()
        );
    }

@Override
    public Estore updateEstore1(Long estoreId, EstoreDTO updatedEstoreDTO, User user) {
        Optional<Estore> existingEstoreOpt = estoreRepository.findByEstoreId(estoreId);
        if (existingEstoreOpt.isPresent()) {
            Estore existingEstore = existingEstoreOpt.get();

            // Update fields from DTO
            existingEstore.setPhoneNumber(updatedEstoreDTO.phoneNumber());
            existingEstore.setName(updatedEstoreDTO.name());
            existingEstore.setEditor(user);
            existingEstore.setEmail(updatedEstoreDTO.email());
            existingEstore.setTimeZone(updatedEstoreDTO.timeZone());
            existingEstore.setOrderPolicy(updatedEstoreDTO.orderPolicy());
            existingEstore.setLastUpdate(LocalDateTime.now()); // Update the lastUpdate timestamp

            System.out.println("here is the food safety"+updatedEstoreDTO.foodSafetyMessage().trim());
            if(updatedEstoreDTO.foodSafetyMessage().trim() != null) {
    //foodSafetyMessage.setMessage(updatedEstoreDTO.message());
    existingEstore.setFoodSafetyMessage(updatedEstoreDTO.foodSafetyMessage());
}
            // Update the primary address using the DTO
            Address primaryAddress = new Address();
            if (primaryAddress == null) {
                primaryAddress = new Address(); // Create a new Address if it doesn't exist
            }
            if(updatedEstoreDTO.primaryAddress() != null) {
                primaryAddress.setStreet(updatedEstoreDTO.primaryAddress().street());
                primaryAddress.setCity(updatedEstoreDTO.primaryAddress().city());
                primaryAddress.setState(updatedEstoreDTO.primaryAddress().state());
                primaryAddress.setZipCode(updatedEstoreDTO.primaryAddress().zipCode());
                primaryAddress.setCountry(updatedEstoreDTO.primaryAddress().country());

            existingEstore.setPrimaryAddress(primaryAddress);
            }
            // Get the existing collection reference
            List<OpenPeriodsForDay> existingPeriods = existingEstore.getOpenDaysAndHours().getOpenPeriodsByDay();
            existingPeriods.clear(); // Clear existing entries without losing the reference
            // Convert DTO to OpenPeriodsForDay entities and add them to the existing list
            // Convert DTO to OpenPeriodsForDay entities and add them to the existing list
            updatedEstoreDTO.openDaysAndHoursDTO().openPeriodsByDay().forEach(
                    periodForDayDTO ->{
                        OpenPeriodsForDay openPeriodsForDay = new OpenPeriodsForDay(
                                periodForDayDTO.dayOfWeek(),
                                periodForDayDTO.openPeriods().stream()
                                        .map(periodDTO -> new OpenPeriod(
                                                periodDTO.openTime(),
                                                periodDTO.closeTime()
                                        ))
                                        .collect(Collectors.toList())
                        );
                        existingPeriods.add(openPeriodsForDay);
                    }
            );



            return estoreRepository.save(existingEstore);
        } else {
            throw new RuntimeException("Estore not found");
        }
    }

    @Override
    @Transactional
    public Estore updateEstoreAddress(Long estoreId, EstoreAddressDTO updatedEstoreDTO) {
        Optional<Estore> existingEstoreOpt = estoreRepository.findById(estoreId);
        if (existingEstoreOpt.isPresent()) {
            Estore existingEstore = existingEstoreOpt.get();
        // Update the primary address using the DTO
        Address primaryAddress = existingEstore.getPrimaryAddress();
        if (primaryAddress == null) {
            primaryAddress = new Address(); // Create a new Address if it doesn't exist
        }

        primaryAddress.setStreet(updatedEstoreDTO.street());
        primaryAddress.setCity(updatedEstoreDTO.city());
        primaryAddress.setState(updatedEstoreDTO.state());
        primaryAddress.setZipCode(updatedEstoreDTO.zipCode());
        primaryAddress.setCountry(updatedEstoreDTO.country());

        existingEstore.setPrimaryAddress(primaryAddress); // Set the updated primary address
        existingEstore.setLastUpdate(LocalDateTime.now()); // Update the lastUpdate timestamp

        return estoreRepository.save(existingEstore);
    } else {
        throw new RuntimeException("Estore not found");
    }
    }
    @Override
    @Transactional
    public Estore updateOpenHours(Long estoreId, OpenDaysAndHoursDTO openDaysAndHoursDTO) {
        // Fetch the existing Estore from the repository
        Estore estore = estoreRepository.findByEstoreId(estoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Estore not found"));

        // Get the existing collection reference
        List<OpenPeriodsForDay> existingPeriods = estore.getOpenDaysAndHours().getOpenPeriodsByDay();
        existingPeriods.clear(); // Clear existing entries without losing the reference

        // Convert DTO to OpenPeriodsForDay entities and add them to the existing list
        openDaysAndHoursDTO.openPeriodsByDay().forEach(periodForDayDTO -> {
            OpenPeriodsForDay openPeriodsForDay = new OpenPeriodsForDay(
                    periodForDayDTO.dayOfWeek(),
                    periodForDayDTO.openPeriods().stream()
                            .map(periodDTO -> new OpenPeriod(
                                    periodDTO.openTime(),
                                    periodDTO.closeTime()
                            ))
                            .collect(Collectors.toList())
            );
            existingPeriods.add(openPeriodsForDay);
        });

        // Save the updated Estore back to the repository
        return estoreRepository.save(estore);
    }
    @Override
    @Transactional
    public EstoreResponse updateEstoreLogo(Long id, MultipartFile file, User user){
        Estore store = estoreRepository.findByEstoreId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id " + id));
        // Check if a file is present and handle file upload
        if (file != null && !file.isEmpty()) {
            try {
                // Create a FileDTO instance
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFile(file); // Set the MultipartFile
                fileDTO.setFileName(file.getOriginalFilename()); // Set the original file name

                // Get the existing avatar filename for deletion
                String oldAvatar = store.getLogo();

                // Call fileManagerService to upload the file and delete the old one
                SavedFileDTO savedFile = fileManagerService.uploadFile(fileDTO, "estore", oldAvatar); // null for default folder

                // Set the new avatar URL based on the uploaded file
                store.setLogo(savedFile.getGeneratedFileName()); // Assuming you store the file URL or name
                store.setEditor(user);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file", e);
            }
        }
        // Save the updated store back to the repository
        return getEstoreResponse(store);
    }
@Override
public FoodSafetyMessageResponse updateEstoreFoodSafetyMessage(Long estoreId, FoodSafetyMessageRequest foodSafetyMessageRequest) {
    Estore store = estoreRepository.findByEstoreId(estoreId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id " + estoreId));
    FoodSafetyMessage foodSafetyMessage = new FoodSafetyMessage();
    foodSafetyMessage.setMessage(foodSafetyMessageRequest.message());
    //store.setFoodSafetyMessage(foodSafetyMessage);
    var savedEstore = estoreRepository.save(store);
    return null;
}



    @Override
    public Page<EstoreResponse> searchEstores(String areaCode, String street, String city, String state, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Query estores based on provided search criteria
        Page<Estore> estores = estoreRepository.searchByAddress(areaCode, street, city, state, pageable);

        // Map Estore entities to EstoreResponse DTOs
        return estores.map(this::getEstoreResponse);
    }

}
