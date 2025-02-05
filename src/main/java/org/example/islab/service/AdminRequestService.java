package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.example.islab.entity.AdminRequest;
import org.example.islab.entity.AdminRequestStatus;
import org.example.islab.entity.User;
import org.example.islab.entity.UserType;
import org.example.islab.repository.AdminRequestRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminRequestService {
    private final AdminRequestRepository adminRequestRepository;
    private final UserService userService;

    public List<AdminRequest> getAll() {
        return adminRequestRepository.findAll();
    }

    public AdminRequest requestRole() {
        return requestRole(userService.getCurrentUser());
    }

    public AdminRequest requestRole(User user) {
        if (user.getUserType() != UserType.ADMIN) {
            if (adminRequestRepository.existsByUser(user)) {
                throw HttpClientErrorException.create(HttpStatusCode.valueOf(429), "Already requested", null, null, null);
            }

            AdminRequest req = new AdminRequest(user);
            adminRequestRepository.save(req);
            return req;
        }
        else {
            throw HttpClientErrorException.create(HttpStatusCode.valueOf(400), "Already admin", null, null, null);
        }
    }

    @Transactional
    public AdminRequest approveRequest(Long requestId) {
        AdminRequest request = adminRequestRepository.findById(requestId).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Role request not found", null, null, null)
        );

        request.getUser().setUserType(UserType.ADMIN);

        request.setStatus(AdminRequestStatus.ACCEPTED);
        request.setComment("Your request successfully approved");

        adminRequestRepository.save(request);
        return request;
    }

    @Transactional
    public AdminRequest rejectRequest(Long requestId) {
        AdminRequest request = adminRequestRepository.findById(requestId).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Role request not found", null, null, null)
        );

        request.getUser().setUserType(UserType.MEMBER);

        request.setStatus(AdminRequestStatus.REJECTED);
        request.setComment("Your request was rejected");

        adminRequestRepository.save(request);
        return request;
    }
}
