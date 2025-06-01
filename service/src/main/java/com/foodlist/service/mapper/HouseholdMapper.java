package com.foodlist.service.mapper;

import com.foodlist.service.dto.HouseholdDTO;
import com.foodlist.service.model.Household;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HouseholdMapper {

    HouseholdDTO householdToHouseholdDTO(Household household);
    Household householdDTOToHousehold(HouseholdDTO householdDTO);

}
