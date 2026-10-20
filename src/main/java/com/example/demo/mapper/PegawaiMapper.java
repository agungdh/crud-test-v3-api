package com.example.demo.mapper;

import com.example.demo.dto.Gender;
import com.example.demo.dto.PegawaiRequest;
import com.example.demo.dto.PegawaiResponse;
import com.example.demo.entity.Pegawai;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PegawaiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "isMale", source = "gender")
    Pegawai toEntity(PegawaiRequest request);

    @Mapping(target = "gender", source = "isMale")
    PegawaiResponse toResponse(Pegawai entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "isMale", source = "gender")
    void updateEntity(PegawaiRequest request, @MappingTarget Pegawai entity);

    default Boolean toIsMale(Gender gender) {
        return gender == null ? null : gender == Gender.MALE;
    }

    default Gender toGender(Boolean isMale) {
        return isMale == null ? null : (isMale ? Gender.MALE : Gender.FEMALE);
    }
}
