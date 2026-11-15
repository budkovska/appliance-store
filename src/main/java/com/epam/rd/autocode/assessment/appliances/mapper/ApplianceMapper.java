package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import org.springframework.stereotype.Component;

@Component
public class ApplianceMapper {

    public ApplianceDto toDto(Appliance a) {
        ApplianceDto d = new ApplianceDto();
        d.setId(a.getId());
        d.setName(a.getName());
        d.setCategory(a.getCategory());
        d.setModel(a.getModel());
        if (a.getManufacturer() != null) {
            d.setManufacturerId(a.getManufacturer().getId());
            d.setManufacturerName(a.getManufacturer().getName());
        }
        d.setPowerType(a.getPowerType());
        d.setCharacteristic(a.getCharacteristic());
        d.setDescription(a.getDescription());
        d.setPower(a.getPower());
        d.setPrice(a.getPrice());
        return d;
    }

    public void copyToEntity(ApplianceDto d, Appliance a, Manufacturer manufacturer) {
        a.setName(d.getName());
        a.setCategory(d.getCategory());
        a.setModel(d.getModel());
        a.setManufacturer(manufacturer);
        a.setPowerType(d.getPowerType());
        a.setCharacteristic(d.getCharacteristic());
        a.setDescription(d.getDescription());
        a.setPower(d.getPower());
        a.setPrice(d.getPrice());
    }
}