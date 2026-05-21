package api.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDates {

    private String checkin;
    private String checkout;
}
