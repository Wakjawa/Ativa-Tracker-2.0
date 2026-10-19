package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ativa Tracker", appName)
  }

  @Test
  fun `verify fund accounts tally calculation`() {
    val sampleAccounts = listOf(
      com.example.data.FundAccountEntity(
        id = "1",
        name = "Maybank MAE",
        accountType = com.example.data.FundAccountType.BANK_ACCOUNT.name,
        collectedAmount = 3000.0
      ),
      com.example.data.FundAccountEntity(
        id = "2",
        name = "Reserve Pocket",
        accountType = com.example.data.FundAccountType.SAVINGS_POCKET.name,
        collectedAmount = 1200.0
      ),
      com.example.data.FundAccountEntity(
        id = "3",
        name = "Touch 'n Go",
        accountType = com.example.data.FundAccountType.EWALLET.name,
        collectedAmount = 300.0
      )
    )

    val totalCollected = sampleAccounts.sumOf { it.collectedAmount }
    assertEquals(4500.0, totalCollected, 0.001)
  }
}
