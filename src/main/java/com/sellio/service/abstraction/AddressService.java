package com.sellio.service.abstraction;

import com.sellio.model.entity.AddressEntity;

public interface AddressService {
    AddressEntity save(String placeId);
}
