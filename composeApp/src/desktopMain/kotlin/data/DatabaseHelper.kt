package data

import app.cash.sqldelight.adapter.primitive.FloatColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

val database: Database = run {
    val driver = JdbcSqliteDriver("jdbc:sqlite:Database.db")
    Database.Schema.create(driver)
    Database(
        driver = driver,
        TestAnalyticsAdapter = TestAnalytics.Adapter(
            numCorrectAdapter = IntColumnAdapter,
            numTotalAdapter = IntColumnAdapter,
            consumedTimeAdapter = FloatColumnAdapter,
            correctRateAdapter = FloatColumnAdapter,
            avgSpeedAdapter = FloatColumnAdapter
        )
    )
}