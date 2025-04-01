package org.formentor.product.api;

import io.swagger.v3.oas.annotations.Parameter;
import org.formentor.product.domain.Hotel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("hotel")
public class HotelController {

    @GetMapping("/destination")
    @Operation(
            description = "Get hotels for a given destination"
    )
    public List<Hotel> getHotelsByLocation(
            @Parameter(description = "Destination where hotels are located", example = "Madrid, Paris", required = true)
            @RequestParam("destination") String destination) {
        return List.of(
                new Hotel("2804", "Hotel Castilla", "Urban hotel located in Madrid"),
                new Hotel("3804", "Hotel Barajas", "Urban hotel located in Madrid")
        );
    }
}
