import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Wallsevice {

    fun notZeroShare(share: Long) = when (share) {
        in 0..999 -> share
        in 1_000..9_999 -> share / 10_000.toDouble()
        in 10_000..99_999 -> share / 10_000
        in 100_000..999_999 -> share / 1000_000.toDouble()
        in 1_000_000..100_000_000 -> share / 1000_000
        else -> share
    }

    fun zeroingOutShare(share: Long) = when (share) {
        in 0..999 -> share
        in 1_000..9_999 -> "${this.notZeroShare(share)} К"
        in 10_000..99_999 -> "${this.notZeroShare(share)} К"
        in 100_000..999_999 -> "${this.notZeroShare(share)} М"
        in 1_000_000..100_000_000 -> "${this.notZeroShare(share)} М"
        else -> share
    }

    fun notZeroLikes(likes: Int) = when (likes) {
        in 0..999 -> likes
        in 1_000..9_999 -> likes / 10_000.toDouble()
        in 10_000..99_999 -> likes / 10_000
        in 100_000..999_999 -> likes / 1000_000.toDouble()
        in 1_000_000..100_000_000 -> likes / 1000_000
        else -> likes
    }

    fun zeroingOutLikes(likes: Long) = when (likes) {
        in 0..999 -> likes
        in 1_000..9_999 -> "${this.notZeroShare(likes)} К"
        in 10_000..99_999 -> "${this.notZeroShare(likes)} К"
        in 100_000..999_999 -> "${this.notZeroShare(likes)} М"
        in 1_000_000..100_000_000 -> "${this.notZeroShare(likes)} М"
        else -> likes
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val then = run {
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("H:m")
        val date = LocalDate.parse("Feb 13 2022", dateFormatter)
        val time = LocalTime.parse("22:13", timeFormatter)
        date.atTime(time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val now = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val hours = then.until(now, ChronoUnit.HOURS)

    @RequiresApi(Build.VERSION_CODES.O)
    val days = then.until(now, ChronoUnit.DAYS)

    @RequiresApi(Build.VERSION_CODES.O)
    val secunds = then.until(now, ChronoUnit.SECONDS)


    fun timeСonverter(hours: Long): String = when (hours) {
        in 0..24 -> "Сегодня!"
        in 24..48 -> "Вчера!"
        in 48..168 -> "На прошлой неделе!"
        else -> "давно"
    }

    val agoToText = timeСonverter(hours)


}
