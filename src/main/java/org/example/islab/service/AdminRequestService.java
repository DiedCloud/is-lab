package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.example.islab.entity.AdminRequest;
import org.example.islab.entity.AdminRequestStatus;
import org.example.islab.entity.UserType;
import org.example.islab.repository.AdminRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminRequestService {
    private final AdminRequestRepository adminRequestRepository;

    public List<AdminRequest> getAll(){
        return adminRequestRepository.findAll();
    }

    public AdminRequest approveRequest(Long requestId) {
        AdminRequest request = adminRequestRepository.getReferenceById(requestId);

        request.getUser().setUserType(UserType.ADMIN);

        request.setStatus(AdminRequestStatus.ACCEPTED);
        request.setComment("Your request successfully approved");

        adminRequestRepository.save(request);
        return request;
    }

    public AdminRequest rejectRequest(Long requestId) {
        AdminRequest request = adminRequestRepository.getReferenceById(requestId);

        request.getUser().setUserType(UserType.MEMBER);

        request.setStatus(AdminRequestStatus.ACCEPTED);
        request.setComment("Your request was rejected");

        adminRequestRepository.save(request);
        return request;
    }
}
