package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.request.StatusRequest;
import edu.miu.horelo.dto.response.StatusResponse;
import edu.miu.horelo.model.Status;
import edu.miu.horelo.repository.StatusRepository;
import edu.miu.horelo.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {
    private final StatusRepository statusRepository;



    @Override
    public StatusResponse createStatus(StatusRequest statusRequest) {
        Status status = new Status(
                null,
                statusRequest.received(),
                statusRequest.seen(),
                statusRequest.confirmed(),
                statusRequest.queued(),
                statusRequest.processing(),
                statusRequest.packed(),
                statusRequest.readyForPickup()
        );
        status = statusRepository.save(status);
        return mapToStatusResponse(status);
    }

    @Override
    public StatusResponse updateStatus(Long id, StatusRequest statusRequest) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));

        status.setReceived(statusRequest.received());
        status.setSeen(statusRequest.seen());
        status.setConfirmed(statusRequest.confirmed());
        status.setQueued(statusRequest.queued());
        status.setProcessing(statusRequest.processing());
        status.setPacked(statusRequest.packed());
        status.setReadyForPickup(statusRequest.readyForPickup());

        return mapToStatusResponse(statusRepository.save(status));
    }

    @Override
    public Optional<StatusResponse> getStatusById(Long id) {
        return statusRepository.findById(id).map(this::mapToStatusResponse);
    }

    @Override
    public List<StatusResponse> getAllStatuses() {
        return statusRepository.findAll().stream()
                .map(this::mapToStatusResponse)
                .toList();
    }

    @Override
    public void deleteStatus(Long id) {
        statusRepository.deleteById(id);
    }

    private StatusResponse mapToStatusResponse(Status status) {
        return new StatusResponse(
                status.getStatusId(),
                status.getReceived(),
                status.getSeen(),
                status.getConfirmed(),
                status.getQueued(),
                status.getProcessing(),
                status.getPacked(),
                status.getReadyForPickup()
        );
    }
}