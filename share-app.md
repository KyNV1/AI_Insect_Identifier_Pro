# Implementation Plan - Share App Feature

This plan details the implementation of the "Share this app" feature in the Settings Screen.

## Proposed Changes

### Settings Screen

#### [MODIFY] [SettingsScreen.kt](file:///d:/chplay/AIInsectIdentifierPro/app/src/main/java/com/kynv1/aiinsectidentifierpro/ui/screens/settings/SettingsScreen.kt)
- Import `android.content.Intent`
- Modify the `onClick` event of the `SettingsRowItem` for sharing:
  - Generate a sharing message template: `Download [App Name] app: https://play.google.com/store/apps/details?id=[Package Name]`
  - Launch the standard Android Sharesheet (Intent Chooser) using `Intent.ACTION_SEND`.

## Verification Plan

### Manual Verification
- Run the application.
- Navigate to the **Settings Screen**.
- Tap **"Tell a Friend About Us"** (Share this app).
- Verify that the Android Sharesheet pops up correctly with the application name and download link.
- Tap "Copy" or share to an app (e.g. Messages, Zalo) and verify the text is correct.
