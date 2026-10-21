package com.example.demo.mapper;

import com.example.demo.dto.DiklatRequest;
import com.example.demo.dto.DiklatResponse;
import com.example.demo.entity.Diklat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DiklatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "pegawai", ignore = true)
    @Mapping(target = "jenisPelatihan", ignore = true)
    Diklat toEntity(DiklatRequest request);

    @Mapping(target = "pegawaiUuid", source = "pegawai.uuid")
    @Mapping(target = "jenisPelatihanUuid", source = "jenisPelatihan.uuid")
    DiklatResponse toResponse(Diklat entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "pegawai", ignore = true)
    @Mapping(target = "jenisPelatihan", ignore = true)
    void updateEntity(DiklatRequest request, @MappingTarget Diklat entity);
}
