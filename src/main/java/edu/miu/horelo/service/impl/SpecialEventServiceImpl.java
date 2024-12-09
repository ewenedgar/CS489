package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.FileDTO;
import edu.miu.horelo.dto.SavedFileDTO;
import edu.miu.horelo.dto.request.SpecialEventRequest;
import edu.miu.horelo.dto.response.SpecialEventResponse;
import edu.miu.horelo.model.Estore;
import edu.miu.horelo.model.SpecialEvent;
import edu.miu.horelo.repository.EstoreRepository;
import edu.miu.horelo.repository.SpecialEventRepository;
import edu.miu.horelo.service.FileManagerService;
import edu.miu.horelo.service.SpecialEventService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SpecialEventServiceImpl implements SpecialEventService {
    private final SpecialEventRepository specialEventRepository;
    private final EstoreRepository estoreRepository;
    private final FileManagerService fileManagerService;;

    @Autowired
    public SpecialEventServiceImpl(SpecialEventRepository specialEventRepository, EstoreRepository estoreRepository, FileManagerService fileManagerService) {
        this.specialEventRepository = specialEventRepository;
        this.estoreRepository = estoreRepository;
        this.fileManagerService = fileManagerService;
    }

    @Override
    public SpecialEventResponse addNewSpecialEvent(SpecialEventRequest specialEventRequest) {
        Estore estore = estoreRepository.findByEstoreId(specialEventRequest.estoreId())
                .orElseThrow(() -> new RuntimeException("Estore not found"));

        SpecialEvent specialEvent = new SpecialEvent(specialEventRequest.name(),
                specialEventRequest.description(),
                specialEventRequest.eventDate(),
                estore);

        specialEvent.setStartTime(specialEvent.getEventDate().atStartOfDay());
        specialEvent.setEndTime(specialEvent.getEventDate().atStartOfDay());
        SpecialEvent savedSpecialEvent =specialEventRepository.save(specialEvent);
        return new SpecialEventResponse(
                savedSpecialEvent.getSpecialEventId(),
                savedSpecialEvent.getName(),
                savedSpecialEvent.getImage(),
                savedSpecialEvent.getEventDate(),
                savedSpecialEvent.getStartTime(),
                savedSpecialEvent.getEndTime(),
                savedSpecialEvent.getDescription(),
                savedSpecialEvent.getPrice(),
                savedSpecialEvent.getEstore().getEstoreId()
                );
    }

    private SpecialEventResponse getSpecialEventResponse(SpecialEventRequest specialEventRequest, Estore estore, SpecialEvent specialEvent) {
        specialEvent.setName(specialEventRequest.name());
        specialEvent.setImage(specialEventRequest.image());
        specialEvent.setEventDate(specialEventRequest.eventDate());
        specialEvent.setStartTime(specialEventRequest.startTime());
        specialEvent.setEndTime(specialEventRequest.endTime());
        specialEvent.setDescription(specialEventRequest.description());
        specialEvent.setPrice(specialEventRequest.price());
        specialEvent.setEstore(estore);

        SpecialEvent savedEvent = specialEventRepository.save(specialEvent);
        return mapToResponse(savedEvent);
    }

    @Override
    public SpecialEventResponse updateSpecialEvent(Long specialEventId, SpecialEventRequest specialEventRequest) {
        SpecialEvent specialEvent = specialEventRepository.findBySpecialEventId(specialEventId)
                .orElseThrow(() -> new RuntimeException("Special Event not found"));

        Estore estore = estoreRepository.findByEstoreId(specialEventRequest.estoreId())
                .orElseThrow(() -> new RuntimeException("Estore not found"));

        return getSpecialEventResponse(specialEventRequest, estore, specialEvent);
    }

    @Override
    public SpecialEventResponse getSpecialEventById(Long specialEventId) {
        SpecialEvent specialEvent = specialEventRepository.findBySpecialEventId(specialEventId)
                .orElseThrow(() -> new RuntimeException("Special Event not found"));
        return mapToResponse(specialEvent);
    }

    @Override
    public void deleteSpecialEventById(Long specialEventId) {
        if (!specialEventRepository.existsBySpecialEventId(specialEventId)) {
            throw new RuntimeException("Special Event not found");
        }
        specialEventRepository.deleteBySpecialEventId(specialEventId);
    }

    @Override
    public List<SpecialEventResponse> listSpecialEvents() {
        List<SpecialEvent> events = specialEventRepository.findAll();
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<SpecialEventResponse> listSpecialEventsByEstoreId(Long estoreId) {
        Estore estore = estoreRepository.findByEstoreId(estoreId)
                .orElseThrow(() -> new RuntimeException("Estore not found"));
        List<SpecialEvent> events = new ArrayList<>(estore.getSpecialEvents());
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<SpecialEventResponse> listSpecialEventsByDate(LocalDate dateTime) {
        List<SpecialEvent> events = specialEventRepository.findAllByEventDate(dateTime);
        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialEventResponse> listSpecialEventsByAreaCode() {
        return null;
    }

    @Override
    public Page<SpecialEventResponse> getEstoreSpecialEvents(Long estoreId, String itemType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Query based on itemType
        Page<SpecialEvent> specialEventItems;
        if (itemType != null && !itemType.isBlank()) {
            specialEventItems = specialEventRepository.findSpecialEventByEstore_EstoreId(estoreId, pageable);
        } else {
            specialEventItems = specialEventRepository.findSpecialEventByEstore_EstoreId(estoreId, pageable);
        }

        // Map FoodItem entities to FoodItemResponse DTOs
        return specialEventItems.map(this::mapToResponse);
    }

    @Override
    public SpecialEventResponse updateSpecialEventLogo(Long id, MultipartFile file) {
        SpecialEvent pdt = specialEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("product not found with id " + id));
        // Check if a file is present and handle file upload
        if (file != null && !file.isEmpty()) {
            try {
                // Create a FileDTO instance
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFile(file); // Set the MultipartFile
                fileDTO.setFileName(file.getOriginalFilename()); // Set the original file name

                // Get the existing avatar filename for deletion
                String oldAvatar = pdt.getImage();

                // Call fileManagerService to upload the file and delete the old one

                SavedFileDTO savedFile = fileManagerService.uploadFile(fileDTO, "specialEvents", oldAvatar); // null for default folder

                // Set the new avatar URL based on the uploaded file
                pdt.setImage(savedFile.getGeneratedFileName()); // Assuming you store the file URL or name
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file", e);
            }
        }
        // Save the updated store back to the repository
        var savedProduct = specialEventRepository.save(pdt);

        return mapToResponse(savedProduct);
    }

    @Override
    public Page<SpecialEventResponse> getSpecialEventsWithoutEstore(int page, int size) {
        // Define pagination
        Pageable pageable = PageRequest.of(page, size);

        // Query food items without an associated estore
        Page<SpecialEvent> foodItems = specialEventRepository.findAll(pageable);

        // Map the results to FoodItemResponse DTOs
        return foodItems.map(this::mapToResponse);
    }

    // Utility method to map SpecialEvent to SpecialEventResponse
    private SpecialEventResponse mapToResponse(SpecialEvent specialEvent) {
        return new SpecialEventResponse(
                specialEvent.getSpecialEventId(),
                specialEvent.getName(),
                specialEvent.getImage(),
                specialEvent.getEventDate(),
                specialEvent.getStartTime(),
                specialEvent.getEndTime(),
                specialEvent.getDescription(),
                specialEvent.getPrice(),
                specialEvent.getEstore().getEstoreId()
        );
    }
}
