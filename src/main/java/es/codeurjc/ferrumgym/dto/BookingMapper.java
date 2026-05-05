package es.codeurjc.ferrumgym.dto;

import es.codeurjc.ferrumgym.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    // --- De Entidad a DTO (Salida) ---
    // Extraemos campos específicos de las entidades relacionadas para evitar ciclos
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "activityId", source = "activity.id")
    @Mapping(target = "activityName", source = "activity.name")
    BookingDTO toDTO(Booking booking);

    // --- De DTO a Entidad (Entrada) ---
    // Este método permite convertir el DTO que viene de la API de vuelta a la Entidad
    Booking toEntity(BookingDTO dto);

    List<BookingDTO> toDTOs(Collection<Booking> bookings);
}