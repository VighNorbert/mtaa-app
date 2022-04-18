package sk.evysetrenie.api.model

import sk.evysetrenie.api.model.contracts.responses.AppointmentResponse
import java.time.LocalDate

data class AppointmentDate(
    val date: LocalDate,
    val appointments: MutableList<AppointmentResponse>
)