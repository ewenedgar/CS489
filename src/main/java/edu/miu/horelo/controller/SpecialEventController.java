package edu.miu.horelo.controller;

import edu.miu.horelo.dto.request.SpecialEventRequest;
import edu.miu.horelo.dto.response.SpecialEventResponse;
import edu.miu.horelo.service.SpecialEventService;
import jakarta.validation.Valid;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = {"/api/v1/public/auth"})
public class SpecialEventController {

    private final SpecialEventService specialEventService;

    @Autowired
    public SpecialEventController(SpecialEventService specialEventService) {
        this.specialEventService = specialEventService;
    }

    // Create a new Special Event
    @PostMapping("/special-event")
    public ResponseEntity<SpecialEventResponse> addNewSpecialEvent(@Valid @RequestBody SpecialEventRequest specialEventRequest) {
        SpecialEventResponse newEvent = specialEventService.addNewSpecialEvent(specialEventRequest);
        return ResponseEntity.ok(newEvent);
    }
    @PatchMapping("/special-event/{id}")
    public ResponseEntity<SpecialEventResponse> updateSpecialEvent(@PathVariable Long id,@RequestBody SpecialEventRequest specialEventRequest) {
        SpecialEventResponse newEvent = specialEventService.updateSpecialEvent(id, specialEventRequest);
        return ResponseEntity.ok(newEvent);
    }

    @DeleteMapping("/special-event/{id}")
    public void updateSpecialEvent(@PathVariable Long id) {
         specialEventService.deleteSpecialEventById(id);
    }


    @GetMapping("/special-event/{id}")
    public ResponseEntity<SpecialEventResponse> getSpecialEventBySpecialEventId(@PathVariable Long id) {
        SpecialEventResponse newEvent = specialEventService.getSpecialEventById(id);
        return ResponseEntity.ok(newEvent);
    }
    @GetMapping("/special-event/estore/{id}")
    public ResponseEntity<List<SpecialEventResponse>> getSpecialEventsByEstoreId(@PathVariable Long id) {
        List<SpecialEventResponse> newEvent = specialEventService.listSpecialEventsByEstoreId(id);
        return ResponseEntity.ok(newEvent);
    }
    @GetMapping("/special-event/estore/{id}/datetime/{dateTime}")
    public ResponseEntity<List<SpecialEventResponse>> getSpecialEventsByEstoreIdAndDateTime(@PathVariable Long id, @PathVariable String dateTime) {
        List<SpecialEventResponse> newEvent = specialEventService.listSpecialEventsByEstoreId(id);
        return ResponseEntity.ok(newEvent);
    }
    @GetMapping("/special-event/datetime/{datetime}")
    public ResponseEntity<List<SpecialEventResponse>> getSpecialEventsByDate(
            @PathVariable LocalDate datetime
            ) {
        List<SpecialEventResponse> newEvent = specialEventService.listSpecialEventsByDate(datetime);
        return ResponseEntity.ok(newEvent);
    }
    @GetMapping("/special-event/")
    public ResponseEntity<List<SpecialEventResponse>> getAllSpecialEvents(@PathVariable Long id) {
        List<SpecialEventResponse> newEvent = specialEventService.listSpecialEvents();
        return ResponseEntity.ok(newEvent);
    }

    @PatchMapping("/special-event/{id}/logo")
    public ResponseEntity<SpecialEventResponse> updateEventLogo(@RequestHeader("Authorization") String token,
                                                            @PathVariable("id") Long id,  // Handle user profile data
                                                            @RequestPart(value="file", required = false) MultipartFile file // Handle file upload (optional)
    ) {

        if (file == null|| file.isEmpty()) {

            System.out.println("No file received " + id );
            return ResponseEntity.badRequest().body(null);
        } else {
            System.out.println("Received file: " + file.getOriginalFilename());
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));

        SpecialEventResponse productResponse = specialEventService.updateSpecialEventLogo(id,  file);
        return ResponseEntity.ok(productResponse);
    }

}