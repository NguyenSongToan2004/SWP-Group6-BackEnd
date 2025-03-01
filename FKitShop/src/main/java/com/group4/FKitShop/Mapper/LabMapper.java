package com.group4.FKitShop.Mapper;

import com.group4.FKitShop.Entity.Lab;
import com.group4.FKitShop.Request.LabRequest;
import com.group4.FKitShop.Response.GetLabResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LabMapper {

    LabMapper INSTANCE = Mappers.getMapper(LabMapper.class);

    public Lab toLab(LabRequest request);

    @Mapping(source = "status", target = "status")
    public void updateLab(LabRequest request, @MappingTarget Lab lab);
    // @MappingTarget : chỉ định đối tượng sẽ được map dữ liệu

    public void toLapResponse(Lab lab, @MappingTarget GetLabResponse response);

}
