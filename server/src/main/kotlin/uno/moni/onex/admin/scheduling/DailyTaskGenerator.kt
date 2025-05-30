package uno.moni.onex.admin.scheduling

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import uno.moni.onex.business.task.service.TaskService
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Component
class DailyTaskGenerator(
    val taskService: TaskService
) {
    companion object {
        private const val SYSTEM_TIME_ZONE = "Asia/Shanghai"
    }
    @Scheduled(cron = "0 0 * * * *")
    fun dailyTask() {
        val dateTime: ZonedDateTime = LocalDateTime.now().atZone(ZoneId.of(SYSTEM_TIME_ZONE))
        taskService.genDailyTask(dateTime.toLocalDate())
        println("Daily Task Generated In [${dateTime.toLocalDate()}]")
    }
}