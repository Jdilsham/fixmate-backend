package com.fixmate.backend.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.fixmate.backend.dto.request.AddProviderServiceRequest;
import com.fixmate.backend.dto.response.ProviderServiceCardResponse;
import com.fixmate.backend.dto.response.PublicServiceCardResponse;
import com.fixmate.backend.entity.Address;
import com.fixmate.backend.entity.ProviderService;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.Services;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.mapper.ProviderMapper;
import com.fixmate.backend.repository.ProviderServiceRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.ServiceRepository;
import com.fixmate.backend.service.ProviderServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.exception.BadRequestException;



@Service
@RequiredArgsConstructor
@Transactional
public class ProviderServiceServiceImpl implements ProviderServiceService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceRepository serviceRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderMapper providerMapper;

    @Override
    public void addServiceToProvider(
            Long userId,
            AddProviderServiceRequest dto,
            MultipartFile qualificationPdf
    ) {

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Service provider not found")
                );

        Services service = serviceRepository
                .findById(dto.getServiceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Service not found")
                );

        boolean exists = providerServiceRepository
                .existsByServiceProvider_ServiceProviderIdAndService_ServiceId(
                        provider.getServiceProviderId(),
                        service.getServiceId()
                );

        if (exists) {
            throw new BadRequestException("Service already exists");
        }

        // pricing validation
        if (dto.getFixedPrice() == null && dto.getHourlyRate() == null) {
            throw new IllegalArgumentException(
                    "Either fixed price or hourly rate must be provided"
            );
        }

        // save PDF
        String pdfPath = saveQualificationPdf(qualificationPdf);

        ProviderService providerService = new ProviderService();
        providerService.setServiceProvider(provider);
        providerService.setService(service);
        providerService.setDescription(dto.getDescription());
        providerService.setFixedPrice(dto.getFixedPrice());
        providerService.setHourlyRate(dto.getHourlyRate());
        providerService.setQualificationDoc(pdfPath);
        providerService.setVerificationStatus(VerificationStatus.PENDING);
        providerService.setIsActive(true);

        providerServiceRepository.save(providerService);
    }

    private String saveQualificationPdf(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Qualification PDF is required");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        try {
            String uploadDir = "uploads/provider-services/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store qualification PDF", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicServiceCardResponse> getApprovedServices() {

        return providerServiceRepository
                .findPublicApprovedServices(VerificationStatus.APPROVED);
    }




    @Override
    @Transactional(readOnly = true)
    public List<ProviderServiceCardResponse> getProviderServices(Long userId) {

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Provider not found")
                );

        List<ProviderService> services =
                providerServiceRepository
                        .findByServiceProvider_ServiceProviderId(
                                provider.getServiceProviderId()
                        );

        return services.stream()
                .map(providerMapper::toProviderServiceDTO)
                .toList();
    }

}

