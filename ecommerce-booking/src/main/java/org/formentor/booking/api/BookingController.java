package org.formentor.booking.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.formentor.booking.domain.Availability;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("booking")
public class BookingController {

    @GetMapping("/availability")
    @Operation(
            description = "Get the price of booking a room stay in a Hotel for given dates and number of adults and children"
    )
    public List<Availability> calculateHotelBookingPrice(
            @Parameter(description = "Hotel to be booked", example = "Hotel Castilla", required = true)
            @RequestParam("hotel") String hotel,
            @Parameter(description = "Arrival date to the hotel", example = "2025/07/26", required = true)
            @RequestParam("arrival") String arrival,
            @Parameter(description = "Duration of stay in number of nights", example = "5", required = true)
            @RequestParam("nights") Integer nights,
            @Parameter(description = "Number of adults", example = "2", required = true)
            @RequestParam("adults") Integer adults,
            @Parameter(description = "Number of children", example = "1", required = true)
            @RequestParam("children") Integer children
    ) {
        return List.of(
                new Availability("Double room", 120.45),
                new Availability("Suite room", 148.65)
        );
    }
}
