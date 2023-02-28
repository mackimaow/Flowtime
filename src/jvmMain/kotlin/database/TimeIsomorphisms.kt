package database

import category.Isomorphism
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object InstantIso: Isomorphism<Instant, kotlinx.datetime.Instant> {
    override fun morph(obj: Instant): kotlinx.datetime.Instant {
        return kotlinx.datetime.Instant.fromEpochMilliseconds(obj.toEpochMilli())
    }
    override fun morphInv(obj: kotlinx.datetime.Instant): Instant {
        return Instant.ofEpochMilli(obj.toEpochMilliseconds())
    }
}

object LocalDateTimeIso: Isomorphism<LocalDateTime, kotlinx.datetime.Instant> {
    override fun morph(obj: LocalDateTime): kotlinx.datetime.Instant {
        return InstantIso.morph(
            obj.toInstant(ZoneOffset.UTC)
        )
    }
    override fun morphInv(obj: kotlinx.datetime.Instant): LocalDateTime {
        return LocalDateTime.ofInstant(
            InstantIso.morphInv(obj),
            ZoneOffset.UTC
        )
    }
}