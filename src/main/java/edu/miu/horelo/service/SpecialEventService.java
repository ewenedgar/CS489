package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.SpecialEventRequest;
import edu.miu.horelo.dto.response.SpecialEventResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface SpecialEventService {
    SpecialEventResponse addNewSpecialEvent(SpecialEventRequest specialEventRequest);
    SpecialEventResponse updateSpecialEvent(Long specialEventId, SpecialEventRequest specialEventRequest);
    SpecialEventResponse getSpecialEventById(Long specialEventId);
    void deleteSpecialEventById(Long specialEventId);
    List<SpecialEventResponse> listSpecialEvents();
    List<SpecialEventResponse> listSpecialEventsByEstoreId(Long id);
    List<SpecialEventResponse> listSpecialEventsByDate(LocalDate dateTime);
    List<SpecialEventResponse> listSpecialEventsByAreaCode();

    Page<SpecialEventResponse> getEstoreSpecialEvents(Long estoreId, String itemType, int page, int size);

    SpecialEventResponse updateSpecialEventLogo(Long id, MultipartFile file);

    Page<SpecialEventResponse> getSpecialEventsWithoutEstore(int page, int size);
}
