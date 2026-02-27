package com.sellio.service.impl;

import com.sellio.mapper.AddressMapper;
import com.sellio.model.dto.domain.AddressDto;
import com.sellio.model.entity.AddressEntity;
import com.sellio.repository.AddressRepository;
import com.sellio.service.abstraction.AddressService;
import com.sellio.service.concrete.GoogleMapsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final GoogleMapsService googleMapsService;
    private final AddressMapper addressMapper;

    @Override
    public AddressEntity save(String placeId) {
        return addressRepository.findByPlaceId(placeId)
                .orElseGet(() -> {
                    AddressDto addressDto = googleMapsService.getAddressByPlaceId(placeId);
                    return addressMapper.toEntity(addressDto);
                });
    }
}
