package ru.octol1ttle.flightassistant.api.alert

import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.api.util.extensions.FAGuiGraphics

interface CenteredAlert {
    /**
     * @return whether or not this alert has rendered and occupied the center of the screen
     */
    fun render(guiGraphics: FAGuiGraphics, y: Int): Boolean
}
