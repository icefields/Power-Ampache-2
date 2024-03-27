package luci.sixsixsix.powerampache2.presentation.screens.notifications

sealed class NotificationsScreenEvent {
    data object OnClearNotifications: NotificationsScreenEvent()
}
