package com.droptechsolution.menus.push.di

import dagger.Subcomponent

@Subcomponent
interface PushComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): PushComponent
    }
}