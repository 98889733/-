# OXIDE PRIVATE PANEL — Android starter

This project builds a draggable Android overlay with FIGHT and BOX tabs.

## Build
Push this folder to a GitHub repository. Open **Actions → Build Android APK → Run workflow**.
Download the `oxide-private-panel-debug` artifact from the completed run.

You can also build locally with Android SDK + JDK 17:
`gradle assembleDebug`

## Scope
This is a UI/overlay starter. It does not read Oxide memory, inspect other apps,
draw player ESP, or automate aiming. LuluBox Super compatibility is not assumed:
overlay permission and behavior depend on the Android version and the host environment.
