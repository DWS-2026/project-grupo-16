package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    // --- From Entity to DTO (Output) ---
    // Extracts specific fields from related entities to prevent infinite recursion cycles
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "activityId", source = "activity.id")
    @Mapping(target = "activityName", source = "activity.name")
    BookingDTO toDTO(Booking booking);

    // --- From DTO to Entity (Input) ---
    // This method allows converting the DTO received from the API back into a persistence Entity
    Booking toEntity(BookingDTO dto);

    List<BookingDTO> toDTOs(Collection<Booking> bookings);
}