package com.sellio.service.impl;

import com.sellio.mapper.AddressMapper;
import com.sellio.model.dto.response.core.AddressResponse;
import com.sellio.model.entity.AddressEntity;
import com.sellio.repository.AddressRepository;
import com.sellio.service.abstraction.AddressService;
import com.sellio.service.concrete.GoogleMapsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final GoogleMapsService googleMapsService;
    private final AddressMapper addressMapper;

    @Override
    public AddressEntity save(String placeId) {
        log.info("ActionLog.loadAddressByPlaceId: {}", placeId);
        return addressRepository.findByPlaceId(placeId)
                .orElseGet(() -> {
                    AddressResponse addressResponse = googleMapsService.getAddressByPlaceId(placeId);
                    return addressMapper.toEntity(addressResponse);
                });
    }
}
