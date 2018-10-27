package com.jskaleel.sangaelakkiyangal.utils

object AppConstants {
    var DOWNLOAD_NOTIFICATION = "download_notification"

    var KEY_CATEGORY_ITEM_RESPONSE = "key_category_item_response"

    var STATUS_NONE = "status_none"
    var STATUS_QUEUED = "status_queued"
    var STATUS_COMPLETED = "status_completed"
    var STATUS_ERROR = "status_error"
    var DOWNLOAD_VIDEO_COMPLETED = "DOWNLOAD_VIDEO_COMPLETED"
    val DOWNLOAD_STATUS = "download_status"


    /**
     * Status of a download request that could not be enqueued.
     */
    val STATUS_NOT_QUEUED_CODE = -900

    /**
     * Status of a download request if it is queued for downloading.
     */
    val STATUS_QUEUED_CODE = 900

    /**
     * Status of a download request if it is currently downloading.
     */
    val STATUS_DOWNLOADING_CODE = 901

    /**
     * Status of a download request if it is paused.
     */
    val STATUS_PAUSED_CODE = 902

    /**
     * Status of a download request if the file has been downloaded successfully.
     */
    val STATUS_DONE_CODE = 903

    /**
     * Status of a download request if an error occurred during downloading.
     */
    val STATUS_ERROR_CODE = 904

    val CUSTOM_STATUS_RESUME_CODE = 911
    val STATUS_REMOVED_CODE = 905
    val NOTIFICATION_ID = 80456
    val ACTION_CANCEL_PLAYBACK = "utilities.ACTION_CANCEL_PLAYBACK"
    val OPEN_SAVED_VIDEO_PAGE = "open_saved_video_page"
}