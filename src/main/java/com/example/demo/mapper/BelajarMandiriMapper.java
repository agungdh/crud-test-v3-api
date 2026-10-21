package com.example.demo.mapper;

import com.example.demo.dto.BelajarMandiriRequest;
import com.example.demo.dto.BelajarMandiriResponse;
import com.example.demo.entity.BelajarMandiri;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BelajarMandiriMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "pegawai", ignore = true)
    BelajarMandiri toEntity(BelajarMandiriRequest request);

    @Mapping(target = "pegawaiUuid", source = "pegawai.uuid")
    BelajarMandiriResponse toResponse(BelajarMandiri entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "pegawai", ignore = true)
    void updateEntity(BelajarMandiriRequest request, @MappingTarget BelajarMandiri entity);
}
