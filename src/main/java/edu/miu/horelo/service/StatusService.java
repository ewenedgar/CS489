package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.StatusRequest;
import edu.miu.horelo.dto.response.StatusResponse;

import java.util.List;
import java.util.Optional;

public interface StatusService {
    StatusResponse createStatus(StatusRequest statusRequest);
    StatusResponse updateStatus(Long id, StatusRequest statusRequest);
    Optional<StatusResponse> getStatusById(Long id);
    List<StatusResponse> getAllStatuses();
    void deleteStatus(Long id);
}