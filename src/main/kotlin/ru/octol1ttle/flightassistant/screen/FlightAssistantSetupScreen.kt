package ru.octol1ttle.flightassistant.screen

import dev.isxander.yacl3.platform.YACLPlatform
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json
import ru.octol1ttle.flightassistant.api.util.extensions.*
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import org.lwjgl.system.MemoryUtil
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedString
import ru.octol1ttle.flightassistant.config.FAConfigScreen
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.autoflight.AutoFlightScreen
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget
import ru.octol1ttle.flightassistant.screen.fms.arrival.ArrivalScreen
import ru.octol1ttle.flightassistant.screen.fms.departure.DepartureScreen
import ru.octol1ttle.flightassistant.screen.fms.enroute.EnrouteScreen
import ru.octol1ttle.flightassistant.screen.system.SystemManagementScreen

class FlightAssistantSetupScreen : FABaseScreen(null, Component.translatable("menu.flightassistant")) {
    private var saveLoadError = false

    override fun init() {
        super.init()

        this.addRenderableWidget(SmartStringWidget(this.centerX, this.centerY - 80, Component.translatable("menu.flightassistant.system")).middleAligned())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.manage_displays")) {
            this.minecraft!!.setScreenSafe(
                SystemManagementScreen(
                    this,
                Component.translatable("menu.flightassistant.system.manage_displays"), "menu.flightassistant.system.name.hud", HudDisplayHost)
            )
        }.pos(this.centerX - 105, this.centerY - 65).width(100).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.manage_computers")) {
            this.minecraft!!.setScreenSafe(
                SystemManagementScreen(
                    this,
                Component.translatable("menu.flightassistant.system.manage_computers"), "menu.flightassistant.system.name.computer", ComputerHost)
            )
        }.pos(this.centerX + 5, this.centerY - 65).width(100).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.autoflight")) {
            this.minecraft!!.setScreenSafe(AutoFlightScreen(this))
        }.pos(this.centerX - 80, this.centerY - 30).width(160).build())

        this.addRenderableWidget(SmartStringWidget(this.centerX, this.centerY + 5, Component.translatable("menu.flightassistant.fms")).middleAligned())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.departure")) {
            this.minecraft!!.setScreenSafe(DepartureScreen(this))
        }.pos(this.centerX - 130, this.centerY + 20).width(80).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.enroute")) {
            this.minecraft!!.setScreenSafe(EnrouteScreen(this))
        }.pos(this.centerX - 40, this.centerY + 20).width(80).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.arrival")) {
            this.minecraft!!.setScreenSafe(ArrivalScreen(this))
        }.pos(this.centerX + 50, this.centerY + 20).width(80).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.save")) {
            showFlightPlanDialog(save = true) { path ->
                FileWriter(path).use { it.write(Json.encodeToString(FlightPlanComputer.FlightPlan(computers.plan))) }

                FlightAssistant.logger.info("Saved flight plan to $path")
            }
        }.pos(this.centerX - 90, this.centerY + 50).width(80).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.load")) {
            showFlightPlanDialog(save = false) { path ->
                FileReader(path).use { computers.plan.load(Json.decodeFromString(it.readText())) }
                DepartureScreen.reload(computers.plan.departureData)
                EnrouteScreen.reload(computers.plan.enrouteData)
                ArrivalScreen.reload(computers.plan.arrivalData)

                FlightAssistant.logger.info("Loaded flight plan from $path")
            }
        }.pos(this.centerX + 10, this.centerY + 50).width(80).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.config")) {
            this.minecraft!!.setScreenSafe(FAConfigScreen.generate(this))
        }.pos(10, this.height - 30).width(120).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.wiki")) {
//? if >=26.3 {
            com.mojang.blaze3d.Blaze3D.openUri(java.net.URI.create("https://github.com/Octol1ttle/FlightAssistant/wiki"))
//?} else {
            /*net.minecraft.Util.getPlatform().openUri("https://github.com/Octol1ttle/FlightAssistant/wiki")
*///?}
        }.pos(140, this.height - 30).width(80).build())

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }

    /**
     * Asks for a flight plan file and runs [action] with its path on the render thread.
     * Failures are logged and shown on the screen.
     */
    private fun showFlightPlanDialog(save: Boolean, action: (String) -> Unit) {
        val verb = if (save) "save" else "load"
        val onPicked: (String?) -> Unit = { path ->
            try {
                if (path != null) {
                    action(path)
                }
                saveLoadError = false
            } catch (e: Throwable) {
                FlightAssistant.logger.error("Unable to $verb flight plan", e)
                saveLoadError = true
            }
        }

        try {
            Files.createDirectories(PLANS_PATH)
        } catch (e: Throwable) {
            FlightAssistant.logger.error("Unable to $verb flight plan", e)
            saveLoadError = true
            return
        }
        val defaultPath = PLANS_PATH.resolve("flight_plan.json").toAbsolutePath().toString()

//? if >=26.3 {
        // 26.3 replaced TinyFD with SDL, whose dialogs return immediately and call back from another thread
        val filterName = MemoryUtil.memUTF8("Flight plans")
        val filterPattern = MemoryUtil.memUTF8("json")
        val location = MemoryUtil.memUTF8(defaultPath)
        val filters = org.lwjgl.sdl.SDL_DialogFileFilter.calloc(1)
        filters.get(0).name(filterName).pattern(filterPattern)

        lateinit var callback: org.lwjgl.sdl.SDL_DialogFileCallback
        val free = {
            callback.free()
            filters.free()
            MemoryUtil.memFree(filterName)
            MemoryUtil.memFree(filterPattern)
            MemoryUtil.memFree(location)
        }
        callback = org.lwjgl.sdl.SDL_DialogFileCallback.create { _, fileList, _ ->
            // A null list means the dialog failed, an empty one that it was cancelled
            val error = if (fileList == MemoryUtil.NULL) org.lwjgl.sdl.SDLError.SDL_GetError() else null
            val firstFile = if (fileList == MemoryUtil.NULL) MemoryUtil.NULL else MemoryUtil.memGetAddress(fileList)
            val path = if (firstFile == MemoryUtil.NULL) null else MemoryUtil.memUTF8(firstFile)
            FlightAssistant.mc.execute {
                free()
                if (error != null) {
                    FlightAssistant.logger.error("Unable to $verb flight plan: $error")
                    saveLoadError = true
                } else {
                    onPicked(path)
                }
            }
        }

        try {
            val window = this.minecraft!!.window.handle()
            if (save) {
                org.lwjgl.sdl.SDLDialog.SDL_ShowSaveFileDialog(callback, MemoryUtil.NULL, window, filters, location)
            } else {
                org.lwjgl.sdl.SDLDialog.SDL_ShowOpenFileDialog(callback, MemoryUtil.NULL, window, filters, location, false)
            }
        } catch (e: Throwable) {
            free()
            FlightAssistant.logger.error("Unable to $verb flight plan", e)
            saveLoadError = true
        }
//?} else {
        /*val patterns = org.lwjgl.PointerBuffer.create(MemoryUtil.memUTF8(".json"))
        try {
            val title = if (save) "Save flight plan" else "Load flight plan"
            val description = "Flight plans"

            val path = if (save) {
                org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_saveFileDialog(title, defaultPath, patterns, description)
            } else {
                org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, patterns, description, false)
            }
            onPicked(path)
        } catch (e: Throwable) {
            FlightAssistant.logger.error("Unable to $verb flight plan", e)
            saveLoadError = true
        } finally {
            patterns.free()
        }
*///?}
    }

//? if >=26.2 {
    override fun extractRenderState(guiGraphics: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, delta)

        if (saveLoadError) {
            guiGraphics.drawMiddleAlignedString(Component.translatable("menu.flightassistant.fms.error"), this.centerX, this.centerY + 75, ChatFormatting.RED.color!!, true)
        }
//?} else {
    /*override fun render(guiGraphics: FAGuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(guiGraphics, mouseX, mouseY, delta)

        if (saveLoadError) {
            guiGraphics.drawMiddleAlignedString(Component.translatable("menu.flightassistant.fms.error"), this.centerX, this.centerY + 75, ChatFormatting.RED.color!!, true)
        }
*///?}
    }

    companion object {
        val PLANS_PATH: Path = YACLPlatform.getConfigDir().resolve("${FlightAssistant.MOD_ID}/plans")
    }
}
