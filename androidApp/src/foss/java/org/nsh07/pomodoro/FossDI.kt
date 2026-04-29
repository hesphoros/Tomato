/*
 * Copyright (c) 2025 Nishant Mishra
 *
 * This file is part of Tomato - a minimalist pomodoro timer for Android.
 */

package org.nsh07.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.dsl.module
import org.nsh07.pomodoro.billing.BillingManager
import org.nsh07.pomodoro.billing.FossBillingManager
import org.nsh07.pomodoro.billing.TomatoPlusPaywallDialog
import org.nsh07.pomodoro.di.FlavorUI

@Composable
private fun EmptyAboutButton(modifier: Modifier = Modifier) = Unit

val flavorModule = module {
    single<BillingManager> { FossBillingManager() }
}

val flavorUiModule = module {
    single {
        FlavorUI(
            tomatoPlusPaywallDialog = ::TomatoPlusPaywallDialog,
            topButton = ::EmptyAboutButton,
            bottomButton = ::EmptyAboutButton
        )
    }
}
